import java.awt.event.ActionEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SystemTrayListener implements ActionListener {

    private static SystemTrayListener instance = null;

    public static SystemTrayListener getInstance() {
        if(instance == null) {
            instance = new SystemTrayListener();
        }
        return instance;
    }

    SystemTrayListener() {
    }

    public void actionPerformed(ActionEvent event) {
        //закрыть трей и включить видимость TimeSlotDialog
    }
}
