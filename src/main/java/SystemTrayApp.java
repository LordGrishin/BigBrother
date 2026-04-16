import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.logging.Logger;

public class SystemTrayApp {
    private static final Logger logger = Logger.getLogger(SystemTrayApp.class.getName());

    private static SystemTrayApp instance = null;
    private TrayIcon trayIcon;

    public static SystemTrayApp getInstance() {
        if (instance == null) {
            instance = new SystemTrayApp();
        }
        return instance;
    }

    public SystemTrayApp() {
        if (!SystemTray.isSupported()) {
            logger.warning("SystemTray is not supported");
            return;
        }

        SystemTray systemTray = SystemTray.getSystemTray();

        Image image = null;

        // Сначала пробуем загрузить из файла рядом с JAR
        File iconFile = new File("eye.jpg");
        if (iconFile.exists()) {
            image = Toolkit.getDefaultToolkit().getImage(iconFile.getAbsolutePath());
        } else {
            // Если нет - пробуем из ресурсов
            java.net.URL imgURL = getClass().getClassLoader().getResource("eye.jpg");
            if (imgURL != null) {
                image = Toolkit.getDefaultToolkit().getImage(imgURL);
            }
        }

        PopupMenu trayPopupMenu = new PopupMenu();

        MenuItem exitItem = new MenuItem("Exit");
        exitItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logger.info("Application exiting");
                System.exit(0);
            }
        });
        trayPopupMenu.add(exitItem);

        trayIcon = new TrayIcon(image, "Time Tracker", trayPopupMenu);
        trayIcon.setImageAutoSize(true);

        trayIcon.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1) {
                    logger.info("Tray icon double-clicked - opening window");
                    openTimeSlotDialog();
                }
            }
        });

        try {
            systemTray.add(trayIcon);
            logger.info("SystemTray initialized");
        } catch (AWTException awtException) {
            logger.severe("Failed to add tray icon: " + awtException.getMessage());
        }
    }

    private void openTimeSlotDialog() {
        TimeSlotDialog.getInstance().setVisible(true);
        TimeSlotDialog.getInstance().setState(Frame.NORMAL);
        TimeSlotDialog.getInstance().toFront();
    }

    public void showNotification(String title, String message) {
        if (trayIcon != null) {
            trayIcon.displayMessage(title, message, TrayIcon.MessageType.INFO);
        }
    }
}