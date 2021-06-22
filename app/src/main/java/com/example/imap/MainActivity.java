package com.example.imap;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );

        imageView = findViewById( R.id.image );

        Thread thread = new Thread() {
            @Override

            public void run() {
                super.run();
                try {
                    Thread.sleep( 2000 );
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {

                    Intent intent = new Intent( MainActivity.this, MapsActivity.class );
                    startActivity( intent );
                }
            }
        };
        thread.start();
    }
}