package UI;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import Data_backend.AccountManager;
import Data_backend.BankAccount;

public class AccountsPanel extends JPanel {

    private JPanel listPanel;
    private JLabel totalLabel;
    private JLabel sectionLabel;

    private static final Color GREEN_TEXT = new Color(50,160,80);

    public AccountsPanel(){

        setLayout(new BorderLayout(20,20));
        setBorder(new EmptyBorder(20,20,20,20));
        applyTheme();
        ThemeManager.addListener(this::applyTheme);
        ThemeManager.addListener(this::refresh);

        JPanel top = new JPanel(new BorderLayout(0,18));
        top.setOpaque(false);

        top.add(header(), BorderLayout.NORTH);
        top.add(totalCard(), BorderLayout.CENTER);

        add(top, BorderLayout.NORTH);
        add(listArea(), BorderLayout.CENTER);

        AccountManager.addListener(this::refresh);

        refresh();
    }

    private void applyTheme(){
        setBackground(ThemeManager.bg());
        repaint();
    }

    private JPanel header(){

        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        JLabel title = new JLabel("Accounts");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(ThemeManager.text());
        ThemeManager.addListener(() -> title.setForeground(ThemeManager.text()));

        JButton addBtn = new JButton("+ Add Account");

        addBtn.addActionListener(e -> {
            new AddAccountDialog((JFrame)SwingUtilities.getWindowAncestor(this));
        });

        addBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        addBtn.setFocusPainted(false);
        addBtn.setBackground(new Color(30,110,50));
        addBtn.setForeground(Color.WHITE);
        addBtn.setBorder(new EmptyBorder(8,14,8,14));
        addBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        panel.add(title, BorderLayout.WEST);
        panel.add(addBtn, BorderLayout.EAST);

        return panel;
    }

    private JPanel totalCard(){

        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(new Color(50,170,80));
        card.setBorder(new EmptyBorder(18,22,18,22));

        JLabel title = new JLabel("Total Balance");
        title.setFont(new Font("Segoe UI", Font.BOLD, 14));
        title.setForeground(Color.WHITE);

        totalLabel = new JLabel("₱0.00");
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        totalLabel.setForeground(Color.WHITE);

        card.add(title, BorderLayout.NORTH);
        card.add(totalLabel, BorderLayout.CENTER);

        return card;
    }

    private JScrollPane listArea(){

        listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setOpaque(false);

        sectionLabel = new JLabel("Your Accounts (0)");
        sectionLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        sectionLabel.setBorder(new EmptyBorder(0,0,10,0));
        sectionLabel.setForeground(ThemeManager.text());
        ThemeManager.addListener(() -> sectionLabel.setForeground(ThemeManager.text()));

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.add(sectionLabel, BorderLayout.NORTH);
        wrapper.add(listPanel, BorderLayout.CENTER);

        JScrollPane scroll = new JScrollPane(wrapper);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        ThemeManager.addListener(() -> scroll.getViewport().setBackground(ThemeManager.bg()));
        scroll.setBorder(null);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.getVerticalScrollBar().setUnitIncrement(12);

        return scroll;
    }

    private JPanel accountRow(BankAccount acc){

        JPanel card = new JPanel(new BorderLayout(12,0));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE,70));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        Border normal = BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeManager.cardBorder()),
                new EmptyBorder(12,16,12,16));

        Border hover = BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeManager.hover()),
                new EmptyBorder(12,16,12,16));

        card.setBorder(normal);
        card.setBackground(ThemeManager.card());

        card.addMouseListener(new java.awt.event.MouseAdapter(){
            public void mouseEntered(java.awt.event.MouseEvent e){ card.setBorder(hover); }
            public void mouseExited(java.awt.event.MouseEvent e){ card.setBorder(normal); }
        });

        JLabel logo = new JLabel("", JLabel.CENTER);
        logo.setPreferredSize(new Dimension(42,42));

        if(acc.logo != null)
            logo.setIcon(new ImageIcon(acc.logo.getImage().getScaledInstance(32,32,Image.SCALE_SMOOTH)));
        else {
            String initials = acc.bankName.substring(0, Math.min(2, acc.bankName.length()));
            logo.setText(initials);
        }

        logo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        logo.setForeground(ThemeManager.text());
        logo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeManager.cardBorder()),
                new EmptyBorder(6,6,6,6)
        ));

        JLabel name = new JLabel(acc.bankName);
        name.setFont(new Font("Segoe UI", Font.BOLD, 15));
        name.setForeground(ThemeManager.text());

        JLabel balance = new JLabel("₱"+String.format("%,.2f", acc.balance));
        balance.setFont(new Font("Segoe UI", Font.BOLD, 15));
        balance.setForeground(GREEN_TEXT);

        JButton edit = new JButton("Edit");
        edit.addActionListener(e -> {
            new AddAccountDialog((JFrame)SwingUtilities.getWindowAncestor(this), acc);
        });

        edit.setFont(new Font("Segoe UI", Font.BOLD, 12));
        edit.setForeground(new Color(30,110,50));
        edit.setBackground(ThemeManager.isDark() ? new Color(40,60,45) : new Color(240,248,242));
        edit.setFocusPainted(false);
        edit.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200,220,205)),
                new EmptyBorder(6,14,6,14)
        ));
        edit.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JPanel text = new JPanel(new GridLayout(2,1));
        text.setOpaque(false);
        text.add(name);
        text.add(balance);

        JPanel left = new JPanel(new BorderLayout(10,0));
        left.setOpaque(false);
        left.add(logo, BorderLayout.WEST);
        left.add(text, BorderLayout.CENTER);

        card.add(left, BorderLayout.WEST);
        card.add(edit, BorderLayout.EAST);

        return card;
    }

    public void refresh(){

        listPanel.removeAll();

        for(BankAccount acc : AccountManager.accounts){
            listPanel.add(accountRow(acc));
            listPanel.add(Box.createVerticalStrut(12));
        }

        sectionLabel.setText("Your Accounts (" + AccountManager.accounts.size() + ")");
        totalLabel.setText("₱"+String.format("%,.2f", AccountManager.totalAssets()));

        revalidate();
        repaint();
        setBackground(ThemeManager.bg());
    }
}
