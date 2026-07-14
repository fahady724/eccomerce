package ui.frames;

import exception.InvalidQuantityException;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;
import model.Product;
import service.CartService;
import service.WishlistService;
import ui.AppFrame;
import ui.Theme;
import ui.components.UiFactory;


public class WishlistFrame extends JFrame {
    private final AppFrame app;
    private final DefaultTableModel model = new DefaultTableModel(new Object[]{"Product", "Category", "Price", "Stock"}, 0) {
        @Override public boolean isCellEditable(int row, int column) { 
            return false; 
        }
    };
    private final JTable table = new JTable(model);
    private final JLabel countLabel = new JLabel();
    private List<Product> products;

    public WishlistFrame(AppFrame app) {
        super("OmniCommerce - Wishlist");
        this.app = app;
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(820, 540);
        setMinimumSize(new Dimension(700, 460));
        setLocationRelativeTo(app);
        buildUi();
        refreshData();
    }

    private void buildUi() {
        JPanel root = new JPanel(new BorderLayout(16, 16));
        root.setBackground(Theme.BACKGROUND);
        root.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        JPanel heading = new JPanel(new BorderLayout());
        heading.setOpaque(false);
        JLabel title = new JLabel("My Wishlist");
        title.setFont(Theme.TITLE);
        title.setForeground(Theme.TEXT);
        countLabel.setFont(Theme.BODY);
        countLabel.setForeground(Theme.TEXT_MUTED);
        heading.add(title, BorderLayout.WEST);
        heading.add(countLabel, BorderLayout.EAST);
        root.add(heading, BorderLayout.NORTH);

        table.setRowHeight(36);
        table.setFont(Theme.BODY);
        table.getTableHeader().setFont(Theme.BODY_BOLD);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) showSelectedDetails();
            }
        });
        root.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actions.setOpaque(false);
        JButton details = UiFactory.secondaryButton("View Details");
        JButton remove = UiFactory.dangerButton("Remove");
        JButton cart = UiFactory.primaryButton("Move to Cart");
        details.addActionListener(e -> showSelectedDetails());
        remove.addActionListener(e -> removeSelected());
        cart.addActionListener(e -> moveSelectedToCart(cart));
        actions.add(details);
        actions.add(remove);
        actions.add(cart);
        root.add(actions, BorderLayout.SOUTH);
        setContentPane(root);
    }

    private void refreshData() {
        products = WishlistService.getAll(app.getCurrentUser().getId());
        model.setRowCount(0);
        for (Product product : products) {
            model.addRow(new Object[]{product.getName(), product.getCategory(), String.format("৳%,.2f", product.getPrice()), product.getStockQuantity() > 0 ? product.getStockQuantity() : "Out of stock"});
        }
        countLabel.setText(products.size() + (products.size() == 1 ? " saved product" : " saved products"));
    }

    private Product selectedProduct() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a product first.", "No selection", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        return products.get(table.convertRowIndexToModel(row));
    }

    private void showSelectedDetails() {
        Product product = selectedProduct();
        if (product != null) new ProductDetailsDialog(this, app, product).setVisible(true);
    }

    private void removeSelected() {
        Product product = selectedProduct();
        if (product == null) return;
        WishlistService.remove(app.getCurrentUser().getId(), product.getId());
        refreshData();
    }

    private void moveSelectedToCart(JButton button) {
        Product product = selectedProduct();
        if (product == null) return;
        if (product.getStockQuantity() <= 0) {
            JOptionPane.showMessageDialog(this, "This product is currently out of stock.", "Unavailable", JOptionPane.WARNING_MESSAGE);
            return;
        }
        button.setEnabled(false);
        button.setText("Moving...");
        new SwingWorker<Boolean, Void>() {
            @Override protected Boolean doInBackground() throws Exception {
                return CartService.addToCart(app.getCurrentUser().getId(), product.getId(), 1);
            }
            @Override protected void done() {
                button.setEnabled(true);
                button.setText("Move to Cart");
                try {
                    if (get()) {
                        WishlistService.remove(app.getCurrentUser().getId(), product.getId());
                        refreshData();
                        JOptionPane.showMessageDialog(WishlistFrame.this, "Product moved to your cart.", "Cart updated", JOptionPane.INFORMATION_MESSAGE);
                    }
                } catch (Exception e) {
                    Throwable cause = e.getCause();
                    String message = cause instanceof InvalidQuantityException ? cause.getMessage() : "Unable to move this product to the cart.";
                    JOptionPane.showMessageDialog(WishlistFrame.this, message, "Cart error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }
}
