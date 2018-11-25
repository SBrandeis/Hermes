package com.example.pierreaverous.hermesproject;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import hermes.Hermes;

public class FileSelectActivity extends AppCompatActivity {

    private static final int RC_BROWSE_LOCAL_FILES = 9002;

    private TextView sizeView;
    private TextView extensionView;
    private EditText nameView;
    private TextView uriView;
    private Button addButton;

    private Hermes hermes;

    private String fileName;
    private long fileSize;
    private String fileExtension;
    private Uri fileUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_select);

        sizeView = findViewById(R.id.sizeField);
        extensionView = findViewById(R.id.extensionField);
        nameView = findViewById(R.id.nameField);
        uriView = findViewById(R.id.uriField);
        addButton = findViewById(R.id.addFileButton);

        hermes = new Hermes(this);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fileName = nameView.getText().toString();
                hermes.shareFile(fileUri, fileName, fileSize, fileExtension);
                setResult(RESULT_OK);
                finish();
            }
        });


        Intent browseFileIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        browseFileIntent.setType("*/*");
        browseFileIntent.addCategory(Intent.CATEGORY_OPENABLE);

        startActivityForResult(browseFileIntent, RC_BROWSE_LOCAL_FILES);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if( requestCode == RC_BROWSE_LOCAL_FILES) {
            if (resultCode == RESULT_OK) {
                Uri fileUri = data.getData();
                if (fileUri != null) {
                    updateUI(fileUri);
                }
                else {
                    setResult(RESULT_CANCELED);
                    finish();
                }
            }
            else {
                setResult(RESULT_CANCELED);
                finish();
            }
        }
    }

    private void updateUI(Uri fileUri) {

        this.fileUri = fileUri;
        fileName = hermes.getFileNameFromUri(fileUri);
        fileSize = hermes.getFileSizeFromUri(fileUri);
        fileExtension = hermes.getFileTypeFromUri(fileUri);


        nameView.setText(fileName);
        sizeView.setText(String.valueOf(fileSize));
        extensionView.setText(fileExtension);
        uriView.setText(fileUri.toString());
    }
}
