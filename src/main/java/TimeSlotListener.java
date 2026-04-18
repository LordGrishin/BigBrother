import java.awt.event.*;

public class TimeSlotListener implements ActionListener {

    private static TimeSlotListener instance = null;

    public static TimeSlotListener getInstance() {
        if (instance == null) {
            instance = new TimeSlotListener();
        }
        return instance;
    }

    TimeSlotListener() {}

    public void actionPerformed(ActionEvent event) {
        String slotString = event.getActionCommand();
        CategorySelectionDialog.getInstance().selectCategory(slotString);
    }
}