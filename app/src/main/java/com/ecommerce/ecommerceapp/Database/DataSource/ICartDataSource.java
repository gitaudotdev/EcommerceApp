package com.ecommerce.ecommerceapp.Database.DataSource;

import com.ecommerce.ecommerceapp.Database.ModelDb.Cart;

import java.util.List;

import io.reactivex.Flowable;

public interface ICartDataSource {
    Flowable<List<Cart>> getCartItems();

    Flowable<List<Cart>> getCartItemById(int cartItemId);

    int countCartItems();
    float sumPrice();
    void emptyCart();
    void insertToCart(Cart...carts);
    void updateCart(Cart...carts);
    void deleteCartItem(Cart cart);
}
