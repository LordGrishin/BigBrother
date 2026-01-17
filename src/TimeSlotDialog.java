import java.awt.*;
import java.util.List;

public class TimeSlotDialog extends Frame {


    TimeSlotDialog(String name) {
        setSize(400, 600);
        setName(name);

        refreshTable();

        setLayout(new FlowLayout(FlowLayout.LEFT));
        addWindowListener(new TimeSlotDialogActions());
        setVisible(true);
    }

    void refreshTable() {
        DataStorage table = DataStorage.getInstance();
        List slots = table.getSlots();
        for(int i = 0; i< slots.size(); i++) {
            add(new Slot( (String) slots.get(i), table.getCategory((String)slots.get(i))));
        }
    }

}
