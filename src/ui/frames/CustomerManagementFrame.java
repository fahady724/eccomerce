package ui.frames;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import model.Customer;
import service.UserService;
import ui.Theme;
import ui.components.UiFactory;


public class CustomerManagementFrame extends JFrame {
    private final DefaultTableModel model = new DefaultTableModel(new Object[]{"ID", "Username", "Email", "Phone", "Address"}, 0) {
        @Override public boolean isCellEditable(int row, int column) { return false; }
    };
    private final JTable table = new JTable(model);
    private final JTextField search = UiFactory.textField(20);
    private List<Customer> customers = new ArrayList<>();

    public CustomerManagementFrame(JFrame owner) {
        super("OmniCommerce - Customer Management");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(900, 560);
        setMinimumSize(new Dimension(760, 500));
        setLocationRelativeTo(owner);
        buildUi();
        refreshData();
    }

    private void buildUi() {
        JPanel root = new JPanel(new BorderLayout(0, 18));
        root.setBackground(Theme.BACKGROUND);
        root.setBorder(BorderFactory.createEmptyBorder(22, 24, 22, 24));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel title = new JLabel("Customer Management");
        title.setFont(Theme.TITLE);
        title.setForeground(Theme.TEXT);
        header.add(title, BorderLayout.WEST);
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        searchPanel.setOpaque(false);
        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(search);
        header.add(searchPanel, BorderLayout.EAST);
        root.add(header, BorderLayout.NORTH);

        table.setRowHeight(32);
        table.setFillsViewportHeight(true);
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);
        search.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { 
                filter(sorter); 
            }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { 
                filter(sorter); 
            }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { 
                filter(sorter); 
            }
        });
        root.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actions.setOpaque(false);
        JButton refresh = UiFactory.secondaryButton("Refresh");
        JButton delete = UiFactory.dangerButton("Delete Customer");
        JButton close = UiFactory.secondaryButton("Close");
        refresh.addActionListener(e -> refreshData());
        delete.addActionListener(e -> deleteSelected());
        close.addActionListener(e -> dispose());
        actions.add(refresh); actions.add(delete); actions.add(close);
        root.add(actions, BorderLayout.SOUTH);
        setContentPane(root);
    }

    private void refreshData() {
        customers = UserService.getAllCustomers();
        model.setRowCount(0);
        for (Customer customer : customers) {
            model.addRow(new Object[]{customer.getId(), customer.getUsername(), customer.getEmail(), customer.getPhone(), customer.getAddress()});
        }
    }

    private void filter(TableRowSorter<DefaultTableModel> sorter) {
        String text = search.getText().trim();
        sorter.setRowFilter(text.isEmpty() ? null : RowFilter.regexFilter("(?i)" + java.util.regex.Pattern.quote(text)));
    }

    private void deleteSelected() {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) {
            JOptionPane.showMessageDialog(this, "Select a customer first.", "No selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int row = table.convertRowIndexToModel(viewRow);
        int id = (int) model.getValueAt(row, 0);
        String username = String.valueOf(model.getValueAt(row, 1));
        int choice = JOptionPane.showConfirmDialog(this, "Delete customer '" + username + "'?\nThis should only be done when the account has no required records.", "Confirm deletion", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (choice == JOptionPane.YES_OPTION) {
            if (UserService.deleteCustomer(id)) {
                refreshData();
            } else {
                JOptionPane.showMessageDialog(this, "Customer could not be deleted.", "Delete failed", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
