package com.ewarranty.warranty.ui.settings;

import com.ewarranty.warranty.models.Language;
import com.ewarranty.warranty.models.Settings;
import com.ewarranty.warranty.models.Theme;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SettingsViewModel extends ViewModel {

    private MutableLiveData<Settings> settings;

    public SettingsViewModel() {
        settings = new MutableLiveData<>();
        settings.setValue(new Settings(Language.ENGLISH, Theme.LITE));
    }

    public LiveData<Settings> getText() {
        return settings;
    }
}