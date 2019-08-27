package com.lamnn.wego.screen.login.phone;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.lamnn.wego.R;
import com.lamnn.wego.data.model.CountryCode;

import java.util.ArrayList;
import java.util.Collections;

public class PhoneLoginActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText mTextPhoneNumber;
    private Button mButtonLoginWithPhoneNumber;
    private TextView mTextCode;
    private ImageView mImageDropDown;
    private String mCodeString;

    private static final int REQUEST_CODE = 0x9345;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_login);
        initView();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login_now:
                break;
            case R.id.image_dropdown:
                startActivityForResult(SelectAreaActivity.getIntent(this), REQUEST_CODE);
                break;
            case R.id.text_phone_code:
                startActivityForResult(SelectAreaActivity.getIntent(this), REQUEST_CODE);
                break;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                mCodeString = data.getStringExtra(SelectAreaActivity.EXTRA_DATA);
//                Toast.makeText(this, "Re44444444444444444444444444sult: " + result, Toast.LENGTH_LONG).show();
            } else {
                // DetailActivity không thành công, không có data trả về.
            }
        }
    }

    public static Intent getIntent(Context context) {
        Intent intent = new Intent(context, PhoneLoginActivity.class);
        return intent;
    }

    private void initView() {
        mTextPhoneNumber = findViewById(R.id.text_phone_number);
        mButtonLoginWithPhoneNumber = findViewById(R.id.btn_login_now);
        mButtonLoginWithPhoneNumber.setOnClickListener(this);
        mImageDropDown = findViewById(R.id.image_dropdown);
        mImageDropDown.setOnClickListener(this);
        mTextCode = findViewById(R.id.text_phone_code);
        mTextCode.setOnClickListener(this);
    }
}
