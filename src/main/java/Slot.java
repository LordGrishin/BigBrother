import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Slot extends JPanel {
    private JButton slotTimeButton;
    private JLabel slotCategoryLabel;
    private TimeSlot timeSlot;
    private boolean isCurrent = false;

    Slot(TimeSlot timeSlot, String category) {
        this.timeSlot = timeSlot;

        setLayout(new BorderLayout());
        setBackground(ThemeColors.BACKGROUND);
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        setPreferredSize(new Dimension(0, 60));
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Кнопка с временем
        slotTimeButton = new JButton(timeSlot.toString());
        slotTimeButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        slotTimeButton.setForeground(ThemeColors.BUTTON_TEXT);
        slotTimeButton.setBackground(ThemeColors.BUTTON);
        slotTimeButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeColors.BORDER, 1, true),
                new EmptyBorder(10, 20, 10, 20)
        ));
        slotTimeButton.setFocusPainted(false);
        slotTimeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        slotTimeButton.setContentAreaFilled(false);
        slotTimeButton.setOpaque(true);
        slotTimeButton.setHorizontalAlignment(SwingConstants.CENTER);

        slotTimeButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (!isCurrent) {
                    slotTimeButton.setBackground(ThemeColors.BUTTON_HOVER);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (!isCurrent) {
                    slotTimeButton.setBackground(ThemeColors.BUTTON);
                }
            }
        });

        slotTimeButton.addActionListener(TimeSlotListener.getInstance());

        // Лейбл с категорией
        slotCategoryLabel = new JLabel(category);
        slotCategoryLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        slotCategoryLabel.setForeground(ThemeColors.TEXT_SECONDARY);
        slotCategoryLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Панель для кнопки времени
        JPanel leftPanel = new JPanel(new GridBagLayout());
        leftPanel.setOpaque(false);
        leftPanel.add(slotTimeButton);

        // Панель для категории
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);
        centerPanel.add(slotCategoryLabel);

        JPanel westPanel = new JPanel(new BorderLayout());
        westPanel.setOpaque(false);
        westPanel.setPreferredSize(new Dimension(140, 60));
        westPanel.add(leftPanel, BorderLayout.CENTER);

        add(westPanel, BorderLayout.WEST);
        add(centerPanel, BorderLayout.CENTER);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                slotTimeButton.doClick();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                if (!isCurrent) {
                    setBackground(ThemeColors.TITLE_BAR);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (!isCurrent) {
                    setBackground(ThemeColors.BACKGROUND);
                }
            }
        });

        setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, ThemeColors.TITLE_BAR));
    }

    void setSlotCategory(String newSlotCategory) {
        slotCategoryLabel.setText(newSlotCategory);
    }

    public String getSlotTime() {
        return slotTimeButton.getText();
    }

    public String getSlotCategory() {
        return slotCategoryLabel.getText();
    }

    public TimeSlot getTimeSlot() {
        return timeSlot;
    }

    public void setCurrentSlot() {
        isCurrent = true;
        slotTimeButton.setBackground(ThemeColors.CURRENT_SLOT);
        slotTimeButton.setForeground(Color.WHITE);
        slotTimeButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeColors.CURRENT_SLOT_BORDER, 2, true),
                new EmptyBorder(9, 19, 9, 19)
        ));
        setBackground(ThemeColors.CURRENT_SLOT_BACKGROUND);
        slotCategoryLabel.setForeground(ThemeColors.TEXT_PRIMARY);
        repaint();
    }

    public void setNormalSlot() {
        isCurrent = false;
        slotTimeButton.setBackground(ThemeColors.BUTTON);
        slotTimeButton.setForeground(ThemeColors.BUTTON_TEXT);
        slotTimeButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeColors.BORDER, 1, true),
                new EmptyBorder(10, 20, 10, 20)
        ));
        setBackground(ThemeColors.BACKGROUND);
        slotCategoryLabel.setForeground(ThemeColors.TEXT_SECONDARY);
        repaint();
    }
}