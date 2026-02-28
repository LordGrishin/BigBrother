import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

        Label setInterval = new Label();
        setInterval.setText("Set Interval (min)");
        add(setInterval);

        TextField interval = new TextField();
        interval.setSize(200, 50);
        add(interval);

        Button setIntervalBtn = new Button("Set");
        setIntervalBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String intervalS = interval.getText();
//                int intervalMin = Integer.valueOf(intervalS);
//                int beginHour = (begin.charAt(0) - '0')*10 + (begin.charAt(1) - '0');
//                int beginMinute = (begin.charAt(2) - '0')*10 + (begin.charAt(3) - '0');
//                int endHour = (end.charAt(0) - '0')*10 + (end.charAt(1) - '0');
//                int endMinute = (end.charAt(2) - '0')*10 + (end.charAt(3) - '0');
            }
        });
        add(setIntervalBtn);




        setVisible(false);

        setLayout(new FlowLayout(FlowLayout.LEFT));
        addWindowListener(new SettingsDialogAdapter());
        setVisible(false);
    }
}
