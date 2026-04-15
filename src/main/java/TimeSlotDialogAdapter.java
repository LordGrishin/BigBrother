import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.logging.Logger;

public class TimeSlotDialogAdapter extends WindowAdapter {
    private static final Logger logger = Logger.getLogger(TimeSlotDialogAdapter.class.getName());

    @Override
    public void windowClosing(WindowEvent e) {
        logger.info("Window closing - minimizing to tray");
        TimeSlotDialog.getInstance().setVisible(false);
        SystemTrayApp.getInstance();
    }

    @Override
    public void windowOpened(WindowEvent e) {
        logger.fine("Window opened");
    }

    @Override
    public void windowIconified(WindowEvent e) {
        logger.info("Window iconified - hiding to tray");
        TimeSlotDialog.getInstance().setVisible(false);
        SystemTrayApp.getInstance();
    }

    @Override
    public void windowDeiconified(WindowEvent e) {
        logger.fine("Window de-iconified");
    }
}