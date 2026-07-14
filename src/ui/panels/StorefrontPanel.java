package ui.panels;

import exception.InvalidQuantityException;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import model.Product;
import service.CartService;
import service.ProductService;
import ui.AppFrame;
import ui.Theme;
import ui.components.UiFactory;
import ui.frames.CartFrame;
import ui.frames.OrdersFrame;
import ui.frames.ProductDetailsDialog;
import ui.frames.ProfileFrame;
import ui.frames.WishlistFrame;
import util.ImageUtil;


public class StorefrontPanel extends JPanel {
    private final AppFrame app;
    private final JPanel productGrid = new JPanel(new GridLayout(0, 4, 16, 16));
    private final JPanel headerHolder = new JPanel(new BorderLayout());
    private final JTextField searchField = UiFactory.textField(20);
    private final JComboBox<String> categoryBox = new JComboBox<>();
    private final JComboBox<String> sortBox = new JComboBox<>(new String[]{
        "Featured", "Price: Low to High", "Price: High to Low", "Name: A to Z"
    });
    private final JLabel resultLabel = UiFactory.mutedLabel("");
    private List<Product> allProducts = new ArrayList<>();

    public StorefrontPanel(AppFrame app) {
        this.app = app;
        setLayout(new BorderLayout());
        setBackground(Theme.BACKGROUND);
        headerHolder.setOpaque(false);
        add(headerHolder, BorderLayout.NORTH);
        add(createContent(), BorderLayout.CENTER);
        bindFilters();
        refreshView();
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Theme.SURFACE);
        header.setBorder(BorderFactory.createEmptyBorder(14, 28, 14, 28));

        header.add(UiFactory.brandLogo(), BorderLayout.WEST);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actions.setOpaque(false);

        if (!app.isLoggedIn()) {
            JButton cartButton = UiFactory.secondaryButton("Cart");
            JButton login = UiFactory.secondaryButton("Login");
            JButton register = UiFactory.primaryButton("Register");
            cartButton.addActionListener(e -> openCart());
            login.addActionListener(e -> app.showScreen(AppFrame.LOGIN));
            register.addActionListener(e -> app.showScreen(AppFrame.REGISTER));
            actions.add(cartButton);
            actions.add(login);
            actions.add(register);
        } else if (app.isAdmin()) {
            JLabel account = new JLabel("Admin: " + app.getCurrentUser().getUsername());
            account.setFont(Theme.BODY_BOLD);
            account.setForeground(Theme.TEXT);
            JButton adminPanel = UiFactory.primaryButton("Admin Panel");
            JButton logout = UiFactory.secondaryButton("Logout");
            adminPanel.addActionListener(e -> app.showScreen(AppFrame.ADMIN));
            logout.addActionListener(e -> app.logout());
            actions.add(account);
            actions.add(adminPanel);
            actions.add(logout);
        } else {
            int cartCount = CartService.getCartItemCount(app.getCurrentUser().getId());
            JButton cartButton = UiFactory.secondaryButton("Cart (" + cartCount + ")");
            JButton wishlist = UiFactory.secondaryButton("Wishlist");
            JButton orders = UiFactory.secondaryButton("My Orders");
            JButton profile = UiFactory.secondaryButton("Profile");
            JLabel account = new JLabel("Hi, " + app.getCurrentUser().getUsername());
            JButton logout = UiFactory.secondaryButton("Logout");

            cartButton.addActionListener(e -> openCart());
            wishlist.addActionListener(e -> new WishlistFrame(app).setVisible(true));
            orders.addActionListener(e -> new OrdersFrame(app).setVisible(true));
            profile.addActionListener(e -> new ProfileFrame(app).setVisible(true));
            logout.addActionListener(e -> app.logout());
            account.setFont(Theme.BODY_BOLD);
            account.setForeground(Theme.TEXT);

            actions.add(cartButton);
            actions.add(wishlist);
            actions.add(orders);
            actions.add(profile);
            actions.add(account);
            actions.add(logout);
        }

        header.add(actions, BorderLayout.EAST);
        return header;
    }

    private JScrollPane createContent() {
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(Theme.BACKGROUND);
        content.setBorder(BorderFactory.createEmptyBorder(28, 36, 36, 36));

        JPanel hero = UiFactory.cardPanel();
        hero.setLayout(new BoxLayout(hero, BoxLayout.Y_AXIS));
        hero.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel title = new JLabel("Discover products built for everyday life", JLabel.CENTER);
        title.setFont(Theme.TITLE);
        title.setForeground(Theme.TEXT);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = UiFactory.mutedLabel("Search, compare and explore our latest collection.");
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        hero.add(Box.createVerticalStrut(8));
        hero.add(title);
        hero.add(Box.createVerticalStrut(8));
        hero.add(subtitle);
        hero.add(Box.createVerticalStrut(8));
        content.add(hero);

        JPanel filters = UiFactory.cardPanel();
        filters.setLayout(new BorderLayout(12, 10));
        filters.setAlignmentX(Component.CENTER_ALIGNMENT);
        filters.setMaximumSize(new java.awt.Dimension(Integer.MAX_VALUE, 120));

        JPanel filterControls = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        filterControls.setOpaque(false);
        searchField.setToolTipText("Search products by name or description");
        categoryBox.setFont(Theme.BODY);
        sortBox.setFont(Theme.BODY);
        JButton clear = UiFactory.secondaryButton("Clear Filters");
        clear.addActionListener(e -> {
            searchField.setText("");
            categoryBox.setSelectedIndex(0);
            sortBox.setSelectedIndex(0);
            applyFilters();
        });
        filterControls.add(new JLabel("Search:"));
        filterControls.add(searchField);
        filterControls.add(new JLabel("Category:"));
        filterControls.add(categoryBox);
        filterControls.add(new JLabel("Sort:"));
        filterControls.add(sortBox);
        filterControls.add(clear);
        filters.add(filterControls, BorderLayout.CENTER);
        filters.add(resultLabel, BorderLayout.SOUTH);

        filters.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0), filters.getBorder()));
        content.add(filters);

        JLabel sectionTitle = new JLabel("Products", JLabel.CENTER);
        sectionTitle.setFont(Theme.HEADING);
        sectionTitle.setForeground(Theme.TEXT);
        sectionTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        sectionTitle.setBorder(BorderFactory.createEmptyBorder(28, 0, 14, 0));
        content.add(sectionTitle);

        productGrid.setBackground(Theme.BACKGROUND);
        productGrid.setAlignmentX(Component.CENTER_ALIGNMENT);
        content.add(productGrid);

        JScrollPane scrollPane = new JScrollPane(content);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        return scrollPane;
    }

    private void bindFilters() {
        searchField.addActionListener(e -> applyFilters());
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { applyFilters(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { applyFilters(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { applyFilters(); }
        });
        categoryBox.addActionListener(e -> applyFilters());
        sortBox.addActionListener(e -> applyFilters());
    }

    public void refreshView() {
        headerHolder.removeAll();
        headerHolder.add(createHeader(), BorderLayout.CENTER);
        loadProducts();
        headerHolder.revalidate();
        headerHolder.repaint();
        revalidate();
        repaint();
    }

    private void loadProducts() {
        allProducts = ProductService.getAllProducts();
        refreshCategories();
        applyFilters();
    }

    private void refreshCategories() {
        Object selected = categoryBox.getSelectedItem();
        Set<String> categories = new LinkedHashSet<>();
        for (Product product : allProducts) {
            if (product.getCategory() != null && !product.getCategory().isBlank()) {
                categories.add(product.getCategory());
            }
        }
        categoryBox.removeAllItems();
        categoryBox.addItem("All Categories");
        categories.stream().sorted(String.CASE_INSENSITIVE_ORDER).forEach(categoryBox::addItem);
        if (selected != null) categoryBox.setSelectedItem(selected);
        if (categoryBox.getSelectedIndex() < 0) categoryBox.setSelectedIndex(0);
    }

    private void applyFilters() {
        if (productGrid == null) return;
        String query = searchField.getText().trim().toLowerCase(Locale.ROOT);
        String category = categoryBox.getSelectedItem() == null ? "All Categories" : categoryBox.getSelectedItem().toString();

        List<Product> filtered = new ArrayList<>();
        for (Product product : allProducts) {
            String name = product.getName() == null ? "" : product.getName().toLowerCase(Locale.ROOT);
            String description = product.getDescription() == null ? "" : product.getDescription().toLowerCase(Locale.ROOT);
            boolean matchesText = query.isEmpty() || name.contains(query) || description.contains(query);
            boolean matchesCategory = "All Categories".equals(category) || category.equalsIgnoreCase(product.getCategory());
            if (matchesText && matchesCategory) filtered.add(product);
        }

        String sort = sortBox.getSelectedItem() == null ? "Featured" : sortBox.getSelectedItem().toString();
        if ("Price: Low to High".equals(sort)) {
            filtered.sort(Comparator.comparingDouble(Product::getPrice));
        } else if ("Price: High to Low".equals(sort)) {
            filtered.sort(Comparator.comparingDouble(Product::getPrice).reversed());
        } else if ("Name: A to Z".equals(sort)) {
            filtered.sort(Comparator.comparing(Product::getName, String.CASE_INSENSITIVE_ORDER));
        }

        renderProducts(filtered);
    }

    private void renderProducts(List<Product> products) {
        productGrid.removeAll();
        for (Product product : products) {
            productGrid.add(createProductCard(product));
        }
        if (products.isEmpty()) {
            JPanel empty = UiFactory.cardPanel();
            empty.add(UiFactory.mutedLabel("No products match your search."));
            productGrid.add(empty);
        }
        resultLabel.setText(products.size() + (products.size() == 1 ? " product found" : " products found"));
        productGrid.revalidate();
        productGrid.repaint();
    }

    private JPanel createProductCard(Product product) {
        JPanel card = UiFactory.cardPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        JLabel imagePlaceholder = new JLabel("No product image", JLabel.CENTER);
        imagePlaceholder.setAlignmentX(Component.CENTER_ALIGNMENT);
        imagePlaceholder.setOpaque(true);
        imagePlaceholder.setBackground(Theme.SURFACE_SOFT);
        imagePlaceholder.setForeground(Theme.TEXT_MUTED);
        imagePlaceholder.setPreferredSize(new java.awt.Dimension(230, 155));
        imagePlaceholder.setMaximumSize(new java.awt.Dimension(Integer.MAX_VALUE, 155));
        imagePlaceholder.setBorder(BorderFactory.createLineBorder(Theme.BORDER));
        javax.swing.ImageIcon productIcon = ImageUtil.scaledIcon(product.getImagePath(), 228, 153);
        if (productIcon != null) {
            imagePlaceholder.setIcon(productIcon);
            imagePlaceholder.setText("");
        }

        JLabel name = new JLabel(product.getName());
        name.setFont(Theme.BODY_BOLD);
        name.setForeground(Theme.TEXT);
        name.setBorder(BorderFactory.createEmptyBorder(12, 0, 4, 0));

        JLabel category = UiFactory.mutedLabel(product.getCategory());
        JLabel stock = new JLabel(product.getStockQuantity() > 0 ? product.getStockQuantity() + " in stock" : "Out of stock");
        stock.setFont(Theme.SMALL);
        stock.setForeground(product.getStockQuantity() > 0 ? Theme.SUCCESS : Theme.DANGER);

        JLabel price = new JLabel(String.format("৳%,.2f", product.getPrice()));
        price.setFont(Theme.HEADING);
        price.setForeground(Theme.TEXT);
        price.setBorder(BorderFactory.createEmptyBorder(8, 0, 12, 0));

        JButton view = UiFactory.secondaryButton("View Details");
        view.addActionListener(e -> showProductDetails(product));

        card.add(imagePlaceholder);
        card.add(name);
        card.add(category);
        card.add(stock);
        card.add(price);
        card.add(view);

        if (!app.isAdmin()) {
            JButton wishlist = UiFactory.secondaryButton("Save to Wishlist");
            wishlist.addActionListener(e -> saveToWishlist(product, wishlist));
            card.add(Box.createVerticalStrut(6));
            card.add(wishlist);

            JButton cart = UiFactory.primaryButton("Add to Cart");
            cart.setEnabled(product.getStockQuantity() > 0);
            cart.setToolTipText(product.getStockQuantity() > 0 ? "Add one item to cart" : "This product is out of stock");
            cart.addActionListener(e -> addProductToCart(product, cart));
            card.add(Box.createVerticalStrut(6));
            card.add(cart);
        }
        return card;
    }

    private void showProductDetails(Product product) {
        new ProductDetailsDialog(app, app, product).setVisible(true);
    }

    private void saveToWishlist(Product product, JButton button) {
        if (!app.isLoggedIn()) {
            JOptionPane.showMessageDialog(this, "Please log in before saving products.", "Login required", JOptionPane.INFORMATION_MESSAGE);
            app.showScreen(AppFrame.LOGIN);
            return;
        }
        if (app.isAdmin()) return;
        if (service.WishlistService.contains(app.getCurrentUser().getId(), product.getId())) {
            JOptionPane.showMessageDialog(this, "This product is already in your wishlist.", "Wishlist", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        if (service.WishlistService.add(app.getCurrentUser().getId(), product.getId())) {
            button.setText("Saved");
            button.setEnabled(false);
        }
    }

    private void addProductToCart(Product product, JButton button) {
        if (!app.isLoggedIn()) {
            JOptionPane.showMessageDialog(this, "Please log in before adding products to your cart.", "Login required", JOptionPane.INFORMATION_MESSAGE);
            app.showScreen(AppFrame.LOGIN);
            return;
        }
        if (app.isAdmin()) return;

        button.setEnabled(false);
        button.setText("Adding...");
        new SwingWorker<Boolean, Void>() {
            @Override protected Boolean doInBackground() throws Exception {
                return CartService.addToCart(app.getCurrentUser().getId(), product.getId(), 1);
            }

            @Override protected void done() {
                button.setText("Add to Cart");
                button.setEnabled(product.getStockQuantity() > 0);
                try {
                    if (get()) {
                        headerHolder.removeAll();
                        headerHolder.add(createHeader(), BorderLayout.CENTER);
                        headerHolder.revalidate();
                        headerHolder.repaint();
                        JOptionPane.showMessageDialog(StorefrontPanel.this, product.getName() + " was added to your cart.", "Cart updated", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(StorefrontPanel.this, "The item could not be saved to the cart.", "Cart error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception exception) {
                    Throwable cause = exception.getCause();
                    String message = cause instanceof InvalidQuantityException ? cause.getMessage() : "Unable to add this item to the cart.";
                    JOptionPane.showMessageDialog(StorefrontPanel.this, message, "Unable to add product", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private void openCart() {
        if (!app.isLoggedIn()) {
            JOptionPane.showMessageDialog(this, "Please log in to view your cart.", "Login required", JOptionPane.INFORMATION_MESSAGE);
            app.showScreen(AppFrame.LOGIN);
            return;
        }
        if (app.isAdmin()) return;
        new CartFrame(app).setVisible(true);
    }
}
