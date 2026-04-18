import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;

public class CategorySelectionDialog extends JDialog {

    private static CategorySelectionDialog instance = null;
    private String currentSlot;
    private JPanel categoriesPanel;
    private List<CategoryButton> categoryButtons;

    private LocalDate historyDate = null;
    private TimeSlot historySlot = null;
    private Consumer<String> historyCallback = null;

    public static CategorySelectionDialog getInstance() {
        if (instance == null) {
            instance = new CategorySelectionDialog();
        }
        return instance;
    }

    private CategorySelectionDialog() {
        super((Frame) null, "Select Category", true);
        setSize(400, 500);
        setMinimumSize(new Dimension(350, 400));
        positionWindowRightOfCenter();
        setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        initComponents();
        setVisible(false);
    }

    private void positionWindowRightOfCenter() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (screenSize.width - getWidth()) / 2 + 250;
        int y = (screenSize.height - getHeight()) / 2;
        setLocation(x, y);
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(ThemeColors.BACKGROUND);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("What were you working on?");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(ThemeColors.TEXT_SECONDARY);
        titleLabel.setBorder(new EmptyBorder(0, 0, 15, 0));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        categoriesPanel = new JPanel();
        categoriesPanel.setLayout(new BoxLayout(categoriesPanel, BoxLayout.Y_AXIS));
        categoriesPanel.setBackground(ThemeColors.BACKGROUND);

        JScrollPane scrollPane = new JScrollPane(categoriesPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(30);
        scrollPane.setBackground(ThemeColors.BACKGROUND);
        scrollPane.getViewport().setBackground(ThemeColors.BACKGROUND);
        scrollPane.getVerticalScrollBar().setUI(new CustomScrollBarUI());
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(10, 0));

        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setBackground(ThemeColors.BACKGROUND);
        bottomPanel.setBorder(new EmptyBorder(15, 0, 0, 0));

        JButton cancelButton = createCancelButton();
        bottomPanel.add(cancelButton);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);
        loadCategories();
    }

    private JButton createCancelButton() {
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cancelButton.setBackground(ThemeColors.BUTTON);
        cancelButton.setForeground(ThemeColors.BUTTON_TEXT);
        cancelButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeColors.BORDER, 1, true),
                new EmptyBorder(8, 16, 8, 16)
        ));
        cancelButton.setFocusPainted(false);
        cancelButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        cancelButton.setContentAreaFilled(false);
        cancelButton.setOpaque(true);

        cancelButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                cancelButton.setBackground(ThemeColors.BUTTON_HOVER);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                cancelButton.setBackground(ThemeColors.BUTTON);
            }
        });

        cancelButton.addActionListener(e -> {
            clearHistoryMode();
            setVisible(false);
        });

        return cancelButton;
    }

    private void loadCategories() {
        DataStorage table = DataStorage.getInstance();
        List<String> categories = table.getCategories();

        categoriesPanel.removeAll();
        categoryButtons = new ArrayList<>();

        for (String category : categories) {
            CategoryButton button = new CategoryButton(category);
            button.addActionListener(e -> selectCategoryAction(button.getCategory()));
            categoryButtons.add(button);
            categoriesPanel.add(button);
            categoriesPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        }

        categoriesPanel.revalidate();
        categoriesPanel.repaint();
    }

    private void selectCategoryAction(String category) {
        if (historyDate != null && historySlot != null && historyCallback != null) {
            DataStorage.getInstance().setCategoryForDate(historyDate, historySlot, category);
            historyCallback.accept(category);
            clearHistoryMode();
        } else {
            DataStorage.getInstance().setCategory(currentSlot, category);
            TimeSlotDialog.getInstance().refreshTable();
        }
        setVisible(false);
    }

    private void clearHistoryMode() {
        historyDate = null;
        historySlot = null;
        historyCallback = null;
        currentSlot = null;
    }

    void refreshList(String slot) {
        currentSlot = slot;
        String selectedCategory;

        if (historyDate != null) {
            Map<TimeSlot, String> dayData = DataStorage.getInstance().getDataForDate(historyDate);
            selectedCategory = dayData.getOrDefault(historySlot, "");
        } else {
            selectedCategory = DataStorage.getInstance().getCategory(slot);
        }

        for (CategoryButton button : categoryButtons) {
            button.setSelected(button.getCategory().equals(selectedCategory));
        }
    }

    void selectCategory(String slot) {
        clearHistoryMode();
        currentSlot = slot;
        refreshList(slot);
        loadCategories();
        refreshList(slot);
        setVisible(true);
    }

    public void selectCategoryForHistory(LocalDate date, TimeSlot slot, Consumer<String> callback) {
        this.historyDate = date;
        this.historySlot = slot;
        this.historyCallback = callback;
        this.currentSlot = slot.toString();
        loadCategories();
        refreshList(slot.toString());
        setVisible(true);
    }

    public String getCurrentSlot() {
        return currentSlot;
    }

    private class CategoryButton extends JPanel {
        private final String category;
        private boolean isSelected = false;
        private boolean isHovered = false;
        private JLabel label;
        private ActionListener actionListener;

        public CategoryButton(String category) {
            this.category = category;
            setLayout(new BorderLayout());
            setBackground(ThemeColors.BACKGROUND);
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(ThemeColors.BORDER, 1, true),
                    new EmptyBorder(12, 16, 12, 16)
            ));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
            setPreferredSize(new Dimension(0, 50));
            setCursor(new Cursor(Cursor.HAND_CURSOR));

            label = new JLabel(category);
            label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            label.setForeground(ThemeColors.TEXT_SECONDARY);
            add(label, BorderLayout.CENTER);

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    isHovered = true;
                    updateAppearance();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    isHovered = false;
                    updateAppearance();
                }

                @Override
                public void mouseClicked(MouseEvent e) {
                    if (actionListener != null) {
                        actionListener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, category));
                    }
                }
            });
        }

        public void setSelected(boolean selected) {
            this.isSelected = selected;
            updateAppearance();
        }

        private void updateAppearance() {
            if (isSelected) {
                setBackground(ThemeColors.CURRENT_SLOT);
                label.setForeground(Color.WHITE);
                setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(ThemeColors.CURRENT_SLOT_BORDER, 2, true),
                        new EmptyBorder(11, 15, 11, 15)
                ));
            } else if (isHovered) {
                setBackground(ThemeColors.BUTTON_HOVER);
                label.setForeground(ThemeColors.TEXT_PRIMARY);
                setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(ThemeColors.BUTTON_HOVER, 1, true),
                        new EmptyBorder(12, 16, 12, 16)
                ));
            } else {
                setBackground(ThemeColors.BACKGROUND);
                label.setForeground(ThemeColors.TEXT_SECONDARY);
                setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(ThemeColors.BORDER, 1, true),
                        new EmptyBorder(12, 16, 12, 16)
                ));
            }
            repaint();
        }

        public String getCategory() {
            return category;
        }

        public void addActionListener(ActionListener listener) {
            this.actionListener = listener;
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
            super.paintComponent(g2);
            g2.dispose();
        }
    }
}