package ui.frames;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import model.Order;
import service.OrderService;
import ui.AppFrame;
import ui.Theme;


public class OrdersFrame extends JFrame {
    private final DefaultTableModel model = new DefaultTableModel(new Object[]{"Order ID", "Date", "Status", "Total"}, 0) {
        @Override public boolean isCellEditable(int row, int column) { 
            return false; 
        }
    };

    public OrdersFrame(AppFrame app) {
        super("OmniCommerce - My Orders");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(780, 500);
        setMinimumSize(new Dimension(680, 420));
        setLocationRelativeTo(app);

        JLabel title = new JLabel("My Orders");
        title.setFont(Theme.TITLE);
        title.setForeground(Theme.TEXT);

        JTable table = new JTable(model);
        table.setRowHeight(34);
        table.setFont(Theme.BODY);
        table.getTableHeader().setFont(Theme.BODY_BOLD);

        javax.swing.JPanel root = new javax.swing.JPanel(new BorderLayout(16, 16));
        root.setBackground(Theme.BACKGROUND);
        root.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
        root.add(title, BorderLayout.NORTH);
        root.add(new JScrollPane(table), BorderLayout.CENTER);
        List<Order> orders = OrderService.getOrdersByCustomer(app.getCurrentUser().getId());
        JButton details = ui.components.UiFactory.primaryButton("View Selected Order");
        details.addActionListener(e -> openSelected(table, orders));
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) openSelected(table, orders);
            }
        });
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.setOpaque(false); actions.add(details);
        root.add(actions, BorderLayout.SOUTH);
        setContentPane(root);

        for (Order order : orders) {
            model.addRow(new Object[]{
                    order.getOrderId(), order.getOrderDate(), order.getStatus(),
                    String.format("৳%,.2f", order.getTotalAmount())
            });
        }
    }


    private void openSelected(JTable table, List<Order> orders) {
        int row = table.getSelectedRow();
        if (row < 0) {
            javax.swing.JOptionPane.showMessageDialog(this, "Select an order first.", "No selection", javax.swing.JOptionPane.WARNING_MESSAGE);
            return;
        }
        new OrderDetailsFrame(this, orders.get(row)).setVisible(true);
    }
}
