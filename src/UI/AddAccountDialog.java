package UI;

import Data_backend.AccountManager;
import Data_backend.BankAccount;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.ComboPopup;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class AddAccountDialog extends JDialog {

    private final JTextField nameField = new JTextField();
    private final JTextField balanceField = new JTextField();
    private final JLabel preview = new JLabel("No logo", JLabel.CENTER);

    private final JComboBox<String> presetDropdown = new JComboBox<>();
    private final Map<String, ImageIcon> presets = new LinkedHashMap<>();

    private ImageIcon logo;
    private BankAccount editing;

    public AddAccountDialog(JFrame parent) {
        super(parent,"Add Account",true);
        build();
        setVisible(true);
    }

    public AddAccountDialog(JFrame parent,BankAccount acc) {
        super(parent,"Edit Account",true);
        editing = acc;
        build();

        nameField.setText(acc.bankName);
        balanceField.setText(String.valueOf(acc.balance));

        logo = acc.logo;
        if(logo!=null) setPreview(logo);

        setVisible(true);
    }

    private void build(){

        loadPresets();

        setSize(320,420);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel root = new JPanel(new BorderLayout(10,10));
        root.setBorder(new EmptyBorder(16,16,16,16));
        root.setBackground(ThemeManager.card());
        setContentPane(root);
        getContentPane().setBackground(ThemeManager.card());

        root.add(formPanel(),BorderLayout.CENTER);
        root.add(buttonPanel(),BorderLayout.SOUTH);

        getRootPane().setDefaultButton(saveBtn);
    }

    private JPanel formPanel(){

        JPanel p = new JPanel(new GridLayout(0,1,6,6));
        p.setBackground(ThemeManager.card());

        preview.setPreferredSize(new Dimension(60,60));
        preview.setBorder(BorderFactory.createLineBorder(ThemeManager.cardBorder()));
        preview.setForeground(ThemeManager.text());
        preview.setBackground(ThemeManager.card());
        preview.setOpaque(true);

        JButton uploadBtn = new JButton("Upload Logo");
        uploadBtn.addActionListener(e->chooseImage());
        styleButton(uploadBtn);

        // ── nameField: was missing all dark-mode styling ──────────
        styleTextField(nameField);

        // ── balanceField ──────────────────────────────────────────
        styleTextField(balanceField);

        // ── presetDropdown: fix invisible arrow + blue highlight ──
        styleDarkComboBox(presetDropdown);

        p.add(label("Choose a preset / create a new Bank"));
        p.add(presetDropdown);

        p.add(label("Bank / Wallet Name"));
        p.add(nameField);

        p.add(label("Balance (₱)"));
        p.add(balanceField);

        p.add(uploadBtn);
        p.add(preview);

        return p;
    }

    /** Applies consistent dark-mode-aware styling to a JTextField. */
    private void styleTextField(JTextField field) {
        Color fieldBg = ThemeManager.isDark() ? new Color(50,50,50) : new Color(245,245,245);
        Color accent  = ThemeManager.isDark() ? new Color(80,160,100) : new Color(70,120,200);
        field.setBackground(fieldBg);
        field.setForeground(ThemeManager.text());
        field.setCaretColor(ThemeManager.text());
        // Use a simple line border; remove the JTextField's default blue focus ring
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeManager.cardBorder()),
                new EmptyBorder(4,6,4,6)));
        // Replace focus highlight with a theme-aware green ring instead of bright blue
        field.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override public void focusGained(java.awt.event.FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(accent, 2),
                        new EmptyBorder(3,5,3,5)));
            }
            @Override public void focusLost(java.awt.event.FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(ThemeManager.cardBorder()),
                        new EmptyBorder(4,6,4,6)));
            }
        });
    }

    /** Styles the Upload Logo button to match dark/light theme. */
    private void styleButton(JButton btn) {
        btn.setBackground(ThemeManager.isDark() ? new Color(50,50,50) : new Color(245,245,245));
        btn.setForeground(ThemeManager.text());
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeManager.cardBorder()),
                new EmptyBorder(4,8,4,8)));
    }

    /**
     * Applies a fully dark-themed UI to JComboBox:
     *  - Dark background / foreground on the button and list
     *  - Visible arrow rendered in light gray (so it shows on dark bg)
     *  - Green focus ring instead of the default bright blue
     */
    private void styleDarkComboBox(JComboBox<String> combo) {
        boolean dark = ThemeManager.isDark();
        Color fieldBg = dark ? new Color(50,50,50) : Color.WHITE;
        Color arrowFg = dark ? new Color(200,200,200) : new Color(60,60,60);
        Color accent  = dark ? new Color(80,160,100) : new Color(70,120,200);

        combo.setBackground(fieldBg);
        combo.setForeground(ThemeManager.text());
        combo.setFocusable(true);

        combo.setUI(new BasicComboBoxUI() {

            // ── Replace the arrow button with a hand-drawn dark one ──
            @Override
            protected JButton createArrowButton() {
                JButton btn = new JButton() {
                    @Override
                    protected void paintComponent(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                RenderingHints.VALUE_ANTIALIAS_ON);
                        // background matches field
                        g2.setColor(fieldBg);
                        g2.fillRect(0, 0, getWidth(), getHeight());
                        // separator line on left
                        g2.setColor(ThemeManager.cardBorder());
                        g2.drawLine(0, 2, 0, getHeight()-2);
                        // downward triangle in visible color
                        int cx = getWidth()/2, cy = getHeight()/2;
                        int[] xs = {cx-4, cx+4, cx};
                        int[] ys = {cy-2, cy-2, cy+3};
                        g2.setColor(arrowFg);
                        g2.fillPolygon(xs, ys, 3);
                        g2.dispose();
                    }
                };
                btn.setBorderPainted(false);
                btn.setContentAreaFilled(false);
                btn.setFocusPainted(false);
                return btn;
            }

            // ── Outer combo border (line + padding) ──────────────────
            @Override
            public void installUI(JComponent c) {
                super.installUI(c);
                combo.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(ThemeManager.cardBorder()),
                        new EmptyBorder(2,4,2,4)));
            }

            // ── Style the drop-down popup list ────────────────────────
            @Override
            protected ComboPopup createPopup() {
                BasicComboPopup popup = new BasicComboPopup((JComboBox) combo) {
                    @Override
                    protected void configureList() {
                        super.configureList();
                        list.setBackground(fieldBg);
                        list.setForeground(ThemeManager.text());
                        list.setSelectionBackground(accent);
                        list.setSelectionForeground(Color.WHITE);
                    }
                };
                popup.getList().setBackground(fieldBg);
                popup.getList().setForeground(ThemeManager.text());
                popup.getList().setSelectionBackground(accent);
                popup.getList().setSelectionForeground(Color.WHITE);
                popup.setBorder(BorderFactory.createLineBorder(ThemeManager.cardBorder()));
                return popup;
            }
        });

        // Green focus ring (replaces the default blue outline)
        combo.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override public void focusGained(java.awt.event.FocusEvent e) {
                combo.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(accent, 2),
                        new EmptyBorder(1,3,1,3)));
            }
            @Override public void focusLost(java.awt.event.FocusEvent e) {
                combo.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(ThemeManager.cardBorder()),
                        new EmptyBorder(2,4,2,4)));
            }
        });
    }

    private JButton saveBtn;

    private JPanel buttonPanel(){

        JPanel p;

        if(editing == null)

            p = new JPanel(new GridLayout(1,1));

        else

            p = new JPanel(new GridLayout(1,2,8,0));


        saveBtn =
                new JButton(editing==null ? "Add" : "Save");


        saveBtn.setBackground(new Color(30,110,50));

        saveBtn.setForeground(Color.WHITE);

        saveBtn.setFocusPainted(false);

        saveBtn.addActionListener(e -> save());


        p.add(saveBtn);


        if(editing != null){

            JButton deleteBtn =
                    new JButton("Delete");

            deleteBtn.setBackground(new Color(200,60,60));

            deleteBtn.setForeground(Color.WHITE);

            deleteBtn.setFocusPainted(false);

            deleteBtn.addActionListener(e -> delete());

            p.add(deleteBtn);
        }

        return p;
    }

    private void loadPresets(){

        presets.put("Select bank...", null);

        presets.put("GCash", loadIcon("gcash.png"));
        presets.put("Maya", loadIcon("maya.png"));
        presets.put("GoTyme", loadIcon("gotyme.png"));

        presets.put("BDO", loadIcon("bdo.png"));
        presets.put("BPI", loadIcon("bpi.png"));
        presets.put("MariBank", loadIcon("maribank.png"));
        presets.put("UnionBank", loadIcon("unionbank.png"));

        for(String name : presets.keySet())
            presetDropdown.addItem(name);

        presetDropdown.addActionListener(e -> {

            String selected = (String)presetDropdown.getSelectedItem();

            if(selected == null || presets.get(selected) == null){

                logo = null;

                preview.setIcon(null);
                preview.setText("No logo");

                nameField.setText("");

                return;
            }

            nameField.setText(selected);

            logo = presets.get(selected);

            setPreview(logo);
        });
    }

    private ImageIcon loadIcon(String fileName){

        java.net.URL url =
                getClass().getResource("/bank_icons/"+fileName);

        if(url==null){
            System.out.println("Missing icon: "+fileName);
            return null;
        }

        return new ImageIcon(url);
    }

    private void save(){

        String name = nameField.getText().trim();

        if(name.isEmpty()){
            JOptionPane.showMessageDialog(this,"Please enter a bank name.");
            return;
        }

        double balance;
        String text = balanceField.getText().trim();
        if(text.isEmpty())
            balance = 0;
        else{
            try{
                balance = Double.parseDouble(text);
                if(balance < 0){
                    JOptionPane.showMessageDialog(
                            this,
                            "Balance cannot be negative."
                    );
                    return;
                }
            }
            catch(NumberFormatException ex){
                JOptionPane.showMessageDialog(
                        this,
                        "Please enter a valid balance."
                );
                return;
            }
        }

        if(editing==null){

            if(AccountManager.accountExists(name)){
                JOptionPane.showMessageDialog(this,"Account already exists.");
                return;
            }

            AccountManager.addAccount(
                    new BankAccount(name,balance,logo)
            );
        }else {
            boolean changed =
                    !editing.bankName.equals(name)
                            || editing.balance != balance
                            || !java.util.Objects.equals(editing.logo, logo);
            if(!changed){
                dispose(); // close dialog but do nothing
                return;
            }
            double oldBalance = editing.balance;

            String oldName = editing.bankName;

            ImageIcon oldLogo = editing.logo;

            editing.bankName = name;
            editing.balance = balance;
            editing.logo = logo;

            AccountManager.updateAccount(

                    editing,

                    oldBalance,

                    oldName,

                    oldLogo

            );
        }
        dispose();
    }

    private void delete(){

        int confirm =
                JOptionPane.showConfirmDialog(
                        this,
                        "Delete "+editing.bankName+"?",
                        "Confirm Delete",
                        JOptionPane.YES_NO_OPTION
                );

        if(confirm==JOptionPane.YES_OPTION){
            AccountManager.deleteAccount(editing);
            dispose();
        }
    }

    private void chooseImage(){

        JFileChooser fc = new JFileChooser();

        fc.setFileFilter(
                new javax.swing.filechooser.FileNameExtensionFilter(
                        "Images",
                        "png","jpg","jpeg"
                )
        );

        if(fc.showOpenDialog(this)==JFileChooser.APPROVE_OPTION){

            logo = new ImageIcon(
                    fc.getSelectedFile().getAbsolutePath()
            );
            setPreview(logo);
        }
    }

    private void setPreview(ImageIcon icon){

        preview.setText("");
        preview.setIcon(
                new ImageIcon(
                        icon.getImage().getScaledInstance(
                                50,50,Image.SCALE_SMOOTH
                        )
                )
        );
    }

    private JLabel label(String text){

        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI",Font.BOLD,12));
        l.setForeground(ThemeManager.text());
        return l;
    }
}