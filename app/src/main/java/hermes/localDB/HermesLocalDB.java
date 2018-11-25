package hermes.localDB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class HermesLocalDB {
    private static final int VERSION_BDD = 3;
    private static final String NOM_BDD = "hermes.db";

     static final String TABLE_FILES = "table_files";
    static final String COL_FILE_ID = "file_id";
    static final int NUM_COL_FILE_ID = 0;
    static final String COL_FILE_HASHCODE = "file_hashcode";
    static final int NUM_COL_FILE_HASHCODE = 1;
    static final String COL_FILE_NAME = "file_name";
    static final int NUM_COL_FILE_NAME = 2;
    static final String COL_FILE_URI = "file_uri";
    static final int NUM_COL_FILE_URI= 3;
    static final String COL_FILE_SIZE = "file_size";
    static final int NUM_COL_FILE_SIZE= 4;
    static final String COL_FILE_NB_PARTS = "file_nb_parts";
    static final int NUM_COL_FILE_NB_PARTS = 5;


    static final String TABLE_PARTS = "`table_parts`";
    static final String COL_PARTS_ID = "parts_id";
    static final int NUM_COL_PARTS_ID = 0;
    static final String COL_PARTS_HASHCODE = "parts_hashcode";
    static final int NUM_COL_PARTS_HASHCODE = 1;
    static final String COL_PARTS_SIZE = "parts_size";
    static final int NUM_COL_PARTS_SIZE = 2;
    static final String COL_PARTS_FILE_HASHCODE = "file_hashcode";
    static final int NUM_COL_PARTS_FILE_HASHCODE = 3;
    static final String COL_PARTS_RANK = "parts_rank";
    static final int NUM_COL_PARTS_RANK = 4;




    private SQLiteDatabase bdd;

    private OpenHelper openHelper;

    /**
     * Constructor, takes application's context as argument
     * @param context (Context), application's context (this)
     */
    public HermesLocalDB(Context context){
        //On crée la BDD et sa table
        openHelper = new OpenHelper(context, NOM_BDD, null, VERSION_BDD);
    }

    /**
     * Establish connection with the database
     */
    public synchronized void open(){
        //on ouvre la BDD en écriture
        bdd = openHelper.getWritableDatabase();
    }

    /**
     * Closes connection with the database
     */
    public synchronized void close(){
        //on ferme l'accès à la BDD
        bdd.close();
    }

    /**
     * returns Database
     * @return bdd (SQLiteDatabase)
     */
    public SQLiteDatabase getBDD(){
        return bdd;
    }

    /**
     * Inserts a row in table_parts, with attributes of HermesDBPart part.
     * @param part (HermesDBPart), describes the row to insert
     * @return (long)
     */
    public synchronized long insertPart(HermesDBPart part){
        //Création d'un ContentValues (fonctionne comme une HashMap)
        ContentValues values = new ContentValues();
        //on lui ajoute une valeur associée à une clé (qui est le nom de la colonne dans laquelle on veut mettre la valeur)
        values.put(COL_PARTS_FILE_HASHCODE, part.getFileHashcode());
        values.put(COL_PARTS_HASHCODE, part.getHashcode());
        values.put(COL_PARTS_RANK, part.getRank());
        values.put(COL_PARTS_SIZE, part.getSize());
        //on insère l'objet dans la BDD via le ContentValues
        return bdd.insert(TABLE_PARTS, null, values);
    }

    /**
     * Updates a part in table_parts with its id
     * @param id (int), id of the row to update
     * @param part (HermesDBPart), describes the new values of the row
     * @return (int)
     */
    public int updatePart(int id, HermesDBPart part){
        //La mise à jour d'un livre dans la BDD fonctionne plus ou moins comme une insertion
        //il faut simplement préciser quel livre on doit mettre à jour grâce à l'ID
        ContentValues values = new ContentValues();
        values.put(COL_PARTS_FILE_HASHCODE, part.getFileHashcode());
        values.put(COL_PARTS_HASHCODE, part.getHashcode());
        values.put(COL_PARTS_RANK, part.getRank());
        values.put(COL_PARTS_SIZE, part.getSize());
        return bdd.update(TABLE_PARTS, values, COL_PARTS_ID + " = " +id, null);
    }

    /**
     * Removes a part from table_parts, using its id
     * @param id (int), id of the row to delete
     * @return (int)
     */
    public int removePartWithID(int id){
        //Suppression d'un livre de la BDD grâce à l'ID
        return bdd.delete(TABLE_PARTS, COL_PARTS_ID + " = " +id, null);
    }

    /**
     * Removes a part from table_parts, using its hashcode and its file's hashcode
     * @param part_hashcode (String) Hashcode of the part
     * @param file_hashcode (String) Hashcode of the file
     * @return (int)
     */
    public int removePartWithHashcodes(String part_hashcode, String file_hashcode){
        //Suppression d'un livre de la BDD grâce à l'ID
        return bdd.delete(TABLE_PARTS, COL_PARTS_FILE_HASHCODE + " = " + file_hashcode
                                        + " AND " + COL_PARTS_HASHCODE + " = " + part_hashcode, null);
    }

    /**
     * Get a HermesDBPart representation of a row, knowing its hashcode and file's hashcode
     * @param part_hashcode (String), part's hashcode
     * @param file_hashcode (String), file's hashcode
     * @return part (HermesDBPart)
     */
    public synchronized HermesDBPart getPartWithHashcodes(String part_hashcode, String file_hashcode){
        //Récupère dans un Cursor les valeurs correspondant à un livre contenu dans la BDD (ici on sélectionne le livre grâce à son titre)
        String cols[] = new String[] {COL_PARTS_ID, COL_PARTS_HASHCODE, COL_PARTS_SIZE, COL_PARTS_FILE_HASHCODE, COL_PARTS_RANK};
        Cursor c = bdd.query(TABLE_PARTS, cols,
                COL_PARTS_HASHCODE + " = \"" + part_hashcode +"\""
                        + " AND " + COL_PARTS_FILE_HASHCODE + " = \"" + file_hashcode + "\"", null, null, null, null);
        return cursorToPart(c);
    }


    //Cette méthode permet de convertir un cursor en un livre

    /**
     * Inserts a row in table_files, using its HermesDBFile represntation
     * @param file (HermesDBFile), file to add in DB
     * @return (long)
     */
    public synchronized long insertFile(HermesDBFile file){
        //Création d'un ContentValues (fonctionne comme une HashMap)
        ContentValues values = new ContentValues();
        //on lui ajoute une valeur associée à une clé (qui est le nom de la colonne dans laquelle on veut mettre la valeur)
        values.put(COL_FILE_HASHCODE, file.getHashcode());
        values.put(COL_FILE_NAME, file.getName());
        values.put(COL_FILE_URI, file.getUri());
        values.put(COL_FILE_SIZE, file.getSize());
        values.put(COL_FILE_NB_PARTS, file.getNumberOfParts());
        //on insère l'objet dans la BDD via le ContentValues
        return bdd.insert(TABLE_FILES, null, values);
    }

    /**
     * Updates a row in table_files, knowing its id.
     * @param id (int), ID of the row to update
     * @param file (HermesDBFile), HermesDBFile representation of the row to add to the table
     * @return (int)
     */
    public int updateFile(int id, HermesDBFile file){
        //La mise à jour d'un livre dans la BDD fonctionne plus ou moins comme une insertion
        //il faut simplement préciser quel livre on doit mettre à jour grâce à l'ID
        ContentValues values = new ContentValues();
        values.put(COL_FILE_HASHCODE, file.getHashcode());
        values.put(COL_FILE_NAME, file.getName());
        values.put(COL_FILE_URI, file.getUri());
        values.put(COL_FILE_SIZE, file.getSize());
        values.put(COL_FILE_NB_PARTS, file.getNumberOfParts());
        return bdd.update(TABLE_FILES, values, COL_FILE_ID + " = " +id, null);
    }

    /**
     * Removes a row in table_files, knowing its ID
     * @param id (int), id of the row to delete
     * @return (int)
     */
    public int removeFileWithID(int id){
        //Suppression d'un livre de la BDD grâce à l'ID
        return bdd.delete(TABLE_FILES, COL_FILE_ID + " = " +id, null);
    }

    /**
     * Removes a file in table_files, knowing its hashcode
     * @param hashcode (String), hashcode of the file to remove from the table
     * @return (int)
     */
    public int removeFileWithHashcode(String hashcode){
        //Suppression d'un livre de la BDD grâce à l'ID
        return bdd.delete(TABLE_FILES, COL_FILE_ID + " = " +hashcode, null);
    }

    /**
     * Get a file from the db using its name
     * @param name (String), name of the file.
     * @return file (HermesDBFile)
     */
    public HermesDBFile getFileWithName(String name){
        //Récupère dans un Cursor les valeurs correspondant à un livre contenu dans la BDD (ici on sélectionne le livre grâce à son titre)
        String cols[] = new String[] {COL_FILE_ID, COL_FILE_HASHCODE, COL_FILE_NAME, COL_FILE_URI, COL_FILE_SIZE, COL_FILE_NB_PARTS};
        Cursor c = bdd.query(TABLE_FILES, cols, COL_FILE_NAME + " LIKE \"" + name +"\"", null, null, null, null);
        return cursorToFile(c);
    }

    /**
     * Get a file from the db using its hashcode
     * @param hashcode (String), hashcode of the file
     * @return file (HermesDBFile)
     */
    public HermesDBFile getFileWithHashcode(String hashcode){
        //Récupère dans un Cursor les valeurs correspondant à un livre contenu dans la BDD (ici on sélectionne le livre grâce à son titre)
        String cols[] = new String[] {COL_FILE_ID, COL_FILE_HASHCODE, COL_FILE_NAME, COL_FILE_URI, COL_FILE_SIZE, COL_FILE_NB_PARTS};
        Cursor c = bdd.query(TABLE_FILES, cols, COL_FILE_HASHCODE + " LIKE \"" + hashcode +"\"", null, null, null, null);
        return cursorToFile(c);
    }

    /**
     * Retrieve parts from local database using file's id
     * @param id (int), file's id
     * @return list of HermesDBPart, associated parts
     */
    public synchronized List<HermesDBPart> getParts(int id) {
        String idStr = getHashcodeFromId(id);
        String file_hashcode[] = new String[] {idStr};
        String cols[] = new String[] {COL_PARTS_ID, COL_PARTS_HASHCODE, COL_PARTS_SIZE , COL_PARTS_FILE_HASHCODE, COL_PARTS_RANK };
        Cursor c = bdd.query(TABLE_PARTS, cols, COL_PARTS_FILE_HASHCODE + " = ?", file_hashcode, null, null, COL_PARTS_RANK + " DESC");
        return cursorToParts(c);
    }

    /**
     * retrieve all files in local database
     * @return List<HermesDBFile>
     */
    public synchronized List<HermesDBFile> getFiles() {
        String cols[] = new String[] {COL_FILE_ID, COL_FILE_HASHCODE, COL_FILE_NAME, COL_FILE_URI, COL_FILE_SIZE, COL_FILE_NB_PARTS};
        Cursor c = bdd.query(TABLE_FILES, cols, null, null, null, null, COL_FILE_ID + " DESC");
        return cursorToFiles(c);
    }


    /**
     * Get HermesDBFile from cursor (obtained through SQL request)
     * @param c (Cursor), cursor
     * @return file (HermesDBFile)
     */
    private synchronized HermesDBFile cursorToFile(Cursor c){
        //si aucun élément n'a été retourné dans la requête, on renvoie null
        if (c.getCount() == 0)
            return null;

        //Sinon on se place sur le premier élément
        c.moveToFirst();
        //On créé un livre
        HermesDBFile file = new HermesDBFile();
        //on lui affecte toutes les infos grâce aux infos contenues dans le Cursor
        file.setId(c.getInt(NUM_COL_FILE_ID));
        file.setHashcode(c.getString(NUM_COL_FILE_HASHCODE));
        file.setName(c.getString(NUM_COL_FILE_NAME));
        file.setNumberOfParts(c.getInt(NUM_COL_FILE_NB_PARTS));
        file.setSize(c.getLong(NUM_COL_FILE_SIZE));
        file.setUri(c.getString(NUM_COL_FILE_URI));
        //On ferme le cursor
        c.close();

        //On retourne le livre
        return file;
    }

    /**
     * Intermediate method - converts a cursor (SQLite request) into List<HermesDBFile>
     * @param c (Cursor), cursor to convert
     * @return List<HermesDBFile>
     */
    private synchronized List<HermesDBFile> cursorToFiles(Cursor c){

        List<HermesDBFile> files = new ArrayList<>();
        //si aucun élément n'a été retourné dans la requête, on renvoie null
        if (c.getCount() == 0)
            return null;

        //Sinon on se place sur le premier élément
        c.moveToFirst();
        //On créé un livre
        do {
            HermesDBFile file = new HermesDBFile();
            //on lui affecte toutes les infos grâce aux infos contenues dans le Cursor
            file.setId(c.getInt(NUM_COL_FILE_ID));
            file.setHashcode(c.getString(NUM_COL_FILE_HASHCODE));
            file.setName(c.getString(NUM_COL_FILE_NAME));
            file.setNumberOfParts(c.getInt(NUM_COL_FILE_NB_PARTS));
            file.setSize(c.getLong(NUM_COL_FILE_SIZE));
            file.setUri(c.getString(NUM_COL_FILE_URI));
            files.add(file);
        }
        while (c.moveToNext());
        //On ferme le cursor
        c.close();

        //On retourne le livre
        return files;
    }

    /**
     * Get HermesDBParts from cursor (obtained through SQL request)
     * @param c (Cursor), cursor
     * @return parts (List<HermesDBPart>)
     */
    private synchronized List<HermesDBPart> cursorToParts(Cursor c){

        List<HermesDBPart> parts = new ArrayList<>();
        //si aucun élément n'a été retourné dans la requête, on renvoie null
        if (c.getCount() == 0)
            return null;

        //Sinon on se place sur le premier élément
        c.moveToFirst();
        //On créé un livre
        do {
            HermesDBPart part = new HermesDBPart();
            //on lui affecte toutes les infos grâce aux infos contenues dans le Cursor
            part.setId(c.getInt(NUM_COL_PARTS_ID));
            part.setFileHashcode(c.getString(NUM_COL_PARTS_FILE_HASHCODE));
            part.setHashcode(c.getString(NUM_COL_PARTS_HASHCODE));
            part.setRank(c.getInt(NUM_COL_PARTS_RANK));
            part.setSize(c.getLong(NUM_COL_PARTS_SIZE));
            parts.add(part);
        }
        while (c.moveToNext());
        //On ferme le cursor
        c.close();

        //On retourne le livre
        return parts;
    }


    /**
     * Get HermesDBPart from cursor (obtained through SQL request)
     * @param c (Cursor), cursor
     * @return part (HermesDBPart)
     */
    private synchronized HermesDBPart cursorToPart(Cursor c){
        //si aucun élément n'a été retourné dans la requête, on renvoie null
        if (c.getCount() == 0) {
            Log.w("LocalDB", "Part not found in db");
            return null;
        }

        //Sinon on se place sur le premier élément
        c.moveToFirst();
        //On créé un livre
        HermesDBPart part = new HermesDBPart();
        //on lui affecte toutes les infos grâce aux infos contenues dans le Cursor
        part.setId(c.getInt(NUM_COL_PARTS_ID));
        part.setHashcode(c.getString(NUM_COL_PARTS_HASHCODE));
        part.setSize(c.getInt(NUM_COL_PARTS_SIZE));
        part.setFileHashcode(c.getString(NUM_COL_PARTS_FILE_HASHCODE));
        part.setRank(c.getInt(NUM_COL_PARTS_RANK));
        Log.w("LocalDB", String.valueOf(part.getRank()) );

        //On ferme le cursor
        c.close();

        //On retourne le livre
        return part;
    }

    /**
     * Get file hashcode from it's id
     * @param id (int), file's id
     * @return hashcode (String)
     */
    private synchronized String getHashcodeFromId(int id) {
        String idStr = String.valueOf(id);
        Cursor c_id = bdd.query(TABLE_FILES, new String[] {COL_FILE_HASHCODE}, COL_FILE_ID + " = ?", new String[] {idStr}, null, null, null);
        if (c_id.getCount() == 0) {
            return null;
        }
        c_id.moveToFirst();
        String hashcode = new String();
        hashcode = c_id.getString(0);
        c_id.close();
        return hashcode;
    }

}

