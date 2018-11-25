package firebase;

import android.content.Context;
import android.content.Intent;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import firebase.storageClasses.AppDestroyManager;
import firebase.onDestroyManaging.LogOutService;

public class FireBase  {

    private static final String TAG = "FireBase";
    private final Context mContext;

    FirebaseDatabase database;
    DatabaseReference myRef;

    private int count;
    private Intent logOutListener;

    private List<DB_File> files;
    private List<DB_Part> parts;
    private List<DB_User> users;

    public FireBase(Context context) {

        this.mContext = context;
        this.count = 10;

        files = new ArrayList<DB_File>();
        parts = new ArrayList<DB_Part>();
        users = new ArrayList<DB_User>();

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
    }

    public void listenForDestroy() {
        AppDestroyManager.setDestroy(true);
        logOutListener = new Intent(mContext, LogOutService.class);
        mContext.startService(logOutListener);
    }

    public void stopListeningForDestroy() {
        AppDestroyManager.setDestroy(false);
        mContext.stopService(logOutListener);
    }

    /**
     * Write new file into the FireBase
     * @param hashCode  the file's hashCode
     * @param filename  the name of the file
     * @param author    the author of the file
     * @param size      the size of the file
     * @param parts     a hashmap containing the hashcodes of the parts making up the file
     */
    public void writeNewFile(String hashCode, String filename, String author, String size, HashMap parts) {
        DB_File file = new DB_File(hashCode, filename, author, size, parts);
        files.add(file);

        myRef.child("DB_File").child(hashCode).setValue(file);
    }

    /**
     * Write a new part into the FireBase
     * @param parentFile    The hashcode of the parent file of the part
     * @param partOwners    A HashMap containing the UIDs of the owners of the part
     * @param size          The size of the part
     * @param hashcode      The hashcode of the part
     * @param rank          The rank of the part in the file
     */
    public void writeNewPart(String parentFile, HashMap partOwners, String size, String hashcode, int rank) {
        DB_Part part = new DB_Part(parentFile, hashcode, partOwners, size, rank);
        this.parts.add(part);

        myRef.child("DB_Part").child(hashcode).setValue(part);
    }

    /**
     * Writes a new User into the Firebase
     * @param uid           The user's UID
     * @param ipAddress     The user's IP address
     * @param isOnline      A boolean telling whether the User is online or not
     * @param eMail         The user's email address
     * @param name          The user's display name
     */
    public void writeNewUser(String uid, String ipAddress, boolean isOnline, String eMail, String name) {
        // l'UID est ici à récupérer dans firebaseAuth et à fournir directement dans la méthode.
        DB_User user = new DB_User(uid, ipAddress, isOnline, eMail, name);
        this.users.add(user);

        AppDestroyManager.setUid(uid);
        AppDestroyManager.setUser(user);

        myRef.child("DB_User").child(uid).setValue(user);
    }

    public List<DB_File> getFiles() {
        return files;
    }
    public List<DB_Part> getParts() {
        return parts;
    }
    public List<DB_User> getUsers() { return users; }
}
