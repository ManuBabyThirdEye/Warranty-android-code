package com.ewarranty.warranty;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class LoadingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        Intent login = new Intent(LoadingActivity.this, LoginActivity.class);
        LoadingActivity.this.startActivity(login);

        LoadingActivity.this.finish();
    }
}
