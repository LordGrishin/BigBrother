import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SettingsDialogListener implements ActionListener {

    private static SettingsDialogListener instance = null;

    public static SettingsDialogListener getInstance() {
        if(instance == null) {
            instance = new SettingsDialogListener();
        }
        return instance;
    }

    SettingsDialogListener() {
    }

    public void actionPerformed(ActionEvent event) {
        //System.out.println("Button pressed: " + event.getActionCommand());
    }
}