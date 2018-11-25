package firebase.storageClasses;

import firebase.DB_User;

/**
 * Class used to manage the state of the LogOutService
 * @see firebase.onDestroyManaging.LogOutService
 */
public class AppDestroyManager {
    private static String uid;
    private static DB_User user;
    private static boolean isDestroy;

    public static boolean isDestroy() {
        return isDestroy;
    }

    public static void setDestroy(boolean destroy) {
        isDestroy = destroy;
    }

    public static String getUid() {
        return uid;
    }

    public static void setUid(String uid) {
        AppDestroyManager.uid = uid;
    }

    public static DB_User getUser() {
        return user;
    }

    public static void setUser(DB_User user) {
        AppDestroyManager.user = user;
    }
}
