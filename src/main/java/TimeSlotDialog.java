import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.logging.Logger;

public class TimeSlotDialog extends JFrame {
    private static final Logger logger = Logger.getLogger(TimeSlotDialog.class.getName());

    private JPanel slotsPanel;
    private Slot[] slotList;
    private JLabel timeLabel;
    private Timer updateTimer;
    private static TimeSlotDialog instance = null;

    public static TimeSlotDialog getInstance() {
        if (instance == null) {
            instance = new TimeSlotDialog();
        }
        return instance;
    }

    private TimeSlotDialog() {
        setTitle("Time Tracker");
        setSize(450, 600);
        setMinimumSize(new Dimension(400, 400));

        setApplicationIcon();
        positionWindowLeftOfCenter();

        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

        addWindowListener(new TimeSlotDialogAdapter());

        initComponents();
        startAutoUpdateTimer();
        refreshTable();
        setVisible(true);

        logger.info("TimeSlotDialog initialized");
    }

    private void startAutoUpdateTimer() {
        updateTimer = new Timer(60000, e -> {
            refreshTable();
            updateTimeLabel();
            checkSlotEnding();
        });
        updateTimer.start();
    }

    private void setApplicationIcon() {
        try {
            Image icon = null;

            // Сначала пробуем загрузить из файла рядом с JAR
            File iconFile = new File("eye.jpg");
            if (iconFile.exists()) {
                icon = Toolkit.getDefaultToolkit().getImage(iconFile.getAbsolutePath());
            } else {
                // Если нет - пробуем из ресурсов
                java.net.URL imgURL = getClass().getClassLoader().getResource("eye.jpg");
                if (imgURL != null) {
                    icon = Toolkit.getDefaultToolkit().getImage(imgURL);
                } else {
                    logger.warning("Icon not found, using default");
                    return;
                }
            }

            if (icon != null) {
                setIconImage(icon);
            }
        } catch (Exception e) {
            logger.warning("Could not load application icon: " + e.getMessage());
        }
    }

    private void positionWindowLeftOfCenter() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (screenSize.width - getWidth()) / 2 - 250;
        int y = (screenSize.height - getHeight()) / 2;
        setLocation(x, y);
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(ThemeColors.BACKGROUND);
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Заголовок
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Панель со слотами
        slotsPanel = new JPanel();
        slotsPanel.setLayout(new BoxLayout(slotsPanel, BoxLayout.Y_AXIS));
        slotsPanel.setBackground(ThemeColors.BACKGROUND);

        JScrollPane scrollPane = new JScrollPane(slotsPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(20);
        scrollPane.setBackground(ThemeColors.BACKGROUND);
        scrollPane.getViewport().setBackground(ThemeColors.BACKGROUND);

        scrollPane.getVerticalScrollBar().setUI(new CustomScrollBarUI());
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(10, 0));

        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Кнопки внизу
        JPanel bottomPanel = createBottomPanel();
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);
        loadSlots();
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(ThemeColors.BACKGROUND);
        headerPanel.setBorder(new EmptyBorder(0, 5, 15, 5));

        JLabel titleLabel = new JLabel("Today's Activity");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(ThemeColors.TEXT_PRIMARY);

        timeLabel = new JLabel();
        updateTimeLabel();
        timeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        timeLabel.setForeground(ThemeColors.TEXT_DIM);

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(timeLabel, BorderLayout.EAST);

        return headerPanel;
    }

    private void updateTimeLabel() {
        timeLabel.setText(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm")));
    }

    private JPanel createBottomPanel() {
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(ThemeColors.BACKGROUND);
        bottomPanel.setBorder(new EmptyBorder(15, 0, 0, 0));

        // Левая часть - кнопки History, Settings, Statistics
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        leftPanel.setOpaque(false);

        JButton historyButton = createHistoryButton();
        leftPanel.add(historyButton);

        JButton settingsButton = createSettingsButton();
        leftPanel.add(settingsButton);

        JButton statisticsButton = createStatisticsButton();
        leftPanel.add(statisticsButton);

        // Правая часть - кнопка Hide to Tray
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        rightPanel.setOpaque(false);

        JButton hideButton = createHideButton();
        rightPanel.add(hideButton);

        bottomPanel.add(leftPanel, BorderLayout.WEST);
        bottomPanel.add(rightPanel, BorderLayout.EAST);

        return bottomPanel;
    }

    private JButton createHistoryButton() {
        JButton historyButton = new JButton("History");
        historyButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        historyButton.setBackground(ThemeColors.BUTTON);
        historyButton.setForeground(ThemeColors.TEXT_SECONDARY);
        historyButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeColors.BORDER, 1, true),
                new EmptyBorder(8, 16, 8, 16)
        ));
        historyButton.setFocusPainted(false);
        historyButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        historyButton.setContentAreaFilled(false);
        historyButton.setOpaque(true);
        historyButton.setToolTipText("View and edit history");

        historyButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                historyButton.setBackground(ThemeColors.BUTTON_HOVER);
                historyButton.setForeground(ThemeColors.TEXT_PRIMARY);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                historyButton.setBackground(ThemeColors.BUTTON);
                historyButton.setForeground(ThemeColors.TEXT_SECONDARY);
            }
        });

        historyButton.addActionListener(e -> {
            HistoryDialog.getInstance().showDialog();
        });

        return historyButton;
    }

    private JButton createStatisticsButton() {
        JButton statisticsButton = new JButton("Statistics");
        statisticsButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statisticsButton.setBackground(ThemeColors.BUTTON);
        statisticsButton.setForeground(ThemeColors.TEXT_SECONDARY);
        statisticsButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeColors.BORDER, 1, true),
                new EmptyBorder(8, 16, 8, 16)
        ));
        statisticsButton.setFocusPainted(false);
        statisticsButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        statisticsButton.setContentAreaFilled(false);
        statisticsButton.setOpaque(true);
        statisticsButton.setToolTipText("View statistics");

        statisticsButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                statisticsButton.setBackground(ThemeColors.BUTTON_HOVER);
                statisticsButton.setForeground(ThemeColors.TEXT_PRIMARY);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                statisticsButton.setBackground(ThemeColors.BUTTON);
                statisticsButton.setForeground(ThemeColors.TEXT_SECONDARY);
            }
        });

        statisticsButton.addActionListener(e -> {
            // Открываем окно статистики
            StatisticsDialog.getInstance().showDialog();
        });

        return statisticsButton;
    }


    private JButton createSettingsButton() {
        JButton settingsButton = new JButton("Settings");
        settingsButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        settingsButton.setBackground(ThemeColors.BUTTON);
        settingsButton.setForeground(ThemeColors.TEXT_SECONDARY);
        settingsButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeColors.BORDER, 1, true),
                new EmptyBorder(8, 16, 8, 16)
        ));
        settingsButton.setFocusPainted(false);
        settingsButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        settingsButton.setContentAreaFilled(false);
        settingsButton.setOpaque(true);
        settingsButton.setToolTipText("Configure time slots");

        settingsButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                settingsButton.setBackground(ThemeColors.BUTTON_HOVER);
                settingsButton.setForeground(ThemeColors.TEXT_PRIMARY);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                settingsButton.setBackground(ThemeColors.BUTTON);
                settingsButton.setForeground(ThemeColors.TEXT_SECONDARY);
            }
        });

        settingsButton.addActionListener(e -> {
            TimeSlotSettingsDialog.getInstance().showDialog();
        });

        return settingsButton;
    }

    private JButton createHideButton() {
        JButton hideButton = new JButton("Hide to Tray");
        hideButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        hideButton.setBackground(ThemeColors.BUTTON);
        hideButton.setForeground(ThemeColors.BUTTON_TEXT);
        hideButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeColors.BORDER, 1, true),
                new EmptyBorder(8, 16, 8, 16)
        ));
        hideButton.setFocusPainted(false);
        hideButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        hideButton.setContentAreaFilled(false);
        hideButton.setOpaque(true);

        hideButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                hideButton.setBackground(ThemeColors.BUTTON_HOVER);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                hideButton.setBackground(ThemeColors.BUTTON);
            }
        });

        hideButton.addActionListener(e -> {
            setVisible(false);
            SystemTrayApp.getInstance();
        });

        return hideButton;
    }

    private void loadSlots() {
        DataStorage table = DataStorage.getInstance();
        List<TimeSlot> slots = table.getSlots();

        slotsPanel.removeAll();
        slotList = new Slot[slots.size()];

        for (int i = 0; i < slots.size(); i++) {
            TimeSlot timeSlot = slots.get(i);
            String category = table.getCategory(timeSlot);
            slotList[i] = new Slot(timeSlot, category);
            slotsPanel.add(slotList[i]);
            slotsPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        }

        slotsPanel.revalidate();
        slotsPanel.repaint();

        updateWindowSize();
    }

    private void updateWindowSize() {
        int slotCount = slotList.length;
        int newHeight = Math.min(600, 200 + slotCount * 68);
        setSize(getWidth(), newHeight);
    }

    void refreshTable() {
        DataStorage table = DataStorage.getInstance();
        List<TimeSlot> slots = table.getSlots();

        if (slotList == null || slotList.length != slots.size()) {
            loadSlots();
        }

        for (int i = 0; i < slots.size() && i < slotList.length; i++) {
            TimeSlot timeSlot = slots.get(i);
            slotList[i].setSlotCategory(table.getCategory(timeSlot));

            if (timeSlot.isCurrent()) {
                slotList[i].setCurrentSlot();
            } else {
                slotList[i].setNormalSlot();
            }
        }
        revalidate();
        repaint();
    }

    public void reloadSlots() {
        loadSlots();
        refreshTable();
    }

    @Override
    public void dispose() {
        if (updateTimer != null) {
            updateTimer.stop();
        }
        super.dispose();
    }
    private void checkSlotEnding() {
        DataStorage table = DataStorage.getInstance();
        TimeSlot currentSlot = table.getCurrentSlot();

        if (currentSlot != null) {
            LocalTime now = LocalTime.now();
            LocalTime slotEnd = currentSlot.getEndTime();

            // Проверяем, осталось ли 5 минут до конца слота
            if (now.plusMinutes(5).isAfter(slotEnd) && now.isBefore(slotEnd)) {
                // Проверяем, не показывали ли уже уведомление для этого слота
                String lastNotification = table.getLastNotificationForSlot(currentSlot.toString());
                if (lastNotification == null) {
                    showSlotEndingNotification(currentSlot);
                    table.setLastNotificationForSlot(currentSlot.toString(), "sent");
                }
            }
        }
    }
    private void showSlotEndingNotification(TimeSlot slot) {
        String category = DataStorage.getInstance().getCategory(slot);
        String message;

        if (category != null && !category.isEmpty()) {
            message = "Current slot ends in 5 minutes!\nYou were: " + category;
        } else {
            message = "Current slot ends in 5 minutes!\nDon't forget to log your activity!";
        }

        SystemTrayApp.getInstance().showNotification("Time Tracker", message);
    }
}