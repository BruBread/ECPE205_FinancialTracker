package UI;

import Data_backend.AccountManager;
import Data_backend.BankAccount;
import Data_backend.Transaction;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Main Dashboard UI
 */
public class DashboardPanel extends JPanel {

    private JPanel bankGrid;
    private JPanel transactionList;
    private JLabel totalLabel;

    // Colors
    private static final Color GREEN_CARD  = new Color(50,170,80);
    private static final Color BG_WHITE    = Color.WHITE;
    private static final Color BORDER_GRAY = new Color(225,225,225);
    private static final Color RED_BADGE   = new Color(210,70,60);
    private static final Color GREEN_BADGE = new Color(50,160,80);
    private static final Color BLUE_BADGE  = new Color(70,120,200);

    public DashboardPanel() {

        setLayout(new BorderLayout(20,20));

        setBorder(new EmptyBorder(20,20,20,20));

        setBackground(new Color(242,244,247));

        add(leftPanel(), BorderLayout.WEST);

        add(rightPanel(), BorderLayout.CENTER);

        refreshAll();
    }

    // LEFT PANEL (TRANSACTION HISTORY)

    private JPanel leftPanel() {

        JPanel panel = new JPanel(new BorderLayout());

        panel.setPreferredSize(new Dimension(380,0));

        panel.setBackground(BG_WHITE);

        panel.setBorder(BorderFactory.createCompoundBorder(

                BorderFactory.createLineBorder(BORDER_GRAY),

                new EmptyBorder(0,0,0,0)));

        JLabel header = new JLabel("   Transaction History");

        header.setFont(new Font("Segoe UI",Font.BOLD,14));

        header.setPreferredSize(new Dimension(0,42));

        header.setBorder(BorderFactory.createMatteBorder(
                0,0,1,0,BORDER_GRAY));

        panel.add(header,BorderLayout.NORTH);

        transactionList = new JPanel();

        transactionList.setLayout(
                new BoxLayout(transactionList,
                        BoxLayout.Y_AXIS));

        transactionList.setBackground(BG_WHITE);

        JScrollPane scroll = new JScrollPane(transactionList);

        scroll.setBorder(null);

        scroll.getVerticalScrollBar()
                .setUnitIncrement(12);

        panel.add(scroll,BorderLayout.CENTER);

        return panel;
    }

    private JPanel transactionItem(Transaction t) {

        JPanel card = new JPanel(new BorderLayout(10,0));

        card.setMaximumSize(
                new Dimension(Integer.MAX_VALUE,64));

        card.setBackground(BG_WHITE);

        card.setBorder(BorderFactory.createCompoundBorder(

                BorderFactory.createMatteBorder(
                        0,0,1,0,BORDER_GRAY),

                new EmptyBorder(10,12,10,12)));

        JLabel logoLabel = new JLabel();

        logoLabel.setPreferredSize(new Dimension(38,38));

        logoLabel.setHorizontalAlignment(JLabel.CENTER);

        if(t.logo!=null){

            logoLabel.setIcon(

                    new ImageIcon(

                            t.logo.getImage()
                                    .getScaledInstance(
                                            36,36,
                                            Image.SCALE_SMOOTH)));
        }
        else{

            logoLabel.setText("?");

            logoLabel.setFont(
                    new Font("Segoe UI",
                            Font.BOLD,18));
        }

        JLabel nameLabel = new JLabel(t.bank);

        nameLabel.setFont(
                new Font("Segoe UI",
                        Font.BOLD,13));

        JLabel dateLabel = new JLabel(t.date);

        dateLabel.setFont(
                new Font("Segoe UI",
                        Font.PLAIN,11));

        dateLabel.setForeground(Color.GRAY);

        JPanel textPanel =
                new JPanel(new GridLayout(2,1));

        textPanel.setOpaque(false);

        textPanel.add(nameLabel);

        textPanel.add(dateLabel);

        JLabel typeBadge =
                new JLabel(t.type,JLabel.CENTER);

        typeBadge.setOpaque(true);

        typeBadge.setForeground(Color.WHITE);

        typeBadge.setFont(
                new Font("Segoe UI",
                        Font.BOLD,11));

        typeBadge.setPreferredSize(
                new Dimension(90,24));

        typeBadge.setBorder(
                new EmptyBorder(2,6,2,6));

        switch(t.type){

            case "Deposit"  ->
                    typeBadge.setBackground(
                            GREEN_BADGE);

            case "Withdraw" ->
                    typeBadge.setBackground(
                            RED_BADGE);

            case "Delete"   ->
                    typeBadge.setBackground(
                            RED_BADGE);

            default ->
                    typeBadge.setBackground(
                            BLUE_BADGE);
        }

        JPanel badgeWrap =
                new JPanel(new BorderLayout());

        badgeWrap.setOpaque(false);

        badgeWrap.setPreferredSize(
                new Dimension(95,24));

        badgeWrap.add(typeBadge,
                BorderLayout.CENTER);

        String sign="";

        if(t.type.equals("Deposit"))
            sign="+";

        if(t.type.equals("Withdraw")
                || t.type.equals("Delete"))
            sign="-";

        JLabel amtLabel =
                new JLabel(
                        sign+"₱"+
                                String.format("%,.2f",
                                        t.amount));

        amtLabel.setFont(
                new Font("Segoe UI",
                        Font.BOLD,13));

        if(sign.equals("+"))
            amtLabel.setForeground(
                    new Color(50,160,80));

        else
            amtLabel.setForeground(
                    new Color(210,70,60));

        amtLabel.setHorizontalAlignment(
                JLabel.RIGHT);

        JPanel amtWrap =
                new JPanel(new BorderLayout());

        amtWrap.setOpaque(false);

        amtWrap.setPreferredSize(
                new Dimension(110,24));

        amtWrap.add(amtLabel,
                BorderLayout.EAST);

        JPanel left =
                new JPanel(new BorderLayout(8,0));

        left.setOpaque(false);

        left.add(logoLabel,
                BorderLayout.WEST);

        left.add(textPanel,
                BorderLayout.CENTER);

        card.add(left,
                BorderLayout.WEST);

        card.add(badgeWrap,
                BorderLayout.CENTER);

        card.add(amtWrap,
                BorderLayout.EAST);

        return card;
    }

    // RIGHT PANEL

    private JPanel rightPanel(){

        JPanel panel =
                new JPanel(new BorderLayout(0,16));

        panel.setOpaque(false);

        panel.add(totalCard(),
                BorderLayout.NORTH);

        panel.add(bankArea(),
                BorderLayout.CENTER);

        return panel;
    }

    private JPanel totalCard(){

        JPanel card =
                new JPanel(new BorderLayout(0,4));

        card.setBackground(GREEN_CARD);

        card.setBorder(
                new EmptyBorder(20,24,20,24));

        JLabel title =
                new JLabel("Total Assets:");

        title.setFont(
                new Font("Segoe UI",
                        Font.BOLD,16));

        title.setForeground(Color.WHITE);

        totalLabel =
                new JLabel("₱0.00");

        totalLabel.setFont(
                new Font("Segoe UI",
                        Font.BOLD,34));

        totalLabel.setForeground(Color.WHITE);

        card.add(title,
                BorderLayout.NORTH);

        card.add(totalLabel,
                BorderLayout.CENTER);

        return card;
    }

    private JPanel bankArea(){

        JPanel wrapper = new JPanel(new BorderLayout());

        wrapper.setBackground(BG_WHITE);

        wrapper.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_GRAY),
                new EmptyBorder(12,12,12,12)));

        JLabel header = new JLabel("Accounts");

        header.setFont(new Font("Segoe UI", Font.BOLD, 14));

        header.setBorder(new EmptyBorder(0,0,10,0));

        wrapper.add(header, BorderLayout.NORTH);


        // container that controls grid alignment
        JPanel container = new JPanel(new FlowLayout(
                FlowLayout.CENTER, 16, 16
        ));

        container.setOpaque(false);


        bankGrid = new JPanel(new GridLayout(0,3,16,16));

        bankGrid.setOpaque(false);


        GridBagConstraints gbc = new GridBagConstraints();

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;

        container.add(bankGrid, gbc);


        JScrollPane scroll = new JScrollPane(container);

        scroll.setBorder(null);

        scroll.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        scroll.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        scroll.getVerticalScrollBar().setUnitIncrement(12);


        wrapper.add(scroll, BorderLayout.CENTER);


        return wrapper;
    }

    private JPanel bankCard(BankAccount acc){

        JPanel card = new JPanel(new BorderLayout(0,8));

        card.setPreferredSize(new Dimension(130,130));

        card.setBackground(Color.WHITE);

        Border normalBorder =
                BorderFactory.createLineBorder(
                        new Color(225,225,225),1,true);

        Border hoverBorder =
                BorderFactory.createLineBorder(
                        new Color(170,190,180),1,true);

        card.setBorder(normalBorder);

        // logo
        JLabel logoLabel = new JLabel();

        logoLabel.setHorizontalAlignment(JLabel.CENTER);

        logoLabel.setVerticalAlignment(JLabel.CENTER);

        if(acc.logo!=null){

            logoLabel.setIcon(

                    new ImageIcon(

                            acc.logo.getImage()
                                    .getScaledInstance(
                                            48,48,
                                            Image.SCALE_SMOOTH)));
        }
        else{

            logoLabel.setText(

                    acc.bankName.substring(
                            0,
                            Math.min(2,
                                    acc.bankName.length())));

            logoLabel.setFont(
                    new Font("Segoe UI",
                            Font.BOLD,18));

            logoLabel.setForeground(
                    new Color(80,80,80));
        }

        // balance
        JLabel balLabel = new JLabel(

                "₱"+
                        String.format("%,.0f",
                                acc.balance),

                JLabel.CENTER);

        balLabel.setFont(
                new Font("Segoe UI",
                        Font.BOLD,14));

        balLabel.setForeground(
                new Color(60,60,60));

        // edit button
        JButton editBtn = new JButton("Edit");

        editBtn.setPreferredSize(
                new Dimension(60,24));

        editBtn.setFont(
                new Font("Segoe UI",
                        Font.BOLD,11));

        editBtn.setBackground(
                new Color(245,245,245));

        editBtn.setBorder(

                BorderFactory.createLineBorder(
                        new Color(220,220,220)));

        editBtn.setFocusPainted(false);

        editBtn.setCursor(
                new Cursor(Cursor.HAND_CURSOR));

        editBtn.addActionListener(e->{

            new AddAccountDialog(

                    (JFrame)
                            SwingUtilities
                                    .getWindowAncestor(this),

                    acc);

            refreshAll();
        });

        // hover effect (no layout shift)
        card.addMouseListener(

                new java.awt.event.MouseAdapter(){

                    public void mouseEntered(
                            java.awt.event.MouseEvent evt){

                        card.setBorder(hoverBorder);
                    }

                    public void mouseExited(
                            java.awt.event.MouseEvent evt){

                        card.setBorder(normalBorder);
                    }
                });

        card.add(editBtn, BorderLayout.NORTH);

        card.add(logoLabel, BorderLayout.CENTER);

        card.add(balLabel, BorderLayout.SOUTH);

        return card;
    }

    private JPanel addCard(){

        JPanel wrapper =
                new JPanel(new GridBagLayout());

        wrapper.setOpaque(false);

        JPanel card =
                new JPanel(new BorderLayout());

        card.setPreferredSize(
                new Dimension(130,130));

        card.setBackground(Color.WHITE);

        card.setBorder(

                BorderFactory.createLineBorder(

                        BORDER_GRAY,
                        1,true));

        JButton addBtn =
                new JButton("+");

        addBtn.setFont(

                new Font("Segoe UI",
                        Font.BOLD,28));

        addBtn.setForeground(
                new Color(150,150,150));

        addBtn.setContentAreaFilled(false);

        addBtn.setBorderPainted(false);

        addBtn.setFocusPainted(false);

        addBtn.setCursor(
                new Cursor(Cursor.HAND_CURSOR));

        addBtn.addActionListener(e->{

            new AddAccountDialog(

                    (JFrame)
                            SwingUtilities
                                    .getWindowAncestor(this));

            refreshAll();
        });

        card.add(addBtn,
                BorderLayout.CENTER);

        wrapper.add(card);

        return wrapper;
    }

    // REFRESH UI

    public void refreshAll(){

        bankGrid.removeAll();

        for(BankAccount a
                :AccountManager.accounts){

            bankGrid.add(
                    bankCard(a));
        }

        bankGrid.add(addCard());

        totalLabel.setText(

                "₱"+
                        String.format("%,.2f",

                                AccountManager
                                        .totalAssets()));

        transactionList.removeAll();

        java.util.List<Transaction>
                reversed=

                new java.util.ArrayList<>(

                        AccountManager.transactions);

        java.util.Collections.reverse(reversed);

        for(Transaction t:reversed){

            transactionList.add(
                    transactionItem(t));
        }

        revalidate();

        repaint();
    }
}