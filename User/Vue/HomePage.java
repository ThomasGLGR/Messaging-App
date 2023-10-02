package User.Vue;

import User.Modele.MessageChat;
import User.Modele.User;
import User.Source.JPanelPseudo;
import User.Source.LectureAudio;
import User.Source.RoundBorder;
import User.Source.VoiceRecorder;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.awt.font.TextAttribute;
import java.awt.geom.RoundRectangle2D;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.file.Files;
import java.util.Base64;

public class HomePage extends JDialog {
    private JPanel panel1;
    private User user;
    private JLabel onlinePerson;

    private JLabel offlinePerson;
    private JLabel onlineLabel;
    private JLabel nameLabel;
    private JButton changeStatutButton;
    private JTextField textField;
    private JPanel OnlinePanel;
    private JPanel OfflinePanel;
    private JPanel BanPanel;
    private JPanel chatAreaPanel;
    private JScrollPane chatAreaScroll;
    private JPanel AwayPanel;
    private JSplitPane SplitPanel;
    private JButton pictureButton;
    private JButton AudioButton;
    private JButton AppelButton;
    private JButton statButton;

    private final LectureAudio audio = new LectureAudio();




    private ArrayList<JPanelPseudo> jpOnline = new ArrayList<>();
    private ArrayList<JPanelPseudo> jpOffline = new ArrayList<>();
    private ArrayList<JPanelPseudo> jpBan = new ArrayList<>();
    private ArrayList<JPanelPseudo> jpAway = new ArrayList<>();

    private ArrayList<MessageChat> listMessageChat = new ArrayList<>();

    public boolean messageSent = false;
    private String listComboBox;
    private boolean BlockBoucle = false;
    private int count;
    private Timer timer;

    private final VoiceRecorder voiceRecorder=new VoiceRecorder();
    public HomePage() throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        super();
    }

    public void HomePageStart(User u, PrintWriter out, VideoCall videoCall,Statistics stats) {
        //Initialisation des données de bases
        this.user = u;
        setTitle("CHAT CSC");
        setContentPane(panel1);
        this.setResizable(true);
        this.setSize(700, 700);
        this.setVisible(true);
        textField.setBorder(new RoundBorder(Color.GRAY, 10));
        SplitPanel.setOneTouchExpandable(true);
        SplitPanel.setDividerLocation(0.65);
        out.println("3&" + user.getId() + "&Nouveau Message");
        out.flush();

        out.println("6&" + user.getUsername() + "&Affiche Status");
        out.flush();
        statButton.setEnabled(false);
        statButton.setVisible(false);

        //bouton des stats qui s'affiche si on est admin
        if (user.getPermission().equals("admin")){
            statButton.setEnabled(true);
            statButton.setVisible(true);
        }
        startTimer(out);

        //Champ pour écrire un message
        textField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                // Check if the key pressed is the enter key
                if (e.getKeyCode() == KeyEvent.VK_ENTER && !textField.getText().isEmpty()) {
                    timer.cancel();
                    out.println("2&" + user.getId() + "&" + textField.getText());
                    out.flush();
                    user.setStatus("online");
                    out.println("4&" + user.getUsername() + "&" + user.getStatus());
                    out.flush();
                    jpOnline = new ArrayList<>();
                    jpOffline = new ArrayList<>();
                    jpBan = new ArrayList<>();
                    jpAway = new ArrayList<>();
                    out.println("6&" + user.getUsername() + "&AfficheStatut");
                    out.flush();
                    textField.setText("");
                    messageSent = true; // mark that a message has been sent
                    try {
                        audio.sendMessage();
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }

                    out.println("3&" + user.getId() + "&Nouveau Message");
                    out.flush();

                    timerRestart(out);

                }

            }
        });

        //Bouton pour changer de statut
        changeStatutButton.addActionListener(e ->

        {
            if (user.getStatus().equals("online")) {
                user.setStatus("offline");
            } else {
                user.setStatus("online");
            }
            jpOnline = new ArrayList<>();
            jpOffline = new ArrayList<>();
            jpBan = new ArrayList<>();
            jpAway = new ArrayList<>();
            out.println("4&" + user.getUsername() + "&" + user.getStatus());
            out.flush();
            out.println("6&" + user.getUsername() + "&Affiche Status");
            out.flush();
        });
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Images", "jpg", "jpeg", "png", "gif");
        fileChooser.setFileFilter(filter);

        //Bouton pour poster une image
        pictureButton.addActionListener(e -> {
            int returnVal = fileChooser.showOpenDialog(null);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                out.println("2&"+user.getId()+"&##"+convertFileToBase64String(file));

                out.flush();
                out.println("3&" + user.getId() + "&Nouveau Message");
                out.flush();
                try {
                    audio.sendMessage();

                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        //Bouton pour envoyer un audio
        AudioButton.addActionListener(e -> {
            voiceRecorder.recordAudio();
            if (!voiceRecorder.getBool()){
                AudioButton.setBackground(new Color(183, 58, 54));
            }else{
                AudioButton.setBackground(new Color(24,83,79));
                try {
                    audio.sendMessage();
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
                out.println("2&"+user.getId()+"&%%"+convertFileToBase64String(new File("User/Ressource/record.wav")));
                out.flush();
                out.println("3&" + user.getId() + "&Nouveau Message");
                out.flush();
            }
        });

        //Bouton pour lancer lappel video
        AppelButton.addActionListener(e -> {
            try {
                videoCall.Start();
            } catch (LineUnavailableException ex) {
                throw new RuntimeException(ex);
            }
            out.println("8&"+user.getUsername()+"&START");
                out.flush();
        });

        //Bouton pour afficher les stats
        statButton.addActionListener(e -> {
                stats.Start();
                out.println("11&Stat&Stat");
                out.flush();
                out.println("12&Stat&Stat");
                out.flush();
                out.println("13&Stat&Stat");
                out.flush();
                out.println("14&Stat&Stat");
                out.flush();
                out.println("15&Stat&Stat");
                out.flush();
        });


        //actualistaion des boutons des différents user
        for (JPanelPseudo jp : jpBan) {
            addButtonAndComboBox(jp, "offline", out);
        }
        for (
                JPanelPseudo jp : jpOffline) {
            addButtonAndComboBox(jp, "ban", out);
        }
        for (
                JPanelPseudo jp : jpOnline) {
            addButtonAndComboBox(jp, "ban", out);
        }

        //Bouton si l'on ferme la fenêtre
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // Do something when the window is closing
                if (user.getStatus().equals("online")) {
                    out.println("4&" + user.getUsername() + "&offline");
                    out.flush();
                }

            }
        });
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setVisible(true);
    }


    //Initialisation des boutons pour changer d'etat ou des droits
    private void addButtonAndComboBox(JPanelPseudo jp, String status, PrintWriter out) {
        jp.getButton().addActionListener(e -> {
            jpOnline = new ArrayList<>();
            jpOffline = new ArrayList<>();
            jpBan = new ArrayList<>();
            jpAway = new ArrayList<>();
            out.println("4&" + jp.getLabel().getText() + "&" + status);
            out.flush();
            out.println("6&" + user.getUsername() + "&Affiche Status");
            out.flush();
        });
        jp.getComboBox().addActionListener(e -> {
            if (!BlockBoucle) {
                jpOnline = new ArrayList<>();
                jpOffline = new ArrayList<>();
                jpBan = new ArrayList<>();
                jpAway = new ArrayList<>();
                out.println("5&" + jp.getLabel().getText() + "&" + jp.getComboBox().getSelectedItem().toString());
                out.flush();
                out.println("6&" + user.getUsername() + "&Affiche Status");
                out.flush();
            }
        });
    }
    //Affichage des messages
    public void ShowMess(String msg) throws Exception {
        //Son du message
        audio.receiveMessage();
        listMessageChat.clear();
        String[] Element = msg.split("¤");
        for (String element : Element) {
            String[] split = element.split("~");
            //Initialisation de la liste des messages
            listMessageChat.add(new MessageChat(split[0], split[1], split[2]));
        }
        chatAreaPanel.removeAll();
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.weightx = 1.0;

        for (MessageChat lm : listMessageChat) {
            //Couleur de base
            constraints.anchor = GridBagConstraints.WEST;
            Color c1 = new Color(24, 83, 79);
            Color cfont = new Color(255, 255, 255);
            if (user.getUsername().equals(lm.getUsername())) {
                //Change de coueleur et de placement pour nos messages
                constraints.anchor = GridBagConstraints.EAST;
                c1 = new Color(124, 183, 179);
                cfont = new Color(10, 10, 10);
            } else {
                //Pseudo affiché si ce n'est pas nos messages
                JLabel jlUsername = new JLabel(lm.getUsername());
                jlUsername.setForeground(c1);
                Font font = jlUsername.getFont().deriveFont(12f);
                jlUsername.setFont(font);
                chatAreaPanel.add(jlUsername, constraints);
            }
            constraints.gridy++;
            if (lm.getContent()==null){
                if(lm.getImageIcon()==null){
                    //Si c'est un audio
                    JButton jb= lm.getStartAudio();
                    jb.setBackground(c1);
                    jb.setMaximumSize(new Dimension(50,50));
                    jb.setMinimumSize(new Dimension(50,50));
                    jb.setPreferredSize(new Dimension(50,50));
                    if (user.getUsername().equals(lm.getUsername())){
                        jb.setIcon(new ImageIcon("User/Ressource/audioBlack.png"));
                    }else{
                        jb.setIcon(new ImageIcon("User/Ressource/audioWhite.png"));
                    }
                    chatAreaPanel.add(jb,constraints);
                }else {
                    //Si c'est une image
                    JLabel tempPanel = new JLabel(lm.getImageIcon());
                    tempPanel.setMaximumSize(new Dimension(120, 120));
                    chatAreaPanel.add(tempPanel, constraints);
                }
            }else{
                //Si le message est trop long, retour à la ligne
                StringBuilder sbContent = new StringBuilder(lm.getContent());
                sbContent.insert(0, "<html>");
                for (int i = 40; i < sbContent.length(); i += 40) {
                    int j=i;
                    while (sbContent.charAt(j)!=' ' && j>0) {
                        j++;
                    }
                    sbContent.insert(i, "<br>");
                }
                sbContent.insert(sbContent.length(), "</html>");
                JLabel jlContent = new JLabel(sbContent.toString());
                Color finalC1 = c1;
                Color finalCfont = cfont;
                //Arrondir les messages
                JPanel tempPanel = new JPanel() {
                    @Override
                    protected void paintComponent(Graphics g) {
                        this.setBackground(new Color(255, 255, 255));
                        super.paintComponent(g);
                        Graphics2D g2 = (Graphics2D) g.create();
                        int arc = 20;
                        g2.setClip(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), arc, arc));
                        g2.setColor(finalC1);
                        g2.fillRect(0, 0, getWidth(), getHeight());
                        Font font = jlContent.getFont().deriveFont(16f);
                        jlContent.setFont(font);
                        jlContent.setForeground(finalCfont);
                        this.add(jlContent);
                        g2.dispose();
                    }
                };
                chatAreaPanel.add(tempPanel, constraints);
            }


            //Ajout des dates de messages
            constraints.gridy++;
            JLabel jlDate = new JLabel(lm.getTimestamp());
            Font font = jlDate.getFont().deriveFont(10f);
            jlDate.setFont(font);

            chatAreaPanel.add(jlDate, constraints);
            constraints.gridy++;
            chatAreaPanel.add(new JLabel(" "), constraints);
            constraints.gridy++;
        }
        //Update de la scroll bar
        JScrollBar scrollBar = chatAreaScroll.getVerticalScrollBar();
        scrollBar.setUnitIncrement(13);

        //Met la scroll barre tout en bas en cas de message
        scrollBar.addAdjustmentListener(new AdjustmentListener() {
            int previousMax = scrollBar.getMaximum();

            @Override
            public void adjustmentValueChanged(AdjustmentEvent e) {
                boolean wasAtBottom = scrollBar.getValue() + scrollBar.getVisibleAmount() >= previousMax;
                previousMax = scrollBar.getMaximum();
                if (wasAtBottom) {
                    scrollBar.setValue(scrollBar.getMaximum());
                }
            }
        });

        chatAreaScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        chatAreaPanel.revalidate();
        chatAreaPanel.repaint();

    }
    public void ShowStatus(String status, String msg, PrintWriter out) {

        //Actualise la liste des utilisateur en fonction de leur permission
        switch (status) {
            case "online":
                jpOnline = new ArrayList<>();
                jpOffline = new ArrayList<>();
                jpBan = new ArrayList<>();
                jpAway = new ArrayList<>();
                String[] Element = msg.split("¤");
                for (String element : Element) {
                    jpOnline.add(new JPanelPseudo(element));
                }
                break;
            case "offline":
                String[] Element2 = msg.split("¤");
                for (String element : Element2) {
                    JPanelPseudo tempJp = new JPanelPseudo(element);
                    jpOffline.add(tempJp);
                }
                break;
            case "ban":
                String[] Element3 = msg.split("¤");
                for (String element : Element3) {
                    //l'utilisateur est ban si il est dans la liste des banni
                    if (element.equals(user.getUsername())){
                        JOptionPane.showMessageDialog(HomePage.this, "You just got banned!");
                        dispose();
                        System.exit(0);
                        return;
                    }
                    JPanelPseudo tempJp = new JPanelPseudo(element);
                    jpBan.add(tempJp);
                }
                break;
            case "away":
                String[] Element4 = msg.split("¤");
                for (String element : Element4) {
                    JPanelPseudo tempJp = new JPanelPseudo(element);
                    jpAway.add(tempJp);
                }
                break;
            case "comboBox":
                listComboBox = msg;
            case "go": {
                OnlinePanel.removeAll();
                OfflinePanel.removeAll();
                BanPanel.removeAll();
                AwayPanel.removeAll();
                //Actualisation des boutons pour changer d'etat ou pour Ban
                for (JPanelPseudo jp : jpBan) {
                    addButtonAndComboBox(jp, "offline", out);
                }
                for (JPanelPseudo jp : jpOffline) {
                    addButtonAndComboBox(jp, "ban", out);
                }
                for (JPanelPseudo jp : jpOnline) {
                    addButtonAndComboBox(jp, "ban", out);
                }
                for (JPanelPseudo jp : jpAway) {
                    addButtonAndComboBox(jp, "ban", out);
                }
                //Affichage en fonction de l'etat du user
                ViewPseudo(jpOnline, OnlinePanel, "online", listComboBox);
                ViewPseudo(jpOffline, OfflinePanel, "offline", listComboBox);
                ViewPseudo(jpBan, BanPanel, "ban", listComboBox);
                ViewPseudo(jpAway, AwayPanel, "away", listComboBox);
                OnlinePanel.revalidate();
                OnlinePanel.repaint();
                OfflinePanel.revalidate();
                OfflinePanel.repaint();
                BanPanel.revalidate();
                BanPanel.repaint();
                AwayPanel.revalidate();
                AwayPanel.repaint();
            }

        }


    }


    private void ViewPseudo(ArrayList<JPanelPseudo> jPseudoList, JPanel jPanel, String Statue, String listComboBox) {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridy = 0;
        constraints.gridx = 0;
        constraints.weightx = 1.0;
        for (JPanelPseudo jp : jPseudoList) {
            //Creation d'un label
            constraints.anchor = GridBagConstraints.EAST;
            JPanel tempPanel = new JPanel(new GridBagLayout());
            GridBagConstraints constraintsPanel = new GridBagConstraints();
            constraintsPanel.gridy = 0;
            constraintsPanel.gridx = 0;
            constraintsPanel.weightx = 1.0;
            constraintsPanel.anchor = GridBagConstraints.CENTER;
            if (!user.getUsername().equals(jp.getLabel().getText())) {
                //Si c'est un autre utilisateur que nous même
                JLabel tempjl = jp.getLabel();
                tempjl.setForeground(new Color(255, 255, 255));
                //Si le user est ban il sera barré
                if (Statue.equals("ban")) {
                    Font font = tempjl.getFont();
                    Map attributes = font.getAttributes();
                    attributes.put(TextAttribute.STRIKETHROUGH, TextAttribute.STRIKETHROUGH_ON);
                    tempjl.setFont(font.deriveFont(attributes));
                }
                tempPanel.add(tempjl, constraintsPanel);
                if (user.getPermission().equals("moderateur") || user.getPermission().equals("admin")) {
                    // Si le user est un modo ou admin il aura acces au bouton pour Ban
                    constraintsPanel.gridx = 1;
                    tempPanel.add(jp.getButton(), constraintsPanel);

                    if (user.getPermission().equals("admin")) {
                        //Si le user est un admin il pourra changer de status
                        constraintsPanel.gridx = 2;
                        BlockBoucle = true;
                        JComboBox jpcb = jp.getComboBox();
                        String[] part = listComboBox.split("~");
                        String[] NameComboBox;
                        for (String Part : part) {
                            NameComboBox = Part.split("¤");
                            if (NameComboBox[0].equals(jp.getLabel().getText())) {
                                jpcb.setSelectedItem(NameComboBox[1]);
                            }

                        }
                        tempPanel.add(jpcb, constraintsPanel);
                        BlockBoucle = false;
                    }
                }
                tempPanel.setBackground(new Color(24, 83, 79));
                constraints.gridx = 0;
                jPanel.add(tempPanel, constraints);
            } else {
                //Initialisation des ComboBox en fonction des status
                String[] part = listComboBox.split("~");
                String[] NameComboBox;
                for (String Part : part) {
                    NameComboBox = Part.split("¤");
                    if (NameComboBox[0].equals(user.getUsername())) {
                        user.setPermission(NameComboBox[1]);
                    }
                }

                CurentUser(jp, jPanel, constraints);
            }
            constraints.gridy++;

        }
    }

    private void CurentUser(JPanelPseudo jp, JPanel jPanel, GridBagConstraints constraints) {
        //Si l'utilisateur est nous même
        JPanel tempPanel = new JPanel(new GridBagLayout());
        GridBagConstraints constraintsPanel = new GridBagConstraints();
        constraintsPanel.gridy = 0;
        constraintsPanel.gridx = 0;
        constraintsPanel.weightx = 1.0;
        constraints.anchor = GridBagConstraints.CENTER;
        JLabel tempjl = jp.getLabel();
        Font font = tempjl.getFont().deriveFont(Font.BOLD);
        tempjl.setFont(font);
        tempjl.setForeground(new Color(255, 255, 255));
        tempPanel.add(tempjl, constraints);
        tempPanel.setBackground(new Color(24, 83, 79));
        jPanel.add(tempPanel, constraints);
    }

    public void startTimer(PrintWriter out) {
        //Timer pour le cas où l'utilisateur est Away
         timer = new Timer();
        count=0;
      TimerTask task = new TimerTask() {
            @Override
            public void run() {
                count++;
                if (count == 180 && !messageSent) {
                    timer.cancel();
                    user.setStatus("away");
                    out.println("4&" + user.getUsername() + "&" + user.getStatus());
                    out.flush();
                    jpOnline = new ArrayList<>();
                    jpOffline = new ArrayList<>();
                    jpBan = new ArrayList<>();
                    jpAway = new ArrayList<>();
                    out.println("6&" + user.getUsername() + "&Affiche Status");
                    out.flush();
                    ConfirmationDeconnexion confirmationDeconnexion = new ConfirmationDeconnexion(HomePage.this, user, out, messageSent,timer, count);
//
                }
            }
        };
        timer.schedule(task, 0, 1000);

    }



    public void timerRestart(PrintWriter out) {
        if(timer!=null) {
            timer.cancel();
        }
        count =0;
        messageSent = false;
       startTimer(out);
    }

    public static String convertFileToBase64String(File file) {
        String base64String = "";
        try {
            byte[] fileContent = Files.readAllBytes(file.toPath());
            base64String = Base64.getEncoder().encodeToString(fileContent);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return base64String;
    }

}

