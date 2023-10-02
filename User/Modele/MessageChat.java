package User.Modele;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import java.util.Base64;
//Modele d'un message
public class MessageChat {

    private final String username;
    private final String timestamp;
    private String content=null;
    private ImageIcon imageIcon=null;
    private JButton StartAudio=new JButton();

    public MessageChat(String username, String timestamp, String content) throws IOException, UnsupportedAudioFileException, LineUnavailableException {
        //Initialisation d'un message
        this.username=username;
        this.timestamp=timestamp;
        if (content.length()>3) {
            if (content.charAt(0)=='#' && content.charAt(1)=='#' ){
                //cas ou c'est une image
                content=content.substring(2);
                byte[] imageBytes = Base64.getDecoder().decode(content);
                 imageIcon = new ImageIcon(imageBytes);
            }else if (content.charAt(0)=='%' && content.charAt(1)=='%' ){
                //cas ou c'est un audio
                content=content.substring(2);
                byte[] audioBytes = Base64.getDecoder().decode(content);
                AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new ByteArrayInputStream(audioBytes));
                Clip clip = AudioSystem.getClip();
                clip.open(audioInputStream);
                StartAudio.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        clip.setFramePosition(0);
                        clip.start();
                    }
                });
            }else{
                //cas ou c'est un message
                this.content=content;
            }
        }else{
            this.content=content;
        }

    }
    public String getContent() {
        return content;
    }
    public String getUsername(){
        return username;
    }
    public String getTimestamp(){
        return timestamp;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public ImageIcon getImageIcon(){
        return imageIcon;
    }

    public JButton getStartAudio() throws LineUnavailableException, IOException {
        return StartAudio;
    }

    public void delete(){

    }

}

