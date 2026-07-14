package ui.panels;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import model.Order;
import model.Product;
import service.OrderService;
import service.ProductService;
import service.ReportService;
import ui.AppFrame;
import ui.Theme;
import ui.components.UiFactory;
import ui.frames.CustomerManagementFrame;
import ui.frames.ProductFormDialog;
import ui.frames.ReportsFrame;


public class AdminDashboardPanel extends JPanel {
    private final AppFrame app;
    private final DefaultTableModel tableModel = new DefaultTableModel(new Object[]{"ID", "Name", "Category", "Price", "Stock"}, 0) {
        @Override public boolean isCellEditable(int row, int column) { 
            return false; 
        }
    };
    private final JTable productTable = new JTable(tableModel);
    private final JPanel summaryPanel = new JPanel(new GridLayout(1, 4, 14, 14));
    private List<Product> products;

    public AdminDashboardPanel(AppFrame app) {
        this.app = app;
        setLayout(new BorderLayout(16, 16));
        setBackground(Theme.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
        add(createHeader(), BorderLayout.NORTH);
        add(createCenter(), BorderLayout.CENTER);
        refreshData();
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel title = new JLabel("Admin Dashboard");
        title.setFont(Theme.TITLE);
        title.setForeground(Theme.TEXT);
        header.add(title, BorderLayout.WEST);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actions.setOpaque(false);
        JButton store = UiFactory.secondaryButton("Back to Store");
        JButton logout = UiFactory.dangerButton("Logout");
        store.addActionListener(e -> app.showScreen(AppFrame.STORE));
        logout.addActionListener(e -> app.logout());
        actions.add(store);
        actions.add(logout);
        header.add(actions, BorderLayout.EAST);
        return header;
    }

    private JPanel createCenter() {
        JPanel center = new JPanel(new BorderLayout(0, 16));
        center.setOpaque(false);
        summaryPanel.setOpaque(false);
        center.add(summaryPanel, BorderLayout.NORTH);

        JPanel management = UiFactory.cardPanel();
        management.setLayout(new BorderLayout(12, 12));
        JLabel heading = new JLabel("Product Management");
        heading.setFont(Theme.HEADING);
        management.add(heading, BorderLayout.NORTH);

        productTable.setRowHeight(32);
        productTable.setFont(Theme.BODY);
        productTable.getTableHeader().setFont(Theme.BODY_BOLD);
        productTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        management.add(new JScrollPane(productTable), BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttons.setOpaque(false);
        JButton refresh = UiFactory.secondaryButton("Refresh");
        JButton manageCustomers = UiFactory.secondaryButton("Manage Customers");
        manageCustomers.addActionListener(e -> new CustomerManagementFrame(app).setVisible(true));
        JButton reports = UiFactory.primaryButton("Reports & Analytics");
        reports.addActionListener(e -> new ReportsFrame(app).setVisible(true));
        JButton manageOrders = UiFactory.secondaryButton("Manage Orders");
        manageOrders.addActionListener(e -> showOrderManager());
        JButton add = UiFactory.primaryButton("Add Product");
        JButton edit = UiFactory.secondaryButton("Edit Selected");
        JButton delete = UiFactory.dangerButton("Delete Selected");
        refresh.addActionListener(e -> refreshData());
        add.addActionListener(e -> showProductForm(null));
        edit.addActionListener(e -> editSelected());
        delete.addActionListener(e -> deleteSelected());
        buttons.add(refresh);
        buttons.add(reports);
        buttons.add(manageCustomers);
        buttons.add(manageOrders);
        buttons.add(add);
        buttons.add(edit);
        buttons.add(delete);
        management.add(buttons, BorderLayout.SOUTH);
        center.add(management, BorderLayout.CENTER);
        return center;
    }

    public void refreshData() {
        products = ProductService.getAllProducts();
        tableModel.setRowCount(0);
        for (Product p : products) {
            tableModel.addRow(new Object[]{p.getId(), p.getName(), p.getCategory(), String.format("৳%,.2f", p.getPrice()), p.getStockQuantity()});
        }
        summaryPanel.removeAll();
        summaryPanel.add(statCard("Products", String.valueOf(products.size())));
        summaryPanel.add(statCard("Customers", String.valueOf(ReportService.getTotalCustomers())));
        summaryPanel.add(statCard("Orders", String.valueOf(ReportService.getTotalOrders())));
        summaryPanel.add(statCard("Revenue", String.format("৳%,.2f", ReportService.getTotalSales())));
        summaryPanel.revalidate();
        summaryPanel.repaint();
    }

    private JPanel statCard(String label, String value) {
        JPanel card = UiFactory.cardPanel();
        card.setLayout(new BorderLayout(0, 8));
        card.add(UiFactory.mutedLabel(label), BorderLayout.NORTH);
        JLabel number = new JLabel(value);
        number.setFont(Theme.HEADING);
        number.setForeground(Theme.TEXT);
        card.add(number, BorderLayout.CENTER);
        return card;
    }

    private void editSelected() {
        int row = productTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a product first.", "No selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        showProductForm(products.get(row));
    }

    private void deleteSelected() {
        int row = productTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a product first.", "No selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Product selected = products.get(row);
        int choice = JOptionPane.showConfirmDialog(this, "Delete " + selected.getName() + "?", "Confirm deletion", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (choice == JOptionPane.YES_OPTION) {
            ProductService.deleteProduct(selected.getId());
            refreshData();
        }
    }

    private void showProductForm(Product product) {
        java.awt.Frame owner = (java.awt.Frame) javax.swing.SwingUtilities.getWindowAncestor(this);
        new ProductFormDialog(owner, product, this::refreshData).setVisible(true);
    }

    private void showOrderManager() {
        java.util.List<Order> orders = OrderService.getAllOrders();
        DefaultTableModel orderModel = new DefaultTableModel(new Object[]{"Order ID", "Customer ID", "Date", "Status", "Total"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { 
                return false; 
            }
        };
        for (Order order : orders) {
            orderModel.addRow(new Object[]{order.getOrderId(), order.getCustomerId(), order.getOrderDate(), order.getStatus(), String.format("৳%,.2f", order.getTotalAmount())});
        }
        JTable orderTable = new JTable(orderModel);
        orderTable.setRowHeight(32);
        JComboBox<String> statuses = new JComboBox<>(new String[]{"PENDING", "PROCESSING", "SHIPPED", "DELIVERED", "CANCELLED"});
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.add(new JScrollPane(orderTable), BorderLayout.CENTER);
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        controls.add(new JLabel("New status:"));
        controls.add(statuses);
        panel.add(controls, BorderLayout.SOUTH);
        int result = JOptionPane.showConfirmDialog(this, panel, "Order Management", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION && orderTable.getSelectedRow() >= 0) {
            int orderId = (int) orderModel.getValueAt(orderTable.getSelectedRow(), 0);
            OrderService.updateOrderStatus(orderId, statuses.getSelectedItem().toString());
            JOptionPane.showMessageDialog(this, "Order status updated.");
            refreshData();
        }
    }
}
