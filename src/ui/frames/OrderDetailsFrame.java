package ui.frames;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import model.Order;
import model.OrderItem;
import service.OrderService;
import ui.Theme;
import ui.components.UiFactory;


public class OrderDetailsFrame extends JFrame {
    public OrderDetailsFrame(JFrame owner, Order order) {
        super("Order #" + order.getOrderId());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(760, 520);
        setMinimumSize(new Dimension(650, 430));
        setLocationRelativeTo(owner);

        JPanel root = new JPanel(new BorderLayout(16, 16));
        root.setBackground(Theme.BACKGROUND);
        root.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
        JLabel title = new JLabel("Order #" + order.getOrderId());
        title.setFont(Theme.TITLE); title.setForeground(Theme.TEXT);
        root.add(title, BorderLayout.NORTH);

        JPanel info = UiFactory.cardPanel();
        info.setLayout(new GridLayout(2, 2, 12, 8));
        info.add(new JLabel("Date: " + order.getOrderDate()));
        info.add(new JLabel("Status: " + order.getStatus()));
        info.add(new JLabel("Customer ID: " + order.getCustomerId()));
        info.add(new JLabel(String.format("Total: ৳%,.2f", order.getTotalAmount())));

        DefaultTableModel m = new DefaultTableModel(new Object[]{"Product", "Quantity", "Unit Price", "Subtotal"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        List<OrderItem> items = OrderService.getOrderItems(order.getOrderId());
        for (OrderItem item : items) m.addRow(new Object[]{item.getProduct().getName(), item.getQuantity(), String.format("৳%,.2f", item.getPrice()), String.format("৳%,.2f", item.getTotal())});
        JTable table = new JTable(m); table.setRowHeight(34); table.setFont(Theme.BODY);
        table.getTableHeader().setFont(Theme.BODY_BOLD);

        JPanel center = new JPanel(new BorderLayout(0, 14)); center.setOpaque(false);
        center.add(info, BorderLayout.NORTH); center.add(new JScrollPane(table), BorderLayout.CENTER);
        root.add(center, BorderLayout.CENTER);
        setContentPane(root);
    }
}
