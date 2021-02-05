package com.ecommerce.ecommerceapp.Database.ModelDb;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;


@Entity(tableName = "Cart")
public class Cart {

    @NonNull
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    public int id;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "link")
    public String link;

    @ColumnInfo(name = "amount")
    public int amount;

    @ColumnInfo(name = "price")
    public double price;

    @ColumnInfo(name = "sugar")
    public int sugar;

    @ColumnInfo(name = "ice")
    public int ice;

    @ColumnInfo(name = "size")
    public int size;

    @ColumnInfo(name = "toppingExtras")
    public String toppingExtras;
}
