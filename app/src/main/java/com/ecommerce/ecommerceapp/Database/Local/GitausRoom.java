package com.ecommerce.ecommerceapp.Database.Local;


import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.ecommerce.ecommerceapp.Database.ModelDb.Cart;
import com.ecommerce.ecommerceapp.Database.ModelDb.Favorites;

@Database(entities ={Cart.class, Favorites.class},version = 1)
public abstract class GitausRoom extends RoomDatabase {



    public abstract CartDAO mCartDAO();
    public abstract FavoritesDao mFavoritesDao();


    private static GitausRoom instance;

    public static GitausRoom getInstance(Context context)
    {
        if(instance ==null)
            instance = Room.databaseBuilder(context,GitausRoom.class,"GITAUDB")
                    .allowMainThreadQueries()
                    .build();
        return instance;
    }
}
