package com.ecommerce.ecommerceapp.Database.DataSource;

import com.ecommerce.ecommerceapp.Database.ModelDb.Favorites;

import java.util.List;

import io.reactivex.Flowable;

public class FavoritesRepository implements IFavoritesDataSource{


    private IFavoritesDataSource mIFavoritesDataSource;


    public FavoritesRepository(IFavoritesDataSource IFavoritesDataSource) {
        mIFavoritesDataSource = IFavoritesDataSource;
    }

    private static FavoritesRepository instance;

    public static FavoritesRepository getInstance(IFavoritesDataSource favoritesDataSource)
    {
        if(instance==null)
            instance= new FavoritesRepository(favoritesDataSource);
        return instance;
    }

    @Override
    public Flowable<List<Favorites>> getFavItems() {
        return mIFavoritesDataSource.getFavItems();
    }

    @Override
    public int isFavorite(int itemId) {
        return mIFavoritesDataSource.isFavorite(itemId);
    }

    @Override
    public void insertFav(Favorites... favorites) {
        mIFavoritesDataSource.insertFav(favorites);
    }

    @Override
    public void delete(Favorites favorites) {
        mIFavoritesDataSource.delete(favorites);
    }
}
