package hermes;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.BufferedInputStream;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import firebase.DB_File;
import firebase.FireBase;
import hermes.fileManager.FileChopper;
import hermes.fileManager.FileHasher;
import hermes.localDB.HermesDBFile;
import hermes.localDB.HermesDBPart;
import hermes.localDB.HermesLocalDB;

public class Hermes {

    private Context _context;
    private HermesLocalDB _localDB;
    private FileHasher _fh;
    private FireBase _fireBase;
    private FirebaseAuth _fireBaseAuth;

    private static final int CHUNK_SIZE = FileHasher.BUFFER_SIZE;
    private static final String TAG = "Hermes";

    /**
     * Constructor of Hermes object.
     * @param _context (Context), Activity's context
     */
    public Hermes(Context _context) {
        this._context = _context;
        this._localDB = new HermesLocalDB(this._context);
        this._fh = new FileHasher(this._context);
        this._fireBase = new FireBase(this._context);
        this._fireBaseAuth = FirebaseAuth.getInstance();
    }

    /*
    =================================================================================
                                Database Reading Methods
    =================================================================================
        Methods below access the local database, which stores the shared files and
        their parts.
     */

    // PUBLIC METHODS

    /**
     * Get all shared files stored in local db.
     * @return files (List<HermesDBFile>
     */
    public synchronized List<HermesDBFile> getLocalSharedFiles() {
        List<HermesDBFile> localSharedFiles;
        _localDB.open();
        localSharedFiles = _localDB.getFiles();
        _localDB.close();
        return localSharedFiles;
    }

    /**
     * Get all parts from a locally stored shared file, using its id
     * @param id (int), local id of the target shared file
     * @return parts (List<HermesDBPart>
     */
    public synchronized List<HermesDBPart> getPartsOfSharedFile(int id) {
        List<HermesDBPart> partsOfSharedFile;
        _localDB.open();
        partsOfSharedFile = _localDB.getParts(id);
        _localDB.close();
        return partsOfSharedFile;
    }

    /**
     * Retrieve a shared file's Uri from its hash
     * @param fileHashcode (String), hexadecimal representation of file's hashcode
     * @return fileUri (Uri)
     */
    public synchronized Uri getFileUriFromHashcode(String fileHashcode) {
        _localDB.open();
        String uriStr = _localDB.getFileWithHashcode(fileHashcode).getUri();
        _localDB.close();
        return Uri.parse(uriStr);
    }


    // PRIVATE METHODS

    /**
     * Get all parts from a locally shared file, using its Uri
     * @param fileUri (Uri), the file's Uri
     * @return parts (List of HermesDBParts)
     */
    private synchronized List<HermesDBPart> getPartsFromUri(Uri fileUri) {

        ArrayList<HermesDBPart> result = new ArrayList<>();

        HashMap<String, ArrayList<String>> hashAndSizes = new HashMap<>();
        hashAndSizes = _fh.hashFile(fileUri);

        ArrayList<String> partsHashcodes =  hashAndSizes.get("hashes");
        ArrayList<String> partsSizes =  hashAndSizes.get("sizes");

        String fileHashcode = getFileHashcode(fileUri);

        for( int i = 0; i < partsHashcodes.size(); i += 1) {
            String partHashcode = partsHashcodes.get(i);
            String partSize = partsSizes.get(i);
            HermesDBPart part = new HermesDBPart(fileHashcode, partHashcode, i, Long.valueOf(partSize));
            result.add(part);
        }
        return result;
    }

    /*
     =================================================================================
                                Database Writing Methods
    =================================================================================
        Methods below access the local database, which stores the shared files and
        their parts.

     */


    /**
     *  Takes in a file's Uri, name, size and extension, chop it into parts, hash those parts, and
     *  put the whole thing into local database.
     * @param fileUri (Uri), the file's Uri
     * @param fileName (String), the file's name
     * @param fileSize (long), the file's size
     * @param fileExtension (String), the file's extension
     */
    public synchronized void shareFile(Uri fileUri, final String fileName, final long fileSize, String fileExtension) {

        final Uri tmpUri = fileUri;
        final String tmpName = fileName;
        final long tmpSize = fileSize;
        final String tmpExtension = fileExtension;

        new Thread(new Runnable() {
            @Override
            public void run() {

                FirebaseUser user = _fireBaseAuth.getCurrentUser();
                // Get file's hashcode
                String fileUriAsString = tmpUri.toString();
                String fileHashcode = getFileHashcode(tmpUri);

                // Chop and hash the file in parts
                List<HermesDBPart> parts = getPartsFromUri(tmpUri);
                HashMap<String, String> partsHashmap = new HashMap<>();

                int fileNbOfParts = parts.size();

                HermesDBFile file = new HermesDBFile(fileUriAsString, fileHashcode, tmpSize, fileNbOfParts, tmpName);

                _localDB.open();
                _localDB.insertFile(file);

                for (int i = 0; i < fileNbOfParts; i+=1) {

                    HermesDBPart part = parts.get(i);
                    String partHashcode = part.getHashcode();
                    String partSize = String.valueOf(part.getSize());
                    HashMap<String, String> partOwners = new HashMap<>();

                    partOwners.put("owner_1", user.getUid());

                    partsHashmap.put("part_" + (part.getRank()+1), partHashcode);

                    _localDB.insertPart(part);
                    _fireBase.writeNewPart(fileHashcode, partOwners, partSize, partHashcode, i );
                }
                _localDB.close();

                _fireBase.writeNewFile(fileHashcode, tmpName, user.getDisplayName(), String.valueOf(tmpSize), partsHashmap);

            }
        }).start();

    }

    /*
    =================================================================================
                                Uri Processing Methods
    =================================================================================
        Methods below retrieve information about a file using its Uri

     */

    // PUBLIC METHODS

    /**
     * Retrieve a file's name from its Uri
     * @param fileUri (Uri)
     * @return fileName (String)
     */
    public synchronized String getFileNameFromUri(Uri fileUri) {

        Cursor returnCursor =
                _context.getContentResolver().query(fileUri, null, null, null, null);
        /*
         * Get the column indexes of the data in the Cursor,
         * move to the first row in the Cursor, get the data,
         * and display it.
         */
        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        returnCursor.moveToFirst();
        return returnCursor.getString(nameIndex);
    }

    /**
     * Retrieve a file's size (in bytes) from its Uri
     * @param fileUri (Uri): file's Uri
     * @return fileSize (long)
     */
    public synchronized long getFileSizeFromUri(Uri fileUri) {
        Cursor returnCursor =
                _context.getContentResolver().query(fileUri, null, null, null, null);

        int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
        returnCursor.moveToFirst();
        return returnCursor.getLong(sizeIndex);

    }

    /**
     * Retrieve file MIME type from it's Uri
     * @param fileUri (Uri): file's Uri
     * @return mimeType (String), String representation of the file's MIME type.
     */
    public synchronized String getFileTypeFromUri(Uri fileUri) {
        String mimeType = _context.getContentResolver().getType(fileUri);
        return mimeType;
    }


    // PRIVATE METHODS
    /**
     * Hashes the file at the end of the Uri, and returns the hexadecimal representation of such hash.
     * @param fileUri (Uri), the file's Uri
     * @return fileHashcode (String)
     */
    private synchronized String getFileHashcode(Uri fileUri) {
        return _fh.getFileHashcode(fileUri);
    }


    /**
     * Checks if the file is shared. Unused.
     * @param fileHashcode (String), file's hashcode
     * @return boolean, whether the file is shared
     */
    public synchronized boolean fileIsShared(String fileHashcode) {
        _localDB.open();
        HermesDBFile file = _localDB.getFileWithHashcode(fileHashcode);
        _localDB.close();
        return (file != null);
    }

    /**
     * Checks if part is in local db, i.e. if it is part of a shared file
     * @param partHashcode (String), part hashcode
     * @param fileHashcode (String), file hashcode
     * @return boolean, whether the part is shared as part of file
     */
    public synchronized boolean partOfFile(String partHashcode, String fileHashcode) {
        _localDB.open();
        HermesDBPart part = _localDB.getPartWithHashcodes(partHashcode, fileHashcode);
        _localDB.close();
        return part != null;
    }

    public synchronized byte[] getPart(String partHashcode, String fileHashcode) {

        FileChopper fileChopper = new FileChopper(_context, _localDB, _fh);
        Log.w(TAG, "Rank of required part: ");
        return fileChopper.execute(fileHashcode, partHashcode);
    }
}
