package com.ecommerce.ecommerceapp;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ecommerce.ecommerceapp.Adapters.CategoryAdapter;
import com.ecommerce.ecommerceapp.Adapters.DrinkAdapter;
import com.ecommerce.ecommerceapp.Model.Drink;
import com.ecommerce.ecommerceapp.Retrofit.EcommerceApi;
import com.ecommerce.ecommerceapp.Utils.Common;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class DrinkActivity extends AppCompatActivity {

    EcommerceApi mService;

    RecyclerView drink_list;
    DrinkAdapter adapter;

    TextView txt_banner_name;
    Button btn_add;


    //rxjava
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drink);

        mService = Common.getApi();


        drink_list = findViewById(R.id.recycler_drinks);
        drink_list.setLayoutManager(new GridLayoutManager(this,2));
        drink_list.setHasFixedSize(true);


        txt_banner_name = findViewById(R.id.text_menu_name);
        txt_banner_name.setText(Common.currentCategory.Name);




        loadListDrink(Common.currentCategory.ID);
    }

    private void loadListDrink(String menuID) {
        compositeDisposable.add(mService.getDrink(menuID)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Consumer<List<Drink>>() {
            @Override
            public void accept(List<Drink> drinks) throws Exception {
                displayDrinkList(drinks);
            }
        }));
    }

    private void displayDrinkList(List<Drink> drinks) {
        DrinkAdapter adapter = new DrinkAdapter(this,drinks);
        drink_list.setAdapter(adapter);
    }

    //Exit application when we click back button
    boolean isBackButtonClicked = false;

    //Ctrl+O
    @Override
    public void onBackPressed() {
        if(isBackButtonClicked){
            super.onBackPressed();
            return;
        }
        this.isBackButtonClicked= true;
        Toast.makeText(this, "Click BACK again to Exit", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        isBackButtonClicked= false;
    }
}
