package com.dnemov.demo.bindings;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.dnemov.demo.BR;
import com.dnemov.demo.models.Item;

/**
 * Created by dnemov on 22.02.17.
 */

public class ItemView extends BaseObservable {
    private final Item item;

    private String title;
    private String description;

    public ItemView(Item item) {
        this.item = item;
        setTitle();
        setDescription();
    }

    @Bindable
    public String getTitle() {
        return title;
    }

    private void setTitle() {
        this.title = item.id;
        notifyPropertyChanged(BR.title);
    }

    @Bindable
    public String getDescription() {
        return description;
    }

    private void setDescription() {
        this.description = item.date.toString() + (item.fromService?" (From Service)":"  (From UI)");
        notifyPropertyChanged(BR.description);
    }
}
