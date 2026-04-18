import com.formdev.flatlaf.FlatDarkLaf;
import javax.swing.*;
import java.awt.*;

public class Main {

    public static void main(String[] args) {
        UIManager.put("TitlePane.background", Color.decode("#4A4D4F"));
        UIManager.put("TitlePane.foreground", Color.decode("#DDDDDD"));
        UIManager.put("TitlePane.buttonHoverBackground", Color.decode("#C75450"));
        UIManager.put("TitlePane.buttonHoverForeground", Color.WHITE);

        FlatDarkLaf.setup();

        UIManager.put("Component.arc", 8);
        UIManager.put("Button.arc", 8);
        UIManager.put("TextComponent.arc", 8);
        UIManager.put("ScrollBar.thumbArc", 999);
        UIManager.put("ScrollBar.thumbInsets", new Insets(2, 2, 2, 2));
        UIManager.put("Panel.background", ThemeColors.BACKGROUND);

        SwingUtilities.invokeLater(() -> {
            TimeSlotDialog.getInstance();
        });
    }
}