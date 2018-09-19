package com.ecommerce.ecommerceapp.Database.Local;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.ecommerce.ecommerceapp.Database.ModelDb.Favorites;

import java.util.List;

import io.reactivex.Flowable;


@Dao
public interface FavoritesDao {
    @Query("SELECT * FROM Favorites")
    Flowable<List<Favorites>>getFavItems();

    @Query("SELECT EXISTS (SELECT 1 FROM Favorites WHERE id =:itemId)")
    int isFavorite(int itemId);

    @Insert
    void insertFav(Favorites...favorites);

    @Delete
    void delete(Favorites favorites);


}
