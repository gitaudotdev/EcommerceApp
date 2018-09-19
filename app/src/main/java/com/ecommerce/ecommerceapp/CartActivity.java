package com.ecommerce.ecommerceapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.ecommerce.ecommerceapp.Adapters.CartAdapter;
import com.ecommerce.ecommerceapp.Adapters.FavoritesAdapter;
import com.ecommerce.ecommerceapp.Database.ModelDb.Cart;
import com.ecommerce.ecommerceapp.Database.ModelDb.Favorites;
import com.ecommerce.ecommerceapp.Retrofit.EcommerceApi;
import com.ecommerce.ecommerceapp.Utils.Common;
import com.ecommerce.ecommerceapp.Utils.RecyclerItemTouchHelper;
import com.ecommerce.ecommerceapp.Utils.RecyclerItemTouchHelperListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartActivity extends AppCompatActivity implements RecyclerItemTouchHelperListener{

    RecyclerView mRecyclerView;
    Button btn_place_order;

    List<Cart> cartList = new ArrayList<>();

    CartAdapter cartAdapter;

    RelativeLayout rootLayout;

    EcommerceApi mService;

    CompositeDisposable mCompositeDisposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);


        mCompositeDisposable = new CompositeDisposable();

        mService = Common.getApi();

        rootLayout = (RelativeLayout) findViewById(R.id.cart_rootLayout);

        mRecyclerView = findViewById(R.id.recycler_cart);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setHasFixedSize(true);

        ItemTouchHelper.SimpleCallback simpleCallback = new RecyclerItemTouchHelper(0,ItemTouchHelper.LEFT,this);
        new ItemTouchHelper(simpleCallback).attachToRecyclerView(mRecyclerView);


        btn_place_order = findViewById(R.id.btn_place_order);
        btn_place_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                placeOrder();
            }
        });

        loadCartItems();
    }

    private void placeOrder() {


        if(Common.currentUser!=null) {
            //create dialog
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle("Submit Order");

            View place_order = LayoutInflater.from(this).inflate(R.layout.order_layout, null);

            final EditText edtComment = place_order.findViewById(R.id.etComment);
            final EditText edt_other_address = place_order.findViewById(R.id.et_other_address);

            final RadioButton rdi_user_address = place_order.findViewById(R.id.rdi_user_address);
            final RadioButton rdi_other_address = place_order.findViewById(R.id.rdi_other_address);

            //Event
            rdi_user_address.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                    if (isChecked)
                        edt_other_address.setEnabled(false);
                }
            });
            rdi_other_address.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                    if (isChecked)
                        edt_other_address.setEnabled(true);
                }
            });
            alert.setView(place_order);

            alert.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            }).setPositiveButton("SUBMIT", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    final String orderComment = edtComment.getText().toString();
                    final String orderAddress;
                    if (rdi_user_address.isChecked())
                        orderAddress = Common.currentUser.getAddress();
                    else if (rdi_other_address.isChecked())
                        orderAddress = edt_other_address.getText().toString();
                    else
                        orderAddress = "";

                    //Submit Order
                    mCompositeDisposable.add(
                            Common.sCartRepository.getCartItems()
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribeOn(Schedulers.io())
                                    .subscribe(new Consumer<List<Cart>>() {
                                        @Override
                                        public void accept(List<Cart> carts) throws Exception {
                                            if (!TextUtils.isEmpty(orderAddress))
                                                sendOrderToServer(Common.sCartRepository.sumPrice(),
                                                        carts,
                                                        orderComment, orderAddress);
                                            else
                                                Toast.makeText(CartActivity.this, "Address Cant be Empty", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                    );
                }
            });

            alert.show();
        }else
        {
            //Require Login
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("NOT LOGGED IN");
            builder.setMessage("Please Login to Submit Order");
            builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int i) {
                    dialog.dismiss();
                }
            }).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    startActivity(new Intent(CartActivity.this,MainActivity.class));
                    finish();
                }
            }).show();
        }


    }

    private void sendOrderToServer(float sumPrice, List<Cart> carts, String orderComment, String orderAddress) {
        if(carts.size()>0)
        {
            String orderDetail = new Gson().toJson(carts);

            mService.submitOrder(sumPrice,orderDetail,orderComment,orderAddress,Common.currentUser.getPhone())
                    .enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            Toast.makeText(CartActivity.this, "Order Placed", Toast.LENGTH_SHORT).show();

                            //Clear cart
                            Common.sCartRepository.emptyCart();
                            finish();

                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            Log.e("ERROR",t.getMessage());
                        }
                    });

        }
    }


    private void loadCartItems() {
        mCompositeDisposable.add(Common.sCartRepository.getCartItems()
        .observeOn(AndroidSchedulers.mainThread())
        .subscribeOn(Schedulers.io())
        .subscribe(new Consumer<List<Cart>>() {
            @Override
            public void accept(List<Cart> carts) throws Exception {
                displayCartItem(carts);
            }
        }));
    }

    private void displayCartItem(List<Cart> carts) {
        cartList =carts;
        cartAdapter = new CartAdapter(this,carts);
        mRecyclerView.setAdapter(cartAdapter);
    }

    @Override
    protected void onDestroy() {
        mCompositeDisposable.clear();
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        mCompositeDisposable.clear();
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCartItems();
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof CartAdapter.CartViewHolder)
        {
            String name =cartList.get(viewHolder.getAdapterPosition()).name;

            final Cart deletedItem = cartList.get(viewHolder.getAdapterPosition());
            final int deletedIndex = viewHolder.getAdapterPosition();

            //Delete Item from adapter
            cartAdapter.removeItem(deletedIndex);

            //Delete from Room Database
            Common.sCartRepository.deleteCartItem(deletedItem);

            Snackbar snackbar = Snackbar.make(rootLayout,new StringBuilder(name).append("removed from your Cart").toString(),Snackbar.LENGTH_LONG);
            snackbar.setAction("UNDO", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cartAdapter.restoreItem(deletedItem,deletedIndex);
                    Common.sCartRepository.insertToCart(deletedItem);
                }
            });
            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();
        }
    }
}
