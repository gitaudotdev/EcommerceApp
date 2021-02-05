package com.ecommerce.ecommerceapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.ecommerce.ecommerceapp.Adapters.OrderAdapter;
import com.ecommerce.ecommerceapp.Model.Order;
import com.ecommerce.ecommerceapp.Retrofit.EcommerceApi;
import com.ecommerce.ecommerceapp.Utils.Common;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class OrdersActivity extends AppCompatActivity {

    EcommerceApi mService;
    RecyclerView ordersRecycler;
    CompositeDisposable disposables = new CompositeDisposable();
    BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);

        mService = Common.getApi();

        ordersRecycler = findViewById(R.id.orders_recycler);
        ordersRecycler.setLayoutManager(new LinearLayoutManager(this));
        ordersRecycler.setHasFixedSize(true);

        bottomNav = findViewById(R.id.bottom_nav);
        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getItemId() == R.id.new_order){
                    loadOrders("0");
                }else if(item.getItemId() == R.id.cancelled_order){
                    loadOrders("-1");
                }else if(item.getItemId() == R.id.processed_order){
                    loadOrders("1");
                }else if(item.getItemId() == R.id.shipping_order){
                    loadOrders("2");
                }else if(item.getItemId() == R.id.shipped_order){
                    loadOrders("3");
                }
                return true;
            }
        });

        loadOrders("0");
    }

    private void loadOrders(String statusCode) {
        if(Common.currentUser != null){
            disposables.add(mService.getOrder(Common.currentUser.getPhone(),statusCode)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new Consumer<List<Order>>() {
                        @Override
                        public void accept(List<Order> orders) throws Exception {
                            displayOrder(orders);
                        }
                    }));
        }else{
            Toast.makeText(this, "Please Login", Toast.LENGTH_SHORT).show();
            //Require Login
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("NOT LOGGED IN");
            builder.setMessage("Please Login to View Your Orders");
            builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int i) {
                    dialog.dismiss();
                }
            }).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    startActivity(new Intent(OrdersActivity.this,MainActivity.class));
                    finish();
                }
            }
            
            ).show();
        }

    }

    private void displayOrder(List<Order> orders) {
        OrderAdapter adapter = new OrderAdapter(this,orders);
        ordersRecycler.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadOrders("0");
    }

    @Override
    protected void onDestroy() {
        disposables.clear();
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        disposables.clear();
        super.onStop();
    }
}