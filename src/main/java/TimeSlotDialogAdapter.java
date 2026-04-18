import java.awt.event.*;

public class TimeSlotDialogAdapter extends WindowAdapter {

    @Override
    public void windowClosing(WindowEvent e) {
        TimeSlotDialog.getInstance().setVisible(false);
        SystemTrayApp.getInstance();
    }

    @Override
    public void windowIconified(WindowEvent e) {
        TimeSlotDialog.getInstance().setVisible(false);
        SystemTrayApp.getInstance();
    }
}