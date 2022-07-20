package com.google.firebase.remoteconfig;

import com.google.android.gms.tasks.Task;

import org.firebase.debug.FalseTask;

public class FirebaseRemoteConfig {
    public static FirebaseRemoteConfig getInstance() {
        return new FirebaseRemoteConfig();
    }

    public Task<Boolean> setConfigSettingsAsync(FirebaseRemoteConfigSettings settings) {
        return new FalseTask();
    }

    public Task<Boolean> setDefaultsAsync(int resourceId) {
        return new FalseTask();
    }

    public Task<Boolean> fetchAndActivate() {
        return new FalseTask();
    }

    public boolean getBoolean(String key) {
        return true;
    }


    public String getString(String key) {
        return "way1";
    }


}
