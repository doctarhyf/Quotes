package com.doctarhyf.quotes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

public class MainActivity extends AppCompatActivity {

    private static final String QUOTE_KEY = "quote";
    private static final String AUTHOR_KEY = "author";
    private static final String TAG = "firestore";
    Button btnSaveQuote, btnFetchQuote;

    TextView tvQuote;

    //private DocumentReference mDocRef = FirebaseFirestore.getInstance().document("sampleData/inspiration");
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    FirebaseUser firebaseUser;// = FirebaseAuth.getInstance().getCurrentUser();
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mAuth = FirebaseAuth.getInstance();

        firebaseUser = mAuth.getCurrentUser();

        if(firebaseUser != null){

            TextView tvUsername = findViewById(R.id.tvUsername);
            TextView tvEmail = findViewById(R.id.tvEmail);


            tvUsername.setText(firebaseUser.getDisplayName() + "\n");
            tvEmail.setText(firebaseUser.getEmail() + "\n");

            ImageView iv = (ImageView) findViewById(R.id.iv);

            Glide.with(this)
                    .load(firebaseUser.getPhotoUrl())
                    .apply(RequestOptions.circleCropTransform())
                    .into(iv);

        }else{
            startActivity(new Intent(this, ActivityLogin.class));
        }



        /*
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        if (acct != null) {
            String personName = acct.getDisplayName();
            String personGivenName = acct.getGivenName();
            String personFamilyName = acct.getFamilyName();
            String personEmail = acct.getEmail();
            String personId = acct.getId();
            Uri personPhoto = acct.getPhotoUrl();


            Log.e(TAG, "updateUI: url : " + personPhoto.toString() );

            ImageView iv = findViewById(R.id.iv);

            Glide.with(this)
                    .load(personPhoto)
                    .into(iv);

        }else{
            startActivity(new Intent(this, ActivityLogin.class));
        }*/
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


        btnFetchQuote = findViewById(R.id.btnFetchQuote);
        btnSaveQuote = findViewById(R.id.btnSaveQuote);
        tvQuote = findViewById(R.id.tvQuote);

        db.collection("quotes").document("quote").addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if(documentSnapshot.exists()){
                    Map<String, Object> data = documentSnapshot.getData();

                    tvQuote.setText(data.get(QUOTE_KEY) + " -- " + data.get(AUTHOR_KEY));

                    Log.e(TAG, "onSuccess: quote fetching successful" );
                }
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        /*mDocRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if(documentSnapshot.exists()){
                    String quoteText = documentSnapshot.getString(QUOTE_KEY);
                    String authorText = documentSnapshot.getString(AUTHOR_KEY);
                    tvQuote.setText("\"" + quoteText + "\" -- " + authorText);
                }else if(e != null){
                    Log.w(TAG, "onEvent: exception", e);
                }
            }
        });*/

        /*db.collection("quotes").document()

                .addOnSuccessListener(this, new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(MainActivity.this, "Document added!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, "Failed adding doc!", Toast.LENGTH_SHORT).show();
                    }
                });*/

        db.collection("quotes").document("quote").addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if(documentSnapshot.exists()){
                    Map<String, Object> data = documentSnapshot.getData();

                    tvQuote.setText(data.get(QUOTE_KEY) + " -- " + data.get(AUTHOR_KEY));

                    Log.e(TAG, "onSuccess: quote fetching successful" );
                }
            }
        });
    }

    public void saveQuote(View view) {
        EditText quoteView = findViewById(R.id.etQuote);
        EditText authorView = findViewById(R.id.etAuthor);
        String quoteText = quoteView.getText().toString();
        String authorText = authorView.getText().toString();


        if(quoteText.isEmpty() || authorText.isEmpty()) { return;}

        Map<String, Object> dataToSave = new HashMap<>();
        dataToSave.put(QUOTE_KEY, quoteText);
        dataToSave.put(AUTHOR_KEY, authorText);


        db.collection("quotes").document("quote")
                .set(dataToSave)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.e(TAG, "onSuccess: doc added" );
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "onFailure: doc adding failed" );
                    }
                });
        /*
        db.collection("quotes")
                .add(dataToSave)
                .addOnSuccessListener(this, new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(MainActivity.this, "Document added!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, "Failed adding doc!", Toast.LENGTH_SHORT).show();
                    }
                });*/

    }

    public void fetchQuote(View view) {



        db.collection("quotes").document("quote")
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Map<String, Object> data = documentSnapshot.getData();

                        tvQuote.setText(data.get(QUOTE_KEY) + " -- " + data.get(AUTHOR_KEY));

                        Log.e(TAG, "onSuccess: quote fetching successful" );
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "onFailure: quote fetching failed " );
                    }
                });

    }

    public void logout(View view) {
        // Firebase sign out
        mAuth.signOut();


        // Google sign out
        mGoogleSignInClient.signOut().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        startActivity(new Intent(MainActivity.this, ActivityLogin.class));
                    }
                });
    }
}
