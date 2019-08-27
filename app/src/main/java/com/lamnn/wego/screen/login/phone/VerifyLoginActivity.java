package com.lamnn.wego.screen.login.phone;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.lamnn.wego.R;

public class VerifyLoginActivity extends AppCompatActivity {


    public static Intent getIntent(Context context) {
        Intent intent = new Intent(context, VerifyLoginActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_login);
    }
}
