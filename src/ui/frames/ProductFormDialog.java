package ui.frames;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;
import java.io.IOException;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;
import model.Product;
import service.ProductService;
import ui.Theme;
import ui.components.UiFactory;
import util.ImageUtil;

public class ProductFormDialog extends JDialog {
    private static final String[] CATEGORIES = {
        "Electronics", "Computers", "Mobile & Accessories", "Fashion",
        "Home Appliances", "Books", "Beauty & Personal Care",
        "Sports & Fitness", "Groceries", "Toys & Games", "Other"
    };

    private final Product product;
    private final Runnable onSaved;
    private final JTextField nameField = UiFactory.textField(24);
    private final JComboBox<String> categoryBox = new JComboBox<>(CATEGORIES);
    private final JTextField priceField = UiFactory.textField(24);
    private final JTextField stockField = UiFactory.textField(24);
    private final JTextArea descriptionArea = new JTextArea(4, 24);
    private final JLabel imagePreview = new JLabel("No image selected", JLabel.CENTER);
    private final JLabel imageName = UiFactory.mutedLabel("Choose a JPG or PNG product image.");
    private File selectedImageFile;
    private String savedImagePath;
    private JButton saveButton;

    public ProductFormDialog(Frame owner, Product product, Runnable onSaved) {
        super(owner, product == null ? "Add Product" : "Edit Product", true);
        this.product = product;
        this.onSaved = onSaved;
        this.savedImagePath = product == null ? null : product.getImagePath();

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setContentPane(createContent());
        setSize(new Dimension(610, 680));
        setMinimumSize(new Dimension(540, 620));
        setLocationRelativeTo(owner);
        getRootPane().setDefaultButton(saveButton);

        if (product != null) {
            populateFields();
        }
    }

    private JPanel createContent() {
        JPanel root = new JPanel(new BorderLayout(0, 16));
        root.setBackground(Theme.BACKGROUND);
        root.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel heading = new JLabel(product == null ? "Create a new product" : "Update product information");
        heading.setFont(Theme.HEADING);
        heading.setForeground(Theme.TEXT);
        root.add(heading, BorderLayout.NORTH);

        JPanel form = UiFactory.cardPanel();
        form.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 6, 0);

        addField(form, gbc, "Product name", nameField);

        JLabel categoryLabel = label("Category");
        form.add(categoryLabel, gbc);
        gbc.gridy++;
        categoryBox.setFont(Theme.BODY);
        categoryBox.setPreferredSize(new Dimension(400, 42));
        form.add(categoryBox, gbc);
        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 14, 0);

        gbc.gridwidth = 1;
        gbc.weightx = 0.5;
        gbc.insets = new Insets(0, 0, 6, 8);
        form.add(label("Price"), gbc);
        gbc.gridx = 1;
        gbc.insets = new Insets(0, 8, 6, 0);
        form.add(label("Stock quantity"), gbc);
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.insets = new Insets(0, 0, 14, 8);
        form.add(priceField, gbc);
        gbc.gridx = 1;
        gbc.insets = new Insets(0, 8, 14, 0);
        form.add(stockField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.weightx = 1;
        gbc.insets = new Insets(0, 0, 6, 0);
        form.add(label("Description"), gbc);
        gbc.gridy++;
        descriptionArea.setFont(Theme.BODY);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        JScrollPane descriptionScroll = new JScrollPane(descriptionArea);
        descriptionScroll.setPreferredSize(new Dimension(440, 100));
        form.add(descriptionScroll, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(14, 0, 6, 0);
        form.add(label("Product image"), gbc);
        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 8, 0);

        JPanel imageRow = new JPanel(new BorderLayout(12, 0));
        imageRow.setOpaque(false);
        imagePreview.setPreferredSize(new Dimension(150, 105));
        imagePreview.setOpaque(true);
        imagePreview.setBackground(Theme.SURFACE_SOFT);
        imagePreview.setForeground(Theme.TEXT_MUTED);
        imagePreview.setBorder(BorderFactory.createLineBorder(Theme.BORDER));
        imageRow.add(imagePreview, BorderLayout.WEST);

        JPanel imageActions = new JPanel();
        imageActions.setOpaque(false);
        imageActions.setLayout(new javax.swing.BoxLayout(imageActions, javax.swing.BoxLayout.Y_AXIS));
        JButton chooseImage = UiFactory.secondaryButton("Choose Image");
        JButton removeImage = UiFactory.secondaryButton("Remove Image");
        chooseImage.setAlignmentX(LEFT_ALIGNMENT);
        removeImage.setAlignmentX(LEFT_ALIGNMENT);
        imageName.setAlignmentX(LEFT_ALIGNMENT);
        chooseImage.addActionListener(e -> chooseImage());
        removeImage.addActionListener(e -> clearImage());
        imageActions.add(chooseImage);
        imageActions.add(javax.swing.Box.createVerticalStrut(8));
        imageActions.add(removeImage);
        imageActions.add(javax.swing.Box.createVerticalStrut(8));
        imageActions.add(imageName);
        imageRow.add(imageActions, BorderLayout.CENTER);
        form.add(imageRow, gbc);

        root.add(new JScrollPane(form, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER), BorderLayout.CENTER);

        JPanel actions = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 10, 0));
        actions.setOpaque(false);
        JButton cancel = UiFactory.secondaryButton("Cancel");
        saveButton = UiFactory.primaryButton(product == null ? "Add Product" : "Save Changes");
        cancel.addActionListener(e -> dispose());
        saveButton.addActionListener(e -> saveProduct());
        actions.add(cancel);
        actions.add(saveButton);
        root.add(actions, BorderLayout.SOUTH);
        return root;
    }

    private JLabel label(String text) {
        JLabel label = new JLabel(text);
        label.setFont(Theme.BODY_BOLD);
        label.setForeground(Theme.TEXT);
        return label;
    }

    private void addField(JPanel form, GridBagConstraints gbc, String labelText, JTextField field) {
        form.add(label(labelText), gbc);
        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 14, 0);
        form.add(field, gbc);
        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 6, 0);
    }

    private void populateFields() {
        nameField.setText(product.getName());
        categoryBox.setSelectedItem(product.getCategory());
        if (categoryBox.getSelectedIndex() < 0) categoryBox.setSelectedItem("Other");
        priceField.setText(String.valueOf(product.getPrice()));
        stockField.setText(String.valueOf(product.getStockQuantity()));
        descriptionArea.setText(product.getDescription());
        updatePreview(savedImagePath, product.getImagePath() == null ? "No image selected" : new File(product.getImagePath()).getName());
    }

    private void chooseImage() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Select product image");
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.setFileFilter(new FileNameExtensionFilter("Image files (JPG, PNG, GIF)", "jpg", "jpeg", "png", "gif"));
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            selectedImageFile = chooser.getSelectedFile();
            updatePreview(selectedImageFile.getAbsolutePath(), selectedImageFile.getName());
        }
    }

    private void clearImage() {
        selectedImageFile = null;
        savedImagePath = null;
        imagePreview.setIcon(null);
        imagePreview.setText("No image selected");
        imageName.setText("Choose a JPG or PNG product image.");
    }

    private void updatePreview(String path, String filename) {
        javax.swing.ImageIcon icon = ImageUtil.scaledIcon(path, 148, 103);
        imagePreview.setIcon(icon);
        imagePreview.setText(icon == null ? "Image unavailable" : "");
        imageName.setText(filename == null ? "No image selected" : filename);
    }

    private void saveProduct() {
        try {
            String name = nameField.getText().trim();
            String category = String.valueOf(categoryBox.getSelectedItem());
            String description = descriptionArea.getText().trim();
            double price = Double.parseDouble(priceField.getText().trim());
            int stock = Integer.parseInt(stockField.getText().trim());

            if (name.isEmpty()) throw new IllegalArgumentException("Product name is required.");
            if (price < 0 || stock < 0) throw new IllegalArgumentException("Price and stock cannot be negative.");

            String finalImagePath = savedImagePath;
            if (selectedImageFile != null) finalImagePath = ImageUtil.copyProductImage(selectedImageFile);

            if (product == null) {
                ProductService.addProduct(name, description, price, stock, category, finalImagePath);
            } else {
                ProductService.updateProduct(product.getId(), name, description, price, stock, category, finalImagePath);
            }

            if (onSaved != null) onSaved.run();
            dispose();
        } catch (NumberFormatException exception) {
            JOptionPane.showMessageDialog(this, "Enter a valid number for price and a whole number for stock.", "Invalid input", JOptionPane.ERROR_MESSAGE);
        } catch (IOException exception) {
            JOptionPane.showMessageDialog(this, "The image could not be copied: " + exception.getMessage(), "Image error", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException exception) {
            JOptionPane.showMessageDialog(this, exception.getMessage(), "Invalid input", JOptionPane.ERROR_MESSAGE);
        }
    }
}
