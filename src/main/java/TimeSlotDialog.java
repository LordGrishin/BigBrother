import javax.swing.*;
import java.awt.*;
import java.time.LocalTime;
import java.util.List;

public class TimeSlotDialog extends Frame {

    private Slot[] slotList;

    private static TimeSlotDialog instance = null;

    public static TimeSlotDialog getInstance(){
        if(instance == null) {
            instance = new TimeSlotDialog();
        }
        return instance;
    }

    private TimeSlotDialog() {
        DataStorage table = DataStorage.getInstance();
        List slots = table.getSlots();

        setSize(400, slots.size()*60);
        setTitle("Time Slots");

        Panel gui = new Panel();
        gui.setPreferredSize(new Dimension(250, 30));
        Button settings = new Button("Settings");
        settings.addActionListener(TimeSlotListener.getInstance());
        gui.add(settings);
        gui.setLayout(new FlowLayout(FlowLayout.LEFT));
        add(gui);

        slotList = new Slot[slots.size()];

        for(int i = 0; i < slots.size(); i++) {
            slotList[i] = new Slot( (String) slots.get(i), table.getCategory((String)slots.get(i)));
            add(slotList[i]);
        }

        refreshTable();

        setLayout(new FlowLayout(FlowLayout.LEFT));
        //setLayout(new GridLayout());
        addWindowListener(new TimeSlotDialogAdapter());
        setVisible(true);
    }

    void refreshTable() {
        DataStorage table = DataStorage.getInstance();
        List slots = table.getSlots();

        for(int i = 0; i < slots.size(); i++) {
            slotList[i].setSlotCategory(table.getCategory((String)slots.get(i)));
            if(slotList[i].getStartHour()*100+slotList[i].getStartMinute() < LocalTime.now().getHour()*100+LocalTime.now().getMinute() &&
                    slotList[i].getEndHour()*100+slotList[i].getEndMinute() > LocalTime.now().getHour()*100+LocalTime.now().getMinute()) {
                slotList[i].setSlotTimeColor();
            }
        }
        revalidate();
        repaint();

    }
}
