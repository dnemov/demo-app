package com.dnemov.demo.services;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.support.annotation.Nullable;
import android.util.Log;

import com.dnemov.demo.models.Item;

import java.util.Date;
import java.util.UUID;

import io.realm.Realm;

/**
 * Created by dnemov on 22.02.17.
 */

public class ItemsService extends Service {

    private static final String TAG = ItemsService.class.getSimpleName();

    public static final int MSG_ADD_ITEM = 1;

    static class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case MSG_ADD_ITEM:
                    if (mReplyTo == null) mReplyTo = msg.replyTo;
                    addItem();
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    private static Messenger mReplyTo;
    final Messenger mMessenger = new Messenger(new IncomingHandler());

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mMessenger.getBinder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_NOT_STICKY;
    }

    private static void addItem() {
        final Item item = new Item();
        item.fromService = true;
        item.date = new Date();
        item.id = UUID.randomUUID().toString();

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Realm realm = Realm.getDefaultInstance();
                try {
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            realm.copyToRealmOrUpdate(item);
                            Log.v(TAG,item.toString());
                        }
                    });
                } finally {
                    realm.close();
                }
            }
        });

        thread.start();
    }
}
