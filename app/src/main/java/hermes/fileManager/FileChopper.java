package hermes.fileManager;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;

import hermes.Hermes;
import hermes.localDB.HermesDBFile;
import hermes.localDB.HermesDBPart;
import hermes.localDB.HermesLocalDB;

public class FileChopper {

    private final static String TAG = "FileChopper";    // Tag used when Logging.
    public final static int CHUNK_SIZE = FileHasher.BUFFER_SIZE;

    private HermesLocalDB _localDB;
    private Context _context;
    private FileHasher _fh;

    public FileChopper(Context context, HermesLocalDB localDB, FileHasher fh) {
        _context = context;
        _localDB = localDB;
        _fh = fh;
    }

    /**
     * Gets the requested part as a byte array. Unefficient, should be upgraded soon.
     * This method slows down download a lot.
     * @param fileHashcode (String), file's hashcode
     * @param partHashcode (String), requested part's hashcode
     * @return chunk (byte[]), requested part as an array of byte
     */
    public synchronized byte[] execute(String fileHashcode, String partHashcode) {

        BufferedInputStream bis = null;

        // Get a representation of the file which is shared
        _localDB.open();
        HermesDBFile file =  _localDB.getFileWithHashcode(fileHashcode);
        HermesDBPart part = _localDB.getPartWithHashcodes(partHashcode, fileHashcode);
        _localDB.close();

        int rank = part.getRank();

        Log.w(TAG, "Rank of part: " + part.getRank());


        // Retrieve file's Uri
        String uriStr = file.getUri();


        try {

            Uri fileUri = Uri.parse(uriStr);
            // Open a BufferedInputStream from target file
            bis = new BufferedInputStream(_context.getContentResolver().openInputStream(fileUri));

            // These variables are used during file reading
            int sizeRead;
            byte[] chunk = new byte[CHUNK_SIZE];
            String chunk_hash = "";

            /*
            if (rank >3) {
                bis.skip((rank-2)*CHUNK_SIZE);
            }
            */

            do {
                // reading BufferedInputStream into chunk.
                // sizeRead keeps trace of number of bytes read, it equals -1 if end of stream is reached
                sizeRead = bis.read(chunk);

                if (sizeRead > 0) {
                    // If we read bytes, we hash the chunk read
                    byte[] chunk_tmp = Arrays.copyOfRange(chunk, 0, sizeRead);
                    chunk_hash = _fh.hashToString(chunk_tmp);
                }
            } while (! chunk_hash.equals(partHashcode) && sizeRead > 0);    // loop ends if part is found, i.e. if hashes match


            if (chunk_hash.equals(partHashcode)) {
                Log.w(TAG, "Hashcodes corresponding, required part has been found. SizeRead : "+sizeRead);
                return Arrays.copyOfRange(chunk, 0, sizeRead);
            }
            else {
                // If file was not found, return empty bytes array
                Log.d(TAG, "Part not found !");
                chunk = new byte[0];
                return chunk;
            }
        }
        catch ( IOException e) {
            e.printStackTrace();
        }
        finally {
            try {
                if (bis != null) {
                    bis.close();
                }
            }
            catch (IOException e ) {
                e.printStackTrace();
            }

        }
        return null;
    }

}

