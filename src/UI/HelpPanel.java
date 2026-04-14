package UI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class HelpPanel extends JPanel {

//    private static final Color BG = new Color(242,244,247);
//    private static final Color CARD = Color.WHITE;
//    private static final Color BORDER = new Color(225,225,225);
//    private static final Color SUBTEXT = new Color(110,110,110);


    public HelpPanel(){

        setLayout(new BorderLayout(20,20));

        setBorder(new EmptyBorder(20,20,20,20));

        applyTheme();
        ThemeManager.addListener(this::applyTheme);

        add(title(), BorderLayout.NORTH);

        add(content(), BorderLayout.CENTER);
    }

    private void applyTheme(){
        setBackground(ThemeManager.bg());
        repaint();
    }

    private JLabel title(){

        JLabel label =
                new JLabel("Help");

        label.setForeground(ThemeManager.text());
        ThemeManager.addListener(() -> label.setForeground(ThemeManager.text()));

        label.setFont(
                new Font("Segoe UI", Font.BOLD, 20)
        );

        return label;
    }

    private JPanel content(){

        JPanel panel = new JPanel(new GridBagLayout());

        panel.setOpaque(false);

        GridBagConstraints c = new GridBagConstraints();

        c.insets = new Insets(10,10,10,10);

        c.fill = GridBagConstraints.BOTH;

        c.weightx = 1;

        c.weighty = 1;

        c.gridx = 0;
        c.gridy = 0;
        panel.add(card(

                "Quick Start",

                """
                1. Click Add Account
                2. Enter balance
                3. Edit anytime
                4. Check Dashboard
                """

        ), c);

        c.gridx = 1;
        panel.add(card(

                "Features",

                """
                Dashboard
                • total balance
                • activity history
    
                Accounts
                • add accounts
                • edit balances
    
                Settings
                • reset data
                """

        ), c);

        c.gridx = 0;
        c.gridy = 1;
        panel.add(card(

                "Tips",

                """
                    Changes are saved automatically

                    Editing balances updates activity history

                    Dashboard and Accounts update instantly
                    """

        ), c);

        c.gridx = 1;
        panel.add(card(

                "Troubleshooting",

                """
                Make sure balances are entered.
    
                If information does not update,
                restart the application.
                """

        ), c);

        return panel;
    }

    private JPanel card(String title, String text){

        JPanel card =
                new JPanel(new BorderLayout());

        card.setBackground(ThemeManager.card());

        card.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(ThemeManager.cardBorder()), new EmptyBorder(18,20,18,20)));
        ThemeManager.addListener(() -> {
            card.setBackground(ThemeManager.card());
            card.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(ThemeManager.cardBorder()), new EmptyBorder(18,20,18,20)));
            repaint();
        });

        JLabel header =
                new JLabel(title);

        header.setForeground(ThemeManager.text());
        ThemeManager.addListener(() -> header.setForeground(ThemeManager.text()));


        header.setFont(
                new Font("Segoe UI", Font.BOLD, 16)
        );

        JTextArea body =
                new JTextArea(text);

        body.setFont(
                new Font("Segoe UI", Font.PLAIN, 13)
        );

        body.setForeground(ThemeManager.subtext());
        ThemeManager.addListener(() -> body.setForeground(ThemeManager.subtext()));

        body.setEditable(false);

        body.setOpaque(false);

        body.setLineWrap(true);

        body.setWrapStyleWord(true);

        body.setBorder(
                new EmptyBorder(10,0,0,0)
        );

        card.add(header, BorderLayout.NORTH);

        card.add(body, BorderLayout.CENTER);

        return card;
    }
}