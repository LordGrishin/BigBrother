import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

public class HistoryDialog extends JDialog {

    private static HistoryDialog instance = null;
    private JPanel datesPanel;
    private JLabel selectedDateLabel;
    private JPanel slotsPanel;
    private LocalDate selectedDate = null;

    public static HistoryDialog getInstance() {
        if (instance == null) {
            instance = new HistoryDialog();
        }
        return instance;
    }

    private HistoryDialog() {
        super((Frame) null, "History", true);
        setSize(700, 600);
        setMinimumSize(new Dimension(600, 500));
        setLocationRelativeTo(null);
        initComponents();
        setVisible(false);
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(ThemeColors.BACKGROUND);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Activity History");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(ThemeColors.TEXT_PRIMARY);
        titleLabel.setBorder(new EmptyBorder(0, 0, 15, 0));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(250);
        splitPane.setDividerSize(2);
        splitPane.setBorder(null);

        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBackground(ThemeColors.BACKGROUND);

        JLabel datesHeader = new JLabel("Available days:");
        datesHeader.setFont(new Font("Segoe UI", Font.BOLD, 13));
        datesHeader.setForeground(ThemeColors.TEXT_SECONDARY);
        datesHeader.setBorder(new EmptyBorder(0, 0, 10, 0));
        leftPanel.add(datesHeader, BorderLayout.NORTH);

        datesPanel = new JPanel();
        datesPanel.setLayout(new BoxLayout(datesPanel, BoxLayout.Y_AXIS));
        datesPanel.setBackground(ThemeColors.BACKGROUND);

        JScrollPane datesScrollPane = new JScrollPane(datesPanel);
        datesScrollPane.setBorder(BorderFactory.createEmptyBorder());
        datesScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        datesScrollPane.getVerticalScrollBar().setUnitIncrement(30);
        datesScrollPane.setBackground(ThemeColors.BACKGROUND);
        datesScrollPane.getViewport().setBackground(ThemeColors.BACKGROUND);
        datesScrollPane.getVerticalScrollBar().setUI(new CustomScrollBarUI());
        datesScrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(10, 0));

        leftPanel.add(datesScrollPane, BorderLayout.CENTER);

        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBackground(ThemeColors.BACKGROUND);
        rightPanel.setBorder(new EmptyBorder(0, 15, 0, 0));

        selectedDateLabel = new JLabel("Select a date to view/edit");
        selectedDateLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        selectedDateLabel.setForeground(ThemeColors.TEXT_DIM);
        selectedDateLabel.setBorder(new EmptyBorder(0, 0, 15, 0));
        rightPanel.add(selectedDateLabel, BorderLayout.NORTH);

        slotsPanel = new JPanel();
        slotsPanel.setLayout(new BoxLayout(slotsPanel, BoxLayout.Y_AXIS));
        slotsPanel.setBackground(ThemeColors.BACKGROUND);

        JScrollPane slotsScrollPane = new JScrollPane(slotsPanel);
        slotsScrollPane.setBorder(BorderFactory.createEmptyBorder());
        slotsScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        slotsScrollPane.getVerticalScrollBar().setUnitIncrement(30);
        slotsScrollPane.setBackground(ThemeColors.BACKGROUND);
        slotsScrollPane.getViewport().setBackground(ThemeColors.BACKGROUND);
        slotsScrollPane.getVerticalScrollBar().setUI(new CustomScrollBarUI());
        slotsScrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(10, 0));

        rightPanel.add(slotsScrollPane, BorderLayout.CENTER);

        splitPane.setLeftComponent(leftPanel);
        splitPane.setRightComponent(rightPanel);

        mainPanel.add(splitPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setBackground(ThemeColors.BACKGROUND);
        bottomPanel.setBorder(new EmptyBorder(15, 0, 0, 0));

        JButton closeButton = createButton("Close", ThemeColors.BUTTON);
        closeButton.addActionListener(e -> setVisible(false));
        bottomPanel.add(closeButton);

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);
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

    private void loadAvailableDates() {
        datesPanel.removeAll();
        List<LocalDate> dates = DataStorage.getInstance().getAvailableDates();

        LocalDate today = LocalDate.now();
        boolean hasToday = dates.contains(today);
        if (!hasToday) {
            dates.add(0, today);
        }

        for (LocalDate date : dates) {
            datesPanel.add(createDateRow(date, date.equals(today) && !hasToday ? " (no data yet)" : ""));
            datesPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        }

        datesPanel.revalidate();
        datesPanel.repaint();
    }

    private JPanel createDateRow(LocalDate date, String suffix) {
        JPanel row = new JPanel(new BorderLayout(5, 0));
        row.setBackground(ThemeColors.BUTTON);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        row.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeColors.BORDER, 1, true),
                new EmptyBorder(8, 15, 8, 15)
        ));
        row.setCursor(new Cursor(Cursor.HAND_CURSOR));

        String dateText = date.format(DateTimeFormatter.ofPattern("MMM d, yyyy"));
        dateText = dateText.substring(0, 1).toUpperCase() + dateText.substring(1);

        final String finalDateText = dateText + suffix;

        JLabel dateLabel = new JLabel(finalDateText);
        dateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        dateLabel.setForeground(suffix.isEmpty() ? ThemeColors.TEXT_PRIMARY : ThemeColors.TEXT_DIM);

        if (date.equals(LocalDate.now())) {
            JLabel todayLabel = new JLabel("(Today)");
            todayLabel.setFont(new Font("Segoe UI", Font.ITALIC, 11));
            todayLabel.setForeground(ThemeColors.TEXT_PRIMARY);
            row.add(todayLabel, BorderLayout.EAST);
        }

        row.add(dateLabel, BorderLayout.WEST);

        row.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                row.setBackground(ThemeColors.BUTTON_HOVER);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                row.setBackground(ThemeColors.BUTTON);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                selectDate(date);
            }
        });

        return row;
    }

    private void selectDate(LocalDate date) {
        this.selectedDate = date;
        String displayDate = date.format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy"));
        displayDate = displayDate.substring(0, 1).toUpperCase() + displayDate.substring(1);
        selectedDateLabel.setText("Editing: " + displayDate);
        selectedDateLabel.setForeground(ThemeColors.TEXT_PRIMARY);

        loadSlotsForDate(date);
    }

    private void loadSlotsForDate(LocalDate date) {
        slotsPanel.removeAll();

        DataStorage storage = DataStorage.getInstance();
        List<TimeSlot> slots = storage.getSlots();
        Map<TimeSlot, String> dayData = storage.getDataForDate(date);

        for (TimeSlot slot : slots) {
            String category = dayData.getOrDefault(slot, "");
            HistorySlot historySlot = new HistorySlot(slot, category, date);
            slotsPanel.add(historySlot);
            slotsPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        }

        slotsPanel.revalidate();
        slotsPanel.repaint();
    }

    public void showDialog() {
        loadAvailableDates();
        slotsPanel.removeAll();
        selectedDateLabel.setText("Select a date to view/edit");
        selectedDateLabel.setForeground(ThemeColors.TEXT_DIM);
        selectedDate = null;
        setVisible(true);
    }

    private class HistorySlot extends JPanel {
        private final TimeSlot timeSlot;
        private final LocalDate date;
        private JLabel categoryLabel;

        public HistorySlot(TimeSlot timeSlot, String category, LocalDate date) {
            this.timeSlot = timeSlot;
            this.date = date;

            setLayout(new BorderLayout());
            setBackground(ThemeColors.BACKGROUND);
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 55));
            setPreferredSize(new Dimension(0, 55));
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, ThemeColors.TITLE_BAR));

            JLabel timeLabel = new JLabel(timeSlot.toString());
            timeLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
            timeLabel.setForeground(ThemeColors.BUTTON_TEXT);
            timeLabel.setPreferredSize(new Dimension(120, 35));

            JPanel timePanel = new JPanel(new GridBagLayout());
            timePanel.setOpaque(false);
            timePanel.add(timeLabel);

            JPanel westPanel = new JPanel(new BorderLayout());
            westPanel.setOpaque(false);
            westPanel.setPreferredSize(new Dimension(140, 55));
            westPanel.add(timePanel, BorderLayout.CENTER);

            categoryLabel = new JLabel(category.isEmpty() ? "Click to set category" : category);
            categoryLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            categoryLabel.setForeground(category.isEmpty() ? ThemeColors.TEXT_DIM : ThemeColors.TEXT_PRIMARY);
            categoryLabel.setHorizontalAlignment(SwingConstants.CENTER);

            JPanel centerPanel = new JPanel(new GridBagLayout());
            centerPanel.setOpaque(false);
            centerPanel.add(categoryLabel);

            add(westPanel, BorderLayout.WEST);
            add(centerPanel, BorderLayout.CENTER);

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    setBackground(ThemeColors.TITLE_BAR);
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    setBackground(ThemeColors.BACKGROUND);
                }

                @Override
                public void mouseClicked(MouseEvent e) {
                    openCategorySelection();
                }
            });
        }

        private void openCategorySelection() {
            final LocalDate tempDate = date;
            final TimeSlot tempSlot = timeSlot;

            CategorySelectionDialog.getInstance().selectCategoryForHistory(tempDate, tempSlot, category -> {
                categoryLabel.setText(category);
                categoryLabel.setForeground(ThemeColors.TEXT_PRIMARY);
            });
        }
    }
}