package com.ecommerce.ecommerceapp.Utils;

import android.graphics.Canvas;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;
import android.view.View;

import com.ecommerce.ecommerceapp.Adapters.CartAdapter;
import com.ecommerce.ecommerceapp.Adapters.FavoritesAdapter;

public class RecyclerItemTouchHelper extends ItemTouchHelper.SimpleCallback{

    RecyclerItemTouchHelperListener listener;

    public RecyclerItemTouchHelper(int dragDirs, int swipeDirs,RecyclerItemTouchHelperListener listener) {
        super(dragDirs, swipeDirs);
        this.listener = listener;
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
        return true;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        if(listener!=null)
            listener.onSwiped(viewHolder,direction,viewHolder.getAdapterPosition());
    }

    @Override
    public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
       if(viewHolder instanceof FavoritesAdapter.FavoriteViewHolder)
       {
           View foregroundView = ((FavoritesAdapter.FavoriteViewHolder)viewHolder).view_foreground;
           getDefaultUIUtil().clearView(foregroundView);
       }else if(viewHolder instanceof CartAdapter.CartViewHolder)
       {
           View foregroundView = ((CartAdapter.CartViewHolder)viewHolder).foregroundView;
           getDefaultUIUtil().clearView(foregroundView);
       }



    }

    @Override
    public int convertToAbsoluteDirection(int flags, int layoutDirection) {
        return super.convertToAbsoluteDirection(flags, layoutDirection);
    }

    @Override
    public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder, int actionState) {
        if(viewHolder !=null)
        {
            if(viewHolder instanceof FavoritesAdapter.FavoriteViewHolder)
            {
                View foregroundView = ((FavoritesAdapter.FavoriteViewHolder)viewHolder).view_foreground;
                getDefaultUIUtil().onSelected(foregroundView);

            }
            else if(viewHolder instanceof CartAdapter.CartViewHolder)
            {
                View foregroundView = ((CartAdapter.CartViewHolder)viewHolder).foregroundView;
                getDefaultUIUtil().onSelected(foregroundView);

            }

        }

    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
       if(viewHolder instanceof FavoritesAdapter.FavoriteViewHolder)
       {
           View foregroundView = ((FavoritesAdapter.FavoriteViewHolder)viewHolder).view_foreground;
           getDefaultUIUtil().onDraw(c,recyclerView,foregroundView,dX,dY,actionState,isCurrentlyActive);

       }else if(viewHolder instanceof CartAdapter.CartViewHolder)
       {
           View foregroundView = ((CartAdapter.CartViewHolder)viewHolder).foregroundView;
           getDefaultUIUtil().onDraw(c,recyclerView,foregroundView,dX,dY,actionState,isCurrentlyActive);

       }
    }

    @Override
    public void onChildDrawOver(@NonNull Canvas c, @NonNull RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        if(viewHolder instanceof FavoritesAdapter.FavoriteViewHolder)
        {
            View foregroundView = ((FavoritesAdapter.FavoriteViewHolder)viewHolder).view_foreground;
            getDefaultUIUtil().onDrawOver(c, recyclerView, foregroundView, dX, dY, actionState, isCurrentlyActive);
        }else if(viewHolder instanceof CartAdapter.CartViewHolder)
        {
            View foregroundView = ((CartAdapter.CartViewHolder)viewHolder).foregroundView;
            getDefaultUIUtil().onDrawOver(c, recyclerView, foregroundView, dX, dY, actionState, isCurrentlyActive);

        }
    }
}
