import java.awt.*;
import java.time.LocalTime;

public class Main {

    public static void main(String[] args) {
        System.out.printf("Main: started at %d:%d:%d", LocalTime.now().getHour(), LocalTime.now().getMinute(), LocalTime.now().getSecond());
        TimeSlotDialog dialog = TimeSlotDialog.getInstance();
    }
}