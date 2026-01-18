import java.awt.*;

public class Slot extends Panel {
    private Button slotTime;
    private Label slotCategory;



    Slot(String newSlotTime, String newSlotCategory) {
        slotTime = new Button(newSlotTime);

        slotTime.addActionListener(TimeSlotListener.getInstance());

        add(slotTime);
        slotCategory = new Label(newSlotCategory);
        add(slotCategory);
        setLayout(new FlowLayout(FlowLayout.LEFT));
        setPreferredSize(new Dimension(250, 50));
    }

    void setSlotCategory(String newSlotCategory) {
        slotCategory.setText(newSlotCategory);
    }

    public String getSlotTime() {
        return slotTime.getLabel();
    }

    public String getSlotCategory() {
        return slotCategory.getText();
    }
}
