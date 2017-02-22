package com.dnemov.demo.models;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by dnemov on 22.02.17.
 */

public class Item extends RealmObject {

    @PrimaryKey
    public String id;

    public boolean fromService;
    public Date date;
}
