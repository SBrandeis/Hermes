package firebase.onDestroyManaging;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import firebase.DB_User;
import firebase.storageClasses.AppDestroyManager;

/**
 * Service used to check when the user goes offline,
 * in order to update his status on the firebase.
 */
public class LogOutService extends Service {

    FirebaseDatabase database;
    DatabaseReference myRef;
    String uid;
    DB_User user;
    boolean destroy;

    static final String TAG = "LogOutService";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.w(TAG, "Service started");
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();

        return super.onStartCommand(intent, flags, startId);
    }



    @Override
    public void onTaskRemoved(Intent rootIntent) {

        uid = AppDestroyManager.getUid();
        user = AppDestroyManager.getUser();
        destroy = AppDestroyManager.isDestroy();
        if(destroy && user!=null) {
            user.setOnline(false);
            myRef.child("DB_User").child(uid).setValue(user);
            Log.w(TAG, "User " + uid + " is now offline in DB");
        }
        Log.w(TAG, "Service stopped (task removed)");
        super.onTaskRemoved(rootIntent);
    }

    @Override
    public void onDestroy() {
        Log.w(TAG, "Service stopped (task destroyed)");
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
