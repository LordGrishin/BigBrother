import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TimeSlotListener implements ActionListener {

    private static TimeSlotListener instance = null;

    public static TimeSlotListener getInstance() {
        if(instance == null) {
            instance = new TimeSlotListener();
        }
        return instance;
    }

    TimeSlotListener() {
    }

    public void actionPerformed(ActionEvent event) {
        System.out.println("Button pressed: " + event.getActionCommand());
        if(event.getActionCommand().equals("Settings")) SettingsDialog.getInstance().setVisible(true);
        else CategorySelectionDialog.getInstance().selectCategory(event.getActionCommand());
    }
}

