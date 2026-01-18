import java.awt.*;
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
        setSize(400, 600);
        setName("Time Slots");

        DataStorage table = DataStorage.getInstance();
        List slots = table.getSlots();

        slotList = new Slot[slots.size()];

        for(int i = 0; i < slots.size(); i++) {
            slotList[i] = new Slot( (String) slots.get(i), table.getCategory((String)slots.get(i)));
            add(slotList[i]);
        }

        setLayout(new FlowLayout(FlowLayout.LEFT));
        addWindowListener(new TimeSlotDialogAdapter());
        setVisible(true);
    }

    void refreshTable() {
        DataStorage table = DataStorage.getInstance();
        List slots = table.getSlots();

        for(int i = 0; i < slots.size(); i++) {
            slotList[i].setSlotCategory(table.getCategory((String)slots.get(i)));
        }
        revalidate();
        repaint();

    }
}
