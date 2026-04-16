import java.io.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.logging.Logger;

public class DataStorage {
    private static final Logger logger = Logger.getLogger(DataStorage.class.getName());
    private static final String DATA_DIR = "data";
    private static final String CATEGORIES_FILE = "categories.properties";
    private static final String SETTINGS_FILE = "timetracker_settings.properties";

    private List<TimeSlot> slots = new ArrayList<>();
    private List<String> categories = new ArrayList<>();
    private Map<TimeSlot, String> todayData = new HashMap<>();
    private Map<String, String> notificationSent = new HashMap<>();

    private int startHour = 9;
    private int endHour = 21;
    private LocalDate currentDate;

    private static DataStorage instance = null;

    public static DataStorage getInstance() {
        if (instance == null) {
            instance = new DataStorage();
        }
        return instance;
    }

    private DataStorage() {
        createDataDirectory();
        loadSettingsFromFile();
        initializeSlots();
        loadCategoriesFromFile();

        // Загружаем данные за сегодня
        currentDate = LocalDate.now();
        loadTodayData();

        logger.info("DataStorage initialized for date: " + currentDate);
    }

    private void createDataDirectory() {
        File dataDir = new File(DATA_DIR);
        if (!dataDir.exists()) {
            dataDir.mkdirs();
            logger.info("Created data directory: " + dataDir.getAbsolutePath());
        }
    }

    private String getTodayFileName() {
        return DATA_DIR + "/" + currentDate.toString() + ".properties";
    }

    private String getFileNameForDate(LocalDate date) {
        return DATA_DIR + "/" + date.toString() + ".properties";
    }

    private void loadSettingsFromFile() {
        File file = new File(SETTINGS_FILE);
        if (!file.exists()) {
            startHour = 9;
            endHour = 21;
            saveSettingsToFile();
            logger.info("Created default settings file");
            return;
        }

        Properties props = new Properties();
        try (FileInputStream in = new FileInputStream(file)) {
            props.load(in);
            startHour = Integer.parseInt(props.getProperty("startHour", "9"));
            endHour = Integer.parseInt(props.getProperty("endHour", "21"));
            logger.info("Settings loaded: " + startHour + ":00 - " + endHour + ":00");
        } catch (IOException | NumberFormatException e) {
            logger.warning("Failed to load settings: " + e.getMessage());
            startHour = 9;
            endHour = 21;
        }
    }

    private void saveSettingsToFile() {
        Properties props = new Properties();
        props.setProperty("startHour", String.valueOf(startHour));
        props.setProperty("endHour", String.valueOf(endHour));

        try (FileOutputStream out = new FileOutputStream(SETTINGS_FILE)) {
            props.store(out, "Time Tracker Settings");
            logger.fine("Settings saved");
        } catch (IOException e) {
            logger.warning("Failed to save settings: " + e.getMessage());
        }
    }

    private void initializeSlots() {
        slots.clear();
        for (int hour = startHour; hour < endHour; hour++) {
            LocalTime start = LocalTime.of(hour, 0);
            LocalTime end = LocalTime.of(hour + 1, 0);
            slots.add(new TimeSlot(start, end));
        }
        logger.info("Initialized " + slots.size() + " time slots");
    }

    private void loadCategoriesFromFile() {
        File file = new File(CATEGORIES_FILE);

        if (!file.exists()) {
            createDefaultCategoriesFile();
        }

        categories.clear();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty() && !line.startsWith("#")) {
                    categories.add(line);
                }
            }

            if (categories.isEmpty()) {
                addDefaultCategories();
            }

            logger.info("Loaded " + categories.size() + " categories");
        } catch (IOException e) {
            logger.warning("Failed to load categories: " + e.getMessage());
            addDefaultCategories();
        }
    }

    private void createDefaultCategoriesFile() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(CATEGORIES_FILE))) {
            writer.println("# Categories for Time Tracker");
            writer.println("doing homework");
            writer.println("watching TV Shows");
            writer.println("watching youtube");
            writer.println("watching youtube shorts");
            writer.println("listening music");
            writer.println("having shower");
            writer.println("doing activities");
            writer.println("doing nothing");
            writer.println("chatting in telegram");
            logger.info("Created default categories file");
        } catch (IOException e) {
            logger.warning("Failed to create categories file: " + e.getMessage());
        }
    }

    private void addDefaultCategories() {
        categories.clear();
        categories.add("doing homework");
        categories.add("watching TV Shows");
        categories.add("watching youtube");
        categories.add("watching youtube shorts");
        categories.add("listening music");
        categories.add("having shower");
        categories.add("doing activities");
        categories.add("doing nothing");
        categories.add("chatting in telegram");
        saveCategoriesToFile();
        logger.info("Added default categories");
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

    private void loadTodayData() {
        File file = new File(getTodayFileName());
        todayData.clear();

        if (!file.exists()) {
            logger.info("No data file for today, starting fresh");
            return;
        }

        Properties props = new Properties();
        try (FileInputStream in = new FileInputStream(file)) {
            props.load(in);

            for (String key : props.stringPropertyNames()) {
                try {
                    TimeSlot slot = TimeSlot.fromString(key);
                    String category = props.getProperty(key);
                    if (!category.isEmpty()) {
                        todayData.put(slot, category);
                    }
                } catch (Exception e) {
                    logger.warning("Failed to parse slot: " + key);
                }
            }
            logger.info("Loaded " + todayData.size() + " entries for today");
        } catch (IOException e) {
            logger.warning("Failed to load today's data: " + e.getMessage());
        }
    }

    private void saveTodayData() {
        Properties props = new Properties();
        for (Map.Entry<TimeSlot, String> entry : todayData.entrySet()) {
            if (!entry.getValue().isEmpty()) {
                props.setProperty(entry.getKey().toString(), entry.getValue());
            }
        }

        try (FileOutputStream out = new FileOutputStream(getTodayFileName())) {
            props.store(out, "Time Tracker - " + currentDate);
            logger.fine("Today's data saved to: " + getTodayFileName());
        } catch (IOException e) {
            logger.warning("Failed to save today's data: " + e.getMessage());
        }
    }

    public void checkAndReloadIfNewDay() {
        LocalDate today = LocalDate.now();
        if (!today.equals(currentDate)) {
            logger.info("New day detected! Switching from " + currentDate + " to " + today);
            currentDate = today;
            todayData.clear();
            notificationSent.clear();
            loadTodayData();
        }
    }

    public String getCategory(TimeSlot slot) {
        return todayData.getOrDefault(slot, "");
    }

    public String getCategory(String slotString) {
        TimeSlot slot = TimeSlot.fromString(slotString);
        return getCategory(slot);
    }

    public void setCategory(TimeSlot slot, String category) {
        checkAndReloadIfNewDay();
        todayData.put(slot, category);
        saveTodayData();
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

    public int getStartHour() {
        return startHour;
    }

    public int getEndHour() {
        return endHour;
    }

    public void updateTimeSlots(int newStartHour, int newEndHour) {
        this.startHour = newStartHour;
        this.endHour = newEndHour;
        saveSettingsToFile();

        Map<String, String> oldData = new HashMap<>();
        for (Map.Entry<TimeSlot, String> entry : todayData.entrySet()) {
            oldData.put(entry.getKey().toString(), entry.getValue());
        }

        slots.clear();
        todayData.clear();

        for (int hour = startHour; hour < endHour; hour++) {
            LocalTime start = LocalTime.of(hour, 0);
            LocalTime end = LocalTime.of(hour + 1, 0);
            TimeSlot newSlot = new TimeSlot(start, end);
            slots.add(newSlot);

            String category = oldData.get(newSlot.toString());
            if (category != null && !category.isEmpty()) {
                todayData.put(newSlot, category);
            }
        }

        saveTodayData();
        logger.info("Time slots updated: " + startHour + ":00 - " + endHour + ":00");
    }

    public String getLastNotificationForSlot(String slotString) {
        return notificationSent.get(slotString);
    }

    public void setLastNotificationForSlot(String slotString, String status) {
        notificationSent.put(slotString, status);
    }

    // Методы для статистики (будут использоваться позже)
    public List<LocalDate> getAvailableDates() {
        List<LocalDate> dates = new ArrayList<>();
        File dataDir = new File(DATA_DIR);
        File[] files = dataDir.listFiles((dir, name) -> name.endsWith(".properties"));

        if (files != null) {
            for (File file : files) {
                String name = file.getName().replace(".properties", "");
                try {
                    dates.add(LocalDate.parse(name));
                } catch (Exception e) {
                    // Пропускаем файлы с неправильным именем
                }
            }
        }
        dates.sort(Collections.reverseOrder());
        return dates;
    }

    public Map<TimeSlot, String> getDataForDate(LocalDate date) {
        Map<TimeSlot, String> result = new HashMap<>();
        File file = new File(getFileNameForDate(date));

        if (!file.exists()) {
            return result;
        }

        Properties props = new Properties();
        try (FileInputStream in = new FileInputStream(file)) {
            props.load(in);
            for (String key : props.stringPropertyNames()) {
                try {
                    TimeSlot slot = TimeSlot.fromString(key);
                    result.put(slot, props.getProperty(key));
                } catch (Exception e) {
                    // Пропускаем
                }
            }
        } catch (IOException e) {
            logger.warning("Failed to load data for date " + date);
        }
        return result;
    }
    // Методы для работы с историей
    public void setCategoryForDate(LocalDate date, TimeSlot slot, String category) {
        Map<TimeSlot, String> dayData = getDataForDate(date);
        dayData.put(slot, category);
        saveDataForDate(date, dayData);
        logger.info("Category set for " + date + ": " + slot + " -> " + category);
    }

    public void setCategoryForDate(LocalDate date, String slotString, String category) {
        TimeSlot slot = TimeSlot.fromString(slotString);
        setCategoryForDate(date, slot, category);
    }

    private void saveDataForDate(LocalDate date, Map<TimeSlot, String> data) {
        Properties props = new Properties();
        for (Map.Entry<TimeSlot, String> entry : data.entrySet()) {
            if (!entry.getValue().isEmpty()) {
                props.setProperty(entry.getKey().toString(), entry.getValue());
            }
        }

        try (FileOutputStream out = new FileOutputStream(getFileNameForDate(date))) {
            props.store(out, "Time Tracker - " + date);
            logger.fine("Data saved for date: " + date);
        } catch (IOException e) {
            logger.warning("Failed to save data for date " + date + ": " + e.getMessage());
        }
    }
}