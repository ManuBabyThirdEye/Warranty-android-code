package com.ewarranty.warranty;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

public class OtpVerificationActivity extends AppCompatActivity {

    private EditText otp;
    private TextView updatePhoneNumber,timer,resentOtp;
    private ImageButton submit;

    private boolean isActiveSubmit = false, isActiveResent = false;
    private String phoneNumberTxt="",role="personal";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_verification);

        if (getIntent()!=null) {
            phoneNumberTxt = getIntent().getStringExtra("phoneNumber");
            role = getIntent().getStringExtra("role");
        }

        updatePhoneNumber = (TextView) findViewById(R.id.updatePhoneNumber);
        timer = (TextView) findViewById(R.id.timer);
        resentOtp = (TextView) findViewById(R.id.resentOtp);

        submit = (ImageButton) findViewById(R.id.submit);

        otp = (EditText) findViewById(R.id.otp);

    }

    @Override
    protected void onStart() {
        super.onStart();

        updatePhoneNumber.setText(getResources().getText(R.string.otp_sub_header)+" "+phoneNumberTxt);

        otp.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(editable != null && editable.toString().length()==6){
                    submit.setBackgroundResource(R.drawable.active_button);
                    isActiveSubmit = true;
                }else{
                    submit.setBackgroundResource(R.drawable.disabled_button);
                    isActiveSubmit = false;
                }
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isActiveSubmit){
                    //TODO
                }
            }
        });

        resentOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isActiveResent){
                    //TODO
                }
            }
        });

    }
}
