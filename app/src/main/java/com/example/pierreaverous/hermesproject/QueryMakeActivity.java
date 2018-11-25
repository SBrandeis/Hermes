package com.example.pierreaverous.hermesproject;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.List;

import firebase.DB_File;
import firebase.DB_Part;
import firebase.DB_User;
import firebase.query.QueryMaker;
import firebase.query.QueryManager;
import firebase.storageClasses.StoreQueryManager;

public class QueryMakeActivity extends AppCompatActivity {

    Spinner dropdownMenu;
    TextView child_field;
    EditText search_field;
    Button searchButton;
    String ref;
    QueryManager queryManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query_make);
        ref = "DB_File";

        queryManager = new QueryManager(this);

        dropdownMenu = findViewById(R.id.dropdown);
        child_field = findViewById(R.id.child_field);
        search_field = findViewById(R.id.search_field);
        searchButton = findViewById(R.id.search_button);

        String[] dropDown_ref = {"File"};
        String[] dropDown_file = {"File Name", "Author"};
        String[] dropDown_user = {"User Name", "eMail"};

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(QueryMakeActivity.this,
                android.R.layout.simple_spinner_item, dropDown_ref);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dropdownMenu.setAdapter(adapter);
        dropdownMenu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        ref = "DB_File";
                        break;
                    case 1:
                        ref = "DB_User";
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // ===================== SEARCH FUNCTIONNALITY ======================
        //
        // When performing a search, the user has to click the search button
        // to get the results of his query. The results are fetched through
        // QueryManager object, which is stored between activities in the
        // StoreQueryManager class. The new activity then displays all
        // results corresponding to the user's query in a listview.
        //
        // ==================================================================
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String child = child_field.getText().toString();
                String search = search_field.getText().toString();

                queryManager.makeQuery(ref, child, search);
                StoreQueryManager.setQueryManager(queryManager);
            }
        });
    }

    /**
     * Method called when the query to look all files up is finished.
     * It starts the next activity, that displays the results of our Query.
     */
    public void next() {
        Intent queryResultIntent = new Intent(QueryMakeActivity.this, QueryResultActivity.class);
        startActivity(queryResultIntent);
    }
}

