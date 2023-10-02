package User.Vue;

import User.Source.RoundBorder;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;

public class Confirmation extends JDialog{
    private JPanel ConfirmationPanel;
    private JTextField ValidationField;
    private JButton ValidationButton;
    private JLabel Text;

    //Page de confirmation lorsque le code est envoyé par mail

    public Confirmation(int code,PrintWriter out,String email,String username,String phone,String password) throws UnsupportedLookAndFeelException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        super();
        setTitle("CHAT CSC");
        setContentPane(ConfirmationPanel);
        this.setResizable(false);
        this.setSize(500, 500);
        ValidationField.setHorizontalAlignment(JTextField.CENTER);
        ValidationField.setBorder(new RoundBorder(Color.GRAY,10));
        ValidationButton.addActionListener(e -> {
                String codeConfirmation = ValidationField.getText();
                if (codeConfirmation.isEmpty()) {
                    //Si le champ est vide
                    JOptionPane.showMessageDialog(Confirmation.this, "Please fill all fields");
                } else if (Integer.parseInt(codeConfirmation)!=code) {
                    //Si le code est faux
                    JOptionPane.showMessageDialog(Confirmation.this, "Invalid Code");
                }else{
                    //Si le code est bon ajout d'un nouvel utilisateur
                    dispose();
                    out.println("1&"+email+"&"+username+"~"+phone+"~"+email+"~"+password);
                    out.flush();
                }
            });
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setVisible(true);
    }
}
