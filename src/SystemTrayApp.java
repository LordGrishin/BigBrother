import java.awt.*;
import java.awt.event.*;

public class SystemTrayApp{

    private static SystemTrayApp instance = null;

    public static SystemTrayApp getInstance() {
        if(instance == null) {
            instance = new SystemTrayApp();
        }
        return instance;
    }

    public SystemTrayApp(){
        SystemTray systemTray = SystemTray.getSystemTray();

        Image image = Toolkit.getDefaultToolkit().getImage("src/eye.jpg");

        PopupMenu trayPopupMenu = new PopupMenu();

        MenuItem action = new MenuItem("Open app");
        action.addActionListener(SystemTrayListener.getInstance());
        trayPopupMenu.add(action);

        MenuItem close = new MenuItem("Close");
        close.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        trayPopupMenu.add(close);

        TrayIcon trayIcon = new TrayIcon(image, "SystemTray App", trayPopupMenu);

        trayIcon.setImageAutoSize(true);

        try{
            systemTray.add(trayIcon);
        }catch(AWTException awtException){
            awtException.printStackTrace();
        }
    }
}
