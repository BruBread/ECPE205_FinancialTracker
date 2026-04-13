package UI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class SettingsPanel extends JPanel {

    private static final Color BG = new Color(242,244,247);
    private static final Color CARD_BORDER = new Color(225,225,225);

    public SettingsPanel(){

        setLayout(new BorderLayout(20,20));

        setBorder(new EmptyBorder(20,20,20,20));

        setBackground(BG);

        add(title(), BorderLayout.NORTH);

        add(content(), BorderLayout.CENTER);
    }

    private JLabel title(){

        JLabel label =
                new JLabel("Settings");

        label.setFont(
                new Font("Segoe UI", Font.BOLD, 20)
        );

        return label;
    }

    private JScrollPane content(){

        JPanel wrapper =
                new JPanel();

        wrapper.setLayout(
                new BoxLayout(wrapper, BoxLayout.Y_AXIS)
        );

        wrapper.setOpaque(false);

        wrapper.add(appearanceCard());

        wrapper.add(Box.createVerticalStrut(18));

        wrapper.add(dataCard());

        JScrollPane scroll =
                new JScrollPane(wrapper);

        scroll.setBorder(null);

        scroll.getVerticalScrollBar()
                .setUnitIncrement(12);

        return scroll;
    }

    private JPanel appearanceCard(){

        JPanel card =
                createCard();

        card.setLayout(
                new BoxLayout(card, BoxLayout.Y_AXIS)
        );

        JLabel header =
                sectionLabel("Appearance");

        JRadioButton light =
                new JRadioButton("Light Mode");

        JRadioButton dark =
                new JRadioButton("Dark Mode");

        light.setSelected(true);

        ButtonGroup group =
                new ButtonGroup();

        group.add(light);

        group.add(dark);

        styleRadio(light);

        styleRadio(dark);

        card.add(header);

        card.add(Box.createVerticalStrut(10));

        card.add(light);

        card.add(dark);

        return card;
    }

    private JPanel dataCard(){

        JPanel card =
                createCard();

        card.setLayout(
                new BoxLayout(card, BoxLayout.Y_AXIS)
        );

        JLabel header =
                sectionLabel("Data");

        JLabel description =
                new JLabel(
                        "<html>Delete all accounts and activity history permanently.</html>"
                );

        description.setFont(
                new Font("Segoe UI", Font.PLAIN, 12)
        );

        description.setForeground(
                new Color(120,120,120)
        );

        JButton reset =
                new JButton("Reset Data");

        reset.setBackground(
                new Color(200,60,60)
        );

        reset.setForeground(Color.WHITE);

        reset.setFocusPainted(false);

        reset.setBorder(
                new EmptyBorder(8,14,8,14)
        );

        reset.setCursor(
                new Cursor(Cursor.HAND_CURSOR)
        );

        card.add(header);

        card.add(Box.createVerticalStrut(8));

        card.add(description);

        card.add(Box.createVerticalStrut(12));

        card.add(reset);

        return card;
    }

    private JPanel createCard(){

        JPanel card =
                new JPanel();

        card.setBackground(Color.WHITE);

        card.setBorder(

                BorderFactory.createCompoundBorder(

                        BorderFactory.createLineBorder(CARD_BORDER),

                        new EmptyBorder(16,18,16,18)

                )

        );

        return card;
    }

    private JLabel sectionLabel(String text){

        JLabel label =
                new JLabel(text);

        label.setFont(
                new Font("Segoe UI", Font.BOLD, 15)
        );

        return label;
    }

    private void styleRadio(JRadioButton r){

        r.setFont(
                new Font("Segoe UI", Font.PLAIN, 13)
        );

        r.setOpaque(false);
    }
}