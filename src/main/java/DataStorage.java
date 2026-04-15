import java.io.*;
import java.time.LocalTime;
import java.util.*;
import java.util.logging.Logger;

public class DataStorage {
    private static final Logger logger = Logger.getLogger(DataStorage.class.getName());
    private static final String DATA_FILE = "timetracker_data.properties";
    private static final String CATEGORIES_FILE = "categories.properties";

    private List<TimeSlot> slots = new ArrayList<>();
    private List<String> categories = new ArrayList<>();
    private Map<TimeSlot, String> data = new HashMap<>();

    private static DataStorage instance = null;

    public static DataStorage getInstance() {
        if (instance == null) {
            instance = new DataStorage();
        }
        return instance;
    }

    private DataStorage() {
        initializeSlots();
        loadCategoriesFromFile();
        loadFromFile();
    }

    private void initializeSlots() {
        for (int hour = 9; hour < 21; hour++) {
            LocalTime start = LocalTime.of(hour, 0);
            LocalTime end = LocalTime.of(hour + 1, 0);
            slots.add(new TimeSlot(start, end));
        }
    }

    private void loadCategoriesFromFile() {
        File file = new File(CATEGORIES_FILE);

        // Если файла нет - создаём пустой
        if (!file.exists()) {
            try {
                file.createNewFile();
                logger.info("Created empty categories file: " + CATEGORIES_FILE);
            } catch (IOException e) {
                logger.warning("Failed to create categories file: " + e.getMessage());
            }
        }

        categories.clear();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                // Пропускаем пустые строки и комментарии
                if (!line.isEmpty() && !line.startsWith("#")) {
                    categories.add(line);
                }
            }
            logger.info("Loaded " + categories.size() + " categories from file");
        } catch (IOException e) {
            logger.warning("Failed to load categories: " + e.getMessage());
        }
    }

    public String getCategory(TimeSlot slot) {
        return data.getOrDefault(slot, "");
    }

    public String getCategory(String slotString) {
        TimeSlot slot = TimeSlot.fromString(slotString);
        return getCategory(slot);
    }

    public void setCategory(TimeSlot slot, String category) {
        data.put(slot, category);
        saveToFile();
        logger.fine("Category set: " + slot + " -> " + category);
    }

    public void setCategory(String slotString, String category) {
        TimeSlot slot = TimeSlot.fromString(slotString);
        setCategory(slot, category);
    }

    public List<TimeSlot> getSlots() {
        return new ArrayList<>(slots);
    }

    public List<String> getSlotStrings() {
        List<String> slotStrings = new ArrayList<>();
        for (TimeSlot slot : slots) {
            slotStrings.add(slot.toString());
        }
        return slotStrings;
    }

    public List<String> getCategories() {
        return new ArrayList<>(categories);
    }

    public void addCategory(String category) {
        if (!categories.contains(category)) {
            categories.add(category);
            saveCategoriesToFile();
            logger.info("Category added: " + category);
        }
    }

    public void removeCategory(String category) {
        if (categories.remove(category)) {
            saveCategoriesToFile();
            logger.info("Category removed: " + category);
        }
    }

    private void saveCategoriesToFile() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(CATEGORIES_FILE))) {
            for (String category : categories) {
                writer.println(category);
            }
            logger.fine("Categories saved to file");
        } catch (IOException e) {
            logger.warning("Failed to save categories: " + e.getMessage());
        }
    }

    public void reloadCategories() {
        loadCategoriesFromFile();
        logger.info("Categories reloaded from file");
    }

    public TimeSlot getCurrentSlot() {
        for (TimeSlot slot : slots) {
            if (slot.isCurrent()) {
                return slot;
            }
        }
        return null;
    }

    private void saveToFile() {
        Properties props = new Properties();
        for (Map.Entry<TimeSlot, String> entry : data.entrySet()) {
            if (!entry.getValue().isEmpty()) {
                props.setProperty(entry.getKey().toString(), entry.getValue());
            }
        }

        try (FileOutputStream out = new FileOutputStream(DATA_FILE)) {
            props.store(out, "Time Tracker Data");
            logger.fine("Data saved to file");
        } catch (IOException e) {
            logger.warning("Failed to save data: " + e.getMessage());
        }
    }

    private void loadFromFile() {
        File file = new File(DATA_FILE);
        if (!file.exists()) {
            logger.info("No saved data file found");
            return;
        }

        Properties props = new Properties();
        try (FileInputStream in = new FileInputStream(file)) {
            props.load(in);

            for (String key : props.stringPropertyNames()) {
                try {
                    TimeSlot slot = TimeSlot.fromString(key);
                    String category = props.getProperty(key);
                    data.put(slot, category);
                } catch (Exception e) {
                    logger.warning("Failed to parse slot: " + key);
                }
            }
            logger.info("Data loaded from file");
        } catch (IOException e) {
            logger.warning("Failed to load data: " + e.getMessage());
        }
    }

    public void clearAllData() {
        data.clear();
        File file = new File(DATA_FILE);
        if (file.exists()) {
            file.delete();
        }
        logger.info("All data cleared");
    }
    public int getStartHour() {
        if (slots.isEmpty()) return 9;
        return slots.get(0).getStartHour();
    }

    public int getEndHour() {
        if (slots.isEmpty()) return 21;
        return slots.get(slots.size() - 1).getEndHour();
    }

    public void updateTimeSlots(int startHour, int endHour) {
        // Сохраняем старые данные
        Map<String, String> oldData = new HashMap<>();
        for (Map.Entry<TimeSlot, String> entry : data.entrySet()) {
            oldData.put(entry.getKey().toString(), entry.getValue());
        }

        // Очищаем слоты
        slots.clear();
        data.clear();

        // Создаём новые слоты
        for (int hour = startHour; hour < endHour; hour++) {
            LocalTime start = LocalTime.of(hour, 0);
            LocalTime end = LocalTime.of(hour + 1, 0);
            TimeSlot newSlot = new TimeSlot(start, end);
            slots.add(newSlot);

            // Восстанавливаем данные, если такая категория была
            String category = oldData.get(newSlot.toString());
            if (category != null && !category.isEmpty()) {
                data.put(newSlot, category);
            }
        }

        saveToFile();
        logger.info("Time slots updated: " + startHour + ":00 - " + endHour + ":00");
    }
}