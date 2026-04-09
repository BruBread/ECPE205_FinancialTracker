package UI;

import Data_backend.AccountManager;
import Data_backend.BankAccount;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;

public class AddAccountDialog extends JDialog {

    private final JTextField  nameField    = new JTextField();
    private final JTextField  balanceField = new JTextField();
    private final JLabel      preview      = new JLabel("No logo", JLabel.CENTER);
    private ImageIcon         logo;
    private BankAccount       editing;

    public AddAccountDialog(JFrame parent) {
        super(parent, "Add Account", true);
        build();
        setVisible(true);
    }

    public AddAccountDialog(JFrame parent, BankAccount acc) {
        super(parent, "Edit Account", true);
        editing = acc;
        build();
        nameField.setText(acc.bankName);
        balanceField.setText(String.valueOf(acc.balance));
        logo = acc.logo;
        if (logo != null) setPreview(logo);
        setVisible(true);
    }


    private void build() {
        setSize(320, 400);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(new EmptyBorder(16, 16, 16, 16));
        setContentPane(root);

        root.add(formPanel(), BorderLayout.CENTER);
        root.add(buttonPanel(), BorderLayout.SOUTH);
    }

    private JPanel formPanel() {
        JPanel p = new JPanel(new GridLayout(0, 1, 6, 6));

        preview.setPreferredSize(new Dimension(60, 60));
        preview.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        JButton uploadBtn = new JButton("Upload Logo");
        uploadBtn.addActionListener(e -> chooseImage());

        p.add(label("Bank / Wallet Name"));
        p.add(nameField);
        p.add(label("Balance (₱)"));
        p.add(balanceField);
        p.add(uploadBtn);
        p.add(preview);

        return p;
    }

    private JPanel buttonPanel() {
        JPanel p = new JPanel(new GridLayout(1, 2, 8, 0));

        JButton saveBtn = new JButton(editing == null ? "Add" : "Save");
        saveBtn.setBackground(new Color(30, 110, 50));
        saveBtn.setForeground(Color.WHITE);
        saveBtn.setFocusPainted(false);
        saveBtn.addActionListener(e -> save());

        JButton deleteBtn = new JButton("Delete");
        deleteBtn.setBackground(new Color(200, 60, 60));
        deleteBtn.setForeground(Color.WHITE);
        deleteBtn.setFocusPainted(false);
        deleteBtn.setEnabled(editing != null);
        deleteBtn.addActionListener(e -> delete());

        p.add(saveBtn);
        p.add(deleteBtn);
        return p;
    }

    private void save() {
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a bank name.");
            return;
        }
        double balance;
        try {
            balance = Double.parseDouble(balanceField.getText().trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid balance.");
            return;
        }

        if (editing == null) {
            AccountManager.addAccount(new BankAccount(name, balance, logo));
        } else {
            double oldBalance = editing.balance;
            editing.bankName  = name;
            editing.balance   = balance;
            editing.logo      = logo;
            AccountManager.updateAccount(editing, oldBalance);
        }
        dispose();
    }

    private void delete() {
        int confirm = JOptionPane.showConfirmDialog(
                this, "Delete " + editing.bankName + "?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            AccountManager.deleteAccount(editing);
            dispose();
        }
    }

    private void chooseImage() {
        JFileChooser fc = new JFileChooser();
        fc.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                "Images", "png", "jpg", "jpeg", "gif"));
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File f = fc.getSelectedFile();
            logo = new ImageIcon(f.getAbsolutePath());
            setPreview(logo);
        }
    }

    private void setPreview(ImageIcon icon) {
        preview.setText("");
        preview.setIcon(new ImageIcon(
                icon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH)));
    }

    private JLabel label(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 12));
        return l;
    }
}
