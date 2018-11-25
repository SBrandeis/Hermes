package com.example.pierreaverous.hermesproject;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import firebase.DB_File;
import firebase.DB_Part;
import firebase.DB_User;
import firebase.query.FileQueryAdapter;
import firebase.query.QueryMaker;
import firebase.query.QueryManager;
import firebase.storageClasses.StoreQueryManager;
import hermesP2P.client.FileReceiveService;

/**
 * This activity is the activity in which the end-user is going to choose the
 * files he is going to download from the results of the query he made.
 * This acrivity extends the QueryMaker interface, because it will be making
 * queries to the FireBase through QueryManager objects in the background,
 * whenever a user clicks a file to get its information.
 */
public class QueryResultActivity extends AppCompatActivity implements QueryMaker {

    FileReceiveService downloadService;
    static final String TAG = "QueryResultActivity";
    boolean mBound;
    boolean isBound;

    private ListView listView;
    private ListAdapter adapter;
    private List<DB_File> files;
    private List<DB_Part> parts;
    private List<DB_User> users;

    private QueryManager queryManager;
    LoadingBar progressDialog;

    public FileReceiveService getDownloadService() {
        return downloadService;
    }

    private ServiceConnection downloadServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            FileReceiveService.DownloadServiceBinder downloadServiceBinder = (FileReceiveService.DownloadServiceBinder) service;
            downloadService = downloadServiceBinder.getService();
            mBound=true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

    /**
     * Binding FileReceiveService to this Activity in order to control downloads
     * from interacting with this Activity
     */
    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, FileReceiveService.class);
        isBound = bindService(intent, downloadServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query_result);
        isBound = false;

        files = new ArrayList<DB_File>();
        parts =new ArrayList<DB_Part>();
        users =new ArrayList<DB_User>();

        // We get the same QueryManager that was used in the QueryMakeActivity
        // In order to get the query results from it.
        queryManager = StoreQueryManager.getQueryManager();

        listView = findViewById(R.id.listView);

        files = queryManager.getFiles();
        Log.w(TAG, "nb of files : " + files.size());

        adapter = new FileQueryAdapter(this, files);
        listView.setAdapter(adapter);

        // ============================ CORE CODE ==========================
        //
        // Whenever a end-user clicks an element of the ListView (a file),
        // a dialog window will open, to give him information on the file and
        // ask him to confirm his intent to download the file.
        // Meanwhile, the app is going to check on the FireBase the parts
        // needed for the selected file, and retrieve them as DB_Part objects.
        // Therefore, if the user decides to download the file, the app will
        // be ready to go.
        //
        // ==================================================================
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HashMap<String, String> partHashes = files.get(position).getParts();
                StoreQueryManager.setFile(files.get(position));
                StoreQueryManager.clearParts();
                if( partHashes != null) {
                    for (int i=0; i<partHashes.size();i++) {
                        (new QueryManager(QueryResultActivity.this)).queryPart(partHashes.get("part_"+(i+1)));
                    }
                }

                // LoadingBar shows progress to get the information on all parts in the file.
                // Need to wait for the queries to be over in order to start the DownloadFragment
                // When the progress is at 100%, start the DownloadDialogFragment.
                progressDialog = new LoadingBar(QueryResultActivity.this);
                progressDialog.setMax(files.get(position).getNumberOfParts());
                progressDialog.show();

            }
        });
    }

    /**
     * Need to unbind the FileReceiveService upon destroying this Activity,
     * or it will cause a Leak.
     */
    @Override
    protected void onDestroy() {
        if(isBound) {
            unbindService(downloadServiceConnection);
        }
        super.onDestroy();
    }

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

    public void addPart(DB_Part part) {
        this.parts.add(part);
        Log.w(TAG, "parts size : " + String.valueOf(this.parts.size()));
        progressDialog.incrementProgressBy(1);
        StoreQueryManager.setParts(parts);
    };
    public void addFile(DB_File file){this.files.add(file);};
    public void addUSer(DB_User user){this.users.add(user);};
    public void delPart(DB_Part part){this.parts.remove(part);};
    public void delFile(DB_File file){this.files.remove(file);};
    public void delUSer(DB_User user){this.users.remove(user);};

}
