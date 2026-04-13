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
        setContentPane(root);

        root.add(formPanel(),BorderLayout.CENTER);
        root.add(buttonPanel(),BorderLayout.SOUTH);

        getRootPane().setDefaultButton(saveBtn);
    }

    private JPanel formPanel(){

        JPanel p = new JPanel(new GridLayout(0,1,6,6));

        preview.setPreferredSize(new Dimension(60,60));
        preview.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        JButton uploadBtn = new JButton("Upload Logo");
        uploadBtn.addActionListener(e->chooseImage());

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

    private JButton saveBtn;

    private JPanel buttonPanel(){

        JPanel p = new JPanel(new GridLayout(1,2,8,0));

        saveBtn = new JButton(editing==null ? "Add" : "Save");

        saveBtn.setBackground(new Color(30,110,50));
        saveBtn.setForeground(Color.WHITE);
        saveBtn.setFocusPainted(false);
        saveBtn.addActionListener(e -> save());

        JButton deleteBtn = new JButton("Delete");

        deleteBtn.setBackground(new Color(200,60,60));
        deleteBtn.setForeground(Color.WHITE);
        deleteBtn.setFocusPainted(false);
        deleteBtn.setEnabled(editing!=null);
        deleteBtn.addActionListener(e -> delete());

        p.add(saveBtn);
        p.add(deleteBtn);

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
            editing.bankName = name;
            editing.balance = balance;
            editing.logo = logo;
            AccountManager.updateAccount(editing, oldBalance);
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

        return l;
    }
}