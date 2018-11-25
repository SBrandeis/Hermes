package com.example.pierreaverous.hermesproject;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import hermes.Hermes;
import hermes.localDB.HermesDBFile;
import hermes.localDB.HermesDBPart;
import hermesP2P.server.HermesP2PServer;

public class BrowseActivity extends AppCompatActivity {

    //HermesP2PClient hermesP2PClient;

    private Hermes hermes;

    Button startClientBtn, startServerButton, getFileBtn;
    TextView ipShowTextview;
    EditText ipEntry;

    private static final int RC_PERMISSION = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse);

        hermes = new Hermes(this);

        startClientBtn = findViewById(R.id.start_client_btn);
        startServerButton = findViewById(R.id.start_server_btn);
        getFileBtn = findViewById(R.id.ask_file_btn);

        ipShowTextview = findViewById(R.id.ip_show_textview);
        ipEntry = findViewById(R.id.ip_enter_edit);

        startClientBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //hermesP2PClient = new HermesP2PClient(BrowseActivity.this);
            }
        });

        startServerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(BrowseActivity.this,
                            new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},
                            RC_PERMISSION);

                } else {
                    Intent service = new Intent(BrowseActivity.this, HermesP2PServer.class);
                    startService(service);
                    ipShowTextview.setText("Ip address for here is : " + HermesP2PServer.getIpAddress());
                    ipEntry.setText(HermesP2PServer.getIpAddress());
                }
            }
        });

        getFileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ip = ipEntry.getText().toString();
                HermesDBFile file = hermes.getLocalSharedFiles().get(0);
                HermesDBPart part = hermes.getPartsOfSharedFile(file.getId()).get(0);
               /* hermesP2PClient.startConnection(ip, 8080, "getfile"
                        + "/" + file.getHashcode()
                        + "/" + part.getHashcode());
                        */
            }
        });

    }
}
