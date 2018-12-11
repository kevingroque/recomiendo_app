package com.huich.roque.app.recomiendo_app.models;

import android.support.annotation.NonNull;

import com.google.firebase.firestore.Exclude;

public class SiteId {
    @Exclude
    public String SiteId;

    public <T extends SiteId> T withId(@NonNull final String id) {
        this.SiteId = id;
        return (T) this;
    }
}
