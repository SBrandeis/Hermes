package firebase.query;

import android.os.AsyncTask;
import android.util.Log;

import com.example.pierreaverous.hermesproject.QueryMakeActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import firebase.DB_File;
import firebase.DB_Part;
import firebase.DB_User;

/**
 * This class is used to make a query to the database and store its results.
 * It may be a simple query, such as retrieving all files, or a more complex
 * one, like retrieving all parts from a given file as well as a list of
 * every user owning the part for each retrieved part.
 */
public class QueryManager {

    private static final String TAG = "QueryManager";

    List<DB_File> files;
    List<DB_Part> parts;
    List<DB_User> users;

    FirebaseDatabase database;
    DatabaseReference myRef;
    QueryMaker qMaker;
    QueryMakeActivity queryMakeActivity;

    Query query;

    /**
     * Basic constructor for making a QueryManager instance.
     */
    public QueryManager() {
        this.files = new ArrayList<DB_File>();
        this.parts = new ArrayList<DB_Part>();
        this.users = new ArrayList<DB_User>();

        this.database = FirebaseDatabase.getInstance();
        this.myRef = database.getReference();

        query = null;
    }


    /**
     * Constructor for a QueryManager used by a class, that needs to
     * be able to retrieve the results in the background.
     * @param qMaker    The QueryMaker that needs the results
     */
    public QueryManager(QueryMaker qMaker) {
        this.files = new ArrayList<DB_File>();
        this.parts = new ArrayList<DB_Part>();
        this.users = new ArrayList<DB_User>();
        this.qMaker = qMaker;

        this.database = FirebaseDatabase.getInstance();
        this.myRef = database.getReference();

        query = null;
    }
    /**
     * Constructor for a QueryManager used by a class, that needs to
     * be able to retrieve the results in the background.
     * @param queryMakeActivity    The QueryMakeActivity that needs the results
     */
    public QueryManager(QueryMakeActivity queryMakeActivity) {
        this.files = new ArrayList<DB_File>();
        this.parts = new ArrayList<DB_Part>();
        this.users = new ArrayList<DB_User>();
        this.queryMakeActivity = queryMakeActivity;

        this.database = FirebaseDatabase.getInstance();
        this.myRef = database.getReference();

        query = null;
    }

    /**
     * Most basic function of this class. Used to retieve all results for a simple query.
     * Will make a query to all files whose searched field <b>starts with</b> the searched value.
     *
     * @param ref           The database Reference in which we should look ("DB_File" for example)
     * @param child         The field that should be searched (search by "filename", or by "author")
     * @param searchedValue The searched value.
     */
    public void makeQuery(String ref, String child, String searchedValue) {
        if(query != null) {
            query.removeEventListener(valueEventListener);
        }
        query = database.getReference(ref)
                .orderByChild(child)
                .startAt(searchedValue)
                .endAt(searchedValue+"\uf8ff");
        Log.w(TAG, "Query made for "+ref+"."+child+"."+searchedValue);
        query.addListenerForSingleValueEvent(valueEventListener);
    }

    /**
     * Make a query to get the part whose hashcode corresponds to the given one
     * @param hashcode  The hashcode of the looked up part
     */
    public void queryPart(String hashcode) {
        if(query != null) {
            query.removeEventListener(valueEventListener);
        }
        query = database.getReference("DB_Part")
                .orderByChild("hashCode")
                .startAt(hashcode)
                .endAt(hashcode+"\uf8ff");
        Log.w(TAG, "Query made for part "+hashcode);
        query.addListenerForSingleValueEvent(partFindListener);
    }


    /**
     * Make a query to get the user whose uid corresponds to the given one
     * @param uid   The uid of the looked up user
     */
    public void queryUser(String uid) {
        if(query != null) {
            query.removeEventListener(valueEventListener);
        }
        query = database.getReference("DB_User")
                .orderByChild("uid")
                .startAt(uid)
                .endAt(uid+"\uf8ff");
        Log.w(TAG, "Query made for User "+uid);
        query.addListenerForSingleValueEvent(userFindListener);
    }

    /**
     * This listener is used by the makeQuery function to get the results of the query.
     */
    ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot snapshot) {
            Log.w(TAG, "onDataChange() called");
            files.clear();
            parts.clear();
            users.clear();
            int i = 0;
            int count = 25;
            if (snapshot.exists()) {
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    i++;
                    files.add(childSnapshot.getValue(DB_File.class));
                    Log.w(TAG, childSnapshot.getValue(DB_File.class).getFilename());
                    if(i > count) {
                        break;
                    }
                }
            }
            queryMakeActivity.next();
        }


        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    /**
     * This listener is used by the partQuery function to get the results of the query.
     */
    ValueEventListener partFindListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot snapshot) {
            Log.w(TAG, "onDataChange() called in partFindListener");
            int i = 0;
            int count = 25;
            if (snapshot.exists()) {
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {

                    // ======================= CORE CODE =====================
                    //
                    // For every part found that matches our search criteria,
                    // we will now look for the users that own this part, in
                    // order to get their information as well.
                    //
                    // =======================================================
                    i++;
                    DB_Part currentPart= childSnapshot.getValue(DB_Part.class);
                    if (currentPart.partOwners != null) {
                        for (int j = 0; j < currentPart.getPartOwners().size(); j++) {
                            queryUser(currentPart.partOwners.get("owner_" + (j + 1)));
                        }
                    }
                    // We then add the part to our part list, and to the part
                    // list in the QueryMaker instance.
                    parts.add(currentPart);
                    qMaker.addPart(currentPart);
                    Log.w(TAG, "Part "+childSnapshot.getValue(DB_Part.class).getHashCode()+" found !");

                    //Finally, we limit the number of results to *count* (here 25)
                    if(i > count) {
                        break;
                    }
                }
            }
        }


        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    /**
     * This listener is used by the userQuery function to get the results of the query.
     */
    ValueEventListener userFindListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot snapshot) {
            Log.w(TAG, "onDataChange() called in userFindListener");
            int i = 0;
            int count = 25;
            if (snapshot.exists()) {
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    i++;
                    DB_User currentUser = childSnapshot.getValue(DB_User.class);

                    // ======================= CORE CODE =====================
                    //
                    // Upon finding matching users for our search criteria,
                    // we add them to the list of users in this QueryManager,
                    // and to the list of users that own the part found at
                    // parts[0].
                    // This only makes sense if we know that the part hashcode
                    // we looked up earlier in the partFindListener is unique.
                    // This is a flaw of our program that will need to be
                    // fixed, but for now it kind of works out for our
                    // purposes. We will come back to this if we have time
                    // for it.
                    //
                    // =======================================================
                    users.add(currentUser);
                    parts.get(0).addOwner(currentUser);
                    Log.w(TAG, "User "+childSnapshot.getValue(DB_User.class).getUid()+" found !");

                    //Finally, we limit the number of results to *count* (here 25)
                    if(i > count) {
                        break;
                    }
                }
            }
        }


        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    public List<DB_File> getFiles() {
        return files;
    }
    public void setFiles(List<DB_File> files) {
        this.files = files;
    }
    public List<DB_Part> getParts() {
        return parts;
    }
    public void setParts(List<DB_Part> parts) {
        this.parts = parts;
    }
    public List<DB_User> getUsers() {
        return users;
    }
    public void setUsers(List<DB_User> users) {
        this.users = users;
    }
    public void clearParts() {
        this.parts.clear();
    }
    public void clearFiles() {
        this.files.clear();
    }
    public void clearUsers() {
        this.users.clear();
    }
}
