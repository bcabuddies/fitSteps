package com.bcabuddies.fitsteps;

import com.google.firebase.firestore.Exclude;

import androidx.annotation.NonNull;

class HomeDataId {

    @Exclude
    String HomeDataId;

    <T extends HomeDataId> T withID(@NonNull final String id) {
        this.HomeDataId = id;
        return (T) this;
    }
}
