package UI;

import Data_backend.AccountManager;
import Data_backend.BankAccount;
import Data_backend.Transaction;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class DashboardPanel extends JPanel {

    private JPanel bankGrid;
    private JPanel transactionList;
    private JLabel totalLabel;

    private static final Color GREEN_CARD  = new Color(50,170,80);
    private static final Color RED_BADGE   = new Color(210,70,60);
    private static final Color GREEN_BADGE = new Color(50,160,80);
    private static final Color BLUE_BADGE  = new Color(70,120,200);

    private static final Color GREEN_TEXT         = new Color(50, 160, 80);
    private static final Color RED_TEXT           = new Color(210, 60, 60);
    private static final double LOW_BALANCE_THRESHOLD = 10.00;

    public DashboardPanel() {
        setLayout(new BorderLayout(20,20));
        setBorder(new EmptyBorder(20,20,20,20));

        JSplitPane split = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                leftPanel(),
                rightPanel()
        );

        split.setDividerLocation(520);
        split.setDividerSize(0);
        split.setBorder(null);
        split.setBackground(ThemeManager.bg());
        split.setOpaque(true);
        split.setResizeWeight(0.45);

        add(split, BorderLayout.CENTER);

        AccountManager.addListener(this::refreshAll);
        ThemeManager.addListener(this::refreshAll);
        ThemeManager.addListener(() -> {
            setBackground(ThemeManager.bg());
            split.setBackground(ThemeManager.bg());
            split.repaint();
        });

        refreshAll();
    }

    // ── Helper: pick balance color based on threshold ────────────────────────
    private Color balanceColor(double balance) {
        return balance < LOW_BALANCE_THRESHOLD ? RED_TEXT : GREEN_TEXT;
    }

    private JPanel leftPanel() {

        JPanel panel = new JPanel(new BorderLayout());
        panel.setPreferredSize(new Dimension(380,0));
        panel.setBackground(ThemeManager.card());
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeManager.cardBorder()),
                new EmptyBorder(0,0,0,0)
        ));

        ThemeManager.addListener(() -> {
            panel.setBackground(ThemeManager.card());
            panel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(ThemeManager.cardBorder()),
                    new EmptyBorder(0,0,0,0)
            ));
        });

        JLabel header = new JLabel("   Activity History");
        header.setFont(new Font("Segoe UI",Font.BOLD,14));
        header.setPreferredSize(new Dimension(0,42));
        header.setForeground(ThemeManager.text());
        header.setBorder(BorderFactory.createMatteBorder(0,0,1,0, ThemeManager.cardBorder()));

        ThemeManager.addListener(() -> {
            header.setForeground(ThemeManager.text());
            header.setBorder(BorderFactory.createMatteBorder(0,0,1,0, ThemeManager.cardBorder()));
        });

        panel.add(header, BorderLayout.NORTH);

        transactionList = new JPanel();
        transactionList.setLayout(new BoxLayout(transactionList, BoxLayout.Y_AXIS));
        transactionList.setBackground(ThemeManager.card());

        ThemeManager.addListener(() -> transactionList.setBackground(ThemeManager.card()));

        JScrollPane scroll = new JScrollPane(transactionList);
        scroll.setBackground(ThemeManager.card());
        scroll.getViewport().setBackground(ThemeManager.card());
        ThemeManager.addListener(() -> {
            scroll.setBackground(ThemeManager.card());
            scroll.getViewport().setBackground(ThemeManager.card());
        });
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(12);

        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    private String shorten(String text, int max){
        if(text == null) return "";
        if(text.length() <= max) return text;
        return text.substring(0,max-3) + "...";
    }

    private JPanel transactionItem(Transaction t) {

        JPanel card = new JPanel(new BorderLayout(8,0));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE,64));
        card.setMinimumSize(new Dimension(0,64));
        card.setBackground(ThemeManager.card());
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0,0,1,0,ThemeManager.cardBorder()),
                new EmptyBorder(10,12,10,12)
        ));

        JLabel logoLabel = new JLabel();
        logoLabel.setForeground(ThemeManager.text());
        logoLabel.setPreferredSize(new Dimension(38,38));
        logoLabel.setHorizontalAlignment(JLabel.CENTER);

        if(t.logo!=null)
            logoLabel.setIcon(new ImageIcon(t.logo.getImage().getScaledInstance(36,36,Image.SCALE_SMOOTH)));
        else {
            logoLabel.setText("?");
            logoLabel.setFont(new Font("Segoe UI",Font.BOLD,18));
        }

        JLabel nameLabel = new JLabel(shorten(t.bank,22));
        nameLabel.setForeground(ThemeManager.text());
        nameLabel.setHorizontalAlignment(JLabel.LEFT);
        nameLabel.setPreferredSize(new Dimension(140,18));
        nameLabel.setFont(new Font("Segoe UI",Font.BOLD,13));
        nameLabel.setMaximumSize(new Dimension(140,20));

        JLabel dateLabel = new JLabel(t.date);
        dateLabel.setFont(new Font("Segoe UI",Font.PLAIN,11));
        dateLabel.setForeground(ThemeManager.subtext());

        JPanel textPanel = new JPanel(new GridLayout(2,1));
        textPanel.setPreferredSize(null);
        textPanel.setMaximumSize(null);
        textPanel.setOpaque(false);
        textPanel.add(nameLabel);
        textPanel.add(dateLabel);

        JLabel typeBadge = new JLabel(t.type, JLabel.CENTER);
        typeBadge.setOpaque(true);
        typeBadge.setForeground(Color.WHITE);
        typeBadge.setFont(new Font("Segoe UI",Font.BOLD,11));
        typeBadge.setPreferredSize(new Dimension(90,24));
        typeBadge.setBorder(new EmptyBorder(2,6,2,6));

        switch(t.type){
            case "Account updated" -> typeBadge.setBackground(new Color(90,120,160));
            case "Deposit"         -> typeBadge.setBackground(GREEN_BADGE);
            case "Withdraw"        -> typeBadge.setBackground(RED_BADGE);
            case "Delete"          -> typeBadge.setBackground(RED_BADGE);
            case "Name changed"    -> typeBadge.setBackground(BLUE_BADGE);
            case "Logo updated"    -> typeBadge.setBackground(new Color(120,120,120));
            default                -> typeBadge.setBackground(BLUE_BADGE);
        }

        JPanel badgeWrap = new JPanel(new BorderLayout());
        badgeWrap.setOpaque(false);
        badgeWrap.setPreferredSize(new Dimension(100,24));
        badgeWrap.setMinimumSize(new Dimension(100,24));
        badgeWrap.add(typeBadge, BorderLayout.CENTER);

        String sign = "";
        if(t.type.equals("Deposit")) sign = "+";
        if(t.type.equals("Withdraw") || t.type.equals("Delete")) sign = "-";

        String amountText = "";
        if(t.amount > 0)
            amountText = sign+"₱"+String.format("%,.2f",t.amount);

        Color amtColor;
        if (sign.equals("+"))       amtColor = new Color(50, 160, 80);
        else if (sign.equals("-"))  amtColor = new Color(210, 70, 60);
        else                        amtColor = ThemeManager.subtext();

        JLabel amtLabel = new JLabel(shortenCurrency(amountText, 16));
        amtLabel.setFont(new Font("Segoe UI",Font.BOLD,13));
        amtLabel.setForeground(amtColor);
        amtLabel.setHorizontalAlignment(JLabel.RIGHT);

        JPanel amtWrap = new JPanel(new BorderLayout());
        amtWrap.setOpaque(false);
        amtWrap.setPreferredSize(new Dimension(140,24));
        amtWrap.setMinimumSize(new Dimension(140,24));
        amtWrap.setMaximumSize(new Dimension(140,24));
        amtWrap.add(amtLabel, BorderLayout.EAST);

        JPanel left = new JPanel(new BorderLayout(8,0));
        left.setOpaque(false);
        left.add(logoLabel, BorderLayout.WEST);
        left.add(textPanel, BorderLayout.CENTER);

        card.add(left,      BorderLayout.WEST);
        card.add(badgeWrap, BorderLayout.CENTER);
        card.add(amtWrap,   BorderLayout.EAST);

        return card;
    }

    private String shortenCurrency(String text, int max){
        if(text == null) return "";
        if(text.length() <= max) return text;
        return text.substring(0,max-1)+"…";
    }

    private JPanel rightPanel(){
        JPanel panel = new JPanel(new BorderLayout(0,16));
        panel.setOpaque(false);
        panel.add(totalCard(), BorderLayout.NORTH);
        panel.add(bankArea(), BorderLayout.CENTER);
        return panel;
    }

    private JPanel totalCard(){

        JPanel card = new JPanel(new BorderLayout(0,4));
        card.setBackground(GREEN_CARD);
        card.setBorder(new EmptyBorder(20,24,20,24));

        JLabel title = new JLabel("Total Assets:");
        title.setFont(new Font("Segoe UI",Font.BOLD,16));
        title.setForeground(Color.WHITE);

        totalLabel = new JLabel("₱0.00");
        totalLabel.setFont(new Font("Segoe UI",Font.BOLD,34));
        totalLabel.setForeground(Color.WHITE);

        card.add(title, BorderLayout.NORTH);
        card.add(totalLabel, BorderLayout.CENTER);

        return card;
    }

    private JPanel bankArea(){

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(ThemeManager.card());
        wrapper.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeManager.cardBorder()),
                new EmptyBorder(12,12,12,12)
        ));

        ThemeManager.addListener(() -> {
            wrapper.setBackground(ThemeManager.card());
            wrapper.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(ThemeManager.cardBorder()),
                    new EmptyBorder(12,12,12,12)
            ));
        });

        JLabel header = new JLabel("Accounts");
        header.setForeground(ThemeManager.text());
        ThemeManager.addListener(() -> header.setForeground(ThemeManager.text()));
        header.setFont(new Font("Segoe UI",Font.BOLD,14));
        header.setBorder(new EmptyBorder(0,0,10,0));

        wrapper.add(header, BorderLayout.NORTH);

        JPanel container = new JPanel(new FlowLayout(FlowLayout.CENTER,16,16));
        container.setOpaque(false);

        bankGrid = new JPanel(new GridLayout(0,3,16,16));
        bankGrid.setOpaque(false);

        container.add(bankGrid);

        JScrollPane scroll = new JScrollPane(container);
        scroll.setBackground(ThemeManager.card());
        scroll.getViewport().setBackground(ThemeManager.card());
        ThemeManager.addListener(() -> {
            scroll.setBackground(ThemeManager.card());
            scroll.getViewport().setBackground(ThemeManager.card());
        });
        scroll.setBorder(null);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroll.getVerticalScrollBar().setUnitIncrement(12);

        wrapper.add(scroll, BorderLayout.CENTER);

        return wrapper;
    }

    private JPanel bankCard(BankAccount acc){

        JPanel card = new JPanel(new BorderLayout(0,8));
        card.setPreferredSize(new Dimension(130,130));
        card.setBackground(ThemeManager.card());

        Border normalBorder = BorderFactory.createLineBorder(ThemeManager.cardBorder(),1,true);
        Border hoverBorder  = BorderFactory.createLineBorder(ThemeManager.hover(),1,true);

        ThemeManager.addListener(() -> {
            card.setBackground(ThemeManager.card());
            card.setBorder(BorderFactory.createLineBorder(ThemeManager.cardBorder(),1,true));
            card.repaint();
        });

        card.setBorder(normalBorder);

        JLabel logoLabel = new JLabel("", JLabel.CENTER);

        if(acc.logo!=null)
            logoLabel.setIcon(new ImageIcon(acc.logo.getImage().getScaledInstance(48,48,Image.SCALE_SMOOTH)));
        else{
            String initials = (acc.bankName == null || acc.bankName.isBlank())
                    ? "?"
                    : acc.bankName.substring(0, Math.min(2, acc.bankName.length()));
            logoLabel.setText(initials);
            logoLabel.setFont(new Font("Segoe UI",Font.BOLD,18));
            logoLabel.setForeground(ThemeManager.text());
            ThemeManager.addListener(() -> logoLabel.setForeground(ThemeManager.text()));
        }

        double total = acc.getTotalBalance();
        JLabel balLabel = new JLabel("₱"+String.format("%,.2f", total), JLabel.CENTER);
        balLabel.setFont(new Font("Segoe UI",Font.BOLD,14));
        // Use red for low balance, otherwise theme text color
        balLabel.setForeground(balanceColor(total));
        ThemeManager.addListener(() -> balLabel.setForeground(balanceColor(acc.getTotalBalance())));

        JButton editBtn = new JButton("Edit");
        editBtn.setPreferredSize(new Dimension(60,24));
        editBtn.setFont(new Font("Segoe UI",Font.BOLD,11));
        editBtn.setBackground(ThemeManager.isDark() ? new Color(50,50,50) : new Color(245,245,245));
        editBtn.setForeground(ThemeManager.text());
        editBtn.setBorder(BorderFactory.createLineBorder(ThemeManager.cardBorder()));
        ThemeManager.addListener(() -> {
            editBtn.setBackground(ThemeManager.isDark() ? new Color(50,50,50) : new Color(245,245,245));
            editBtn.setForeground(ThemeManager.text());
            editBtn.setBorder(BorderFactory.createLineBorder(ThemeManager.cardBorder()));
        });
        editBtn.setFocusPainted(false);
        editBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        editBtn.addActionListener(e -> {
            new AddAccountDialog((JFrame)SwingUtilities.getWindowAncestor(this), acc);
            refreshAll();
            AccountManager.addListener(this::refreshAll);
        });

        card.addMouseListener(new java.awt.event.MouseAdapter(){
            public void mouseEntered(java.awt.event.MouseEvent e){ card.setBorder(hoverBorder); }
            public void mouseExited(java.awt.event.MouseEvent e){ card.setBorder(normalBorder); }
        });

        card.add(editBtn, BorderLayout.NORTH);
        card.add(logoLabel, BorderLayout.CENTER);
        card.add(balLabel, BorderLayout.SOUTH);

        return card;
    }

    private JPanel addCard(){

        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setOpaque(false);

        JPanel card = new JPanel(new BorderLayout());
        card.setPreferredSize(new Dimension(130,130));
        card.setBackground(ThemeManager.card());
        card.setBorder(BorderFactory.createLineBorder(ThemeManager.cardBorder(),1,true));
        ThemeManager.addListener(() -> {
            card.setBackground(ThemeManager.card());
            card.setBorder(BorderFactory.createLineBorder(ThemeManager.cardBorder(),1,true));
        });

        JButton addBtn = new JButton("+");
        addBtn.setFont(new Font("Segoe UI",Font.BOLD,28));
        addBtn.setForeground(new Color(150,150,150));
        addBtn.setContentAreaFilled(false);
        addBtn.setBorderPainted(false);
        addBtn.setFocusPainted(false);
        addBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        addBtn.addActionListener(e -> {
            new AddAccountDialog((JFrame)SwingUtilities.getWindowAncestor(this));
            refreshAll();
        });

        card.add(addBtn, BorderLayout.CENTER);
        wrapper.add(card);

        return wrapper;
    }

    public void refreshAll(){

        bankGrid.removeAll();

        for(BankAccount a : AccountManager.accounts)
            bankGrid.add(bankCard(a));

        bankGrid.add(addCard());

        totalLabel.setText("₱"+String.format("%,.2f", AccountManager.totalAssets()));

        transactionList.removeAll();

        java.util.List<Transaction> reversed = new java.util.ArrayList<>(AccountManager.transactions);
        java.util.Collections.reverse(reversed);

        for(Transaction t : reversed)
            transactionList.add(transactionItem(t));

        revalidate();
        repaint();
        transactionList.setBackground(ThemeManager.card());
        setBackground(ThemeManager.bg());
    }
}
