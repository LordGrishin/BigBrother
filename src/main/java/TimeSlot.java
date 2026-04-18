import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class TimeSlot {
    private final LocalTime startTime;
    private final LocalTime endTime;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    public TimeSlot(LocalTime startTime, LocalTime endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public static TimeSlot fromString(String slotString) {
        String[] parts = slotString.split(" - ");
        LocalTime start = LocalTime.parse(parts[0].trim(), FORMATTER);
        LocalTime end = LocalTime.parse(parts[1].trim(), FORMATTER);
        return new TimeSlot(start, end);
    }

    public boolean isCurrent() {
        LocalTime now = LocalTime.now();
        return !now.isBefore(startTime) && now.isBefore(endTime);
    }

    public LocalTime getStartTime() { return startTime; }
    public LocalTime getEndTime() { return endTime; }
    public int getStartHour() { return startTime.getHour(); }
    public int getStartMinute() { return startTime.getMinute(); }
    public int getEndHour() { return endTime.getHour(); }
    public int getEndMinute() { return endTime.getMinute(); }

    @Override
    public String toString() {
        return startTime.format(FORMATTER) + " - " + endTime.format(FORMATTER);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        TimeSlot timeSlot = (TimeSlot) obj;
        return startTime.equals(timeSlot.startTime) && endTime.equals(timeSlot.endTime);
    }

    @Override
    public int hashCode() {
        return 31 * startTime.hashCode() + endTime.hashCode();
    }
}