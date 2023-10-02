  package User.Vue;

        import User.Source.RoundBorder;

        import javax.swing.*;
        import java.awt.*;
        import java.io.PrintWriter;

//Page de login
public class Form extends JDialog {

    private JTextField emailTextField;

    private JPasswordField passwordField;
    private JButton LoginButton;
    private JButton SignUpButton;
    private JPanel loginPanel;


    public Form(PrintWriter out) throws UnsupportedLookAndFeelException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        super();
        setLocation(300,300);
        setTitle("CHAT CSC");
        setContentPane(loginPanel);
        this.setResizable(false);
        this.setSize(500, 500);
        emailTextField.setBorder(new RoundBorder(Color.GRAY,10));
        passwordField.setBorder(new RoundBorder(Color.GRAY,10));

        //Bouton pour s'identifier
        LoginButton.addActionListener(e -> {
            String email = emailTextField.getText();
            String password = String.valueOf(passwordField.getPassword());
            if (email.isEmpty() || password.isEmpty()){
                JOptionPane.showMessageDialog(Form.this, "Please fill all fields");
            }else{
                out.println("0&" + email + "&" + password);
                out.flush();
                dispose();
            }
        });
        //Bouton pour creer un compte
        SignUpButton.addActionListener(e -> {
            try {
                dispose();
                //Page d'inscription
                InscriptionPage inscriptionPage = new InscriptionPage(out);
            } catch (UnsupportedLookAndFeelException | IllegalAccessException | InstantiationException |
                     ClassNotFoundException ex) {
                throw new RuntimeException(ex);
            }
        });
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setVisible(true);
    }
    //erreur de champs
    public void ErrorConnection(){
        JOptionPane.showMessageDialog(Form.this, "Email or password Invalid", "Try again", JOptionPane.ERROR_MESSAGE);
    }
    //Si l'utilisteur est ban
      public void ErrorBan(){
          JOptionPane.showMessageDialog(Form.this, "You are ban!", "Ban", JOptionPane.ERROR_MESSAGE);
      }
}

