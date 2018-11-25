package hermesP2P.server;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * This Thread will run within the HermesP2PServer Service.
 * It is used to manage the sending of a given chunk through a socket.
 */
public class ChunkSendingThread extends Thread {

    Context mContext;
    Socket socket;
    byte[] chunk;

    static final String TAG = "ChunkSendingThread";
    static final int BUFFER_SIZE = 5000;           // Size of ByteBuffer used to hash files. Currently 5ko.

    FileChannel fileChannel;
    FileInputStream fileInputStream;

    ChunkSendingThread(Context context, Socket socket, byte[] chunk) {
        this.mContext = context;
        this.socket = socket;
        this.chunk = chunk;

        this.fileChannel = null;
        this.fileInputStream = null;
    }



    @Override
    public void run() {

        try {

            OutputStream os = socket.getOutputStream();
            os.write(chunk);

            // Once the whole file has been written onto the OutputStream, the Stream is
            // closed as well as the socket.
            String sentMsg = "File sent to: " + socket.getInetAddress();

            os.flush();
            socket.close();

            Log.w(TAG, sentMsg);

        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, e.toString());
        } finally {
            if(socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}