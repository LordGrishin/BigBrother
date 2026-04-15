import java.awt.*;
import java.util.List;

public class CategorySelectionDialog extends Frame{

    private static CategorySelectionDialog instance = null;

    public static CategorySelectionDialog getInstance() {
        if(instance == null) {
            instance = new CategorySelectionDialog();
        }
        return instance;
    }
    private String currentSlot;
    private Button[] categoriesList;

    private CategorySelectionDialog() {
        setSize(400, 600);
        setTitle("Category Selection");

        DataStorage table = DataStorage.getInstance();
        List categories = table.getCategories();

        setLayout(new GridLayout(categories.size(), 1));
        addWindowListener(new CategorySelectionDialogAdapter());
        setVisible(false);

        categoriesList = new Button[categories.size()];

        for(int i = 0; i < categories.size(); i++) {
            categoriesList[i] = new Button((String) categories.get(i));
            categoriesList[i].addActionListener(CategorySelectionListener.getInstance());
            add(categoriesList[i]);
        }
    }

    void refreshList(String slot) {
        for (int i = 0; i < categoriesList.length; i++) {
            categoriesList[i].setBackground(Color.white);
        }
        for (int i = 0; i < categoriesList.length; i++) {
            if((categoriesList[i].getLabel().equals(DataStorage.getInstance().getCategory(slot)))) {
                categoriesList[i].setBackground(Color.getHSBColor(115, 33, 98));
            }
        }
    }

    void selectCategory (String slot) {
        currentSlot = slot;
        refreshList(slot);

        setVisible(true);
    }

    public String getCurrentSlot() {
        return currentSlot;
    }
}
