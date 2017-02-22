package com.dnemov.demo.fragments;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.dnemov.demo.R;
import com.dnemov.demo.adapters.ItemRecyclerViewAdapter;
import com.dnemov.demo.interfaces.EditItemsIterface;
import com.dnemov.demo.models.Item;
import com.dnemov.demo.services.ItemsService;

import java.util.Date;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by dnemov on 22.02.17.
 */

public class ItemsFragment extends Fragment implements EditItemsIterface {


    private static final String TAG = ItemsFragment.class.getSimpleName();
    public static final String EXTRA_FROM_SERVICE = "from_service";

    private Realm mRealm;
    private boolean mFromService;

    Messenger mService = null;
    boolean mBound;

    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            mService = new Messenger(service);
            mBound = true;
        }

        public void onServiceDisconnected(ComponentName className) {
            mService = null;
            mBound = false;
        }
    };
    private RealmResults<Item> mItems;

    public static ItemsFragment newInstance(boolean fromService) {
        Bundle args = new Bundle();

        args.putBoolean(EXTRA_FROM_SERVICE, fromService);
        ItemsFragment fragment = new ItemsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        mFromService = bundle.getBoolean(EXTRA_FROM_SERVICE, false);
        mRealm = Realm.getDefaultInstance();
        mItems = mRealm.where(Item.class).equalTo("fromService", mFromService).findAllAsync();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_items, container, false);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
        setupRecyclerView(recyclerView, mItems);

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mFromService)
                    addItemFromService();
                else
                    addItemFromUI();
            }
        });
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mFromService) {
            getActivity().bindService(new Intent(getContext(), ItemsService.class), mConnection,
                    Context.BIND_AUTO_CREATE);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mBound) {
            getActivity().unbindService(mConnection);
            mBound = false;
        }
    }

    @Override
    public void addItemFromUI() {

        final Item item = new Item();
        item.id = UUID.randomUUID().toString();
        item.date = new Date();
        item.fromService = false;
        mRealm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealmOrUpdate(item);
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                Toast.makeText(getContext(),"A new record from UI has been created", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void addItemFromService() {
        if (!mBound) return;
        Message msg = Message.obtain(null, ItemsService.MSG_ADD_ITEM, 0, 0);
        try {
            mService.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void setupRecyclerView(RecyclerView recyclerView, RealmResults<Item> items) {
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(new ItemRecyclerViewAdapter(recyclerView.getContext(),items));
    }
}