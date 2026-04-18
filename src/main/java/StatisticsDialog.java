import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.util.*;
import java.util.List;

public class StatisticsDialog extends JDialog {

    private static StatisticsDialog instance = null;
    private JPanel chartPanel;
    private JLabel titleLabel;
    private JLabel totalTimeLabel;
    private JComboBox<String> periodComboBox;
    private JComboBox<String> chartTypeComboBox;
    private Map<String, Integer> currentData = new HashMap<>();

    private static final String PERIOD_TODAY = "Today";
    private static final String PERIOD_LAST_7_DAYS = "Last 7 days";
    private static final String PERIOD_LAST_30_DAYS = "Last 30 days";
    private static final String PERIOD_ALL_TIME = "All time";
    private static final String CHART_BAR = "Bar Chart";
    private static final String CHART_PIE = "Pie Chart";

    public static StatisticsDialog getInstance() {
        if (instance == null) {
            instance = new StatisticsDialog();
        }
        return instance;
    }

    private StatisticsDialog() {
        super((Frame) null, "Statistics", true);
        setSize(850, 650);
        setMinimumSize(new Dimension(750, 550));
        setLocationRelativeTo(null);
        getContentPane().setBackground(ThemeColors.BACKGROUND);
        initComponents();
        setVisible(false);
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(ThemeColors.BACKGROUND);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel topPanel = createTopPanel();
        mainPanel.add(topPanel, BorderLayout.NORTH);

        JPanel mainContentPanel = new JPanel(new BorderLayout());
        mainContentPanel.setBackground(ThemeColors.BACKGROUND);
        mainContentPanel.setBorder(new EmptyBorder(15, 0, 15, 0));

        chartPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                String chartType = (String) chartTypeComboBox.getSelectedItem();
                if (CHART_BAR.equals(chartType)) {
                    drawBarChart((Graphics2D) g);
                } else {
                    drawPieChart((Graphics2D) g);
                }
            }
        };
        chartPanel.setBackground(ThemeColors.BACKGROUND);
        chartPanel.setPreferredSize(new Dimension(750, 450));

        mainContentPanel.add(chartPanel, BorderLayout.CENTER);

        totalTimeLabel = new JLabel("Total time: 0h 0m");
        totalTimeLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        totalTimeLabel.setForeground(ThemeColors.TEXT_PRIMARY);
        totalTimeLabel.setBorder(new EmptyBorder(10, 0, 0, 0));
        mainContentPanel.add(totalTimeLabel, BorderLayout.SOUTH);

        mainPanel.add(mainContentPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setBackground(ThemeColors.BACKGROUND);
        bottomPanel.setBorder(new EmptyBorder(15, 0, 0, 0));

        JButton closeButton = createButton("Close", ThemeColors.BUTTON);
        closeButton.addActionListener(e -> setVisible(false));
        bottomPanel.add(closeButton);

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);
        loadDataForPeriod(PERIOD_TODAY);
    }

    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(ThemeColors.BACKGROUND);
        topPanel.setBorder(new EmptyBorder(0, 0, 15, 0));

        titleLabel = new JLabel("Activity Statistics");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(ThemeColors.TEXT_PRIMARY);

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        filterPanel.setBackground(ThemeColors.BACKGROUND);

        JLabel chartTypeLabel = new JLabel("Chart:");
        chartTypeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        chartTypeLabel.setForeground(ThemeColors.TEXT_SECONDARY);

        chartTypeComboBox = new JComboBox<>(new String[]{CHART_BAR, CHART_PIE});
        chartTypeComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        chartTypeComboBox.setBackground(ThemeColors.BUTTON);
        chartTypeComboBox.setForeground(ThemeColors.TEXT_PRIMARY);
        chartTypeComboBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeColors.BORDER, 1, true),
                new EmptyBorder(5, 10, 5, 10)
        ));
        chartTypeComboBox.setFocusable(false);
        chartTypeComboBox.addActionListener(e -> chartPanel.repaint());

        JLabel periodLabel = new JLabel("Period:");
        periodLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        periodLabel.setForeground(ThemeColors.TEXT_SECONDARY);

        periodComboBox = new JComboBox<>(new String[]{
                PERIOD_TODAY, PERIOD_LAST_7_DAYS, PERIOD_LAST_30_DAYS, PERIOD_ALL_TIME
        });
        periodComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        periodComboBox.setBackground(ThemeColors.BUTTON);
        periodComboBox.setForeground(ThemeColors.TEXT_PRIMARY);
        periodComboBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeColors.BORDER, 1, true),
                new EmptyBorder(5, 10, 5, 10)
        ));
        periodComboBox.setFocusable(false);
        periodComboBox.addActionListener(e -> loadDataForPeriod((String) periodComboBox.getSelectedItem()));

        filterPanel.add(chartTypeLabel);
        filterPanel.add(chartTypeComboBox);
        filterPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        filterPanel.add(periodLabel);
        filterPanel.add(periodComboBox);

        topPanel.add(titleLabel, BorderLayout.WEST);
        topPanel.add(filterPanel, BorderLayout.EAST);

        return topPanel;
    }

    private JButton createButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        button.setBackground(bgColor);
        button.setForeground(ThemeColors.TEXT_PRIMARY);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeColors.BORDER, 1, true),
                new EmptyBorder(8, 20, 8, 20)
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

    private void loadDataForPeriod(String period) {
        currentData.clear();
        DataStorage storage = DataStorage.getInstance();

        LocalDate today = LocalDate.now();
        LocalDate startDate;

        switch (period) {
            case PERIOD_TODAY:
                startDate = today;
                break;
            case PERIOD_LAST_7_DAYS:
                startDate = today.minusDays(6);
                break;
            case PERIOD_LAST_30_DAYS:
                startDate = today.minusDays(29);
                break;
            case PERIOD_ALL_TIME:
            default:
                startDate = null;
                break;
        }

        List<LocalDate> availableDates = storage.getAvailableDates();

        for (LocalDate date : availableDates) {
            if (startDate != null && date.isBefore(startDate)) {
                continue;
            }
            if (date.isAfter(today)) {
                continue;
            }

            Map<TimeSlot, String> dayData = storage.getDataForDate(date);
            for (Map.Entry<TimeSlot, String> entry : dayData.entrySet()) {
                String category = entry.getValue();
                if (category != null && !category.isEmpty()) {
                    currentData.merge(category, 1, Integer::sum);
                }
            }
        }

        titleLabel.setText("Activity Statistics - " + period);

        int totalHours = currentData.values().stream().mapToInt(Integer::intValue).sum();
        totalTimeLabel.setText(String.format("Total tracked time: %dh %dm", totalHours, 0));

        chartPanel.repaint();
    }

    private void drawBarChart(Graphics2D g2) {
        if (currentData.isEmpty()) {
            drawEmptyMessage(g2);
            return;
        }

        List<Map.Entry<String, Integer>> sortedData = new ArrayList<>(currentData.entrySet());
        sortedData.sort((a, b) -> b.getValue().compareTo(a.getValue()));

        int width = chartPanel.getWidth();
        int height = chartPanel.getHeight();

        int leftMargin = 120;
        int rightMargin = 40;
        int topMargin = 40;
        int bottomMargin = 80;

        int chartWidth = width - leftMargin - rightMargin;
        int chartHeight = height - topMargin - bottomMargin;

        int maxValue = sortedData.stream().mapToInt(Map.Entry::getValue).max().orElse(1);
        maxValue = ((maxValue / 5) + 1) * 5;
        if (maxValue == 0) maxValue = 5;

        g2.setColor(ThemeColors.TEXT_DIM);
        g2.setStroke(new BasicStroke(2));
        g2.drawLine(leftMargin, topMargin, leftMargin, height - bottomMargin);
        g2.drawLine(leftMargin, height - bottomMargin, width - rightMargin, height - bottomMargin);

        g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        g2.setColor(ThemeColors.TEXT_SECONDARY);
        FontMetrics fm = g2.getFontMetrics();

        for (int i = 0; i <= maxValue; i += (maxValue <= 10 ? 1 : maxValue / 5)) {
            int y = height - bottomMargin - (i * chartHeight / maxValue);
            g2.drawLine(leftMargin - 5, y, leftMargin, y);
            String label = i + "h";
            g2.drawString(label, leftMargin - 10 - fm.stringWidth(label), y + 5);
        }

        int barWidth = Math.min(60, (chartWidth - 40) / sortedData.size());
        int barSpacing = 20;
        int totalBarWidth = barWidth + barSpacing;

        Color[] colors = {
                new Color(45, 95, 139), new Color(62, 126, 179),
                new Color(80, 150, 200), new Color(100, 170, 220),
                new Color(120, 180, 230), new Color(140, 190, 240)
        };

        int x = leftMargin + 20;

        for (int i = 0; i < sortedData.size(); i++) {
            Map.Entry<String, Integer> entry = sortedData.get(i);
            String category = entry.getKey();
            int value = entry.getValue();

            int barHeight = value * chartHeight / maxValue;
            int y = height - bottomMargin - barHeight;

            Color barColor = colors[i % colors.length];

            GradientPaint gradient = new GradientPaint(
                    x, y, barColor.brighter(),
                    x + barWidth, y + barHeight, barColor.darker()
            );
            g2.setPaint(gradient);
            g2.fillRoundRect(x, y, barWidth, barHeight, 8, 8);

            g2.setColor(barColor.darker().darker());
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawRoundRect(x, y, barWidth, barHeight, 8, 8);

            g2.setColor(ThemeColors.TEXT_PRIMARY);
            g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
            String valueStr = value + "h";
            int textX = x + (barWidth - g2.getFontMetrics().stringWidth(valueStr)) / 2;
            g2.drawString(valueStr, textX, y - 5);

            g2.setColor(ThemeColors.TEXT_SECONDARY);
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            drawWrappedText(g2, category, x, barWidth, height - bottomMargin + 15);

            x += totalBarWidth;
        }

        g2.setColor(ThemeColors.TEXT_DIM);
        g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        String xAxisLabel = "Categories";
        g2.drawString(xAxisLabel, (width - fm.stringWidth(xAxisLabel)) / 2, height - 10);

        Graphics2D g2d = (Graphics2D) g2.create();
        g2d.translate(15, height / 2);
        g2d.rotate(-Math.PI / 2);
        g2d.setColor(ThemeColors.TEXT_DIM);
        g2d.drawString("Hours", 0, 0);
        g2d.dispose();
    }

    private void drawPieChart(Graphics2D g2) {
        if (currentData.isEmpty()) {
            drawEmptyMessage(g2);
            return;
        }

        List<Map.Entry<String, Integer>> sortedData = new ArrayList<>(currentData.entrySet());
        sortedData.sort((a, b) -> b.getValue().compareTo(a.getValue()));

        int totalValue = sortedData.stream().mapToInt(Map.Entry::getValue).sum();

        int width = chartPanel.getWidth();
        int height = chartPanel.getHeight();

        int pieSize = Math.min(width - 300, height - 100);
        int pieX = (width - pieSize) / 2;
        int pieY = (height - pieSize) / 2;

        Color[] colors = {
                new Color(45, 95, 139), new Color(62, 126, 179),
                new Color(80, 150, 200), new Color(100, 170, 220),
                new Color(120, 180, 230), new Color(140, 190, 240),
                new Color(160, 200, 250), new Color(180, 210, 255)
        };

        int startAngle = 0;
        int legendX = pieX + pieSize + 20;
        int legendY = pieY + 20;

        g2.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        FontMetrics fm = g2.getFontMetrics();

        for (int i = 0; i < sortedData.size(); i++) {
            Map.Entry<String, Integer> entry = sortedData.get(i);
            int value = entry.getValue();
            int angle = (int) Math.round(360.0 * value / totalValue);

            Color color = colors[i % colors.length];
            g2.setColor(color);
            g2.fillArc(pieX, pieY, pieSize, pieSize, startAngle, angle);

            g2.setColor(ThemeColors.BACKGROUND);
            g2.setStroke(new BasicStroke(2));
            g2.drawArc(pieX, pieY, pieSize, pieSize, startAngle, angle);

            String legendText = entry.getKey();
            String shortLegend = legendText.length() > 20 ? legendText.substring(0, 17) + "..." : legendText;
            String percentage = String.format(" (%.1f%%)", 100.0 * value / totalValue);

            g2.setColor(color);
            g2.fillRect(legendX, legendY, 15, 15);
            g2.setColor(ThemeColors.BORDER);
            g2.drawRect(legendX, legendY, 15, 15);

            g2.setColor(ThemeColors.TEXT_PRIMARY);
            g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
            g2.drawString(shortLegend, legendX + 22, legendY + 12);

            g2.setColor(ThemeColors.TEXT_SECONDARY);
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            int hoursWidth = fm.stringWidth(shortLegend);
            g2.drawString(value + "h" + percentage, legendX + 22 + hoursWidth + 10, legendY + 12);

            legendY += 25;

            if (legendY > pieY + pieSize - 30 && i < sortedData.size() - 1) {
                legendY = pieY + 20;
                legendX += 200;
            }

            startAngle += angle;
        }

        g2.setColor(ThemeColors.TEXT_PRIMARY);
        g2.setFont(new Font("Segoe UI", Font.BOLD, 16));
        String title = "Distribution of " + totalValue + " total hours";
        int titleWidth = g2.getFontMetrics().stringWidth(title);
        g2.drawString(title, (width - titleWidth) / 2, pieY - 10);
    }

    private void drawEmptyMessage(Graphics2D g2) {
        g2.setColor(ThemeColors.TEXT_DIM);
        g2.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        String message = "No data for this period";
        FontMetrics fm = g2.getFontMetrics();
        int x = (chartPanel.getWidth() - fm.stringWidth(message)) / 2;
        int y = chartPanel.getHeight() / 2;
        g2.drawString(message, x, y);
    }

    private void drawWrappedText(Graphics2D g2, String text, int x, int maxWidth, int startY) {
        FontMetrics fm = g2.getFontMetrics();
        String[] words = text.split(" ");
        StringBuilder line = new StringBuilder();
        int lineY = startY;
        int maxLines = 2;

        for (String word : words) {
            String testLine = line.length() == 0 ? word : line + " " + word;
            if (fm.stringWidth(testLine) <= maxWidth) {
                if (line.length() > 0) line.append(" ");
                line.append(word);
            } else {
                drawCenteredString(g2, line.toString(), x, maxWidth, lineY);
                lineY += fm.getHeight();
                line = new StringBuilder(word);
                maxLines--;
                if (maxLines == 0) break;
            }
        }

        if (line.length() > 0 && maxLines > 0) {
            drawCenteredString(g2, line.toString(), x, maxWidth, lineY);
        }
    }

    private void drawCenteredString(Graphics2D g2, String text, int x, int width, int y) {
        FontMetrics fm = g2.getFontMetrics();
        int textWidth = fm.stringWidth(text);
        int textX = x + (width - textWidth) / 2;
        g2.drawString(text, textX, y);
    }

    public void showDialog() {
        loadDataForPeriod(PERIOD_TODAY);
        periodComboBox.setSelectedItem(PERIOD_TODAY);
        chartTypeComboBox.setSelectedItem(CHART_BAR);
        setVisible(true);
    }
}