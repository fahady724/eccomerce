package ui.frames;

import exception.InvalidQuantityException;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;
import model.CashPayment;
import model.CreditCardPay;
import model.MobilePayment;
import model.OrderItem;
import model.Payment;
import service.CartService;
import service.OrderService;
import service.PaymentService;
import ui.AppFrame;
import ui.Theme;
import ui.components.UiFactory;

/** Checkout and payment-selection window. */
public class CheckoutFrame extends JFrame {
    private final AppFrame app;
    private final List<OrderItem> items;
    private final double total;
    private final JButton placeOrder = UiFactory.primaryButton("Place Order");

    private final JRadioButton cash = new JRadioButton("Cash on Delivery", true);
    private final JRadioButton mobile = new JRadioButton("Mobile Banking");
    private final JRadioButton card = new JRadioButton("Credit Card");
    private final CardLayout paymentCards = new CardLayout();
    private final JPanel paymentDetails = new JPanel(paymentCards);

    private final JTextField mobileNumber = UiFactory.textField(20);
    private final JComboBox<String> provider = new JComboBox<>(new String[]{"bKash", "Nagad"});
    private final JTextField cardHolder = UiFactory.textField(20);
    private final JTextField cardNumber = UiFactory.textField(20);
    private final JPasswordField cvv = new JPasswordField(20);
    private final JTextField expiry = UiFactory.textField(20);

    public CheckoutFrame(AppFrame app, List<OrderItem> items) {
        super("OmniCommerce - Checkout");
        this.app = app;
        this.items = items;
        this.total = items.stream().mapToDouble(OrderItem::getTotal).sum();
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(920, 650);
        setMinimumSize(new Dimension(820, 600));
        setLocationRelativeTo(app);
        buildUi();
    }

    private void buildUi() {
        JPanel root = new JPanel(new BorderLayout(0, 18));
        root.setBackground(Theme.BACKGROUND);
        root.setBorder(BorderFactory.createEmptyBorder(24, 26, 24, 26));

        JPanel titlePanel = new JPanel();
        titlePanel.setOpaque(false);
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        JLabel title = new JLabel("Checkout");
        title.setFont(Theme.TITLE);
        title.setForeground(Theme.TEXT);
        JLabel subtitle = UiFactory.mutedLabel("Review your order and choose a payment method.");
        titlePanel.add(title);
        titlePanel.add(Box.createVerticalStrut(5));
        titlePanel.add(subtitle);
        root.add(titlePanel, BorderLayout.NORTH);

        JPanel center = new JPanel(new GridBagLayout());
        center.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 0, 0, 10);
        gbc.gridx = 0;
        gbc.weightx = 0.52;
        center.add(createOrderPanel(), gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.48;
        gbc.insets = new Insets(0, 10, 0, 0);
        center.add(createPaymentPanel(), gbc);
        root.add(center, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new BorderLayout(12, 0));
        bottom.setOpaque(false);
        JButton cancel = UiFactory.secondaryButton("Back to Cart");
        cancel.addActionListener(e -> dispose());
        bottom.add(cancel, BorderLayout.WEST);

        JPanel totalActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 0));
        totalActions.setOpaque(false);
        JLabel totalLabel = new JLabel(String.format("Total: ৳%,.2f", total));
        totalLabel.setFont(Theme.HEADING);
        totalLabel.setForeground(Theme.TEXT);
        totalActions.add(totalLabel);
        placeOrder.addActionListener(e -> submitOrder());
        totalActions.add(placeOrder);
        bottom.add(totalActions, BorderLayout.EAST);
        root.add(bottom, BorderLayout.SOUTH);

        setContentPane(root);
        getRootPane().setDefaultButton(placeOrder);
    }

    private JPanel createOrderPanel() {
        JPanel panel = UiFactory.cardPanel();
        panel.setLayout(new BorderLayout(0, 16));

        JPanel customer = new JPanel(new GridBagLayout());
        customer.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 12, 0);
        JLabel heading = new JLabel("Delivery information");
        heading.setFont(Theme.HEADING);
        heading.setForeground(Theme.TEXT);
        customer.add(heading, gbc);

        gbc.gridwidth = 1;
        addInfoRow(customer, gbc, "Customer", app.getCurrentUser().getUsername());
        addInfoRow(customer, gbc, "Email", safe(app.getCurrentUser().getEmail()));
        addInfoRow(customer, gbc, "Phone", safe(app.getCurrentUser().getPhone()));
        addInfoRow(customer, gbc, "Address", safe(app.getCurrentUser().getAddress()));
        panel.add(customer, BorderLayout.NORTH);

        DefaultTableModel model = new DefaultTableModel(new Object[]{"Product", "Qty", "Price", "Subtotal"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        for (OrderItem item : items) {
            model.addRow(new Object[]{
                    item.getProduct().getName(),
                    item.getQuantity(),
                    String.format("৳%,.2f", item.getProduct().getPrice()),
                    String.format("৳%,.2f", item.getTotal())
            });
        }
        JTable table = new JTable(model);
        table.setRowHeight(30);
        table.setFillsViewportHeight(true);
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(Theme.BORDER));
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private void addInfoRow(JPanel panel, GridBagConstraints gbc, String labelText, String valueText) {
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.insets = new Insets(4, 0, 4, 18);
        JLabel label = UiFactory.mutedLabel(labelText);
        label.setPreferredSize(new Dimension(78, 22));
        panel.add(label, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(4, 0, 4, 0);
        JLabel value = new JLabel("<html>" + valueText + "</html>");
        value.setFont(Theme.BODY_BOLD);
        value.setForeground(Theme.TEXT);
        panel.add(value, gbc);
    }

    private JPanel createPaymentPanel() {
        JPanel panel = UiFactory.cardPanel();
        panel.setLayout(new BorderLayout(0, 16));

        JLabel heading = new JLabel("Payment method");
        heading.setFont(Theme.HEADING);
        heading.setForeground(Theme.TEXT);
        panel.add(heading, BorderLayout.NORTH);

        JPanel body = new JPanel();
        body.setOpaque(false);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));

        ButtonGroup group = new ButtonGroup();
        JPanel methods = new JPanel(new GridBagLayout());
        methods.setOpaque(false);
        GridBagConstraints optionGbc = new GridBagConstraints();
        optionGbc.gridx = 0;
        optionGbc.weightx = 1;
        optionGbc.fill = GridBagConstraints.HORIZONTAL;
        optionGbc.anchor = GridBagConstraints.WEST;
        optionGbc.insets = new Insets(3, 0, 3, 0);
        int row = 0;
        for (JRadioButton option : new JRadioButton[]{cash, mobile, card}) {
            option.setOpaque(false);
            option.setFont(Theme.BODY_BOLD);
            option.setForeground(Theme.TEXT);
            group.add(option);
            optionGbc.gridy = row++;
            methods.add(option, optionGbc);
        }
        body.add(methods);
        body.add(Box.createVerticalStrut(14));

        paymentDetails.setOpaque(false);
        paymentDetails.setAlignmentX(Component.LEFT_ALIGNMENT);
        paymentDetails.add(createEmptyPaymentCard(), "CASH");
        paymentDetails.add(createFormPanel(new String[]{"Provider", "Mobile number"}, new Component[]{provider, mobileNumber}), "MOBILE");
        paymentDetails.add(createFormPanel(new String[]{"Card holder", "Card number", "CVV", "Expiry (MM/YY)"},new Component[]{cardHolder, cardNumber, cvv, expiry}), "CARD");
        body.add(paymentDetails);

        cash.addActionListener(e -> paymentCards.show(paymentDetails, "CASH"));
        mobile.addActionListener(e -> paymentCards.show(paymentDetails, "MOBILE"));
        card.addActionListener(e -> paymentCards.show(paymentDetails, "CARD"));
        paymentCards.show(paymentDetails, "CASH");

        panel.add(body, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createEmptyPaymentCard() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        JLabel note = UiFactory.mutedLabel("Pay when your order is delivered.");
        note.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));
        panel.add(note, BorderLayout.NORTH);
        return panel;
    }

    private JPanel createFormPanel(String[] labels, Component[] components) {
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        for (int i = 0; i < labels.length; i++) {
            gbc.gridy = i * 2;
            gbc.insets = new Insets(i == 0 ? 0 : 10, 0, 5, 0);
            JLabel label = new JLabel(labels[i]);
            label.setFont(Theme.BODY_BOLD);
            label.setForeground(Theme.TEXT);
            form.add(label, gbc);

            gbc.gridy = i * 2 + 1;
            gbc.insets = new Insets(0, 0, 0, 0);
            Component component = components[i];
            component.setPreferredSize(new Dimension(310, 42));
            form.add(component, gbc);
        }
        return form;
    }

    private void submitOrder() {
        Payment payment;
        String method;
        if (cash.isSelected()) {
            payment = new CashPayment(total);
            method = "CASH_ON_DELIVERY";
        } else if (mobile.isSelected()) {
            payment = new MobilePayment(total, mobileNumber.getText().trim(), provider.getSelectedItem().toString());
            method = provider.getSelectedItem().toString();
        } else {
            payment = new CreditCardPay(total, cardNumber.getText().trim(), cardHolder.getText().trim(), new String(cvv.getPassword()), expiry.getText().trim());
            method = "CREDIT_CARD";
        }

        if (!payment.processPayment()) {
            JOptionPane.showMessageDialog(this, "Payment details are invalid or payment was declined.", "Payment failed", JOptionPane.ERROR_MESSAGE);
            return;
        }

        placeOrder.setEnabled(false);
        placeOrder.setText("Processing...");
        new SwingWorker<Integer, Void>() {
            @Override protected Integer doInBackground() throws Exception {
                return OrderService.placeOrderAndReturnId(app.getCurrentUser().getId(), items);
            }
            @Override protected void done() {
                try {
                    int orderId = get();
                    if (orderId <= 0) throw new IllegalStateException("Order could not be saved.");
                    PaymentService.savePayment(orderId, total, method, payment.getStatus());
                    CartService.clearCart(app.getCurrentUser().getId());
                    JOptionPane.showMessageDialog(CheckoutFrame.this, "Order #" + orderId + " placed successfully.", "Order complete", JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                } catch (Exception exception) {
                    Throwable cause = exception.getCause();
                    String message = cause instanceof InvalidQuantityException ? cause.getMessage() : exception.getMessage();
                    JOptionPane.showMessageDialog(CheckoutFrame.this, message, "Checkout failed", JOptionPane.ERROR_MESSAGE);
                } finally {
                    placeOrder.setEnabled(true);
                    placeOrder.setText("Place Order");
                }
            }
        }.execute();
    }

    private String safe(String value) {
        return value == null || value.isBlank() ? "Not provided" : value;
    }
}
