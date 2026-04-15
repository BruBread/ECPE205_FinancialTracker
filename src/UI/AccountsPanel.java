package UI;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import Data_backend.AccountManager;
import Data_backend.BankAccount;
import Data_backend.SubAccount;

public class AccountsPanel extends JPanel {

    private JPanel listPanel;
    private JLabel totalLabel;
    private JLabel sectionLabel;

    private static final Color GREEN_TEXT = new Color(50, 160, 80);

    public AccountsPanel() {
        setLayout(new BorderLayout(20, 20));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        applyTheme();
        ThemeManager.addListener(this::applyTheme);
        ThemeManager.addListener(this::refresh);

        JPanel top = new JPanel(new BorderLayout(0, 18));
        top.setOpaque(false);
        top.add(header(), BorderLayout.NORTH);
        top.add(totalCard(), BorderLayout.CENTER);

        add(top, BorderLayout.NORTH);
        add(listArea(), BorderLayout.CENTER);

        AccountManager.addListener(this::refresh);
        refresh();
    }

    private void applyTheme() {
        setBackground(ThemeManager.bg());
        repaint();
    }

    // ── Header row: "Accounts" title + "+ Add Account" button ──────────────
    private JPanel header() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        JLabel title = new JLabel("Accounts");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(ThemeManager.text());
        ThemeManager.addListener(() -> title.setForeground(ThemeManager.text()));

        JButton addBtn = new JButton("+ Add Account");
        addBtn.addActionListener(e ->
                new AddAccountDialog((JFrame) SwingUtilities.getWindowAncestor(this)));
        addBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        addBtn.setFocusPainted(false);
        addBtn.setBackground(new Color(30, 110, 50));
        addBtn.setForeground(Color.WHITE);
        addBtn.setBorder(new EmptyBorder(8, 14, 8, 14));
        addBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        panel.add(title, BorderLayout.WEST);
        panel.add(addBtn, BorderLayout.EAST);
        return panel;
    }

    // ── Green "Total Balance" card at the very top ──────────────────────────
    private JPanel totalCard() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(new Color(50, 170, 80));
        card.setBorder(new EmptyBorder(18, 22, 18, 22));

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

    // ── Scrollable list of bank cards ───────────────────────────────────────
    private JScrollPane listArea() {
        listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setOpaque(false);

        sectionLabel = new JLabel("  Your Accounts (0)");
        sectionLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        sectionLabel.setBorder(new EmptyBorder(0, 0, 10, 0));
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

    // ── One bank card + its sub-accounts below ──────────────────────────────
    private JPanel accountRow(BankAccount acc) {

        // ---- outer container: bank header on top, sub-accounts below ----
        JPanel container = new JPanel(new BorderLayout());
        container.setOpaque(false);
        container.setAlignmentX(Component.LEFT_ALIGNMENT);
        container.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

        // ---- bank header card ----
        JPanel bankCard = new JPanel(new BorderLayout(12, 0));
        bankCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
        bankCard.setBackground(ThemeManager.card());

        Border normal = BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeManager.cardBorder()),
                new EmptyBorder(12, 16, 12, 16));
        Border hover = BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeManager.hover()),
                new EmptyBorder(12, 16, 12, 16));

        bankCard.setBorder(normal);
        bankCard.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) { bankCard.setBorder(hover); }
            public void mouseExited(java.awt.event.MouseEvent e)  { bankCard.setBorder(normal); }
        });

        // logo / initials
        JLabel logo = new JLabel("", JLabel.CENTER);
        logo.setPreferredSize(new Dimension(42, 42));
        if (acc.logo != null)
            logo.setIcon(new ImageIcon(acc.logo.getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH)));
        else {
            logo.setText(acc.bankName.substring(0, Math.min(2, acc.bankName.length())));
        }
        logo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        logo.setForeground(ThemeManager.text());
        logo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeManager.cardBorder()),
                new EmptyBorder(6, 6, 6, 6)));

        // bank name + total balance of this bank
        JLabel nameLabel = new JLabel(acc.bankName);
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        nameLabel.setForeground(ThemeManager.text());

        JLabel balLabel = new JLabel("₱" + String.format("%,.2f", acc.getTotalBalance()));
        balLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        balLabel.setForeground(GREEN_TEXT);

        JPanel textPanel = new JPanel(new GridLayout(2, 1));
        textPanel.setOpaque(false);
        textPanel.add(nameLabel);
        textPanel.add(balLabel);

        // Edit button for the bank itself
        JButton editBtn = new JButton("Edit");
        editBtn.addActionListener(e ->
                new AddAccountDialog((JFrame) SwingUtilities.getWindowAncestor(this), acc));
        editBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        editBtn.setForeground(new Color(30, 110, 50));
        editBtn.setBackground(ThemeManager.isDark() ? new Color(40, 60, 45) : new Color(240, 248, 242));
        editBtn.setFocusPainted(false);
        editBtn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 220, 205)),
                new EmptyBorder(6, 14, 6, 14)));
        editBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JPanel leftPanel = new JPanel(new BorderLayout(10, 0));
        leftPanel.setOpaque(false);
        leftPanel.add(logo, BorderLayout.WEST);
        leftPanel.add(textPanel, BorderLayout.CENTER);

        bankCard.add(leftPanel, BorderLayout.WEST);
        bankCard.add(editBtn,   BorderLayout.EAST);

        // ---- sub-accounts panel (shown below the bank card) ----
        JPanel subPanel = new JPanel();
        subPanel.setLayout(new BoxLayout(subPanel, BoxLayout.Y_AXIS));
        subPanel.setBackground(ThemeManager.isDark() ? new Color(35, 45, 38) : new Color(245, 252, 247));
        subPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 1, 1, 1, ThemeManager.cardBorder()),
                new EmptyBorder(4, 58, 6, 16)));

        for (SubAccount sub : acc.subAccounts) {
            JPanel subRow = new JPanel(new BorderLayout(8, 0));
            subRow.setOpaque(false);
            subRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
            subRow.setAlignmentX(Component.LEFT_ALIGNMENT);
            subRow.setBorder(new EmptyBorder(3, 0, 3, 0));

            JLabel subName = new JLabel("• " + sub.name);
            subName.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            subName.setForeground(ThemeManager.text());

            JLabel subBal = new JLabel("₱" + String.format("%,.2f", sub.balance));
            subBal.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            subBal.setForeground(GREEN_TEXT);

            JButton editSub = new JButton("Edit");
            editSub.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            editSub.setForeground(new Color(30, 110, 50));
            editSub.setBackground(ThemeManager.isDark() ? new Color(40, 60, 45) : new Color(240, 248, 242));
            editSub.setFocusPainted(false);
            editSub.setBorderPainted(true);
            editSub.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(200, 220, 205)),
                    new EmptyBorder(3, 10, 3, 10)));
            editSub.setCursor(new Cursor(Cursor.HAND_CURSOR));
            editSub.addActionListener(e ->
                    new AddSubAccountDialog(
                            (JFrame) SwingUtilities.getWindowAncestor(this), acc, sub));

            JPanel subRight = new JPanel(new BorderLayout(8, 0));
            subRight.setOpaque(false);
            subRight.add(subBal,  BorderLayout.WEST);
            subRight.add(editSub, BorderLayout.EAST);

            subRow.add(subName,  BorderLayout.WEST);
            subRow.add(subRight, BorderLayout.EAST);

            subPanel.add(subRow);
        }

        // "+ Add Sub-Account" link-style button at the bottom of sub panel
        JButton addSubBtn = new JButton("+ Add Sub-Account");
        addSubBtn.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        addSubBtn.setForeground(new Color(30, 110, 50));
        addSubBtn.setBackground(null);
        addSubBtn.setOpaque(false);
        addSubBtn.setBorderPainted(false);
        addSubBtn.setFocusPainted(false);
        addSubBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        addSubBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        addSubBtn.addActionListener(e ->
                new AddSubAccountDialog(
                        (JFrame) SwingUtilities.getWindowAncestor(this), acc, null));
        subPanel.add(Box.createVerticalStrut(2));
        subPanel.add(addSubBtn);

        container.add(bankCard, BorderLayout.NORTH);
        container.add(subPanel, BorderLayout.CENTER);

        return container;
    }

    // ── Rebuild the list whenever data changes ───────────────────────────────
    public void refresh() {
        listPanel.removeAll();

        for (BankAccount acc : AccountManager.accounts) {
            listPanel.add(accountRow(acc));
            listPanel.add(Box.createVerticalStrut(12));
        }

        sectionLabel.setText("Your Accounts (" + AccountManager.accounts.size() + ")");
        totalLabel.setText("₱" + String.format("%,.2f", AccountManager.totalAssets()));

        revalidate();
        repaint();
        setBackground(ThemeManager.bg());
    }
}