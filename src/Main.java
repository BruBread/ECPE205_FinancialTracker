import UI.*;
import Data_backend.AccountManager;

import javax.swing.*;
import java.awt.*;

public class Main extends JFrame {

    public Main() {
        setTitle("Financial Tracker");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1230, 600);
        setResizable(false);
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane(JTabbedPane.LEFT);
        tabs.addTab("Dashboard", new DashboardPanel());
        tabs.addTab("Accounts", new AccountsPanel());
        tabs.addTab("Settings", new SettingsPanel());
        tabs.addTab("Help", new HelpPanel());

        styleTabs(tabs);
        add(tabs);
        ThemeManager.addListener(() -> {
            tabs.setBackground(new Color(30,110,50));
            getContentPane().setBackground(ThemeManager.bg());
            SwingUtilities.updateComponentTreeUI(this);
            repaint();
        });
    }

    JPanel emptyPanel(String title) {
        JPanel p = new JPanel(new BorderLayout());
        JLabel label = new JLabel(title, SwingConstants.CENTER);
        label.setFont(new Font("Segoe UI", Font.BOLD, 22));
        p.add(label);
        return p;
    }

    void styleTabs(JTabbedPane tabs) {

        Color sidebarGreen = new Color(30,110,50);
        Color selectedGreen = new Color(24,90,42);

        tabs.setBackground(sidebarGreen);

        tabs.setUI(new javax.swing.plaf.basic.BasicTabbedPaneUI() {

            @Override
            protected void paintContentBorder(Graphics g,
                                              int tabPlacement,
                                              int selectedIndex) {

                // removes white border around content
            }

            @Override
            protected void paintTabBackground(Graphics g,
                                              int tabPlacement,
                                              int tabIndex,
                                              int x, int y,
                                              int w, int h,
                                              boolean isSelected) {

                g.setColor(
                        isSelected
                                ? selectedGreen
                                : sidebarGreen
                );

                g.fillRect(x, y, w, h);
            }

            @Override
            protected void paintFocusIndicator(Graphics g,
                                               int tabPlacement,
                                               Rectangle[] rects,
                                               int tabIndex,
                                               Rectangle iconRect,
                                               Rectangle textRect,
                                               boolean isSelected) {

                // removes dotted focus box
            }
        });

        for (int i = 0; i < tabs.getTabCount(); i++) {

            JPanel tab = new JPanel(new GridBagLayout());

            tab.setOpaque(false);

            tab.setPreferredSize(
                    new Dimension(140, 55));

            JLabel label =
                    new JLabel(
                            tabs.getTitleAt(i));

            label.setFont(
                    new Font("Segoe UI",
                            Font.BOLD,
                            15));

            label.setForeground(Color.WHITE);

            tab.add(label);

            tabs.setTabComponentAt(i, tab);
        }
    }


    public static void main(String[] args) {
        AccountManager.loadData();
        SwingUtilities.invokeLater(() -> new Main().setVisible(true));
    }
}
