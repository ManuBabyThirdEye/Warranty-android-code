package com.ewarranty.warranty.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.PixelFormat;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.ewarranty.warranty.R;
import com.ewarranty.warranty.models.Language;
import com.ewarranty.warranty.models.Theme;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import androidx.appcompat.app.AppCompatDelegate;
import lombok.NonNull;
import okhttp3.RequestBody;

public class SettingsAction {

    private static SettingsAction INSTANCE = null;
    private static final String THEME = "THEME";
    private static final String LANGUAGE = "LANGUAGE";

    private static final Map<Language,String> LANGUAGE_TO_LOCALE ;

    static {
        LANGUAGE_TO_LOCALE = new HashMap<>();
        LANGUAGE_TO_LOCALE.put(Language.ENGLISH,"en");
        LANGUAGE_TO_LOCALE.put(Language.MALAYALAM,"ml");
    }


    private Context context;

    private SettingsAction(){}

    private SettingsAction(Context context){
        this.context = context;
    }

    public static SettingsAction getInstance(Context context){
        if(INSTANCE==null){
            INSTANCE = new SettingsAction(context);
        }
        return INSTANCE;
    }

    public Theme getCurrentTheme(){
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(context);
        return Theme.valueOf(sharedPreferences.getString(THEME,Theme.LITE.toString()));
    }

    public void updateCurrentTheme(Theme theme){
        SharedPreferences.Editor sharedPreferencesEditor =
                PreferenceManager.getDefaultSharedPreferences(context).edit();
        sharedPreferencesEditor.putString(THEME, theme.toString());
        sharedPreferencesEditor.apply();
    }

    public Language getCurrentLanguage(){
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(context);
        return Language.valueOf(sharedPreferences.getString(LANGUAGE, Language.ENGLISH.toString()));
    }

    public void updateCurrentLanguage(Language language,String locale){
        SharedPreferences.Editor sharedPreferencesEditor =
                PreferenceManager.getDefaultSharedPreferences(context).edit();
        sharedPreferencesEditor.putString(LANGUAGE, language.toString());
        sharedPreferencesEditor.apply();
        setLanguage(locale);
    }

    public int getStyleRespectToTheme(boolean isActionBar) {
        if(getCurrentTheme()==Theme.DARK){
            return AppCompatDelegate.MODE_NIGHT_YES;
        }else if(getCurrentTheme()==Theme.LITE){
            return AppCompatDelegate.MODE_NIGHT_NO;
        }else if(getCurrentTheme()==Theme.AUTO){
            return AppCompatDelegate.MODE_NIGHT_AUTO;
        }
        return AppCompatDelegate.MODE_NIGHT_AUTO;
    }

    public void setLanguage(String localeName){
        Locale myLocale = new Locale(localeName);
        Resources res = context.getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
    }

    public void setLanguage() {
        Language language = getCurrentLanguage();
        setLanguage(LANGUAGE_TO_LOCALE.get(language));
    }

    public static boolean canDrawOverlays(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return true;
        else if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            return Settings.canDrawOverlays(context);
        } else {
            if (Settings.canDrawOverlays(context)) return true;
            try {
                WindowManager mgr = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
                if (mgr == null) return false; //getSystemService might return null
                View viewToAdd = new View(context);
                WindowManager.LayoutParams params = new WindowManager.LayoutParams(0, 0, android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O ?
                        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY : WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSPARENT);
                viewToAdd.setLayoutParams(params);
                mgr.addView(viewToAdd, params);
                mgr.removeView(viewToAdd);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }
    }

    @NonNull
    public static RequestBody createPartFromString(String descriptionString) {
        return RequestBody.create(
                okhttp3.MultipartBody.FORM, descriptionString);
    }

    public static void hideSoftKeyboard(Activity activity) {
        activity.getWindow().setSoftInputMode(2);
        if(activity.getCurrentFocus()!=null) {
            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        }else{
            activity.getWindow().setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        }
    }

    public static void showSoftKeyboard(View view, Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(activity.INPUT_METHOD_SERVICE);
        view.requestFocus();
        inputMethodManager.showSoftInput(view, 0);
    }
}

