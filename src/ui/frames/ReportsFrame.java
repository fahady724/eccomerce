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
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import service.ReportService;
import ui.AppFrame;
import ui.Theme;
import ui.components.UiFactory;


public class ReportsFrame extends JFrame {
    public ReportsFrame(AppFrame app) {
        super("OmniCommerce - Reports & Analytics");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(940, 650);
        setMinimumSize(new Dimension(780, 520));
        setLocationRelativeTo(app);

        JPanel root = new JPanel(new BorderLayout(16, 16));
        root.setBackground(Theme.BACKGROUND);
        root.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
        JLabel title = new JLabel("Reports & Analytics");
        title.setFont(Theme.TITLE);
        title.setForeground(Theme.TEXT);
        root.add(title, BorderLayout.NORTH);

        JPanel stats = new JPanel(new GridLayout(1, 3, 14, 14));
        stats.setOpaque(false);
        stats.add(stat("Total Revenue", String.format("৳%,.2f", ReportService.getTotalSales())));
        stats.add(stat("Total Orders", String.valueOf(ReportService.getTotalOrders())));
        stats.add(stat("Customers", String.valueOf(ReportService.getTotalCustomers())));

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(Theme.BODY_BOLD);
        tabs.addTab("Monthly Sales", table(ReportService.getSalesByMonth(), new String[]{"Month", "Orders", "Revenue"}, 2));
        tabs.addTab("Category Performance", table(ReportService.getSalesByCategory(), new String[]{"Category", "Units Sold", "Revenue"}, 2));
        tabs.addTab("Order Status", table(ReportService.getOrderStatusSummary(), new String[]{"Status", "Orders"}, -1));
        List<model.Product> low = ReportService.getLowStockProducts();
        DefaultTableModel lowModel = readonly(new String[]{"ID", "Product", "Category", "Stock"});
        for (model.Product p : low) lowModel.addRow(new Object[]{p.getId(), p.getName(), p.getCategory(), p.getStockQuantity()});
        tabs.addTab("Low Stock", scroll(new JTable(lowModel)));

        JPanel center = new JPanel(new BorderLayout(0, 16));
        center.setOpaque(false);
        center.add(stats, BorderLayout.NORTH);
        center.add(tabs, BorderLayout.CENTER);
        root.add(center, BorderLayout.CENTER);
        setContentPane(root);
    }

    private JPanel stat(String label, String value) {
        JPanel p = UiFactory.cardPanel();
        p.setLayout(new BorderLayout(0, 8));
        p.add(UiFactory.mutedLabel(label), BorderLayout.NORTH);
        JLabel v = new JLabel(value); v.setFont(Theme.HEADING); v.setForeground(Theme.TEXT);
        p.add(v, BorderLayout.CENTER); return p;
    }

    private JScrollPane table(List<Object[]> rows, String[] columns, int moneyColumn) {
        DefaultTableModel m = readonly(columns);
        for (Object[] row : rows) {
            Object[] copy = row.clone();
            if (moneyColumn >= 0 && copy[moneyColumn] instanceof Number)
                copy[moneyColumn] = String.format("৳%,.2f", ((Number) copy[moneyColumn]).doubleValue());
            m.addRow(copy);
        }
        return scroll(new JTable(m));
    }

    private JScrollPane scroll(JTable t) {
        t.setRowHeight(32); t.setFont(Theme.BODY); t.getTableHeader().setFont(Theme.BODY_BOLD);
        return new JScrollPane(t);
    }

    private DefaultTableModel readonly(String[] columns) {
        return new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { 
                return false; 
            }
        };
    }
}
