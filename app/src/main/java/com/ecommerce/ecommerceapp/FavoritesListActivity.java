package com.ecommerce.ecommerceapp;

import android.graphics.Color;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;
import android.view.View;
import android.widget.RelativeLayout;

import com.ecommerce.ecommerceapp.Adapters.FavoritesAdapter;
import com.ecommerce.ecommerceapp.Database.ModelDb.Favorites;
import com.ecommerce.ecommerceapp.Utils.Common;
import com.ecommerce.ecommerceapp.Utils.RecyclerItemTouchHelper;
import com.ecommerce.ecommerceapp.Utils.RecyclerItemTouchHelperListener;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class FavoritesListActivity extends AppCompatActivity implements RecyclerItemTouchHelperListener{

    RecyclerView mRecyclerView;

    RelativeLayout rootLayout;

    CompositeDisposable mCompositeDisposable;

    FavoritesAdapter adapter;

    List<Favorites> favList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites_list);

        mCompositeDisposable = new CompositeDisposable();

        rootLayout = (RelativeLayout) findViewById(R.id.rootLayout);

        mRecyclerView = findViewById(R.id.recycler_fav);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setHasFixedSize(true);

        ItemTouchHelper.SimpleCallback simpleCallback = new RecyclerItemTouchHelper(0,ItemTouchHelper.LEFT,this);
        new ItemTouchHelper(simpleCallback).attachToRecyclerView(mRecyclerView);

        loadFavorites();

    }

    @Override
    protected void onResume() {
        super.onResume();
        loadFavorites();
    }

    private void loadFavorites() {
        mCompositeDisposable.add(Common.sFavoritesRepository.getFavItems()
        .observeOn(AndroidSchedulers.mainThread())
        .subscribeOn(Schedulers.io())
        .subscribe(new Consumer<List<Favorites>>() {
            @Override
            public void accept(List<Favorites> favorites) throws Exception {
                displayFavoriteItem(favorites);
            }
        }));
    }

    private void displayFavoriteItem(List<Favorites> favorites) {
        favList= favorites;
        adapter = new FavoritesAdapter(this,favorites);
        mRecyclerView.setAdapter(adapter);
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
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof FavoritesAdapter.FavoriteViewHolder)
        {
            String name =favList.get(viewHolder.getAdapterPosition()).name;

            final Favorites deletedItem = favList.get(viewHolder.getAdapterPosition());
            final int deletedIndex = viewHolder.getAdapterPosition();

            //Delete Item from adapter
           adapter.removeItem(deletedIndex);

           //Delete from Room Database
            Common.sFavoritesRepository.delete(deletedItem);

            Snackbar snackbar = Snackbar.make(rootLayout,new StringBuilder(name).append("removed from Favorites List").toString(),Snackbar.LENGTH_LONG);
            snackbar.setAction("UNDO", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    adapter.restoreItem(deletedItem,deletedIndex);
                    Common.sFavoritesRepository.insertFav(deletedItem);
                }
            });
            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();
        }
    }
}
