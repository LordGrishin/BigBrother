import java.time.LocalTime;
import java.util.logging.Logger;
import com.formdev.flatlaf.FlatDarkLaf;
import javax.swing.*;
import java.awt.*;

public class Main {
    private static final Logger logger = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
        // Настройки ДО установки Look & Feel (используем прямые значения цветов)
        UIManager.put("TitlePane.background", Color.decode("#4A4D4F"));
        UIManager.put("TitlePane.foreground", Color.decode("#DDDDDD"));
        UIManager.put("TitlePane.buttonHoverBackground", Color.decode("#C75450"));
        UIManager.put("TitlePane.buttonHoverForeground", Color.WHITE);

        // Настройка FlatLaf Look & Feel
        FlatDarkLaf.setup();

        // Общие настройки скруглений (после setup)
        UIManager.put("Component.arc", 8);
        UIManager.put("Button.arc", 8);
        UIManager.put("TextComponent.arc", 8);
        UIManager.put("ScrollBar.thumbArc", 999);
        UIManager.put("ScrollBar.thumbInsets", new Insets(2, 2, 2, 2));

        // Цвет фона панелей (теперь ThemeColors уже загружен)
        UIManager.put("Panel.background", ThemeColors.BACKGROUND);

        logger.info(String.format("Application started at %02d:%02d:%02d",
                LocalTime.now().getHour(),
                LocalTime.now().getMinute(),
                LocalTime.now().getSecond()));

        SwingUtilities.invokeLater(() -> {
            TimeSlotDialog.getInstance();
        });
    }
}