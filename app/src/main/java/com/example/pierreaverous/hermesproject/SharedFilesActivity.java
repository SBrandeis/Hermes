package com.example.pierreaverous.hermesproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import hermes.Hermes;
import hermes.localDB.HermesDBFile;
import hermes.localDB.HermesDBPart;
import SharedFilesAdapter.FileAdapter;
import SharedFilesAdapter.FileViewHolder;
import SharedFilesAdapter.PartAdapter;

public class SharedFilesActivity extends AppCompatActivity {

    private static final int RC_SHARE_FILE = 9003;

    private ListView listView;
    private Button addFileButton;

    private ListAdapter adapter;

    private List<HermesDBFile> files;
    private List<HermesDBPart> parts;

    private Hermes hermes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shared_files);

        listView = findViewById(R.id.listView);
        addFileButton = findViewById(R.id.add);

        addFileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent fileSelectIntent = new Intent(SharedFilesActivity.this, FileSelectActivity.class);
                startActivityForResult(fileSelectIntent, RC_SHARE_FILE);
            }
        });

        hermes = new Hermes(this);
        files = getFiles();
        if (files == null) {
            files = new ArrayList<>();
        }

        adapter = new FileAdapter(this, files);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                parts = getParts(Integer.valueOf(((String) ((FileViewHolder) view.getTag()).getId().getText()).substring(4)));
                adapter = new PartAdapter(SharedFilesActivity.this, parts);
                listView.setAdapter(adapter);
            }
        });
    }

    private List<HermesDBFile> getFiles() {
        return hermes.getLocalSharedFiles();
    }

    private List<HermesDBPart> getParts(int id) {
        return hermes.getPartsOfSharedFile(id);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SHARE_FILE) {

            if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "File selection cancelled", Toast.LENGTH_LONG).show();
            }
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Sharing file ?", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
