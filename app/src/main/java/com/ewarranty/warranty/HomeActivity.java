package com.ewarranty.warranty;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.ewarranty.warranty.models.Role;
import com.ewarranty.warranty.models.Theme;
import com.ewarranty.warranty.util.SettingsAction;
import com.ewarranty.warranty.util.Store;
import com.google.android.gms.common.util.CollectionUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

public class HomeActivity extends AppCompatActivity {

    private int pageCount=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Theme currentTheme = SettingsAction.getInstance(HomeActivity.this).getCurrentTheme();
        AppCompatDelegate.setDefaultNightMode(
                currentTheme == Theme.LITE?AppCompatDelegate.MODE_NIGHT_NO:currentTheme == Theme.DARK?AppCompatDelegate.MODE_NIGHT_YES:AppCompatDelegate.MODE_NIGHT_AUTO);

        super.onCreate(savedInstanceState);

        if (getIntent()!=null) {
            pageCount = getIntent().getIntExtra("page",0);
        }



        setContentView(R.layout.activity_home);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupWithNavController(navView, navController);
        if(pageCount == 0){
            navView.setSelectedItemId(R.id.navigation_home);
        }else if(pageCount == 1){
            navView.setSelectedItemId(R.id.navigation_category);
        }else if(pageCount == 2){
            navView.setSelectedItemId(R.id.navigation_settings);
        }else{
            navView.setSelectedItemId(R.id.navigation_home);
        }

        if(Store.PROFILE != null && Store.PROFILE.getRole() == Role.SHOP && CollectionUtils.isEmpty(Store.SELECTED_SHOP.getCategoryList())){
            Intent home = new Intent(HomeActivity.this, CategorySelectionActivity.class);
            HomeActivity.this.startActivity(home);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

}
