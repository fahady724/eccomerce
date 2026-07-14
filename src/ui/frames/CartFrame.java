package ui.frames;

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
import javax.swing.table.DefaultTableModel;
import model.OrderItem;
import service.CartService;
import ui.AppFrame;
import ui.Theme;
import ui.components.UiFactory;


public class CartFrame extends JFrame {
    private final AppFrame app;
    private final DefaultTableModel model = new DefaultTableModel(new Object[]{"Product", "Price", "Quantity", "Subtotal"}, 0) {
        @Override public boolean isCellEditable(int row, int column) { 
            return false; 
        }
    };
    private final JTable table = new JTable(model);
    private final JLabel totalLabel = new JLabel();
    private List<OrderItem> currentItems;

    public CartFrame(AppFrame app) {
        super("OmniCommerce - Shopping Cart");
        this.app = app;
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(760, 520);
        setMinimumSize(new Dimension(680, 460));
        setLocationRelativeTo(app);
        buildUi();
        refreshCart();
    }

    private void buildUi() {
        JPanel root = new JPanel(new BorderLayout(16, 16));
        root.setBackground(Theme.BACKGROUND);
        root.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        JLabel title = new JLabel("Shopping Cart");
        title.setFont(Theme.TITLE);
        title.setForeground(Theme.TEXT);
        root.add(title, BorderLayout.NORTH);

        table.setRowHeight(34);
        table.setFont(Theme.BODY);
        table.getTableHeader().setFont(Theme.BODY_BOLD);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        root.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setOpaque(false);
        totalLabel.setFont(Theme.HEADING);
        totalLabel.setForeground(Theme.TEXT);
        bottom.add(totalLabel, BorderLayout.WEST);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actions.setOpaque(false);
        JButton remove = UiFactory.dangerButton("Remove Selected");
        JButton clear = UiFactory.secondaryButton("Clear Cart");
        JButton checkout = UiFactory.primaryButton("Checkout");
        remove.addActionListener(e -> removeSelected());
        clear.addActionListener(e -> clearCart());
        checkout.addActionListener(e -> openCheckout());
        actions.add(remove);
        actions.add(clear);
        actions.add(checkout);
        bottom.add(actions, BorderLayout.EAST);
        root.add(bottom, BorderLayout.SOUTH);
        setContentPane(root);
    }

    private void refreshCart() {
        model.setRowCount(0);
        currentItems = CartService.getCart(app.getCurrentUser().getId());
        for (OrderItem item : currentItems) {
            model.addRow(new Object[]{
                item.getProduct().getName(),
                String.format("৳%,.2f", item.getPrice()),
                item.getQuantity(),
                String.format("৳%,.2f", item.getTotal())
            });
        }
        totalLabel.setText(String.format("Total: ৳%,.2f", CartService.getCartTotal(app.getCurrentUser().getId())));
    }

    private void removeSelected() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a product first.", "No selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        CartService.removeFromCart(app.getCurrentUser().getId(), currentItems.get(row).getProduct().getId());
        refreshCart();
    }

    private void clearCart() {
        if (currentItems.isEmpty()) return;
        int choice = JOptionPane.showConfirmDialog(this, "Remove every item from the cart?", "Clear cart", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (choice == JOptionPane.YES_OPTION) {
            CartService.clearCart(app.getCurrentUser().getId());
            refreshCart();
        }
    }

    private void openCheckout() {
        if (currentItems.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Your cart is empty.", "Checkout", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        new CheckoutFrame(app, currentItems).setVisible(true);
        dispose();
    }
}
