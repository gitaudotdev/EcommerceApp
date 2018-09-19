package com.ecommerce.ecommerceapp.Database.Local;

import com.ecommerce.ecommerceapp.Database.DataSource.ICartDataSource;
import com.ecommerce.ecommerceapp.Database.ModelDb.Cart;

import java.util.List;

import io.reactivex.Flowable;

public class CartDataSource implements ICartDataSource {

    private CartDAO  mCartDAO;
    private static CartDataSource instance;


    public CartDataSource(CartDAO cartDAO) {
        mCartDAO = cartDAO;
    }

    public static CartDataSource getInstance(CartDAO cartDAO)
    {
        if(instance == null)
            instance = new CartDataSource(cartDAO);
        return instance;
    }

    @Override
    public Flowable<List<Cart>> getCartItems() {
        return mCartDAO.getCartItems();
    }

    @Override
    public Flowable<List<Cart>> getCartItemById(int cartItemId) {
        return mCartDAO.getCartItemById(cartItemId);
    }

    @Override
    public int countCartItems() {
        return mCartDAO.countCartItems();
    }

    @Override
    public float sumPrice() {
        return mCartDAO.sumPrice();
    }

    @Override
    public void emptyCart() {
        mCartDAO.emptyCart();
    }

    @Override
    public void insertToCart(Cart... carts) {
        mCartDAO.insertToCart(carts);
    }

    @Override
    public void updateCart(Cart... carts) {
        mCartDAO.updateCart(carts);
    }

    @Override
    public void deleteCartItem(Cart cart) {
        mCartDAO.deleteCartItem(cart);
    }
}
