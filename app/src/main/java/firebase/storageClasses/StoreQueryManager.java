package firebase.storageClasses;

import java.util.List;

import firebase.DB_File;
import firebase.DB_Part;
import firebase.DB_User;
import firebase.query.QueryManager;

/**
 * Class used to store querymanagers and the results of queries between activities.
 */
public class StoreQueryManager {
    private static QueryManager queryManager;
    private static List<DB_Part> parts;
    private static List<DB_User> users;
    private static List<DB_File> files;
    private static DB_File file;
    private static DB_User user;
    private static DB_Part part;

    public static DB_File getFile() {
        return file;
    }

    public static void setFile(DB_File file) {
        StoreQueryManager.file = file;
    }

    public static DB_User getUser() {
        return user;
    }

    public static void setUser(DB_User user) {
        StoreQueryManager.user = user;
    }

    public static DB_Part getPart() {
        return part;
    }

    public static void setPart(DB_Part part) {
        StoreQueryManager.part = part;
    }

    public static List<DB_Part> getParts() {
        return parts;
    }

    public static void setParts(List<DB_Part> parts) {
        StoreQueryManager.parts = parts;
    }

    public static List<DB_User> getUsers() {
        return users;
    }

    public static void setUsers(List<DB_User> users) {
        StoreQueryManager.users = users;
    }

    public static List<DB_File> getFiles() {
        return files;
    }

    public static void setFiles(List<DB_File> files) {
        StoreQueryManager.files = files;
    }

    public static QueryManager getQueryManager() {
        return queryManager;
    }

    public static void setQueryManager(QueryManager queryManager) {
        StoreQueryManager.queryManager = queryManager;
    }

    public static void clearFiles() {if(files!=null)files.clear();};
    public static void clearUsers() {if(users!=null)users.clear();};
    public static void clearParts() {if(parts!=null)parts.clear();};


}
