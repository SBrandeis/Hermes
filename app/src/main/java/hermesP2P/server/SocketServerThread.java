package hermesP2P.server;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import hermes.Hermes;

/**
 * This Thread will run inside the HermesP2PServer service.
 * It will handle the connections to this device through the HermesProject app.
 */
public class SocketServerThread extends Thread {

    private static final int SocketServerCOM_PORT = 4200;
    private static final int SocketServerDATA_PORT = 8080;
    private static final String TAG = "SocketServerThread";

    private Context mContext;
    private int count = 0;
    private ServerSocket serverSocketCOM;
    private ServerSocket serverSocketDATA;
    private Socket socketCOM = null;
    private Socket socketDATA = null;
    private DataInputStream dataInputStream = null;
    private DataOutputStream dataOutputStream = null;

    private Hermes hermes;

    public SocketServerThread(Context context) {
        mContext = context;
        hermes = new Hermes(mContext);
    }

    /**
     * The core code for this class is here. The thread runs, and while it runs will listen for
     * connections on the server socket it has opened, reacting adequately to what the client
     * is connecting to the socket for.
     */
    @Override
    public void run() {
        try {
            serverSocketCOM = new ServerSocket(SocketServerCOM_PORT);
            serverSocketDATA = new ServerSocket(SocketServerDATA_PORT);

            while (true) {
                socketCOM = serverSocketCOM.accept();
                socketDATA = serverSocketDATA.accept();

                dataInputStream = new DataInputStream(socketCOM.getInputStream());

                String messageFromClient = "";

                //If there is no message sent by the client, this code will block the program
                messageFromClient = dataInputStream.readUTF();

                count++;
                String message = "Connection #" + count + " from " + socketCOM.getInetAddress()
                        + ":" + socketCOM.getPort() + "\n"
                        + "Msg from client: " + messageFromClient + "\n";
                Log.w(TAG, message);

                //Once we have gotten the client's message, we ave to handle it. This function
                //is used for that.
                reactToInput(messageFromClient);
            }
        } catch (IOException e) {
            e.printStackTrace();
            String errMsg = e.toString();
            Log.e(TAG, errMsg);
        } finally {
            if (dataInputStream != null) {
                try {
                    dataInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (dataOutputStream != null) {
                try {
                    dataOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (socketCOM != null) {
                try {
                    socketCOM.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * A simple function to stop the server.
     */
    public void stopSocketServer() {
        if (serverSocketCOM != null) {
            try {
                serverSocketCOM.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (serverSocketDATA != null) {
            try {
                serverSocketDATA.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * This function is called when the server gets a connection. It is used to know what to do
     * depending on the input.
     * @param clientMessage A String containing the message the client sent, a list of instructions
     *                      in form of a classic Web API.
     */
    private void reactToInput(String clientMessage) {
        String[] messageParts = clientMessage.split("/");

        // ====================== CORE OF THE CODE ==================
        //
        // This part of the code works like a CLUI or a Web API,
        // depending on the String in the client message, multiple
        // actions can follow.
        // All actions are not coded yet, all will later have to be
        // implemented.
        //
        // ==========================================================

        switch (messageParts[0].toLowerCase()) {

            case "getfile":
                //If the clients message has a Header "getfile", proceed to send him the file he seeks
                Uri uri;

                if (messageParts.length == 1) {

                    try {
                        //======================== IMPORTANT ==========================
                        //
                        //For now, this is an URI, but in the future the second message
                        //part will contain the file's hashcode, which will then have
                        //to be transcribed into a URI by the FileHasher package.
                        //
                        //=============================================================
                        String fileUri = hermes.getLocalSharedFiles().get(0).getUri();
                        Log.d(TAG, "Trying to send: " + fileUri);
                        uri = Uri.parse(fileUri);
                        String filename = hermes.getFileNameFromUri(uri);
                        sendMessageThrough(socketCOM, HermesP2PServer.ALLOW_FILE_TRANSFER + "\\" + filename);
                        sendFileThrough(socketDATA, uri);

                    } catch(IOException e){
                        e.printStackTrace();
                    }
                    break;
                }
                else {
                    // Second part of the message is the file's hashcode
                    String fileHashcode = messageParts[1];
                    Log.d(TAG, "Hashcode asked: " + fileHashcode);

                    if (messageParts.length >= 3) {
                        String partHashcode = messageParts[2];
                        Log.d(TAG, "Part's hashcode asked: " + partHashcode);

                        try {
                            byte[] chunk = hermes.getPart(partHashcode, fileHashcode);
                            int size = chunk.length;
                            Log.d(TAG, "Size of part found: " + size);

                            if (size > 0) {

                                sendMessageThrough(socketCOM, HermesP2PServer.ALLOW_FILE_TRANSFER + "\\" + partHashcode);
                                sendFileThrough(socketDATA, chunk);
                            }
                            else {
                                sendMessageThrough(socketCOM, HermesP2PServer.DENY_FILE_TRANSFER);
                            }

                        }
                        catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;

                    } else {
                        try {
                            uri = hermes.getFileUriFromHashcode(fileHashcode);
                            if (uri != null) {
                                Log.d(TAG, "Uri found for hashcode: " + uri.toString());

                                String fileName = hermes.getFileNameFromUri(uri);
                                sendMessageThrough(socketCOM, HermesP2PServer.ALLOW_FILE_TRANSFER + "\\" + fileName);
                                sendFileThrough(socketDATA, uri);
                            }
                            else {
                                Log.d(TAG, "Uri not found for hashcode");
                                sendMessageThrough(socketCOM, HermesP2PServer.DENY_FILE_TRANSFER);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;

                    }
                }

            case "hello":
                try {
                    String message = "Hello to you my dear";
                    sendMessageThrough(socketCOM, message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;

            default:
                break;
        }
    }

    /**
     * This method is used to send a file found at URI fileUri to another
     * device through a socket.
     * @param socket    The Socket through which the file should be sent
     * @param fileUri   The path where to find the path to send
     */
    private void sendFileThrough(Socket socket, Uri fileUri) {
        FileSendingThread fileSendingThread = new FileSendingThread(mContext, socket, fileUri);
        fileSendingThread.start();
    }

    /**
     * This method is used to send a file found at URI fileUri to another
     * device through a socket.
     * @param socket    The Socket through which the file should be sent
     * @param chunk     The chunk of data to send
     */
    private void sendFileThrough(Socket socket, byte[] chunk) {
        ChunkSendingThread chunkSendingThread = new ChunkSendingThread(mContext, socket, chunk);
        chunkSendingThread.start();
    }

    /**
     * Method to send a message through a socket to another device
     * @param socket    The Socket through which the file should be sent
     * @param message   The message to send as a String object
     * @throws IOException
     */
    private void sendMessageThrough(Socket socket, String message) throws IOException {
        DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
        dataOutputStream.writeUTF(message);
    }

}

