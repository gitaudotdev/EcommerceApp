package com.ecommerce.ecommerceapp.Database.ModelDb;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

@Entity(tableName = "Favorites")
public class Favorites {

    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "id")
    public String id;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "link")
    public String link;

    @ColumnInfo(name = "price")
    public String price;


    @ColumnInfo(name = "menuId")
    public String menuId;



}
