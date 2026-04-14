package inventory.ui;

import javax.swing.*;
import java.awt.*;
import java.io.File;

/**
 * Utility to load and scale images from the res/images directory.
 */
public class ImageHelper {

    private static final String IMG_DIR = "D:\\ElectronicsInventorySystem\\res\\images\\";

    /** Load and scale image to fit within given dimensions, preserving aspect ratio. */
    public static ImageIcon loadScaled(String filename, int maxW, int maxH) {
        try {
            File f = new File(IMG_DIR + filename);
            if (!f.exists()) return null;
            ImageIcon raw = new ImageIcon(f.getAbsolutePath());
            Image img = raw.getImage();

            int origW = img.getWidth(null);
            int origH = img.getHeight(null);
            double scale = Math.min((double) maxW / origW, (double) maxH / origH);
            int newW = (int) (origW * scale);
            int newH = (int) (origH * scale);

            Image scaled = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
            return new ImageIcon(scaled);
        } catch (Exception e) {
            return null;
        }
    }

    /** Load image at exact dimensions (stretching). */
    public static ImageIcon loadExact(String filename, int w, int h) {
        try {
            File f = new File(IMG_DIR + filename);
            if (!f.exists()) return null;
            ImageIcon raw = new ImageIcon(f.getAbsolutePath());
            Image scaled = raw.getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH);
            return new ImageIcon(scaled);
        } catch (Exception e) {
            return null;
        }
    }
}
