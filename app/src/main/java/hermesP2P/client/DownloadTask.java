package hermesP2P.client;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import firebase.DB_File;
import firebase.DB_Part;
import firebase.DB_User;
import hermesP2P.server.HermesP2PServer;

/**
 * This AsyncTask manages the download of a given file.
 * Process might be time consuming, so it's done in background of the app.
 */

class DownloadTask extends AsyncTask<Void, Integer, Boolean> {


    private static final String TAG = "DownloadTask";
    private static final int DST_PORT_COM = FileReceiveService.DST_PORT_COM;
    private static final int DST_PORT_DATA = FileReceiveService.DST_PORT_DATA;

    private Context context;
    private DB_File file;
    private List<DB_Part> parts;

    private boolean launchedFileTransfer;

    DownloadTask(Context context, DB_File file, List<DB_Part> parts) {
        this.file = file;
        this.parts = parts;
        this.context = context;
        launchedFileTransfer = false;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Log.w(TAG, "Download of file " + file.getFilename() + " has started in background");
        Toast.makeText(context.getApplicationContext(), "Download of file " + file.getFilename() + " has started in background",
                        Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPostExecute(Boolean downloadSuccessful) {
        super.onPostExecute(downloadSuccessful);
        if (downloadSuccessful) {
            Log.w(TAG, "Download of file " + file.getFilename() + " is finished.");
            Toast.makeText(context.getApplicationContext(), "Download of file " + file.getFilename() + " is finished.",
                    Toast.LENGTH_SHORT).show();
        }
        else {
            Log.w(TAG, "Download of file " + file.getFilename() + " has failed.");
            Toast.makeText(context.getApplicationContext(), "Download of file " + file.getFilename() + " has failed.",
                    Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected Boolean doInBackground(Void... voids) {

        boolean downloadFinished = true;

        // Orders parts (rank ascendant)
        parts = orderParts(parts);
        // Opens a new File in downloads directory, this is where downloaded data will be written
        File downloadedFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                file.getFilename());

        // socketCOM is the socket used to communicate with server terminal
        // socketDATA is the socket used to download data from server terminal
        Socket socketCOM = null;
        Socket socketDATA = null;

        DataOutputStream dataOutputStreamCOM = null;
        DataInputStream dataInputStreamCOM = null;

        DataOutputStream dataOutputStreamDATA = null;

        try {

            // Loops on parts (list of parts to download)
            for (int i = 0; i< parts.size(); i+=1) {

                Log.w(TAG, "Part nb " + i + " is being handled");

                DB_Part part = parts.get(i);
                DB_User user = part.getOwners().get(0);

                // Retrieve the IP address of part's owner
                String dstAddress = user.getIpAddress();

                // Retrieve hashcodes of file and part
                String fileHashcode = file.getId();
                String partHashcode = part.getHashCode();

                // Message to send to server (request part download)
                String msgToServer = "getfile/"
                        + fileHashcode + "/"
                        + partHashcode;

                String filename = file.getFilename();

                Log.w(TAG, "Got following filename " + filename);

                Log.w(TAG, "Connecting to ip " + dstAddress + " on port " + DST_PORT_COM);

                // Opening socket pointing to a specific IP address and a given port
                socketCOM = new Socket(dstAddress, DST_PORT_COM);
                socketDATA = new Socket(dstAddress, DST_PORT_DATA);

                // Opening streams
                dataOutputStreamCOM = new DataOutputStream(socketCOM.getOutputStream());
                dataInputStreamCOM = new DataInputStream(socketCOM.getInputStream());
                dataOutputStreamDATA = new DataOutputStream(socketDATA.getOutputStream());


                // Writing message for receiver device
                if(msgToServer != null){
                    dataOutputStreamCOM.writeUTF(msgToServer);
                }
                String response = dataInputStreamCOM.readUTF();
                Log.w(TAG, "Got response from server : " + response);

                // Handles the server terminalresponse
                handleResponse(response, downloadedFile, socketDATA);
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            downloadFinished = false;

        } finally{
            if((socketCOM != null) && (!launchedFileTransfer)) {
                try {
                    socketCOM.close();
                } catch (IOException e) {
                    e.printStackTrace();

                }
            }
            if((socketDATA != null) && (!launchedFileTransfer)) {
                try {
                    socketCOM.close();
                } catch (IOException e) {
                    e.printStackTrace();

                }
            }
            if ((dataOutputStreamCOM != null) && (!launchedFileTransfer)) {
                try {
                    dataOutputStreamCOM.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if ((dataOutputStreamDATA != null) && (!launchedFileTransfer)) {
                try {
                    dataOutputStreamDATA.close();
                } catch (IOException e) {
                    e.printStackTrace();

                }
            }
            if ((dataInputStreamCOM != null) && (!launchedFileTransfer)) {
                try {
                    dataInputStreamCOM.close();
                } catch (IOException e) {
                    e.printStackTrace();

                }
            }
        }
        return downloadFinished;
    }

    /**
     * This function is used to get an ordered list of all parts that make up a file.
     * After being given the list of all parts, it orders them by their rank in the file.
     * @param partlist The list of parts to order
     * @return  the ordered list of parts.
     */
    private List<DB_Part> orderParts(List<DB_Part> partlist) {
        ArrayList<DB_Part> parts = new ArrayList<DB_Part>();
        for (int i=0; i<partlist.size();i++) {
            for (int j = 0; j < partlist.size(); j++) {
                DB_Part itrPart = partlist.get(j);
                if (itrPart.getRank() == i) {
                    parts.add(itrPart);
                    break;
                }
            }
        }
        return parts;
    }


    /**
     * Method to handle the response given by the server side of the receiver
     * device, after sending it the set of instruction.
     * @param response  The reply of the other terminal to the set of instructions
     *                  given to it
     */
    private void handleResponse(String response, File file, Socket socketDATA) throws InterruptedException {

        String[] res = response.split("\\\\");
        // If response is "ALLOW_FILE_TRANSFER", begin chunk downloading with ChunkDownloadThread
        if(res[0].equals(HermesP2PServer.ALLOW_FILE_TRANSFER)) {
            Log.w(TAG, "Starting download of chunk");
            ChunkDownloadThread downloadThread = new ChunkDownloadThread(file, socketDATA);
            downloadThread.start();
            launchedFileTransfer = true;
            downloadThread.join();
        }
    }
}

