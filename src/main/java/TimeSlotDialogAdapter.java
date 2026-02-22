import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class TimeSlotDialogAdapter extends WindowAdapter {
    public void windowClosing(WindowEvent e)
    {
        System.out.println("Window: closed");
        System.exit(0);
    }
    public void windowOpened(WindowEvent e) {
        System.out.println("Window: opened");
    }
    public void windowIconified(WindowEvent e) {
        TimeSlotDialog.getInstance().setVisible(false);
        SystemTrayApp.getInstance();
        System.out.println("Window: iconified");
        //вызвать метод из storage и отослать на гит
    }

    public void windowDeiconified(WindowEvent e) {
        System.out.println("Window: de-iconified");
    }
}
