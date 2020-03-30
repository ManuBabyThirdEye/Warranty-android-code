package com.ewarranty.warranty;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.ewarranty.warranty.models.Role;
import com.ewarranty.warranty.models.User;
import com.ewarranty.warranty.models.Theme;
import com.ewarranty.warranty.webservies.ServiceGenerator;
import com.ewarranty.warranty.webservies.UserServices;
import com.ewarranty.warranty.util.SettingsAction;
import com.ewarranty.warranty.util.Store;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.util.CollectionUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText phoneNumber;
    private RelativeLayout googleButton,facebookButton,accountSelectionLayout;
    private Switch accountSwitch;
    private TextView privacyPolicy,termsOfService,personalText,shopText,mainHeading,subHeading,orConnectWith;
    private ImageButton loginButton;

    private boolean isPersonal = true,isValidPhoneNumber = false;

    private GoogleSignInClient mGoogleSignInClient;

    private static final int RC_SIGN_IN = 1;
    private FirebaseAuth mAuth;

    private String phoneNumberTxt="";
    private Role role;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Theme currentTheme = SettingsAction.getInstance(LoginActivity.this).getCurrentTheme();
        AppCompatDelegate.setDefaultNightMode(
                currentTheme == Theme.LITE?AppCompatDelegate.MODE_NIGHT_NO:currentTheme == Theme.DARK?AppCompatDelegate.MODE_NIGHT_YES:AppCompatDelegate.MODE_NIGHT_AUTO);

        setContentView(R.layout.activity_login);

        if (getIntent()!=null) {
            phoneNumberTxt = getIntent().getStringExtra("phoneNumber");
            String r = getIntent().getStringExtra("role");
            role = r == null ? Role.CUSTOMER : Role.valueOf(r);
        }

        phoneNumber = (EditText) findViewById(R.id.phoneNumber);

        googleButton = (RelativeLayout) findViewById(R.id.googleButton);
        facebookButton = (RelativeLayout) findViewById(R.id.facebookButton);
        accountSelectionLayout = (RelativeLayout) findViewById(R.id.accountSelectionLayout);

        accountSwitch = (Switch) findViewById(R.id.accountSwitch);

        privacyPolicy = (TextView) findViewById(R.id.privacyPolicy);
        termsOfService = (TextView) findViewById(R.id.termsOfService);
        personalText = (TextView) findViewById(R.id.personalText);
        shopText = (TextView) findViewById(R.id.shopText);
        mainHeading = (TextView) findViewById(R.id.mainHeading);
        subHeading = (TextView) findViewById(R.id.subHeading);
        orConnectWith = (TextView) findViewById(R.id.orConnectWith);

        loginButton = (ImageButton) findViewById(R.id.loginButton);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mAuth = FirebaseAuth.getInstance();

        updateUiToGetPhoneNumber(false);

    }

    @Override
    protected void onStart() {
        super.onStart();

        if(phoneNumberTxt!=null && !phoneNumberTxt.equals("")){
            phoneNumber.setText(phoneNumberTxt);
            if(phoneNumberTxt != null && phoneNumberTxt.toString().length()==10){
                loginButton.setBackgroundResource(R.drawable.active_button);
                isValidPhoneNumber = true;
            }else{
                loginButton.setBackgroundResource(R.drawable.disabled_button);
                isValidPhoneNumber = false;
            }
        }

        if(role!=null && !role.equals("")){
            if(role == Role.CUSTOMER){
                accountSwitch.setChecked(false);
            }else{
                accountSwitch.setChecked(true);
            }
        }

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
            public void onCheckedChanged(CompoundButton compoundButton, boolean shop) {

                if(shop){
                    role = Role.SHOP;
                    personalText.setTextColor(getResources().getColor(R.color.baseLite,null));
                    shopText.setTextColor(getResources().getColor(R.color.white,null));
                }else{
                    role = Role.CUSTOMER;
                    personalText.setTextColor(getResources().getColor(R.color.white,null));
                    shopText.setTextColor(getResources().getColor(R.color.baseLite,null));
                }
            }
        });

        googleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
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
                    otpVerification.putExtra("role", role.toString());
                    LoginActivity.this.startActivity(otpVerification);
                    LoginActivity.this.finish();
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);

            } catch (ApiException e) {
                e.printStackTrace();
            }

        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            SharedPreferences sharedPreferences =
                                    PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
                            String deviceId = sharedPreferences.getString("deviceId",null);
                            Store.PROFILE = new User(user.getUid(),deviceId,user.getDisplayName(),user.getEmail(),user.getPhoneNumber(),user.getPhotoUrl()!=null?user.getPhotoUrl().toString():"",accountSwitch.isChecked()? Role.SHOP:Role.CUSTOMER,null);
                            Store.SERVICE = ServiceGenerator.createService(UserServices.class);

                            Store.SERVICE.getPhoneNumberByEmail(user.getEmail()).enqueue(new Callback<String>() {
                                @Override
                                public void onResponse(Call<String> call, Response<String> response) {
                                    if(response.body() == null || response.body() == null || response.body().isEmpty()){
                                        updateUiToGetPhoneNumber(true);
                                        mAuth.signOut();
                                    }else{
                                        Store.PROFILE.setPhoneNumber(response.body());
                                        Store.SERVICE.loginAsUser(Store.PROFILE).enqueue(new Callback<User>() {
                                            @Override
                                            public void onResponse(Call<User> call, Response<User> response) {
                                                Store.PROFILE = response.body();
                                                SharedPreferences.Editor sharedPreferencesEditor =
                                                        PreferenceManager.getDefaultSharedPreferences(LoginActivity.this).edit();
                                                sharedPreferencesEditor.putString("role",Store.PROFILE.getRole().toString());
                                                sharedPreferencesEditor.apply();
                                                if(Store.PROFILE.getRole()==Role.SHOP && CollectionUtils.isEmpty(Store.PROFILE.getShopVOList())){
                                                    Intent noShopActivity = new Intent(LoginActivity.this, NoShopActivity.class);
                                                    LoginActivity.this.startActivity(noShopActivity);
                                                    LoginActivity.this.finish();
                                                }else{
                                                    if(Store.PROFILE.getRole() == Role.SHOP){
                                                        Store.SELECTED_SHOP = Store.PROFILE.getShopVOList().get(0);
                                                    }
                                                    Intent home = new Intent(LoginActivity.this, HomeActivity.class);
                                                    LoginActivity.this.startActivity(home);
                                                    LoginActivity.this.finish();
                                                }
                                            }
                                            @Override
                                            public void onFailure(Call<User> call, Throwable t) {
                                                Toast.makeText(LoginActivity.this,t.getMessage(),Toast.LENGTH_LONG).show();
                                                Log.d("Test", "onFailure: "+t.getMessage());
                                            }
                                        });
                                    }
                                }

                                @Override
                                public void onFailure(Call<String> call, Throwable t) {
                                    updateUiToGetPhoneNumber(true);
                                    mAuth.signOut();
                                    Log.d("Test", "onFailure: "+t.getMessage());
                                }
                            });
                        }
                    }
                });
    }

    private void updateUiToGetPhoneNumber(boolean showOnlyPhone) {
        if(showOnlyPhone){
            facebookButton.setVisibility(View.GONE);
            googleButton.setVisibility(View.GONE);
            orConnectWith.setVisibility(View.GONE);
            accountSelectionLayout.setVisibility(View.GONE);
            mainHeading.setText(getResources().getString(R.string.login_header_only_phone));
            subHeading.setText(getResources().getString(R.string.login_sub_header_only_phone));
        }else{
            facebookButton.setVisibility(View.VISIBLE);
            googleButton.setVisibility(View.VISIBLE);
            orConnectWith.setVisibility(View.VISIBLE);
            accountSelectionLayout.setVisibility(View.VISIBLE);
            mainHeading.setText(getResources().getString(R.string.login_header));
            subHeading.setText(getResources().getString(R.string.login_sub_header));
        }

    }
}
