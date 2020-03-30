package com.ewarranty.warranty;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import com.airbnb.lottie.LottieAnimationView;
import com.ewarranty.warranty.models.Category;
import com.ewarranty.warranty.models.CategoryType;
import com.ewarranty.warranty.models.Role;
import com.ewarranty.warranty.models.ServiceType;
import com.ewarranty.warranty.models.Shop;
import com.ewarranty.warranty.models.User;
import com.ewarranty.warranty.models.Theme;
import com.ewarranty.warranty.webservies.ServiceGenerator;
import com.ewarranty.warranty.webservies.UserServices;
import com.ewarranty.warranty.util.SettingsAction;
import com.ewarranty.warranty.util.Store;
import com.google.android.gms.common.util.CollectionUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Arrays;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoadingActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private LottieAnimationView loader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Theme currentTheme = SettingsAction.getInstance(LoadingActivity.this).getCurrentTheme();
        AppCompatDelegate.setDefaultNightMode(
                currentTheme == Theme.LITE?AppCompatDelegate.MODE_NIGHT_NO:currentTheme == Theme.DARK?AppCompatDelegate.MODE_NIGHT_YES:AppCompatDelegate.MODE_NIGHT_AUTO);

        setContentView(R.layout.activity_loading);

        loader = (LottieAnimationView)findViewById(R.id.loader);

        Store.CATEGORY_LIST = new ArrayList<>(Arrays.asList(
                new Category(0,CategoryType.ALL_CATEGORY,R.string.category_all,R.drawable.all_category,false),
                new Category(1,CategoryType.ELECTRONICS,R.string.category_electronics,R.drawable.electronics,false),
                new Category(2,CategoryType.HOME_FURNITURE,R.string.category_home_furniture,R.drawable.furniture,false),
                new Category(3,CategoryType.TV_APPLIANCES,R.string.category_tv_appliances,R.drawable.appliances,false),
                new Category(4,CategoryType.SPORTS,R.string.category_sports,R.drawable.sports,false),
                new Category(5,CategoryType.KID,R.string.category_kids,R.drawable.kids,false),
                new Category(6,CategoryType.JEWELS,R.string.category_jewels,R.drawable.jewels,false),
                new Category(7,CategoryType.UNKNOWN,R.string.category_unknown,R.drawable.unknown,false),
                new Category(8,CategoryType.CUSTOM,R.string.category_custom_1,R.drawable.add_category,false),
                new Category(9,CategoryType.CUSTOM,R.string.category_custom_2,R.drawable.add_category,false),
                new Category(10,CategoryType.CUSTOM,R.string.category_custom_3,R.drawable.add_category,false)
                ));

        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user = mAuth.getCurrentUser();

        SettingsAction.getInstance(LoadingActivity.this).setLanguage();
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(this);
        String deviceId = sharedPreferences.getString("deviceId",null);

        if(user!=null && user.getUid()!=null){
            Store.PROFILE = new User(user.getUid(),deviceId,user.getDisplayName(),user.getEmail(),user.getPhoneNumber(),user.getPhotoUrl()!=null?user.getPhotoUrl().toString():"", Role.valueOf(sharedPreferences.getString("role","CUSTOMER")),null);
            Store.SERVICE = ServiceGenerator.createService(UserServices.class);
            Store.SERVICE.loginAsUser(Store.PROFILE).enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    Store.PROFILE = response.body();
                    //Store.PROFILE.setShopList(Arrays.asList(new Shop(111,"Shop 1","", ServiceType.NO_SERVICE,Arrays.asList(CategoryType.HOME_FURNITURE,CategoryType.JEWELS)),
                    //        new Shop(112,"Shop 2","", ServiceType.NO_SERVICE,Arrays.asList(CategoryType.ELECTRONICS,CategoryType.SPORTS))));
                    if(Store.PROFILE!=null && Store.PROFILE.getPhoneNumber()!=null && !Store.PROFILE.getPhoneNumber().equals("")){
                        if(Store.PROFILE.getRole()==Role.SHOP && CollectionUtils.isEmpty(Store.PROFILE.getShopVOList())){
                            Intent noShopActivity = new Intent(LoadingActivity.this, NoShopActivity.class);
                            LoadingActivity.this.startActivity(noShopActivity);
                            LoadingActivity.this.finish();
                        }else {
                            if(Store.PROFILE.getRole() == Role.SHOP){
                                Store.SELECTED_SHOP = Store.PROFILE.getShopVOList().get(0);
                            }
                            Intent home = new Intent(LoadingActivity.this, HomeActivity.class);
                            LoadingActivity.this.startActivity(home);
                            LoadingActivity.this.finish();
                        }
                    }else{
                            Intent login = new Intent(LoadingActivity.this, LoginActivity.class);
                            LoadingActivity.this.startActivity(login);
                            LoadingActivity.this.finish();
                    }
                }
                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    Intent login = new Intent(LoadingActivity.this, LoginActivity.class);
                    LoadingActivity.this.startActivity(login);
                    LoadingActivity.this.finish();
                }
            });

        }else{
            Intent login = new Intent(LoadingActivity.this, LoginActivity.class);
            LoadingActivity.this.startActivity(login);
            LoadingActivity.this.finish();
        }


    }
}

