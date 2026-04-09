import UI.DashboardPanel;

import javax.swing.*;
import java.awt.*;

public class Main extends JFrame {

    public Main() {
        setTitle("Financial Tracker");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 600);
        setMinimumSize(new Dimension(900, 550));
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane(JTabbedPane.LEFT);
        tabs.addTab("Dashboard", new DashboardPanel());
        tabs.addTab("Accounts",  emptyPanel("Accounts"));
        tabs.addTab("Settings",  emptyPanel("Settings"));
        tabs.addTab("Help",      emptyPanel("Help"));

        styleTabs(tabs);
        add(tabs);
    }

    JPanel emptyPanel(String title) {
        JPanel p = new JPanel(new BorderLayout());
        JLabel label = new JLabel(title, SwingConstants.CENTER);
        label.setFont(new Font("Segoe UI", Font.BOLD, 22));
        p.add(label);
        return p;
    }

    void styleTabs(JTabbedPane tabs) {
        Color sidebarGreen = new Color(30, 110, 50);
        Color textColor    = Color.WHITE;
        tabs.setBackground(sidebarGreen);

        for (int i = 0; i < tabs.getTabCount(); i++) {
            JPanel tab = new JPanel(new GridBagLayout());
            tab.setPreferredSize(new Dimension(140, 55));
            tab.setBackground(sidebarGreen);
            tab.setOpaque(true);

            JLabel label = new JLabel(tabs.getTitleAt(i));
            label.setFont(new Font("Segoe UI", Font.BOLD, 15));
            label.setForeground(textColor);
            tab.add(label);

            tabs.setTabComponentAt(i, tab);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Main().setVisible(true));
    }
}
