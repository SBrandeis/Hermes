package hermesP2P.server;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * This service runs once the HermesProject app is launched. It is used
 * to listen to any request from another device to download certain chunks
 * of files / certain files.
 */
public class HermesP2PServer extends Service {

    SocketServerThread socketServer;
    Context mContext;

    public static final String ALLOW_FILE_TRANSFER = "ALLOW_FILE_TRANSFER";
    public static final String DENY_FILE_TRANSFER = "DENY_FILE_TRANSFER";
    private static final String TAG = "HermesP2PServer";


    @Override
    public void onCreate() {
        super.onCreate();
        mContext = HermesP2PServer.this;
        // Main part : starting the socketServerThread
        // This thread of our service will then Handle the traffic
        Thread socketServerThread = new Thread(new SocketServerThread(mContext));
        socketServerThread.start();
        Log.w(TAG, "Server service started, listening on port 8080, IP "+getIpAddress());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        // Stop socket server when killing service
        super.onDestroy();
        Log.d(TAG, "Service killed !");
        socketServer.stopSocketServer();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * Simple method to get the IP address of the device in use.
     * @return the IP address of the device as a string
     */
    public static String getIpAddress() {
        String ip = "";
        try {
            Enumeration<NetworkInterface> enumNetworkInterfaces = NetworkInterface
                    .getNetworkInterfaces();
            while (enumNetworkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = enumNetworkInterfaces
                        .nextElement();
                Enumeration<InetAddress> enumInetAddress = networkInterface
                        .getInetAddresses();
                while (enumInetAddress.hasMoreElements()) {
                    InetAddress inetAddress = enumInetAddress.nextElement();

                    if (inetAddress.isSiteLocalAddress()) {
                        ip = inetAddress.getHostAddress();
                    }

                }

            }

        } catch (SocketException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            ip += "Something Wrong! " + e.toString() + "\n";
        }

        return ip;
    }

}
