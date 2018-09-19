package com.ecommerce.ecommerceapp.Database.DataSource;

import com.ecommerce.ecommerceapp.Database.ModelDb.Cart;

import java.util.List;

import io.reactivex.Flowable;

public class CartRepository implements ICartDataSource{

    private  ICartDataSource mICartDataSource;

    public CartRepository(ICartDataSource ICartDataSource) {
        mICartDataSource = ICartDataSource;
    }

    private static CartRepository instance;

    public static CartRepository getInstance(ICartDataSource iCartDataSource)
    {
        if(instance==null)
            instance = new CartRepository(iCartDataSource);
        return instance;
    }

    @Override
    public Flowable<List<Cart>> getCartItems() {
        return mICartDataSource.getCartItems();
    }

    @Override
    public Flowable<List<Cart>> getCartItemById(int cartItemId) {
        return mICartDataSource.getCartItemById(cartItemId);
    }

    @Override
    public int countCartItems() {
        return mICartDataSource.countCartItems();
    }

    @Override
    public float sumPrice() {
        return mICartDataSource.sumPrice();
    }

    @Override
    public void emptyCart() {
        mICartDataSource.emptyCart();
    }

    @Override
    public void insertToCart(Cart... carts) {
        mICartDataSource.insertToCart(carts);
    }

    @Override
    public void updateCart(Cart... carts) {
        mICartDataSource.updateCart(carts);
    }

    @Override
    public void deleteCartItem(Cart cart) {
        mICartDataSource.deleteCartItem(cart);
    }
}
