package firebase;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class DB_User extends DbElement {

    public String uid;
    public String ipAddress;
    public boolean online;
    public String eMail;
    public String name;


    public DB_User() {
    }

    /**
     * Creates an instance of DB_User

     * @param uid the ID of the User
     * @param ipAddress the IP address of the User
     * @param online  a boolean telling whether the user is online or not
     * @param eMail the user's email
     * @param name the user's display name
     */
    DB_User(String uid, String ipAddress, boolean online, String eMail, String name) {
        this.uid = uid;
        this.ipAddress = ipAddress;
        this.online = online;
        this.eMail = eMail;
        this.name = name;
    }

    public String getUid() {
        return uid;
    }
    public void setUid(String uid) {
        this.uid = uid;
    }
    public String getIpAddress() {
        return ipAddress;
    }
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
    public boolean isOnline() {
        return online;
    }
    public void setOnline(boolean isOnline) {
        this.online = isOnline;
    }
    public String geteMail() {
        return eMail;
    }
    public void seteMail(String eMail) {
        this.eMail = eMail;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    @Exclude
    @Override
    Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("UID", uid);
        result.put("IP", ipAddress);
        result.put("online", online);
        result.put("email", eMail);
        result.put("name", name);
        return result;
    }
}
