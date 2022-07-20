package com.google.firebase.remoteconfig;

public class FirebaseRemoteConfigSettings {

    public static class Builder {
        public Builder setFetchTimeoutInSeconds(long duration) throws IllegalArgumentException {
            return this;
        }

        public FirebaseRemoteConfigSettings build() {
            return new FirebaseRemoteConfigSettings();
        }


        public Builder setMinimumFetchIntervalInSeconds(int i) {
            return this;
        }
    }
}
