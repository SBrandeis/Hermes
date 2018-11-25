package com.example.pierreaverous.hermesproject;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

import firebase.FireBase;
import hermes.Hermes;
import hermesP2P.server.HermesP2PServer;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener{

    private SignInButton signInButton;
    private TextView statusTextView;
    private Button signOutButton;
    private Button hashButton;
    private Button sharedFilesButton;
    private Button testButton;

    private Hermes hermes;
    private FireBase fireBase;

    GoogleApiClient googleApiClient;
    private boolean isAuthenticated;
    private FirebaseAuth mAuth;
    FirebaseUser user;


    private static final String TAG = "SignInActivity";
    private static final int RC_SIGN_IN = 123;
    private static final int RC_PERMISSION = 9001;

    // Choose authentication providers
    List<AuthUI.IdpConfig> providers = Arrays.asList(
            new AuthUI.IdpConfig.EmailBuilder().build(),
            new AuthUI.IdpConfig.GoogleBuilder().build());


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        isAuthenticated = false;
        mAuth = FirebaseAuth.getInstance();

        fireBase = new FireBase(this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        statusTextView = findViewById(R.id.status_text_view);
        signInButton = findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        signInButton.setOnClickListener(this);
        signOutButton = findViewById(R.id.sign_out_button);
        signOutButton.setOnClickListener(this);
        testButton = findViewById(R.id.testButton);
        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isAuthenticated) {
                    Intent queryMaking = new Intent(MainActivity.this, QueryMakeActivity.class);
                    startActivity(queryMaking);
                } else {
                    Toast.makeText(MainActivity.this, "You need to login first, in order to proceed !", Toast.LENGTH_LONG).show();
                }
            }
        });
        signIn();

        signOutButton.setEnabled(false);

        sharedFilesButton = findViewById(R.id.sharedFilesButton);
        sharedFilesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            RC_PERMISSION);

                } else {

                    if (isAuthenticated) {
                        Intent intent = new Intent(MainActivity.this, SharedFilesActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(MainActivity.this, "You need to login first, in order to proceed !", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        hermes = new Hermes(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_out_button:
                signOut();
                break;
            case R.id.sign_in_button:
                signIn();
                break;
        }
    }

    private void signOut() {
        if(user != null) {
            fireBase.writeNewUser(user.getUid(), HermesP2PServer.getIpAddress(), false,  user.getEmail(), user.getDisplayName());
        }
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            statusTextView.setText("Non connecté");
                            signInButton.setEnabled(true);
                            signOutButton.setEnabled(false);
                            Log.w(TAG, "AuthUI sign out completed");
                            isAuthenticated = false;
                        }
                        else {
                            Log.e(TAG, "AuthUI sign out failed");
                        }
                    }
                });


    }

    private void signIn() {
        // Create and launch sign-in intent
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN);
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                //Firebase Sign in was successful
                Log.w(TAG, "signInWithCredential:success");
                user = mAuth.getCurrentUser();

                // Add the signed in User to the DB_User table in our firebase.
                fireBase.writeNewUser(user.getUid(), HermesP2PServer.getIpAddress(), true,  user.getEmail(), user.getDisplayName());
                signInButton.setEnabled(false);
                signOutButton.setEnabled(true);
                statusTextView.setText("Connecté");
                isAuthenticated = true;

                Intent server = new Intent(MainActivity.this, HermesP2PServer.class);
                startService(server);

            } else {
                //Google Sign In failed
                Log.e(TAG, "AuthUI sign in failed");
            }
        }

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onStop() {
        fireBase.listenForDestroy();
        super.onStop();
    }

    @Override
    protected void onRestart() {
        fireBase.stopListeningForDestroy();
        super.onRestart();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case RC_PERMISSION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(MainActivity.this, "Permission was granted. Please retry.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Permission must be granted to proceed.", Toast.LENGTH_SHORT).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }
}
