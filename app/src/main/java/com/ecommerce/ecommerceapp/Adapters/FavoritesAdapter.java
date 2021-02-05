package com.ecommerce.ecommerceapp.Adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ecommerce.ecommerceapp.Database.ModelDb.Favorites;
import com.ecommerce.ecommerceapp.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.FavoriteViewHolder>{

    Context context;
    List<Favorites> mFavoritesList;

    public FavoritesAdapter(Context context, List<Favorites> favoritesList) {
        this.context = context;
        mFavoritesList = favoritesList;
    }

    @NonNull
    @Override
    public FavoriteViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.fav_item_layout,viewGroup,false);
        return new FavoriteViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteViewHolder holder, int position) {
        Picasso.with(context).load(mFavoritesList.get(position).link).into(holder.img_product);
        holder.txt_price.setText(new StringBuilder("Kshs").append(mFavoritesList.get(position).price).toString());
        holder.txt_product_name.setText(mFavoritesList.get(position).name);


    }

    @Override
    public int getItemCount() {
        return mFavoritesList.size();
    }

   public class FavoriteViewHolder extends RecyclerView.ViewHolder{

        ImageView img_product;
        TextView txt_product_name,txt_price;

        public RelativeLayout view_background;
        public LinearLayout view_foreground;

        public FavoriteViewHolder(@NonNull View itemView) {
            super(itemView);
            img_product =itemView.findViewById(R.id.fav_img_product);
            txt_product_name = itemView.findViewById(R.id.fav_product_name_txt);
            txt_price = itemView.findViewById(R.id.fav_price_txt);

            view_background = (RelativeLayout)itemView.findViewById(R.id.view_background);
            view_foreground = (LinearLayout)itemView.findViewById(R.id.view_foreground);
        }
    }

    public void removeItem(int position)
    {
        mFavoritesList.remove(position);
        notifyItemRemoved(position);
    }
    public void restoreItem(Favorites item,int position)
    {
        mFavoritesList.add(position,item);
        notifyItemInserted(position);
    }
}
