package hermes.localDB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static hermes.localDB.HermesLocalDB.COL_FILE_HASHCODE;
import static hermes.localDB.HermesLocalDB.COL_FILE_ID;
import static hermes.localDB.HermesLocalDB.COL_FILE_NAME;
import static hermes.localDB.HermesLocalDB.COL_FILE_NB_PARTS;
import static hermes.localDB.HermesLocalDB.COL_FILE_SIZE;
import static hermes.localDB.HermesLocalDB.COL_FILE_URI;
import static hermes.localDB.HermesLocalDB.COL_PARTS_FILE_HASHCODE;
import static hermes.localDB.HermesLocalDB.COL_PARTS_HASHCODE;
import static hermes.localDB.HermesLocalDB.COL_PARTS_ID;
import static hermes.localDB.HermesLocalDB.COL_PARTS_RANK;
import static hermes.localDB.HermesLocalDB.COL_PARTS_SIZE;
import static hermes.localDB.HermesLocalDB.TABLE_FILES;
import static hermes.localDB.HermesLocalDB.TABLE_PARTS;

/**
 * This class is a tool to create the tables in the local database
 */
public class OpenHelper extends SQLiteOpenHelper {

    // Database tables and columns

    private static final String CREATE_FILES_TABLE = "CREATE TABLE " + TABLE_FILES + " ("
            + COL_FILE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COL_FILE_HASHCODE + " TEXT NOT NULL, "
            + COL_FILE_NAME + " TEXT NOT NULL, "
            + COL_FILE_URI + " TEXT NOT NULL, "
            + COL_FILE_NB_PARTS + " INTEGER NOT NULL, "
            + COL_FILE_SIZE + " INTEGER NOT NULL"
            + ");";

    private static final String CREATE_PARTS_TABLE = "CREATE TABLE " + TABLE_PARTS +  " ("
            + COL_PARTS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COL_PARTS_HASHCODE + " TEXT NOT NULL, "
            + COL_PARTS_SIZE + " INTEGER NOT NULL, "
            + COL_PARTS_FILE_HASHCODE + " TEXT NOT NULL, "
            + COL_PARTS_RANK + " INTEGER NOT NULL "
            + ", FOREIGN KEY (" + COL_PARTS_FILE_HASHCODE + ") REFERENCES " + TABLE_FILES + "(" + COL_FILE_HASHCODE + ")"
            + ");";


    OpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //on crée la table à partir de la requête écrite dans la variable CREATE_BDD
        db.execSQL(CREATE_PARTS_TABLE);
        db.execSQL(CREATE_FILES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //On peut faire ce qu'on veut ici moi j'ai décidé de supprimer la table et de la recréer
        //comme ça lorsque je change la version les id repartent de 0
        db.execSQL("DROP TABLE " + TABLE_FILES + ";");
        db.execSQL("DROP TABLE " + TABLE_PARTS + ";");
        onCreate(db);
    }
}
