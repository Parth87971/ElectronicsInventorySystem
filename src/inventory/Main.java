package inventory;

import inventory.ui.LoginFrame;

import javax.swing.*;
import java.awt.*;

/**
 * Application entry point.
 */
public class Main {

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new com.formdev.flatlaf.FlatIntelliJLaf());
        } catch (Exception e) {
            try { UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName()); }
            catch (Exception ignored) {}
        }

        UIManager.put("Button.arc", 8);
        UIManager.put("Component.arc", 8);
        UIManager.put("TextComponent.arc", 6);
        UIManager.put("ScrollBar.width", 10);
        UIManager.put("ScrollBar.trackArc", 999);
        UIManager.put("ScrollBar.thumbArc", 999);

        // Tahoma font throughout
        Font font14   = new Font("Tahoma", Font.PLAIN, 14);
        Font fontB14  = new Font("Tahoma", Font.BOLD,  14);
        Font fontB13  = new Font("Tahoma", Font.BOLD,  13);

        String[] fontKeys = {"Label.font", "TextField.font", "TextArea.font",
                "ComboBox.font", "CheckBox.font", "Spinner.font",
                "Table.font", "List.font", "OptionPane.messageFont",
                "PasswordField.font"};
        for (String k : fontKeys) UIManager.put(k, font14);

        UIManager.put("TableHeader.font", fontB14);
        UIManager.put("Button.font", fontB13);
        UIManager.put("TitledBorder.font", fontB14);
        UIManager.put("OptionPane.buttonFont", font14);
        UIManager.put("Menu.font", fontB13);
        UIManager.put("MenuBar.font", fontB13);

        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}
