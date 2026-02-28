import java.awt.*;
import java.util.List;

public class SettingsDialog extends Frame{

    private static SettingsDialog instance = null;

    public static SettingsDialog getInstance() {
        if(instance == null)
            instance = new SettingsDialog();
        return instance;
    }

    private SettingsDialog() {
        setSize(400, 600);
        setTitle("Settings");





        setVisible(false);

        setLayout(new FlowLayout());
        addWindowListener(new SettingsDialogAdapter());
        setVisible(false);
    }
}
