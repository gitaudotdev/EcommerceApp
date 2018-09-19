package com.ecommerce.ecommerceapp.Database.DataSource;

import com.ecommerce.ecommerceapp.Database.ModelDb.Favorites;

import java.util.List;

import io.reactivex.Flowable;

public interface IFavoritesDataSource {
    Flowable<List<Favorites>>getFavItems();
    int isFavorite(int itemId);
    void insertFav(Favorites...favorites);
    void delete(Favorites favorites);

}
