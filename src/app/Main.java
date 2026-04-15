package app;

import UI.AccountsPanel;
import UI.DashboardPanel;
import Data_backend.AccountManager;
import UI.HelpPanel;
import UI.SettingsPanel;


import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.awt.image.BufferedImage;

public class Main extends JFrame {

    ImageIcon dashIcon;
    ImageIcon accIcon;
    ImageIcon settIcon;
    ImageIcon helpIcon;

    public Main() {
        setTitle("Financial Tracker");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1230, 600);
        setResizable(false);
        setLocationRelativeTo(null);

        dashIcon = loadIcon("/icons/dashboard.png");
        accIcon = loadIcon("/icons/account.png");
        settIcon = loadIcon("/icons/settings.png");
        helpIcon = loadIcon("/icons/help.png");

        JTabbedPane tabs = new JTabbedPane(JTabbedPane.LEFT);

        tabs.addTab("Dashboard", dashIcon, new DashboardPanel());
        tabs.addTab("Accounts ", accIcon, new AccountsPanel());
        tabs.addTab("Settings ", settIcon, new SettingsPanel());
        tabs.addTab("Help", helpIcon, new HelpPanel());

        styleTabs(tabs);
        add(tabs);

        UI.ThemeManager.addListener(() -> {
            tabs.setBackground(new Color(30,110,50));
            getContentPane().setBackground(UI.ThemeManager.bg());
            SwingUtilities.updateComponentTreeUI(this);
            repaint();
        });
    }

    // ✅ CLEAN ICON LOADER
    private ImageIcon loadIcon(String path) {
        try {
            URL iconURL = getClass().getResource(path);
            if (iconURL == null) {
                throw new IllegalStateException("Missing resource: " + path);
            }

            ImageIcon originalIcon = new ImageIcon(iconURL);
            Image img = originalIcon.getImage();

            int size = 35;

            BufferedImage buffered = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = buffered.createGraphics();

            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);

            g2.drawImage(img, 0, 0, size, size, null);
            g2.dispose();

            return new ImageIcon(buffered);

        } catch (Exception e) {
            System.err.println("Error loading icon: " + path + " -> " + e.getMessage());
            return new ImageIcon();
        }
    }

    void styleTabs(JTabbedPane tabs) {

        Color sidebarGreen = new Color(30,110,50);
        Color selectedGreen = new Color(24,90,42);

        tabs.setBackground(sidebarGreen);

        tabs.setUI(new javax.swing.plaf.basic.BasicTabbedPaneUI() {

            @Override
            protected void paintContentBorder(Graphics g, int tabPlacement, int selectedIndex) {}

            @Override
            protected void paintTabBackground(Graphics g, int tabPlacement,
                                              int tabIndex, int x, int y,
                                              int w, int h, boolean isSelected) {

                g.setColor(isSelected ? selectedGreen : sidebarGreen);
                g.fillRect(x, y, w, h);
            }

            @Override
            protected void paintFocusIndicator(Graphics g, int tabPlacement,
                                               Rectangle[] rects, int tabIndex,
                                               Rectangle iconRect, Rectangle textRect,
                                               boolean isSelected) {}
        });

        for (int i = 0; i < tabs.getTabCount(); i++) {

            JPanel tab = new JPanel();
            tab.setOpaque(false);
            tab.setPreferredSize(new Dimension(140, 55));
            tab.setLayout(new BorderLayout());

            JLabel label;

            if (i == 0) {
                label = new JLabel(tabs.getTitleAt(i), dashIcon, JLabel.LEFT);
            } else if (i == 1) {
                label = new JLabel(tabs.getTitleAt(i), accIcon, JLabel.LEFT);
            } else if (i == 2) {
                label = new JLabel(tabs.getTitleAt(i), settIcon, JLabel.LEFT);
            } else if (i == 3) {
                label = new JLabel(tabs.getTitleAt(i), helpIcon, JLabel.LEFT);
            } else {
                label = new JLabel(tabs.getTitleAt(i));
            }

            label.setFont(new Font("Segoe UI", Font.BOLD, 15));
            label.setForeground(Color.WHITE);
            label.setIconTextGap(8);

            tab.add(label, BorderLayout.WEST); // ✅ LEFT ALIGN EVERYTHING

            tabs.setTabComponentAt(i, tab);
        }
    }

    public static void main(String[] args) {
        AccountManager.loadData();
        SwingUtilities.invokeLater(() -> new Main().setVisible(true));
    }
}