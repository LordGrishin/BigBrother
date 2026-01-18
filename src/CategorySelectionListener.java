import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CategorySelectionListener implements ActionListener {

    private static CategorySelectionListener instance = null;

    public static CategorySelectionListener getInstance() {
        if(instance == null) {
            instance = new CategorySelectionListener();
        }
        return instance;
    }

    CategorySelectionListener() {
    }

    public void actionPerformed(ActionEvent event) {
        System.out.println("Category Selected: " + event.getActionCommand());
        DataStorage.getInstance().setCategory(CategorySelectionDialog.getInstance().getCurrentSlot(), (String) event.getActionCommand());
        TimeSlotDialog.getInstance().refreshTable();
        CategorySelectionDialog.getInstance().setVisible(false);
    }
}

