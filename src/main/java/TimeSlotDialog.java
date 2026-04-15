import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
        setUndecorated(true);

        initComponents();
        addWindowListener(new TimeSlotDialogAdapter());

        startAutoUpdateTimer();

        refreshTable();
        setVisible(true);

        logger.info("TimeSlotDialog initialized");
    }

    private void startAutoUpdateTimer() {
        updateTimer = new Timer(60000, e -> {
            refreshTable();
            updateTimeLabel();
        });
        updateTimer.start();
    }

    private void setApplicationIcon() {
        try {
            Image icon = Toolkit.getDefaultToolkit().getImage("src/main/resources/eye.jpg");
            setIconImage(icon);
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

        JPanel titleBar = createTitleBar();
        mainPanel.add(titleBar, BorderLayout.NORTH);

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(ThemeColors.BACKGROUND);
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel headerPanel = createHeaderPanel();
        contentPanel.add(headerPanel, BorderLayout.NORTH);

        slotsPanel = new JPanel();
        slotsPanel.setLayout(new BoxLayout(slotsPanel, BoxLayout.Y_AXIS));
        slotsPanel.setBackground(ThemeColors.BACKGROUND);

        JScrollPane scrollPane = new JScrollPane(slotsPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBackground(ThemeColors.BACKGROUND);
        scrollPane.getViewport().setBackground(ThemeColors.BACKGROUND);

        scrollPane.getVerticalScrollBar().setUI(new CustomScrollBarUI());
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(10, 0));

        contentPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = createBottomPanel();
        contentPanel.add(bottomPanel, BorderLayout.SOUTH);

        mainPanel.add(contentPanel, BorderLayout.CENTER);

        add(mainPanel);
        loadSlots();
    }

    private JPanel createTitleBar() {
        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setBackground(ThemeColors.TITLE_BAR);
        titleBar.setPreferredSize(new Dimension(getWidth(), 35));

        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.X_AXIS));
        leftPanel.setOpaque(false);
        leftPanel.setBorder(new EmptyBorder(0, 10, 0, 0));

        try {
            Image icon = Toolkit.getDefaultToolkit().getImage("src/main/resources/eye.jpg");
            Image scaledIcon = icon.getScaledInstance(20, 20, Image.SCALE_SMOOTH);
            JLabel iconLabel = new JLabel(new ImageIcon(scaledIcon));
            iconLabel.setAlignmentY(Component.CENTER_ALIGNMENT);
            leftPanel.add(iconLabel);
            leftPanel.add(Box.createRigidArea(new Dimension(8, 0)));
        } catch (Exception e) {
            // Icon not found, continue without it
        }

        JLabel titleLabel = new JLabel("Time Tracker");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        titleLabel.setForeground(ThemeColors.TEXT_PRIMARY);
        titleLabel.setAlignmentY(Component.CENTER_ALIGNMENT);
        leftPanel.add(titleLabel);

        JButton closeButton = createCloseButton();

        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setOpaque(false);
        rightPanel.add(Box.createVerticalGlue());

        JPanel closeButtonWrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        closeButtonWrapper.setOpaque(false);
        closeButtonWrapper.setMaximumSize(new Dimension(45, 35));
        closeButtonWrapper.add(closeButton);

        rightPanel.add(closeButtonWrapper);
        rightPanel.add(Box.createVerticalGlue());

        addDragListener(titleBar);

        titleBar.add(leftPanel, BorderLayout.WEST);
        titleBar.add(rightPanel, BorderLayout.EAST);

        return titleBar;
    }

    private JButton createCloseButton() {
        JButton closeButton = new JButton("X");
        closeButton.setFont(new Font("Arial", Font.BOLD, 16));
        closeButton.setForeground(ThemeColors.TEXT_SECONDARY);
        closeButton.setPreferredSize(new Dimension(45, 35));
        closeButton.setMaximumSize(new Dimension(45, 35));
        closeButton.setFocusPainted(false);
        closeButton.setBorderPainted(false);
        closeButton.setContentAreaFilled(false);
        closeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeButton.setAlignmentY(Component.CENTER_ALIGNMENT);

        closeButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                closeButton.setBackground(ThemeColors.CLOSE_BUTTON_HOVER);
                closeButton.setForeground(Color.WHITE);
                closeButton.setContentAreaFilled(true);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                closeButton.setBackground(ThemeColors.TITLE_BAR);
                closeButton.setForeground(ThemeColors.TEXT_SECONDARY);
                closeButton.setContentAreaFilled(false);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                setVisible(false);
                SystemTrayApp.getInstance();
            }
        });

        return closeButton;
    }

    private void addDragListener(JPanel titleBar) {
        MouseAdapter dragAdapter = new MouseAdapter() {
            private Point initialClick;

            @Override
            public void mousePressed(MouseEvent e) {
                initialClick = e.getPoint();
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                int thisX = getLocation().x;
                int thisY = getLocation().y;

                int xMoved = e.getX() - initialClick.x;
                int yMoved = e.getY() - initialClick.y;

                setLocation(thisX + xMoved, thisY + yMoved);
            }
        };

        titleBar.addMouseListener(dragAdapter);
        titleBar.addMouseMotionListener(dragAdapter);
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

        // Левая часть - кнопки Categories и Settings
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        leftPanel.setOpaque(false);

        JButton categoriesButton = createCategoriesButton();
        leftPanel.add(categoriesButton);

        JButton settingsButton = createSettingsButton();
        leftPanel.add(settingsButton);

        // Правая часть - кнопка Hide to Tray
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        rightPanel.setOpaque(false);

        JButton hideButton = createHideButton();
        rightPanel.add(hideButton);

        bottomPanel.add(leftPanel, BorderLayout.WEST);
        bottomPanel.add(rightPanel, BorderLayout.EAST);

        return bottomPanel;
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

    private JButton createCategoriesButton() {
        JButton categoriesButton = new JButton("Categories");
        categoriesButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        categoriesButton.setBackground(ThemeColors.BUTTON);
        categoriesButton.setForeground(ThemeColors.TEXT_SECONDARY);
        categoriesButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeColors.BORDER, 1, true),
                new EmptyBorder(8, 16, 8, 16)
        ));
        categoriesButton.setFocusPainted(false);
        categoriesButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        categoriesButton.setContentAreaFilled(false);
        categoriesButton.setOpaque(true);
        categoriesButton.setToolTipText("Manage Categories");

        categoriesButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                categoriesButton.setBackground(ThemeColors.BUTTON_HOVER);
                categoriesButton.setForeground(ThemeColors.TEXT_PRIMARY);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                categoriesButton.setBackground(ThemeColors.BUTTON);
                categoriesButton.setForeground(ThemeColors.TEXT_SECONDARY);
            }
        });

        categoriesButton.addActionListener(e -> {
            CategoryManagerDialog.getInstance().showDialog();
        });

        return categoriesButton;
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

        // Обновляем размер окна в зависимости от количества слотов
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

        // Проверяем, изменилось ли количество слотов
        if (slotList == null || slotList.length != slots.size()) {
            // Если количество изменилось - полностью перезагружаем слоты
            loadSlots();
        }

        // Обновляем отображение для существующих слотов
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
}