package ui.frames;

import exception.InvalidQuantityException;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingWorker;
import model.Product;
import service.CartService;
import service.WishlistService;
import ui.AppFrame;
import ui.Theme;
import ui.components.UiFactory;
import util.ImageUtil;


public class ProductDetailsDialog extends JDialog {
    private final AppFrame app;
    private final Product product;
    private final JSpinner quantity;

    public ProductDetailsDialog(Component owner, AppFrame app, Product product) {
        super(owner instanceof JFrame ? (JFrame) owner : app, "Product Details", true);
        this.app = app;
        this.product = product;
        int maximum = Math.max(1, product.getStockQuantity());
        quantity = new JSpinner(new SpinnerNumberModel(1, 1, maximum, 1));
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(760, 540);
        setMinimumSize(new Dimension(680, 500));
        setLocationRelativeTo(owner);
        buildUi();
    }

    private void buildUi() {
        JPanel root = new JPanel(new BorderLayout(20, 16));
        root.setBackground(Theme.BACKGROUND);
        root.setBorder(BorderFactory.createEmptyBorder(22, 22, 18, 22));

        JLabel image = new JLabel("No product image", JLabel.CENTER);
        image.setOpaque(true);
        image.setBackground(Theme.SURFACE_SOFT);
        image.setForeground(Theme.TEXT_MUTED);
        image.setPreferredSize(new Dimension(280, 350));
        javax.swing.ImageIcon icon = ImageUtil.scaledIcon(product.getImagePath(), 278, 348);
        if (icon != null) {
            image.setIcon(icon);
            image.setText("");
        }
        image.setBorder(BorderFactory.createLineBorder(Theme.BORDER));
        root.add(image, BorderLayout.WEST);

        JPanel right = new JPanel(new BorderLayout(0, 14));
        right.setOpaque(false);

        JPanel info = UiFactory.cardPanel();
        info.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.WEST;
        c.insets = new Insets(0, 0, 8, 0);

        JLabel name = new JLabel(product.getName());
        name.setFont(Theme.TITLE);
        name.setForeground(Theme.TEXT);
        info.add(name, c);

        c.gridy++;
        info.add(UiFactory.mutedLabel(product.getCategory()), c);

        c.gridy++;
        JLabel price = new JLabel(String.format("৳%,.2f", product.getPrice()));
        price.setFont(Theme.HEADING);
        price.setForeground(Theme.TEXT);
        info.add(price, c);

        c.gridy++;
        JLabel stock = new JLabel(product.getStockQuantity() > 0 ? product.getStockQuantity() + " available" : "Out of stock");
        stock.setFont(Theme.BODY_BOLD);
        stock.setForeground(product.getStockQuantity() > 0 ? Theme.SUCCESS : Theme.DANGER);
        info.add(stock, c);

        c.gridy++;
        c.weighty = 1;
        c.fill = GridBagConstraints.BOTH;
        JTextArea description = new JTextArea(product.getDescription());
        description.setEditable(false);
        description.setLineWrap(true);
        description.setWrapStyleWord(true);
        description.setFont(Theme.BODY);
        description.setForeground(Theme.TEXT);
        description.setBackground(Theme.SURFACE);
        description.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));
        JScrollPane descriptionScroll = new JScrollPane(description);
        descriptionScroll.setBorder(null);
        descriptionScroll.setPreferredSize(new Dimension(320, 150));
        info.add(descriptionScroll, c);
        right.add(info, BorderLayout.CENTER);

        JPanel controls = UiFactory.cardPanel();
        controls.setLayout(new BorderLayout(12, 10));

        JPanel qtyPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        qtyPanel.setOpaque(false);
        JLabel qtyLabel = new JLabel("Quantity");
        qtyLabel.setFont(Theme.BODY_BOLD);
        qtyPanel.add(qtyLabel);
        quantity.setPreferredSize(new Dimension(90, 38));
        quantity.setEnabled(product.getStockQuantity() > 0);
        qtyPanel.add(quantity);
        controls.add(qtyPanel, BorderLayout.NORTH);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setOpaque(false);
        JButton close = UiFactory.secondaryButton("Close");
        close.addActionListener(e -> dispose());
        actions.add(close);

        if (!app.isAdmin()) {
            JButton wishlist = UiFactory.secondaryButton("Save to Wishlist");
            wishlist.addActionListener(e -> saveWishlist(wishlist));
            JButton cart = UiFactory.primaryButton("Add to Cart");
            cart.setEnabled(product.getStockQuantity() > 0);
            cart.addActionListener(e -> addToCart(cart));
            actions.add(wishlist);
            actions.add(cart);
        }
        controls.add(actions, BorderLayout.SOUTH);
        right.add(controls, BorderLayout.SOUTH);
        root.add(right, BorderLayout.CENTER);

        setContentPane(root);
        getRootPane().registerKeyboardAction(e -> dispose(), javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE, 0), javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW);
    }

    private boolean requireCustomerLogin() {
        if (!app.isLoggedIn()) {
            JOptionPane.showMessageDialog(this, "Please log in to continue.", "Login required", JOptionPane.INFORMATION_MESSAGE);
            dispose();
            app.showScreen(AppFrame.LOGIN);
            return false;
        }
        return !app.isAdmin();
    }

    private void saveWishlist(JButton button) {
        if (!requireCustomerLogin()) return;
        if (WishlistService.contains(app.getCurrentUser().getId(), product.getId())) {
            JOptionPane.showMessageDialog(this, "This product is already in your wishlist.", "Wishlist", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        if (WishlistService.add(app.getCurrentUser().getId(), product.getId())) {
            button.setText("Saved");
            button.setEnabled(false);
        }
    }

    private void addToCart(JButton button) {
        if (!requireCustomerLogin()) return;
        int requested = (Integer) quantity.getValue();
        button.setEnabled(false);
        button.setText("Adding...");
        new SwingWorker<Boolean, Void>() {
            @Override protected Boolean doInBackground() throws Exception {
                return CartService.addToCart(app.getCurrentUser().getId(), product.getId(), requested);
            }

            @Override protected void done() {
                button.setText("Add to Cart");
                button.setEnabled(product.getStockQuantity() > 0);
                try {
                    if (get()) {
                        JOptionPane.showMessageDialog(ProductDetailsDialog.this, requested + " item(s) added to your cart.", "Cart updated", JOptionPane.INFORMATION_MESSAGE);
                    }
                } catch (Exception e) {
                    Throwable cause = e.getCause();
                    String message = cause instanceof InvalidQuantityException ? cause.getMessage() : "Unable to add this product to the cart.";
                    JOptionPane.showMessageDialog(ProductDetailsDialog.this, message, "Cart error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }
}
