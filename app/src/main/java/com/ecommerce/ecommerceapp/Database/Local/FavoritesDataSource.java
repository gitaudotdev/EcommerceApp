package com.ecommerce.ecommerceapp.Database.Local;

import com.ecommerce.ecommerceapp.Database.DataSource.IFavoritesDataSource;
import com.ecommerce.ecommerceapp.Database.ModelDb.Favorites;

import java.util.List;

import io.reactivex.Flowable;

public class FavoritesDataSource implements IFavoritesDataSource {

    private FavoritesDao mFavoritesDao;
    private static  FavoritesDataSource instance;

    public FavoritesDataSource(FavoritesDao favoritesDao) {
        mFavoritesDao = favoritesDao;
    }

    public static  FavoritesDataSource getInstance(FavoritesDao favoritesDao)
    {
        if(instance==null)
            instance= new FavoritesDataSource(favoritesDao);
        return instance;
    }

    @Override
    public Flowable<List<Favorites>> getFavItems() {
        return mFavoritesDao.getFavItems();
    }

    @Override
    public int isFavorite(int itemId) {
        return mFavoritesDao.isFavorite(itemId);
    }

    @Override
    public void insertFav(Favorites... favorites) {
        mFavoritesDao.insertFav(favorites);
    }

    @Override
    public void delete(Favorites favorites) {
        mFavoritesDao.delete(favorites);
    }
}
