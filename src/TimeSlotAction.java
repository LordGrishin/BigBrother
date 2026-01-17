import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TimeSlotAction implements ActionListener {

    private static TimeSlotAction instance = null;

    public static TimeSlotAction getInstance() {
        if(instance == null) {
            instance = new TimeSlotAction();
        }
        return instance;
    }

    TimeSlotAction() {
    }

    public void actionPerformed(ActionEvent event) {
        System.out.println("Button pressed: " + event.getActionCommand());
    }
}

