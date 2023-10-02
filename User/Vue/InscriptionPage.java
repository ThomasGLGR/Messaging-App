package User.Vue;

import User.Source.GmailSender;
import User.Source.RoundBorder;

import javax.mail.MessagingException;
import javax.swing.*;
import java.awt.*;
import java.io.PrintWriter;
import java.util.Random;

public class InscriptionPage extends JDialog {
    private JPanel loginPanel;
    private JTextField textField1;
    private JTextField emailTextField1;
    private JPasswordField passwordField1;
    private JPasswordField passwordField2;
    private JButton ConfirmSignUpButton;
    private JLabel confirmationMDP;
    private JTextField phoneField;

    public InscriptionPage(PrintWriter out) throws UnsupportedLookAndFeelException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        //page d'inscription pour un nouvel utilisateur
        super();
        setTitle("CHAT CSC");
        setContentPane(loginPanel);
        this.setResizable(false);
        this.setSize(500, 500);
        this.setVisible(true);
        textField1.setBorder(new RoundBorder(Color.GRAY,10));
        emailTextField1.setBorder(new RoundBorder(Color.GRAY,10));
        passwordField1.setBorder(new RoundBorder(Color.GRAY,10));
        passwordField2.setBorder(new RoundBorder(Color.GRAY,10));
        phoneField.setBorder(new RoundBorder(Color.GRAY,10));

        ConfirmSignUpButton.addActionListener(e -> {
            String username = textField1.getText();
            String email = emailTextField1.getText();
            String phone = phoneField.getText();
            String password = String.valueOf(passwordField1.getPassword());
            String confirmationPassword = String.valueOf(passwordField2.getPassword());

            //Verification si tout les conditions sont valide
            if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmationPassword.isEmpty()) {
                JOptionPane.showMessageDialog(InscriptionPage.this, "Please fill all fields");
            } else if (!password.equals(confirmationPassword)) {
                JOptionPane.showMessageDialog(InscriptionPage.this, "Passwords do not match");
            } else if (username.length()>17){
                JOptionPane.showMessageDialog(InscriptionPage.this, "Username is too long");
            }else if(password.length()<6){
                JOptionPane.showMessageDialog(InscriptionPage.this, "Password is too short");
            }else {
                try {
                    //Envoie d'un code aléatoire par mail
                    Random rand = new Random();
                    int code = rand.nextInt(900001)+100000;
                    GmailSender.sendConfirmationMail(email, String.valueOf(code));
                    dispose();
                    //ouverture de la page pour confirmer le code
                    Confirmation confirmation =new Confirmation(code,out,email,username,phone,password);
                }
                catch (MessagingException ex) {
                    throw new RuntimeException(ex);
                } catch (UnsupportedLookAndFeelException ex) {
                    throw new RuntimeException(ex);
                } catch (ClassNotFoundException ex) {
                    throw new RuntimeException(ex);
                } catch (InstantiationException ex) {
                    throw new RuntimeException(ex);
                } catch (IllegalAccessException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);


    }
}

