package util;

import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import javax.swing.ImageIcon;


public final class ImageUtil {
    private static final Path PRODUCT_IMAGE_DIR = Path.of("data", "product_images");

    private ImageUtil() {}

    public static String copyProductImage(File source) throws IOException {
        if (source == null || !source.isFile()) return null;
        Files.createDirectories(PRODUCT_IMAGE_DIR);
        String extension = extensionOf(source.getName());
        String filename = UUID.randomUUID() + extension;
        Path target = PRODUCT_IMAGE_DIR.resolve(filename);
        Files.copy(source.toPath(), target, StandardCopyOption.REPLACE_EXISTING);
        return target.toString().replace('\\', '/');
    }

    public static ImageIcon scaledIcon(String imagePath, int width, int height) {
        if (imagePath == null || imagePath.isBlank()) return null;
        File file = new File(imagePath);
        if (!file.isFile()) return null;
        ImageIcon original = new ImageIcon(file.getAbsolutePath());
        if (original.getIconWidth() <= 0 || original.getIconHeight() <= 0) return null;
        Image scaled = original.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(scaled);
    }

    private static String extensionOf(String filename) {
        int dot = filename.lastIndexOf('.');
        if (dot < 0) return ".png";
        String ext = filename.substring(dot).toLowerCase();
        return switch (ext) {
            case ".jpg", ".jpeg", ".png", ".gif" -> ext;
            default -> ".png";
        };
    }
}
