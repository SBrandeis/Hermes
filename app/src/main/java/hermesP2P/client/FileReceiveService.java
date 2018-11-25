package hermesP2P.client;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import firebase.DB_File;
import firebase.DB_Part;
import firebase.DB_User;
import hermesP2P.server.HermesP2PServer;

/**
 * This service is used to make Chunk downloading a background task, that
 * doesn't need the app to be open to run.
 */
public class FileReceiveService extends Service {

    private static final String TAG = "FileReceiveThread";
    private final IBinder downloadBinder = new DownloadServiceBinder();

    private String dstAddress;
    public static final int DST_PORT_COM = 4200;
    public static final int DST_PORT_DATA = 8080;
    private String response = "";
    private String msgToServer;
    private boolean launchedFileTransfer;
    Thread chunkDownloadThread;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.w(TAG, "Service started.");

        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class DownloadServiceBinder extends Binder {
        public FileReceiveService getService() {
            // Return this instance of LocalService so clients can call public methods
            return FileReceiveService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.w(TAG, "Bound detected");
        return downloadBinder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    /**
     * Downloads requested file
     * @param file (DB_File), file as represented in Firebase
     * @param parts (List<DB_Parts>), file's parts as represented in Firebase
     */
    public void download(DB_File file, List<DB_Part> parts) {

        Log.w(TAG, "download method called");
        boolean downloadFinished = true;

        // Launches DownLoadTask to handle download
        DownloadTask downloadTask = new DownloadTask(this, file, parts);
        downloadTask.execute();

    }
}



