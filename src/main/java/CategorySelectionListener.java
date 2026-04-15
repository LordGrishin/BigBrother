import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Logger;

public class CategorySelectionListener implements ActionListener {
    private static final Logger logger = Logger.getLogger(CategorySelectionListener.class.getName());

    private static CategorySelectionListener instance = null;

    public static CategorySelectionListener getInstance() {
        if (instance == null) {
            instance = new CategorySelectionListener();
        }
        return instance;
    }

    CategorySelectionListener() {
    }

    public void actionPerformed(ActionEvent event) {
        String category = event.getActionCommand();
        String slot = CategorySelectionDialog.getInstance().getCurrentSlot();

        logger.info("Category selected: " + category + " for slot: " + slot);

        DataStorage.getInstance().setCategory(slot, category);
        TimeSlotDialog.getInstance().refreshTable();
        CategorySelectionDialog.getInstance().setVisible(false);
    }
}