import java.awt.*;
import java.time.LocalTime;
import java.util.logging.Logger;
import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.*;

public class Main {
    private static final Logger logger = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
        // Настройка FlatLaf Look & Feel для стиля IntelliJ IDEA
        FlatLightLaf.setup();
        UIManager.put("Component.arc", 8);
        UIManager.put("Button.arc", 8);
        UIManager.put("TextComponent.arc", 8);
        UIManager.put("ScrollBar.thumbArc", 999);
        UIManager.put("ScrollBar.thumbInsets", new Insets(2, 2, 2, 2));

        logger.info(String.format("Application started at %02d:%02d:%02d",
                LocalTime.now().getHour(),
                LocalTime.now().getMinute(),
                LocalTime.now().getSecond()));

        SwingUtilities.invokeLater(() -> {
            TimeSlotDialog.getInstance();
        });
    }
}