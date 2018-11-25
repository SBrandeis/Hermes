package hermesP2P.client;

import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Arrays;

import static java.util.Arrays.copyOfRange;

/**
 * This Thread is used to download chunks of data.
 * It is started from the FileReceive Service. Therefore, it runs in the background
 * and does not need the app to be open to run.
 */
public class ChunkDownloadThread extends Thread {

    private static final String TAG = "ChunkDownloadThread";
    private static final int BUFFER_SIZE = 5000;
    private Socket socket;
    private File file;

    /**
     * Constructor
     * @param file (File), file in which to write
     * @param socket (Socket), socket from which to retrieve data
     */
    public ChunkDownloadThread(File file, Socket socket) {
        this.file = file;
        this.socket = socket;
    }



    public void run() {
        byte[] buffer;
        try {
            buffer = new byte[BUFFER_SIZE];

            // Input stream to retrieve data from socket
            InputStream inputStream = socket.getInputStream();
            // Output stream to write into file
            OutputStream outputStream = new FileOutputStream(file, true);

            // Quick loop to read all of the file in the inputStream.
            // Once all of the file has been read and written to the
            // new path, close the outputStream and the socket.
            int bytesRead;
            do {
                bytesRead = inputStream.read(buffer);
                if (bytesRead>0) {
                    byte[] data = Arrays.copyOfRange(buffer, 0, bytesRead);
                    outputStream.write(data);
                }
            } while( bytesRead>0 );

            outputStream.close();
            socket.close();

            Log.w(TAG, "Chunk download finished");

        } catch (IOException e) {

            e.printStackTrace();
            Log.e(TAG, "Something wrong: " + e.getMessage());

        } finally {
            if(socket != null){
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e(TAG, e.toString());

                }
            }
        }
    }
}
