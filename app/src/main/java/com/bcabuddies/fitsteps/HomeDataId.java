package com.bcabuddies.fitsteps;

import com.google.firebase.firestore.Exclude;

import androidx.annotation.NonNull;

public class HomeDataId {

    @Exclude
    public String HomeDataId;

    public <T extends HomeDataId> T withID(@NonNull final String id) {
        this.HomeDataId = id;
        return (T) this;
    }
}
