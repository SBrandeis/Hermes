package hermes.fileManager;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;


import java.io.BufferedInputStream;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.SyncFailedException;
import java.io.UnsupportedEncodingException;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import firebase.FireBase;

public class FileHasher {

    // VARIABLES
    private final static String TAG = "hermes/fileManager";         // Tag used when Logging.
    public final static int BUFFER_SIZE = 5000;           // Size of ByteBuffer used to hash files. Currently 5ko.
    private static boolean permissionGranted;

    private Context ctx;                                    // Application's context
    private MessageDigest digester = null;                  // MessageDigest to hash chunks of files

    private FireBase fb;

    /**
     * CONSTRUCTOR
     * @param context - application's context
     */
    public FileHasher(Context context) {
        ctx = context;
        fb = new FireBase(ctx);
        try {
            digester = MessageDigest.getInstance("SHA");
        }
        catch( NoSuchAlgorithmException exception ) {
            Log.e(TAG, "ERROR: Algorithm for MessageDigest does not exists");
            exception.printStackTrace();
        }
    }

    /**
     * Hashes the file to which points the Uri, by chunks of size BUFFER_SIZE (5ko).
     * Returns an ArrayList containing the hexadecimal representation of those hashes as String objects.
     * @param fileUri Uri, Uri of the file to hash
     * @return hashTable ArrayList<String>, ArrayList storing the hexadecimal representation of the hashes of the file.
     */
    public synchronized HashMap<String, ArrayList<String>> hashFile(Uri fileUri) {

        // ===============================================================
        // ====================       VARIABLES       ====================
        // ===============================================================

        // hashTable is the return result, stores the hashes of file's chunks (hexadecimal string representation)
        ArrayList<String> hashTable = new ArrayList<String>();
        ArrayList<String> sizeTable = new ArrayList<>();

        // File reading utilities
        FileInputStream fis = null;                                 // InputStream pointing to the file
        BufferedInputStream bis = null;                                      // FileChannel of this InputStream, for faster reading
        FileDescriptor fd;

        // chunk is a file chunk, hash stores the hash of file chunks


        // ===============================================================
        // ====================       CORE          ======================
        // ===============================================================


        // Try to open an FileInputStream and a FileChannel on the file
        try {
            int sizeRead;                                               // Used to stop the reading loop.

            byte[] chunk;                                               // Stores chunks read from the file
            byte hash[];                                                // Stores the hash of chunk

            bis = new BufferedInputStream(ctx.getContentResolver().openInputStream(fileUri));

            // Try to read chunks from the file
            try {
                // Clean the buffer before reading
                int rank = 0;


                do {
                    byte[] byteBuffer = new byte[BUFFER_SIZE];   // ByteBuffer, used to read chunks of the file. Size is 5ko.

                    // sizeRead equals -1 if end of stream is reached
                    // We read a chunk from the file channel, of size BUFFER_SIZE
                    sizeRead = bis.read(byteBuffer);
                    // Flipping the buffer is necessary before reading its content.

                    // if there's data to read from the buffer, read it

                    if (sizeRead > 0) {
                        // Store the chunk in the chunk variable
                        chunk = new byte[sizeRead];
                        // Get the size of the chunk
                        chunk = Arrays.copyOfRange(byteBuffer, 0, sizeRead);
                        // Get the hash of the chunk
                        hash = hash(chunk);
                        // Get the hexadecimal representation of the Hash
                        String hexHash = toHex(hash);
                        // Store the hash in hashTable
                        hashTable.add(hexHash);
                        sizeTable.add(String.valueOf(sizeRead));
                        // Send the part to Firebase
                        //fb.writeNewPart(null, String.valueOf(size), hexHash);
                        rank = rank + 1;
                    }
                    if (rank % 10 == 0) {
                        Log.d(TAG, "Rank: " + rank);
                    }

                } while(sizeRead > 0);  // The reading loop stops when end of stream is reached


                Log.w(TAG, "Hashing finished");
            }
            catch (IOException e) {
                Log.e(TAG, "ERROR: While reading file.");
                e.printStackTrace();
            }
        }
        catch (FileNotFoundException e) {
            Log.e(TAG, "ERROR: File not found exception");
            e.printStackTrace();
        }
        catch (NullPointerException e) {
            Log.e(TAG, "ERROR: NullPointerException thrown by content Resolver");
            e.printStackTrace();
        }
        // Close FileInputStream and FileChannel if they were opened
        finally {
            try {
                if (fis != null) {
                    fis.close();
                }
            }
            catch (IOException e) {
                Log.e(TAG, "ERROR: IO Exception");
                e.printStackTrace();
            }
            try {
                if (bis != null) {
                    bis.close();
                    Log.d(TAG, "BufferedInputStream close");
                }
            }
            catch (IOException e) {
                Log.e(TAG, "ERROR: IO Exception");
                e.printStackTrace();
            }
        }

        HashMap<String, ArrayList<String>> result = new HashMap<>();
        result.put("hashes", hashTable);
        result.put("sizes", sizeTable);

        return result;
    }

    // ===============================================================
    // ====================        METHODS       =====================
    // ===============================================================

    /**
     * Converts an array of bytes into its hexadecimal representation.
     * @param hash byte[], array of bytes
     * @return hexString String
     */
    private synchronized String toHex(byte[] hash) {
        StringBuilder sb = new StringBuilder(hash.length * 2);
        for(byte b: hash)
            sb.append(String.format("%02x", b));
        return sb.toString();
    }

    private String toASCII(byte[] hash) {
        String asciiString = null;
        try {
            asciiString = new String(hash, "UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            Log.e(TAG, "ERROR while encoding hash");
            e.printStackTrace();
        }
        return asciiString;
    }

    private String toBinaryString(byte[] hash) {
        String binaryString = null;
        binaryString = hash.toString();
        return binaryString;
    }

    /**
     * Hashes a chunk of file into 160-bit long SHA-1 hash.
     * @param chunk byte[], chunk of file to hash
     * @return hashed_chunk byte[160], hashed chunk through SHA-1 algorithm
     */
    @Nullable
    private synchronized byte[] hash(byte[] chunk) {
        if(digester != null) {
            byte[] hashed_chunk;
            digester.reset();
            hashed_chunk = digester.digest(chunk);
            return hashed_chunk;
        }
        else {
            Log.e(TAG, "ERROR: Digester not initialized");
            return null;
        }
    }

    /**
     * Takes in a byte array and returns the hexadecimal String representation of its hash.
     * @param chunk (byte[]), the chunk to hash
     * @return  chunk_hash (String), the chunk's hash
     */
    public synchronized String hashToString(byte[] chunk) {
        return toHex(hash(chunk));
    }

    /**
     * Takes in an Uri pointing to a file and returns the hexadecimal String representation of its hash.
     * @param fileUri (Uri)
     * @return  fileHashcode (String), files's hash
     */
    public synchronized String getFileHashcode(Uri fileUri) {

        FileInputStream fis = null;                                 // InputStream pointing to the file
        BufferedInputStream bis = null;                                      // FileChannel of this InputStream, for faster reading
        FileDescriptor fd;

        String hashcode = "Failed to compute hashcode";

        try {
            fd = ctx.getContentResolver().openFileDescriptor(fileUri, "r").getFileDescriptor();
            bis = new BufferedInputStream(new FileInputStream(fd));
            try {
                int size = bis.available();
                byte buffer[] = new byte[size];
                bis.read(buffer, 0, size);
                hashcode = toHex(hash(buffer));
            }
            catch (IOException e) {
                Log.e(TAG, "IOException: ");
                e.printStackTrace();
            }
        }
        catch (FileNotFoundException e) {
            Log.e(TAG, "FileNotFoundException");
            e.printStackTrace();
        }
        finally {
            try {
                if (bis != null) {
                    bis.close();
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }

        }

        return hashcode;

    }

}
