package com.doctarhyf.quotes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

public class MainActivity extends AppCompatActivity {

    private static final String QUOTE_KEY = "quote";
    private static final String AUTHOR_KEY = "author";
    private static final String TAG = "firestore";
    Button btnSaveQuote, btnFetchQuote;

    TextView tvQuote;

    private DocumentReference mDocRef =
            FirebaseFirestore.getInstance().document("sampleData/inspiration");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        btnFetchQuote = findViewById(R.id.btnFetchQuote);
        btnSaveQuote = findViewById(R.id.btnSaveQuote);
        tvQuote = findViewById(R.id.tvQuote);

    }

    @Override
    protected void onStart() {
        super.onStart();
        mDocRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
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
        
        mDocRef.set(dataToSave).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.e(TAG, "Document has been saved " );
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "Document was not saved " );
            }
        });

    }

    public void fetchQuote(View view) {

        mDocRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    String quoteText = documentSnapshot.getString(QUOTE_KEY);
                    String authorText = documentSnapshot.getString(AUTHOR_KEY);
                    tvQuote.setText("\"" + quoteText + "\" -- " + authorText);
                }
            }
        });
    }
}
