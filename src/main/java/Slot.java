import java.awt.*;
import java.time.LocalTime;

public class Slot extends Panel {
    private Button slotTime;
    private Label slotCategory;
    private int startHour;
    private int startMinute;
    private int endHour;
    private int endMinute;


    Slot(String newSlotTime, String newSlotCategory) {
        slotTime = new Button(newSlotTime);

        startHour = (newSlotTime.charAt(0)-'0')*10 + (newSlotTime.charAt(1)-'0');
        startMinute = + (newSlotTime.charAt(3)-'0')*10 + newSlotTime.charAt(4)-'0';
        endHour = (newSlotTime.charAt(8)-'0')*10 + (newSlotTime.charAt(9)-'0');
        endMinute = + (newSlotTime.charAt(11)-'0')*10 + newSlotTime.charAt(12)-'0';

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

    public int getEndHour() {
        return endHour;
    }

    public int getEndMinute() {
        return endMinute;
    }

    public int getStartHour() {
        return startHour;
    }

    public int getStartMinute() {
        return startMinute;
    }

    public void setSlotTimeColor() {
        slotTime.setBackground(Color.getHSBColor(120, 39, 93));
    }
}
