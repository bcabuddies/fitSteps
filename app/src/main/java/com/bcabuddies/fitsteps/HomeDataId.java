package com.bcabuddies.fitsteps;

import android.support.annotation.NonNull;

import com.google.firebase.firestore.Exclude;

public class HomeDataId {

    @Exclude
    public String HomeDataId;

    public <T extends HomeDataId> T withID(@NonNull final String id) {
        this.HomeDataId = id;
        return (T) this;
    }
}
