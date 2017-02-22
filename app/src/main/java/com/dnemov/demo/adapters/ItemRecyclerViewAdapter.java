package com.dnemov.demo.adapters;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.dnemov.demo.R;
import com.dnemov.demo.bindings.ItemView;
import com.dnemov.demo.databinding.ListItemBinding;
import com.dnemov.demo.models.Item;

import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;

/**
 * Created by dnemov on 22.02.17.
 */

public class ItemRecyclerViewAdapter extends RealmRecyclerViewAdapter<Item, ItemRecyclerViewAdapter.ItemViewHolder> {

    public ItemRecyclerViewAdapter(@NonNull Context context, @Nullable OrderedRealmCollection<Item> data) {
        super(context, data, true);
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewDataBinding binding =
                DataBindingUtil.inflate(inflater, R.layout.list_item, parent, false);
        View itemView = binding.getRoot();
        return new ItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        holder.binding.setItemView(new ItemView(getData().get(position)));
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {

        final ListItemBinding binding;

        ItemViewHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }
    }
}
