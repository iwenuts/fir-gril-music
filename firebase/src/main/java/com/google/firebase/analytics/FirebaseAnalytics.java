package com.google.firebase.analytics;

import android.content.Context;
import android.os.Bundle;

public class FirebaseAnalytics {

    public static FirebaseAnalytics getInstance(Context context) {
        return new FirebaseAnalytics();
    }

    public void logEvent(String name, Bundle args) {
    }

    public static class Event {
        public static final String ECOMMERCE_PURCHASE = "";
        public static final String AD_IMPRESSION = "AD_IMPRESSION";
    }

    public class Param {
        public static final String AD_UNIT_NAME = "";
        public static final String AD_PLATFORM = "";
        public static final String AD_SOURCE = "";
        public static final String AD_FORMAT = "";
        public static final String CURRENCY = "";
        public static final String VALUE = "";
    }
}
