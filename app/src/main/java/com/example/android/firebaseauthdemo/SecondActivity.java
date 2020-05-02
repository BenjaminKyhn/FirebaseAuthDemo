package com.example.android.firebaseauthdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import java.util.ArrayList;

public class SecondActivity extends AppCompatActivity {
    private TextView outputText;
    ArrayList<GoogleSignInAccount> activeAccounts = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        outputText = findViewById(R.id.outputText);

        Intent startIntent = getIntent();
        activeAccounts = (ArrayList<GoogleSignInAccount>) startIntent.getSerializableExtra("accountList");

        for(int i = 0; i < activeAccounts.size(); i++) {
            outputText.setText("Account ID is: " + activeAccounts.get(i).getId() + " and the user's name is " + activeAccounts.get(i).getDisplayName() + "\n");
        }
    }
}
