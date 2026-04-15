import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class CategorySelectionDialog extends JDialog {
    private static final Logger logger = Logger.getLogger(CategorySelectionDialog.class.getName());

    private static CategorySelectionDialog instance = null;
    private String currentSlot;
    private JPanel categoriesPanel;
    private List<CategoryButton> categoryButtons;
    private JScrollPane scrollPane;

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
        setUndecorated(true);

        initComponents();

        setVisible(false);
        logger.info("CategorySelectionDialog initialized");
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

        JPanel titleBar = createTitleBar();
        mainPanel.add(titleBar, BorderLayout.NORTH);

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(ThemeColors.BACKGROUND);
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("What were you working on?");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(ThemeColors.TEXT_SECONDARY);
        titleLabel.setBorder(new EmptyBorder(0, 0, 15, 0));
        contentPanel.add(titleLabel, BorderLayout.NORTH);

        categoriesPanel = new JPanel();
        categoriesPanel.setLayout(new BoxLayout(categoriesPanel, BoxLayout.Y_AXIS));
        categoriesPanel.setBackground(ThemeColors.BACKGROUND);

        scrollPane = new JScrollPane(categoriesPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBackground(ThemeColors.BACKGROUND);
        scrollPane.getViewport().setBackground(ThemeColors.BACKGROUND);

        scrollPane.getVerticalScrollBar().setUI(new CustomScrollBarUI());
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(10, 0));

        contentPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setBackground(ThemeColors.BACKGROUND);
        bottomPanel.setBorder(new EmptyBorder(15, 0, 0, 0));

        JButton cancelButton = createCancelButton();
        bottomPanel.add(cancelButton);
        contentPanel.add(bottomPanel, BorderLayout.SOUTH);

        mainPanel.add(contentPanel, BorderLayout.CENTER);

        add(mainPanel);
        loadCategories();
    }

    private JPanel createTitleBar() {
        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setBackground(ThemeColors.TITLE_BAR);
        titleBar.setPreferredSize(new Dimension(getWidth(), 35));

        JLabel titleLabel = new JLabel("  Select Category");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        titleLabel.setForeground(ThemeColors.TEXT_PRIMARY);

        JButton closeButton = new JButton("X");
        closeButton.setFont(new Font("Arial", Font.BOLD, 16));
        closeButton.setForeground(ThemeColors.TEXT_SECONDARY);
        closeButton.setPreferredSize(new Dimension(45, 35));
        closeButton.setFocusPainted(false);
        closeButton.setBorderPainted(false);
        closeButton.setContentAreaFilled(false);
        closeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

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
            }
        });

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

        titleBar.add(titleLabel, BorderLayout.WEST);
        titleBar.add(closeButton, BorderLayout.EAST);

        return titleBar;
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

        cancelButton.addActionListener(e -> setVisible(false));

        return cancelButton;
    }

    private void loadCategories() {
        DataStorage table = DataStorage.getInstance();
        List<String> categories = table.getCategories();

        categoriesPanel.removeAll();
        categoryButtons = new ArrayList<>();

        for (String category : categories) {
            CategoryButton button = new CategoryButton(category);
            button.addActionListener(CategorySelectionListener.getInstance());

            categoryButtons.add(button);
            categoriesPanel.add(button);
            categoriesPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        }

        categoriesPanel.revalidate();
        categoriesPanel.repaint();
    }

    void refreshList(String slot) {
        currentSlot = slot;
        String selectedCategory = DataStorage.getInstance().getCategory(slot);

        for (CategoryButton button : categoryButtons) {
            button.setSelected(button.getCategory().equals(selectedCategory));
        }
    }

    void selectCategory(String slot) {
        currentSlot = slot;
        refreshList(slot);
        loadCategories();
        refreshList(slot);
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
        private java.awt.event.ActionListener actionListener;

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
                        actionListener.actionPerformed(new java.awt.event.ActionEvent(
                                this,
                                java.awt.event.ActionEvent.ACTION_PERFORMED,
                                category
                        ));
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

        public void addActionListener(java.awt.event.ActionListener listener) {
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