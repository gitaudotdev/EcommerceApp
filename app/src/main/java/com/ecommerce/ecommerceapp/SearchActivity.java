package com.ecommerce.ecommerceapp;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;

import com.ecommerce.ecommerceapp.Adapters.DrinkAdapter;
import com.ecommerce.ecommerceapp.Model.Drink;
import com.ecommerce.ecommerceapp.Retrofit.EcommerceApi;
import com.ecommerce.ecommerceapp.Utils.Common;
import com.mancj.materialsearchbar.MaterialSearchBar;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class SearchActivity extends AppCompatActivity {

    List<String> suggestList = new ArrayList<>();
    List<Drink> localDatasource = new ArrayList<>();
    MaterialSearchBar searchBar;

    EcommerceApi mService;

    RecyclerView search_recycler;

    CompositeDisposable mCompositeDisposable = new CompositeDisposable();

    DrinkAdapter adapter,searchAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        mService = Common.getApi();

        search_recycler = findViewById(R.id.recycler_search);
        search_recycler.setHasFixedSize(true);
        search_recycler.setLayoutManager(new GridLayoutManager(this,2));


        searchBar = findViewById(R.id.searchBar);
        searchBar.setHint("Enter Your Drink");

        loadAllDrinks();

        searchBar.setCardViewElevation(10);
        searchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                List<String> suggest = new ArrayList<>();
                for(String search:suggest)
                {
                    if(search.toLowerCase().contains(searchBar.getText().toLowerCase()))
                        suggest.add(search);
                }
                searchBar.setLastSuggestions(suggest);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        searchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {
                if(!enabled)
                    search_recycler.setAdapter(adapter); //restore full list of drinks
            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                startSearch(text);
            }

            @Override
            public void onButtonClicked(int buttonCode) {

            }
        });

    }

    private void startSearch(CharSequence text) {
        List<Drink> result = new ArrayList<>();
        for(Drink drink:localDatasource)
            if(drink.Name.contains(text))
                result.add(drink);
        searchAdapter = new DrinkAdapter(this,result);
        search_recycler.setAdapter(searchAdapter);
    }

    @Override
    protected void onStop() {
        mCompositeDisposable.clear();
        super.onStop();
    }

    private void loadAllDrinks() {
        mCompositeDisposable.add(mService.getAllDrinks().observeOn(AndroidSchedulers.mainThread())
        .subscribeOn(Schedulers.io())
        .subscribe(new Consumer<List<Drink>>() {
            @Override
            public void accept(List<Drink> drinks) throws Exception {
                displayListDrinks(drinks);
                buildSuggestionList(drinks);
            }
        }));
    }

    private void buildSuggestionList(List<Drink> drinks) {
        for(Drink drink: drinks)
            suggestList.add(drink.Name);
        searchBar.setLastSuggestions(suggestList);

    }

    private void displayListDrinks(List<Drink> drinks) {
        localDatasource = drinks;
        adapter = new DrinkAdapter(this,drinks);
        search_recycler.setAdapter(adapter);
    }


}
