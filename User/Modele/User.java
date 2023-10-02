package User.Modele;

import java.net.Socket;
import java.util.Date;

//Modele d'un User
public class User {

    private int id;
    private String username;
    private String email;
    private String password;
    private String permission;
    private String status;

    public User() {

    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }



    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void InitUser(int id, String username, String email, String password, String permission, String status) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.permission = permission;
        this.status = status;
    }

}
