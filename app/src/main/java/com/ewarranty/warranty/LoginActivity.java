package com.ewarranty.warranty;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    private EditText phoneNumber;
    private RelativeLayout googleButton,facebookButton;
    private Switch accountSwitch;
    private TextView privacyPolicy,termsOfService,personalText,shopText;
    private ImageButton loginButton;

    private boolean isPersonal = false,isValidPhoneNumber = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        phoneNumber = (EditText) findViewById(R.id.phoneNumber);

        googleButton = (RelativeLayout) findViewById(R.id.googleButton);
        facebookButton = (RelativeLayout) findViewById(R.id.facebookButton);

        accountSwitch = (Switch) findViewById(R.id.accountSwitch);

        privacyPolicy = (TextView) findViewById(R.id.privacyPolicy);
        termsOfService = (TextView) findViewById(R.id.termsOfService);
        personalText = (TextView) findViewById(R.id.personalText);
        shopText = (TextView) findViewById(R.id.shopText);

        loginButton = (ImageButton) findViewById(R.id.loginButton);

    }

    @Override
    protected void onStart() {
        super.onStart();
        phoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(editable != null && editable.toString().length()==10){
                    loginButton.setBackgroundResource(R.drawable.active_button);
                    isValidPhoneNumber = true;
                }else{
                    loginButton.setBackgroundResource(R.drawable.disabled_button);
                    isValidPhoneNumber = false;
                }
            }
        });

        accountSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                isPersonal = !b;
                if(isPersonal){
                    personalText.setTextColor(getResources().getColor(R.color.white));
                    shopText.setTextColor(getResources().getColor(R.color.baseLite));
                }else{
                    personalText.setTextColor(getResources().getColor(R.color.baseLite));
                    shopText.setTextColor(getResources().getColor(R.color.white));
                }
            }
        });

        googleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO:
            }
        });

        facebookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO:
            }
        });

        privacyPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO:
            }
        });

        termsOfService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO:
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isValidPhoneNumber && phoneNumber.getText()!=null){
                    Intent otpVerification = new Intent(LoginActivity.this, OtpVerificationActivity.class);
                    otpVerification.putExtra("phoneNumber", phoneNumber.getText().toString());
                    otpVerification.putExtra("role", isPersonal?"personal":"shop");
                    LoginActivity.this.startActivity(otpVerification);
                    LoginActivity.this.finish();
                }
            }
        });
    }
}
