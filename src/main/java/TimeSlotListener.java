import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Logger;

public class TimeSlotListener implements ActionListener {
    private static final Logger logger = Logger.getLogger(TimeSlotListener.class.getName());

    private static TimeSlotListener instance = null;

    public static TimeSlotListener getInstance() {
        if (instance == null) {
            instance = new TimeSlotListener();
        }
        return instance;
    }

    TimeSlotListener() {
    }

    public void actionPerformed(ActionEvent event) {
        String slotString = event.getActionCommand();
        logger.fine("Category selection requested for slot: " + slotString);
        CategorySelectionDialog.getInstance().selectCategory(slotString);
    }
}