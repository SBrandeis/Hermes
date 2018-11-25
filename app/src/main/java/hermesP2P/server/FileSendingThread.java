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
 * It is used to manage the sending of a given file through a socket.
 */
public class FileSendingThread extends Thread {

    Context mContext;
    Socket socket;
    Uri fileUri;

    static final String TAG = "FileSendingThread";
    static final int BUFFER_SIZE = 5000;           // Size of ByteBuffer used to hash files. Currently 5ko.

    FileDescriptor fileDescriptor;
    FileChannel fileChannel;
    FileInputStream fileInputStream;

    FileSendingThread(Context context, Socket socket, Uri fileUri) {
        this.mContext = context;
        this.socket = socket;
        this.fileUri = fileUri;

        this.fileChannel = null;
        this.fileDescriptor = null;
        this.fileInputStream = null;
    }


    @Override
    public void run() {


        // With parameters as they are right now, the whole File is read in one go and sent
        // in one go. Maybe this can be optimized later to do it chunk by chunk.
        // However, since we will only be sending Chunks of files, it may not be necessary
        // to change this for our project.
        byte[] bytes = new byte[BUFFER_SIZE];
        BufferedInputStream bis;
        int readBytes;
        try {
            fileDescriptor = mContext.getContentResolver().openFileDescriptor(fileUri, "r").getFileDescriptor();
            bis = new BufferedInputStream(new FileInputStream(fileDescriptor));
            OutputStream os = socket.getOutputStream();
            Log.d(TAG, "Sending loop begins");

            do {
                readBytes = bis.read(bytes);
                os.write(bytes);
            } while (readBytes > 0);


            // Once the whole file has been written onto the OutputStream, the Stream is
            // closed as well as the socket.
            String sentMsg = "File sent to: " + socket.getInetAddress();

            os.flush();
            socket.close();

            Log.w(TAG, sentMsg);

        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, e.toString());

        }
        finally {
            try {
                socket.close();
            }
            catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, e.toString());
            }
        }

    }
}