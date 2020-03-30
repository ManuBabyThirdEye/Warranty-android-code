package com.ewarranty.warranty;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.ewarranty.warranty.models.Role;
import com.ewarranty.warranty.models.User;
import com.ewarranty.warranty.models.Theme;
import com.ewarranty.warranty.webservies.ServiceGenerator;
import com.ewarranty.warranty.webservies.UserServices;
import com.ewarranty.warranty.util.SettingsAction;
import com.ewarranty.warranty.util.Store;
import com.google.android.gms.common.util.CollectionUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OtpVerificationActivity extends AppCompatActivity {

    private EditText otp;
    private TextView updatePhoneNumber,timer,resentOtp;
    private ImageButton submit,back;

    private boolean isActiveSubmit = false, isActiveResent = false;
    private String phoneNumberTxt="";
    private Role role;

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private boolean phoneVerificationInProgress = false;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Theme currentTheme = SettingsAction.getInstance(OtpVerificationActivity.this).getCurrentTheme();
        AppCompatDelegate.setDefaultNightMode(
                currentTheme == Theme.LITE?AppCompatDelegate.MODE_NIGHT_NO:currentTheme == Theme.DARK?AppCompatDelegate.MODE_NIGHT_YES:AppCompatDelegate.MODE_NIGHT_AUTO);

        setContentView(R.layout.activity_otp_verification);

        if (getIntent()!=null) {
            phoneNumberTxt = getIntent().getStringExtra("phoneNumber");
            String r = getIntent().getStringExtra("role");
            role = r == null ? Role.CUSTOMER : Role.valueOf(r);
        }

        updatePhoneNumber = (TextView) findViewById(R.id.updatePhoneNumber);
        timer = (TextView) findViewById(R.id.timer);
        resentOtp = (TextView) findViewById(R.id.resentOtp);

        submit = (ImageButton) findViewById(R.id.submit);
        back = (ImageButton) findViewById(R.id.back);

        otp = (EditText) findViewById(R.id.otp);

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks(){

            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                submit.setBackgroundResource(R.drawable.active_button);
                isActiveSubmit = true;
                otp.setText(phoneAuthCredential.getSmsCode());
                if (!phoneVerificationInProgress){
                    signInWithPhoneAuthCredential(phoneAuthCredential);
                }
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Log.d("ERROR", "onVerificationFailed: "+e);
            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                mVerificationId = verificationId;
                mResendToken = token;

            }
        };

        startPhoneNumberVerification("+91"+phoneNumberTxt);


    }

    @Override
    protected void onStart() {
        super.onStart();



        resentOtp.setOnClickListener(view -> {
            startPhoneNumberVerification("+91"+phoneNumberTxt);
        });



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

        submit.setOnClickListener(view -> {
            if(isActiveSubmit){

                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, otp.getText().toString());
                signInWithPhoneAuthCredential(credential);
            }
        });

        back.setOnClickListener(view -> {
            Intent login = new Intent(OtpVerificationActivity.this, LoginActivity.class);
            login.putExtra("phoneNumber", phoneNumberTxt.toString());
            login.putExtra("role", role.toString());
            OtpVerificationActivity.this.startActivity(login);
            OtpVerificationActivity.this.finish();
        });

    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential phoneAuthCredential) {
        phoneVerificationInProgress = true;
        FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    FirebaseUser user = task.getResult().getUser();
                    SharedPreferences sharedPreferences =
                            PreferenceManager.getDefaultSharedPreferences(OtpVerificationActivity.this);
                    String deviceId = sharedPreferences.getString("deviceId",null);
                    if(Store.PROFILE==null){
                        Store.PROFILE = new User(user.getUid(),deviceId,user.getDisplayName(),user.getEmail(),user.getPhoneNumber(),user.getPhotoUrl()!=null?user.getPhotoUrl().toString():"",role,null);
                    }else{
                        Store.PROFILE.setPhoneNumber(user.getPhoneNumber());
                    }
                    Store.SERVICE = ServiceGenerator.createService(UserServices.class);
                    Log.d("loginAsUser", "onComplete: "+Store.PROFILE);
                    Store.SERVICE.loginAsUser(Store.PROFILE).enqueue(new Callback<User>() {
                        @Override
                        public void onResponse(Call<User> call, Response<User> response) {
                            Store.PROFILE = response.body();
                            SharedPreferences.Editor sharedPreferencesEditor =
                                    PreferenceManager.getDefaultSharedPreferences(OtpVerificationActivity.this).edit();
                            sharedPreferencesEditor.putString("role",Store.PROFILE.getRole().toString());
                            sharedPreferencesEditor.apply();
                            if(Store.PROFILE.getRole()==Role.SHOP && CollectionUtils.isEmpty(Store.PROFILE.getShopVOList())){
                                Intent noShopActivity = new Intent(OtpVerificationActivity.this, NoShopActivity.class);
                                OtpVerificationActivity.this.startActivity(noShopActivity);
                                OtpVerificationActivity.this.finish();
                            }else {
                                if(Store.PROFILE.getRole() == Role.SHOP){
                                    Store.SELECTED_SHOP = Store.PROFILE.getShopVOList().get(0);
                                }
                                Intent home = new Intent(OtpVerificationActivity.this, HomeActivity.class);
                                OtpVerificationActivity.this.startActivity(home);
                                OtpVerificationActivity.this.finish();
                            }

                        }

                        @Override
                        public void onFailure(Call<User> call, Throwable t) {

                        }
                    });

                }else{
                    phoneVerificationInProgress = false;
                }
            }
        });
    }

    private void startPhoneNumberVerification(String phoneNumber) {
        timer.setVisibility(View.VISIBLE);
        new CountDownTimer(30000, 1000){
            public void onTick(long millisUntilFinished){
                timer.setText(String.valueOf(millisUntilFinished/1000)+"s");
            }
            public  void onFinish(){
                resentOtp.setTextColor(getResources().getColor(R.color.base,null));
                timer.setVisibility(View.GONE);
            }
        }.start();

        resentOtp.setTextColor(getResources().getColor(R.color.baseLite,null));

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                OtpVerificationActivity.this,               // Activity (for callback binding)
                mCallbacks);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent login = new Intent(OtpVerificationActivity.this, LoginActivity.class);
        login.putExtra("phoneNumber", phoneNumberTxt.toString());
        login.putExtra("role", role);
        OtpVerificationActivity.this.startActivity(login);
        OtpVerificationActivity.this.finish();
    }
}
