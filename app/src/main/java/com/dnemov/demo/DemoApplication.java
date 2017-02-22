package com.dnemov.demo;

import android.app.Application;
import android.content.Intent;

import com.dnemov.demo.services.ItemsService;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by dnemov on 22.02.17.
 */

public class DemoApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Realm.init(this);
        RealmConfiguration realmConfig =
                new RealmConfiguration.Builder().name("demo.realm").build();
        Realm.deleteRealm(realmConfig); // Delete Realm between app restarts.
        Realm.setDefaultConfiguration(realmConfig);

        startService(new Intent(this, ItemsService.class));
    }
}
