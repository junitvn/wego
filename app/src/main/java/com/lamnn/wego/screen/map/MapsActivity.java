package com.lamnn.wego.screen.map;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.lamnn.wego.R;
import com.lamnn.wego.screen.login.LoginActivity;
import com.lamnn.wego.screen.login.phone.SelectAreaActivity;

public class MapsActivity extends AppCompatActivity implements View.OnClickListener {

    public static Intent getIntent(Context context) {
        Intent intent = new Intent(context, MapsActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        Button btnSignOut = findViewById(R.id.btn_sign_out);
        btnSignOut.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_sign_out:
                FirebaseAuth.getInstance().signOut();
                startActivity(LoginActivity.getIntent(this));
                break;
        }
    }
}
