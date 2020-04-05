package com.doctarhyf.quotes;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {


    EditText etQuote, etAuthor;
    Button btnSaveQuote, btnReadQuote;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        etQuote = findViewById(R.id.etQuote);
        etAuthor = findViewById(R.id.etAuthor);

        btnReadQuote = findViewById(R.id.btnReadQuote);
        btnSaveQuote = findViewById(R.id.btnSaveQuote);

        btnSaveQuote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btnReadQuote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
            }
        });
    }
}
