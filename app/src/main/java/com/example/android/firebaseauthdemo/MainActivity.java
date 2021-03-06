package com.example.android.firebaseauthdemo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private SignInButton signInButton;
    private Button signOutButton;
    private Button btnNext;
    private GoogleSignInClient mGoogleSignInClient;
    private  String TAG = "MainActivity";
    private FirebaseAuth mAuth;
    private int RC_SIGN_IN = 1;
    GoogleSignInAccount account;
    ArrayList<GoogleSignInAccount> activeAccounts = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        signInButton = findViewById(R.id.sign_in_button);
        signOutButton = findViewById(R.id.sign_out_button);
        btnNext = findViewById(R.id.btnNext);
        mAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });

        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mGoogleSignInClient.signOut();
                Toast.makeText(MainActivity.this,"You are Logged Out",Toast.LENGTH_SHORT).show();
                signInButton.setVisibility(View.VISIBLE);
                signOutButton.setVisibility(View.INVISIBLE);
                btnNext.setVisibility(View.INVISIBLE);
            }
        });

        signOutButton.setVisibility(View.INVISIBLE);
        btnNext.setVisibility(View.INVISIBLE);
    }

    public void next(View v){
            Intent myIntent = new Intent(this, SecondActivity.class);
            myIntent.putExtra("accountList", activeAccounts);
            startActivity(myIntent);

            // TODO: Pass only the current GoogleSignInAccount instead of an array with the intent
    }

    private void signIn(){
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_SIGN_IN){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask){
        try{

            GoogleSignInAccount acc = completedTask.getResult(ApiException.class);
            Toast.makeText(MainActivity.this,"Signed In Successfully",Toast.LENGTH_SHORT).show();
            FirebaseGoogleAuth(acc);
        }
        catch (ApiException e){
            Toast.makeText(MainActivity.this,"Sign In Failed",Toast.LENGTH_SHORT).show();
            FirebaseGoogleAuth(null);
        }
    }

    private void FirebaseGoogleAuth(GoogleSignInAccount acc) {
        //check if the account is null
        if (acc != null) {
            AuthCredential authCredential = GoogleAuthProvider.getCredential(acc.getIdToken(), null);
            mAuth.signInWithCredential(authCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        updateUI(user);
                    } else {
                        updateUI(null);
                    }
                }
            });
        }
    }

    private void updateUI(FirebaseUser fUser){
        account = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
        if(account !=  null){
            activeAccounts.add(account);
            String personName = account.getDisplayName();
            String personEmail = account.getEmail();
            signInButton.setVisibility(View.INVISIBLE);
            btnNext.setVisibility(View.VISIBLE);
            signOutButton.setVisibility(View.VISIBLE);
            Toast.makeText(MainActivity.this,personName + " " + personEmail ,Toast.LENGTH_SHORT).show();
        }
    }
}
