package UI;

import Data_backend.AccountManager;
import Data_backend.BankAccount;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class AddAccountDialog extends JDialog {



    private final JTextField nameField = new JTextField();
    private final JTextField balanceField = new JTextField();
    private final JLabel preview = new JLabel("No logo", JLabel.CENTER);

    private final JComboBox<String> presetDropdown = new JComboBox<>();
    private final Map<String, ImageIcon> presets = new LinkedHashMap<>();

    private static final double FEE_RATE = 0.01; // 1% fee

    private ImageIcon logo;
    private BankAccount editing;

    public AddAccountDialog(JFrame parent) {
        super(parent,"Add Account",true);
        build();
        setVisible(true);
    }

    public AddAccountDialog(JFrame parent, BankAccount acc) {
        super(parent,"Edit Account",true);
        editing = acc;
        build();

        nameField.setText(acc.bankName);
        // Balance is managed via sub-accounts; hide the field when editing
        balanceField.setVisible(false);
        balanceField.getParent(); // field is in the form but hidden

        logo = acc.logo;
        if(logo!=null) setPreview(logo);

        setVisible(true);
    }

    private void build(){

        loadPresets();

        setSize(320,440);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel root = new JPanel(new BorderLayout(10,10));
        root.setBorder(new EmptyBorder(16,16,16,16));
        root.setBackground(ThemeManager.card());
        getContentPane().setBackground(ThemeManager.card());
        setContentPane(root);

        root.add(formPanel(), BorderLayout.CENTER);
        root.add(buttonPanel(), BorderLayout.SOUTH);

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
        uploadBtn.setBackground(ThemeManager.isDark() ? new Color(50,50,50) : new Color(245,245,245));
        uploadBtn.setForeground(ThemeManager.text());
        uploadBtn.setFocusPainted(false);
        uploadBtn.setBorder(BorderFactory.createLineBorder(ThemeManager.cardBorder()));
        uploadBtn.addActionListener(e -> chooseImage());

        presetDropdown.setBackground(ThemeManager.isDark() ? new Color(50,50,50) : Color.WHITE);
        presetDropdown.setForeground(ThemeManager.text());

        nameField.setBackground(ThemeManager.isDark() ? new Color(50,50,50) : Color.WHITE);
        nameField.setForeground(ThemeManager.text());
        nameField.setCaretColor(ThemeManager.text());
        nameField.setBorder(BorderFactory.createLineBorder(ThemeManager.cardBorder()));

        balanceField.setBackground(ThemeManager.isDark() ? new Color(50,50,50) : Color.WHITE);
        balanceField.setForeground(ThemeManager.text());
        balanceField.setCaretColor(ThemeManager.text());
        balanceField.setBorder(BorderFactory.createLineBorder(ThemeManager.cardBorder()));

        // Fee notice label (only shown when adding, not editing)
        JLabel feeNote = new JLabel("<html><i>A 1% transaction fee will be applied to the initial balance.</i></html>");
        feeNote.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        feeNote.setForeground(new Color(130, 130, 130));

        p.add(label("Choose a preset / create a new Bank"));
        p.add(presetDropdown);

        p.add(label("Bank / Wallet Name"));
        p.add(nameField);

        p.add(label("Balance (₱)"));
        p.add(balanceField);

        if (editing == null) {
            p.add(feeNote);
        }

        p.add(uploadBtn);
        p.add(preview);

        return p;
    }

    private JButton saveBtn;

    private JPanel buttonPanel(){

        JPanel p;

        if(editing == null)
            p = new JPanel(new GridLayout(1,1));
        else
            p = new JPanel(new GridLayout(1,2,8,0));

        saveBtn = new JButton(editing==null ? "Add" : "Save");
        saveBtn.setBackground(new Color(30,110,50));
        saveBtn.setForeground(Color.WHITE);
        saveBtn.setFocusPainted(false);
        saveBtn.addActionListener(e -> save());

        p.add(saveBtn);

        if(editing != null){

            JButton deleteBtn = new JButton("Delete");
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

        java.net.URL url = getClass().getResource("/bank_icons/"+fileName);

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

        double rawBalance = 0;
        if (editing == null) {
            // Only process balance when adding a new account
            String text = balanceField.getText().trim();
            if (!text.isEmpty()) {
                try {
                    rawBalance = Double.parseDouble(text);
                    if (rawBalance < 0) {
                        JOptionPane.showMessageDialog(this, "Balance cannot be negative.");
                        return;
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Please enter a valid balance.");
                    return;
                }
            }
        }

        if(editing==null){

            if(AccountManager.accountExists(name)){
                JOptionPane.showMessageDialog(this,"Account already exists.");
                return;
            }

            // Apply 1% fee to the initial deposit
            double afterFee = rawBalance * (1.0 - FEE_RATE);
            afterFee = Math.round(afterFee * 100.0) / 100.0;

            // Show fee confirmation if balance is non-zero
            if (rawBalance > 0) {
                int confirm = JOptionPane.showConfirmDialog(this,
                        String.format(
                                "A 1%% fee will be deducted from your initial deposit.\n\nEntered:  ₱%,.2f\nFee (1%%): ₱%,.2f\nFinal:      ₱%,.2f\n\nProceed?",
                                rawBalance, rawBalance * FEE_RATE, afterFee),
                        "Confirm Transaction Fee",
                        JOptionPane.YES_NO_OPTION);

                if (confirm != JOptionPane.YES_OPTION) return;
            }

            // Bank carries no direct balance — initial amount (after fee) goes to "Wallet" sub-account
            AccountManager.addAccount(new BankAccount(name, logo), afterFee);

        } else {

            boolean nameChanged = !editing.bankName.equalsIgnoreCase(name);

            if(nameChanged && AccountManager.accountExists(name)){
                JOptionPane.showMessageDialog(this, "Another account already uses this name.");
                return;
            }

            boolean changed =
                    !editing.bankName.equals(name)
                            || !java.util.Objects.equals(editing.logo, logo);

            if(!changed){
                dispose();
                return;
            }

            String oldName    = editing.bankName;
            ImageIcon oldLogo = editing.logo;

            editing.bankName = name;
            editing.logo     = logo;

            AccountManager.updateAccount(editing, oldName, oldLogo);
        }
        dispose();
    }

    private void delete(){

        int confirm = JOptionPane.showConfirmDialog(
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
        fc.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                "Images","png","jpg","jpeg"
        ));

        if(fc.showOpenDialog(this)==JFileChooser.APPROVE_OPTION){
            logo = new ImageIcon(fc.getSelectedFile().getAbsolutePath());
            setPreview(logo);
        }
    }

    private void setPreview(ImageIcon icon){
        preview.setText("");
        preview.setIcon(new ImageIcon(icon.getImage().getScaledInstance(50,50,Image.SCALE_SMOOTH)));
    }

    private JLabel label(String text){
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI",Font.BOLD,12));
        l.setForeground(ThemeManager.text());
        return l;
    }
}
