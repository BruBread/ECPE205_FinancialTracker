package UI;

import Data_backend.AccountManager;
import Data_backend.BankAccount;
import Data_backend.Transaction;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Main UI
 */
public class DashboardPanel extends JPanel {

    private JPanel bankGrid;
    private JPanel transactionList;
    private JLabel totalLabel;

    // Colors yes
    private static final Color GREEN_CARD  = new Color(50, 170, 80);
    private static final Color BG_WHITE    = Color.WHITE;
    private static final Color BORDER_GRAY = new Color(220, 220, 220);
    private static final Color RED_BADGE   = new Color(210, 70, 60);
    private static final Color GREEN_BADGE = new Color(50, 160, 80);
    private static final Color BLUE_BADGE  = new Color(70, 120, 200);

    public DashboardPanel() {
        setLayout(new BorderLayout(20, 20));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        setBackground(new Color(245, 246, 248));

        add(leftPanel(),  BorderLayout.WEST);
        add(rightPanel(), BorderLayout.CENTER);

        refreshAll();
    }

    // Everything Below here is transaction history.

    private JPanel leftPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setPreferredSize(new Dimension(370, 0));
        panel.setBackground(BG_WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_GRAY),
                new EmptyBorder(0, 0, 0, 0)));

        // Headers here
        JLabel header = new JLabel("   Transaction History");
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setPreferredSize(new Dimension(0, 42));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_GRAY));
        panel.add(header, BorderLayout.NORTH);

        transactionList = new JPanel();
        transactionList.setLayout(new BoxLayout(transactionList, BoxLayout.Y_AXIS));
        transactionList.setBackground(BG_WHITE);

        JScrollPane scroll = new JScrollPane(transactionList);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(12);
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    private JPanel transactionItem(Transaction t) {
        JPanel card = new JPanel(new BorderLayout(10, 0));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 64));
        card.setBackground(BG_WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_GRAY),
                new EmptyBorder(10, 12, 10, 12)));

        // Logo here
        JLabel logoLabel = new JLabel();
        logoLabel.setPreferredSize(new Dimension(38, 38));
        if (t.logo != null) {
            logoLabel.setIcon(new ImageIcon(
                    t.logo.getImage().getScaledInstance(36, 36, Image.SCALE_SMOOTH)));
        } else {
            logoLabel.setText("?");
            logoLabel.setHorizontalAlignment(JLabel.CENTER);
            logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        }

        // Name + date
        JLabel nameLabel = new JLabel(t.bank);
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));

        JLabel dateLabel = new JLabel(t.date);
        dateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        dateLabel.setForeground(Color.GRAY);

        JPanel textPanel = new JPanel(new GridLayout(2, 1, 0, 1));
        textPanel.setOpaque(false);
        textPanel.add(nameLabel);
        textPanel.add(dateLabel);

        // Type badge
        JLabel typeBadge = new JLabel(t.type, JLabel.CENTER);
        typeBadge.setOpaque(true);
        typeBadge.setForeground(Color.WHITE);
        typeBadge.setFont(new Font("Segoe UI", Font.BOLD, 11));

        typeBadge.setPreferredSize(new Dimension(90, 24));
        typeBadge.setMinimumSize(new Dimension(90, 24));
        typeBadge.setMaximumSize(new Dimension(90, 24));

        typeBadge.setBorder(new EmptyBorder(2, 6, 2, 6));

        switch (t.type) {
            case "Deposit"  -> typeBadge.setBackground(GREEN_BADGE);
            case "Delete"   -> typeBadge.setBackground(RED_BADGE);
            case "Withdraw" -> typeBadge.setBackground(RED_BADGE);
            default         -> typeBadge.setBackground(BLUE_BADGE);
        }

        JPanel badgeWrap = new JPanel(new BorderLayout());
        badgeWrap.setOpaque(false);
        badgeWrap.setPreferredSize(new Dimension(95, 24)); // fixed width
        badgeWrap.setMinimumSize(new Dimension(95, 24));
        badgeWrap.setMaximumSize(new Dimension(95, 24));

        badgeWrap.add(typeBadge, BorderLayout.CENTER);

        // Amount
        String sign = "";

        switch (t.type) {

            case "Deposit":
                sign = "+";
                break;

            case "Withdraw":
            case "Delete":
                sign = "-";
                break;

            default:
                sign = "";
        }

        JLabel amtLabel =
                new JLabel(sign + "₱" +
                        String.format("%,.2f", t.amount));

        amtLabel.setFont(
                new Font("Segoe UI", Font.BOLD, 13));

        if (t.type.equals("Deposit")) {

            amtLabel.setForeground(new Color(50,160,80));

        }
        else if (
                t.type.equals("Withdraw")
                        || t.type.equals("Delete")
        ) {

            amtLabel.setForeground(new Color(210,70,60));

        }

        // Assemble
        JPanel left = new JPanel(new BorderLayout(8, 0));
        left.setOpaque(false);
        left.add(logoLabel,  BorderLayout.WEST);
        left.add(textPanel,  BorderLayout.CENTER);

        JPanel middle = new JPanel(new BorderLayout());
        middle.setOpaque(false);

        middle.add(badgeWrap, BorderLayout.WEST);

        card.add(left, BorderLayout.WEST);
        card.add(middle, BorderLayout.CENTER);
        amtLabel.setHorizontalAlignment(JLabel.RIGHT);

        JPanel amtWrap =
                new JPanel(new BorderLayout());

        amtWrap.setOpaque(false);

        amtWrap.setPreferredSize(
                new Dimension(110, 24));

        amtWrap.add(
                amtLabel,
                BorderLayout.EAST);

        card.add(amtWrap, BorderLayout.EAST);

        return card;
    }

    // Total Assets

    private JPanel rightPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 16));
        panel.setOpaque(false);
        panel.add(totalCard(), BorderLayout.NORTH);
        panel.add(bankArea(),  BorderLayout.CENTER);
        return panel;
    }

    private JPanel totalCard() {
        JPanel card = new JPanel(new BorderLayout(0, 4));
        card.setBackground(GREEN_CARD);
        card.setBorder(new EmptyBorder(20, 24, 20, 24));

        JLabel title = new JLabel("Total Assets:");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setForeground(Color.WHITE);

        totalLabel = new JLabel("₱0.00");
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 34));
        totalLabel.setForeground(Color.WHITE);

        card.add(title,      BorderLayout.NORTH);
        card.add(totalLabel, BorderLayout.CENTER);
        return card;
    }

    private JPanel bankArea() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(BG_WHITE);
        wrapper.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_GRAY),
                new EmptyBorder(12, 12, 12, 12)));

        JLabel header = new JLabel("Accounts");
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBorder(new EmptyBorder(0, 0, 10, 0));
        wrapper.add(header, BorderLayout.NORTH);

        bankGrid = new JPanel(new GridLayout(2, 3, 12, 12));
        bankGrid.setOpaque(false);
        wrapper.add(bankGrid, BorderLayout.CENTER);

        return wrapper;
    }

    private JPanel bankCard(BankAccount acc) {
        JPanel card = new JPanel(new BorderLayout(0, 6));
        card.setBackground(BG_WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_GRAY),
                new EmptyBorder(10, 10, 10, 10)));

        JLabel logoLabel = new JLabel();
        logoLabel.setHorizontalAlignment(JLabel.CENTER);
        if (acc.logo != null) {
            logoLabel.setIcon(new ImageIcon(
                    acc.logo.getImage().getScaledInstance(48, 48, Image.SCALE_SMOOTH)));
        } else {
            logoLabel.setText(acc.bankName.substring(0, Math.min(2, acc.bankName.length())));
            logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
            logoLabel.setHorizontalAlignment(JLabel.CENTER);
        }

        JLabel balLabel = new JLabel(
                "₱" + String.format("%,.0f", acc.balance), JLabel.CENTER);
        balLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));

        JButton editBtn = new JButton("Edit");
        editBtn.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        editBtn.setFocusPainted(false);
        editBtn.addActionListener(e -> {
            new AddAccountDialog(
                    (JFrame) SwingUtilities.getWindowAncestor(this), acc);
            refreshAll();
        });

        card.add(editBtn,   BorderLayout.NORTH);
        card.add(logoLabel, BorderLayout.CENTER);
        card.add(balLabel,  BorderLayout.SOUTH);
        return card;
    }

    private JPanel addCard() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(new Color(248, 249, 250));
        card.setBorder(BorderFactory.createDashedBorder(Color.GRAY, 4, 4));

        JButton addBtn = new JButton("+");
        addBtn.setFont(new Font("Segoe UI", Font.BOLD, 28));
        addBtn.setForeground(Color.GRAY);
        addBtn.setContentAreaFilled(false);
        addBtn.setBorderPainted(false);
        addBtn.setFocusPainted(false);
        addBtn.addActionListener(e -> {
            new AddAccountDialog((JFrame) SwingUtilities.getWindowAncestor(this));
            refreshAll();
        });

        card.add(addBtn, BorderLayout.CENTER);
        return card;
    }

    // -------------------------------------------------------------------------
    // Refresh
    // -------------------------------------------------------------------------

    public void refreshAll() {
        // Rebuild bank grid
        bankGrid.removeAll();
        for (BankAccount a : AccountManager.accounts) {
            bankGrid.add(bankCard(a));
        }
        bankGrid.add(addCard());

        // Update total
        totalLabel.setText("₱" + String.format("%,.2f", AccountManager.totalAssets()));

        // Rebuild transaction list (newest first)
        transactionList.removeAll();
        java.util.List<Transaction> reversed = new java.util.ArrayList<>(AccountManager.transactions);
        java.util.Collections.reverse(reversed);
        for (Transaction t : reversed) {
            transactionList.add(transactionItem(t));
        }

        revalidate();
        repaint();
    }
}
