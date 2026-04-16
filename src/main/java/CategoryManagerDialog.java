import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class CategoryManagerDialog extends JDialog {

    private static CategoryManagerDialog instance = null;
    private JPanel categoriesPanel;
    private JTextField newCategoryField;

    public static CategoryManagerDialog getInstance() {
        if (instance == null) {
            instance = new CategoryManagerDialog();
        }
        return instance;
    }

    private CategoryManagerDialog() {
        super((Frame) null, "Manage Categories", true);
        setSize(450, 500);
        setMinimumSize(new Dimension(400, 400));
        setLocationRelativeTo(null);

        initComponents();
        setVisible(false);
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(ThemeColors.BACKGROUND);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Заголовок
        JLabel titleLabel = new JLabel("Manage Categories");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(ThemeColors.TEXT_PRIMARY);
        titleLabel.setBorder(new EmptyBorder(0, 0, 15, 0));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Панель с категориями
        categoriesPanel = new JPanel();
        categoriesPanel.setLayout(new BoxLayout(categoriesPanel, BoxLayout.Y_AXIS));
        categoriesPanel.setBackground(ThemeColors.BACKGROUND);

        JScrollPane scrollPane = new JScrollPane(categoriesPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(20);
        scrollPane.setBackground(ThemeColors.BACKGROUND);
        scrollPane.getViewport().setBackground(ThemeColors.BACKGROUND);
        scrollPane.getVerticalScrollBar().setUI(new CustomScrollBarUI());
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(10, 0));

        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Панель для добавления новой категории
        JPanel addPanel = new JPanel(new BorderLayout(10, 0));
        addPanel.setBackground(ThemeColors.BACKGROUND);
        addPanel.setBorder(new EmptyBorder(15, 0, 15, 0));

        newCategoryField = new JTextField();
        newCategoryField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        newCategoryField.setBackground(ThemeColors.BUTTON);
        newCategoryField.setForeground(ThemeColors.TEXT_PRIMARY);
        newCategoryField.setCaretColor(ThemeColors.TEXT_PRIMARY);
        newCategoryField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeColors.BORDER, 1, true),
                new EmptyBorder(8, 12, 8, 12)
        ));

        JButton addButton = createButton("Add", ThemeColors.CURRENT_SLOT);
        addButton.addActionListener(e -> addNewCategory());

        addPanel.add(newCategoryField, BorderLayout.CENTER);
        addPanel.add(addButton, BorderLayout.EAST);

        // Кнопки внизу
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        bottomPanel.setBackground(ThemeColors.BACKGROUND);

        JButton reloadButton = createButton("Reload", ThemeColors.BUTTON);
        reloadButton.addActionListener(e -> reloadCategories());

        JButton closeButton = createButton("Close", ThemeColors.BUTTON);
        closeButton.addActionListener(e -> setVisible(false));

        bottomPanel.add(reloadButton);
        bottomPanel.add(closeButton);

        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.setBackground(ThemeColors.BACKGROUND);
        southPanel.add(addPanel, BorderLayout.NORTH);
        southPanel.add(bottomPanel, BorderLayout.SOUTH);

        mainPanel.add(southPanel, BorderLayout.SOUTH);

        add(mainPanel);
        loadCategories();
    }

    private JButton createButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        button.setBackground(bgColor);
        button.setForeground(ThemeColors.TEXT_PRIMARY);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeColors.BORDER, 1, true),
                new EmptyBorder(8, 16, 8, 16)
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

    private void loadCategories() {
        categoriesPanel.removeAll();
        List<String> categories = DataStorage.getInstance().getCategories();

        for (String category : categories) {
            categoriesPanel.add(createCategoryRow(category));
            categoriesPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        }

        categoriesPanel.revalidate();
        categoriesPanel.repaint();
    }

    private JPanel createCategoryRow(String category) {
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setBackground(ThemeColors.BACKGROUND);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));

        JLabel categoryLabel = new JLabel(category);
        categoryLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        categoryLabel.setForeground(ThemeColors.TEXT_PRIMARY);
        categoryLabel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeColors.BORDER, 1, true),
                new EmptyBorder(10, 15, 10, 15)
        ));
        categoryLabel.setOpaque(true);
        categoryLabel.setBackground(ThemeColors.BUTTON);

        JButton removeButton = new JButton("X");
        removeButton.setFont(new Font("Arial", Font.BOLD, 14));
        removeButton.setForeground(ThemeColors.TEXT_SECONDARY);
        removeButton.setPreferredSize(new Dimension(45, 45));
        removeButton.setFocusPainted(false);
        removeButton.setBorderPainted(false);
        removeButton.setContentAreaFilled(false);
        removeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        removeButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                removeButton.setForeground(Color.WHITE);
                removeButton.setBackground(ThemeColors.CLOSE_BUTTON_HOVER);
                removeButton.setContentAreaFilled(true);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                removeButton.setForeground(ThemeColors.TEXT_SECONDARY);
                removeButton.setContentAreaFilled(false);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                DataStorage.getInstance().removeCategory(category);
                loadCategories();
            }
        });

        row.add(categoryLabel, BorderLayout.CENTER);
        row.add(removeButton, BorderLayout.EAST);

        return row;
    }

    private void addNewCategory() {
        String newCategory = newCategoryField.getText().trim();
        if (!newCategory.isEmpty()) {
            DataStorage.getInstance().addCategory(newCategory);
            newCategoryField.setText("");
            loadCategories();
        }
    }

    private void reloadCategories() {
        DataStorage.getInstance().reloadCategories();
        loadCategories();
    }

    public void showDialog() {
        loadCategories();
        setVisible(true);
    }
}