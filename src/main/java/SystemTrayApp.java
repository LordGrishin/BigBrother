import java.awt.*;
import java.awt.event.*;
import java.io.File;

public class SystemTrayApp {

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
            return;
        }

        SystemTray systemTray = SystemTray.getSystemTray();

        Image image = null;
        File iconFile = new File("eye.jpg");
        if (iconFile.exists()) {
            image = Toolkit.getDefaultToolkit().getImage(iconFile.getAbsolutePath());
        } else {
            java.net.URL imgURL = getClass().getClassLoader().getResource("eye.jpg");
            if (imgURL != null) {
                image = Toolkit.getDefaultToolkit().getImage(imgURL);
            }
        }

        PopupMenu trayPopupMenu = new PopupMenu();
        MenuItem exitItem = new MenuItem("Exit");
        exitItem.addActionListener(e -> System.exit(0));
        trayPopupMenu.add(exitItem);

        trayIcon = new TrayIcon(image, "Time Tracker", trayPopupMenu);
        trayIcon.setImageAutoSize(true);

        trayIcon.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1) {
                    openTimeSlotDialog();
                }
            }
        });

        try {
            systemTray.add(trayIcon);
        } catch (AWTException e) {
            // Ignore
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