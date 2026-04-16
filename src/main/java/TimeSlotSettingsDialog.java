import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class TimeSlotSettingsDialog extends JDialog {

    private static TimeSlotSettingsDialog instance = null;
    private JTextField startHourField;
    private JTextField endHourField;
    private JLabel statusLabel;
    private JLabel currentSettingsLabel;

    public static TimeSlotSettingsDialog getInstance() {
        if (instance == null) {
            instance = new TimeSlotSettingsDialog();
        }
        return instance;
    }

    private TimeSlotSettingsDialog() {
        super((Frame) null, "Time Slot Settings", true);
        setSize(450, 480); // Увеличена высота с 350 до 480
        setMinimumSize(new Dimension(400, 450));
        setLocationRelativeTo(null);

        initComponents();
        setVisible(false);
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(ThemeColors.BACKGROUND);
        mainPanel.setBorder(new EmptyBorder(25, 25, 25, 25));

        // Заголовок
        JLabel titleLabel = new JLabel("Configure Time Slots");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(ThemeColors.TEXT_PRIMARY);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Пояснение
        JLabel descriptionLabel = new JLabel("Set working hours (24-hour format, e.g., 9 to 21)");
        descriptionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        descriptionLabel.setForeground(ThemeColors.TEXT_SECONDARY);
        descriptionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(descriptionLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 25)));

        // Панель для ввода начального часа
        JPanel startPanel = createTimeInputPanel("Start hour (0-23):", "start");
        startPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(startPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Панель для ввода конечного часа
        JPanel endPanel = createTimeInputPanel("End hour (0-23):", "end");
        endPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(endPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Текущие настройки
        currentSettingsLabel = new JLabel(getCurrentSettingsText());
        currentSettingsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        currentSettingsLabel.setForeground(ThemeColors.TEXT_DIM);
        currentSettingsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(currentSettingsLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Статус
        statusLabel = new JLabel(" ");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusLabel.setForeground(ThemeColors.CURRENT_SLOT);
        statusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(statusLabel);

        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Кнопка управления категориями
        JButton categoriesButton = new JButton("Manage Categories...");
        categoriesButton.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        categoriesButton.setBackground(ThemeColors.BUTTON);
        categoriesButton.setForeground(ThemeColors.TEXT_PRIMARY);
        categoriesButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeColors.BORDER, 1, true),
                new EmptyBorder(10, 24, 10, 24)
        ));
        categoriesButton.setFocusPainted(false);
        categoriesButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        categoriesButton.setContentAreaFilled(false);
        categoriesButton.setOpaque(true);
        categoriesButton.setAlignmentX(Component.LEFT_ALIGNMENT);

        categoriesButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                categoriesButton.setBackground(ThemeColors.BUTTON_HOVER);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                categoriesButton.setBackground(ThemeColors.BUTTON);
            }
        });

        categoriesButton.addActionListener(e -> {
            CategoryManagerDialog.getInstance().showDialog();
        });

        mainPanel.add(categoriesButton);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 25)));

        // Кнопки Cancel и Apply
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(ThemeColors.BACKGROUND);
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton cancelButton = createButton("Cancel", ThemeColors.BUTTON);
        cancelButton.addActionListener(e -> setVisible(false));

        JButton applyButton = createButton("Apply", ThemeColors.CURRENT_SLOT);
        applyButton.addActionListener(e -> applySettings());

        buttonPanel.add(cancelButton);
        buttonPanel.add(applyButton);

        mainPanel.add(buttonPanel);

        // Добавляем растягивающееся пространство внизу
        mainPanel.add(Box.createVerticalGlue());

        add(mainPanel);
    }

    private JPanel createTimeInputPanel(String labelText, String fieldName) {
        JPanel panel = new JPanel(new BorderLayout(10, 0));
        panel.setBackground(ThemeColors.BACKGROUND);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        label.setForeground(ThemeColors.TEXT_SECONDARY);
        label.setPreferredSize(new Dimension(150, 35));

        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBackground(ThemeColors.BUTTON);
        field.setForeground(ThemeColors.TEXT_PRIMARY);
        field.setCaretColor(ThemeColors.TEXT_PRIMARY);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeColors.BORDER, 1, true),
                new EmptyBorder(8, 12, 8, 12)
        ));
        field.setHorizontalAlignment(SwingConstants.CENTER);
        field.setPreferredSize(new Dimension(80, 35));

        JPanel fieldWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        fieldWrapper.setOpaque(false);
        fieldWrapper.add(field);

        if (fieldName.equals("start")) {
            startHourField = field;
            startHourField.setText(String.valueOf(DataStorage.getInstance().getStartHour()));
        } else {
            endHourField = field;
            endHourField.setText(String.valueOf(DataStorage.getInstance().getEndHour()));
        }

        panel.add(label, BorderLayout.WEST);
        panel.add(fieldWrapper, BorderLayout.CENTER);

        return panel;
    }

    private JButton createButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        button.setBackground(bgColor);
        button.setForeground(ThemeColors.TEXT_PRIMARY);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeColors.BORDER, 1, true),
                new EmptyBorder(10, 24, 10, 24)
        ));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setContentAreaFilled(false);
        button.setOpaque(true);

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(ThemeColors.BUTTON_HOVER);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });

        return button;
    }

    private String getCurrentSettingsText() {
        int start = DataStorage.getInstance().getStartHour();
        int end = DataStorage.getInstance().getEndHour();
        return String.format("Current settings: %02d:00 - %02d:00 (%d hours)",
                start, end, end - start);
    }

    private void applySettings() {
        try {
            int startHour = Integer.parseInt(startHourField.getText().trim());
            int endHour = Integer.parseInt(endHourField.getText().trim());

            if (startHour < 0 || startHour > 23 || endHour < 0 || endHour > 23) {
                statusLabel.setText("Error: Hours must be between 0 and 23");
                statusLabel.setForeground(ThemeColors.CLOSE_BUTTON_HOVER);
                return;
            }

            if (startHour >= endHour) {
                statusLabel.setText("Error: Start hour must be less than end hour");
                statusLabel.setForeground(ThemeColors.CLOSE_BUTTON_HOVER);
                return;
            }

            DataStorage.getInstance().updateTimeSlots(startHour, endHour);
            TimeSlotDialog.getInstance().reloadSlots();

            statusLabel.setText("Settings applied successfully!");
            statusLabel.setForeground(ThemeColors.CURRENT_SLOT);

            currentSettingsLabel.setText(getCurrentSettingsText());

            Timer timer = new Timer(1500, e -> setVisible(false));
            timer.setRepeats(false);
            timer.start();

        } catch (NumberFormatException e) {
            statusLabel.setText("Error: Please enter valid numbers");
            statusLabel.setForeground(ThemeColors.CLOSE_BUTTON_HOVER);
        }
    }

    public void showDialog() {
        startHourField.setText(String.valueOf(DataStorage.getInstance().getStartHour()));
        endHourField.setText(String.valueOf(DataStorage.getInstance().getEndHour()));
        currentSettingsLabel.setText(getCurrentSettingsText());
        statusLabel.setText(" ");
        setVisible(true);
    }
}