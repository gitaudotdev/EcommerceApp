package com.ecommerce.ecommerceapp.Adapters;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ecommerce.ecommerceapp.DrinkActivity;
import com.ecommerce.ecommerceapp.Interface.ItemClickListener;
import com.ecommerce.ecommerceapp.Model.Category;
import com.ecommerce.ecommerceapp.R;
import com.ecommerce.ecommerceapp.Utils.Common;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryViewHolder> {

    Context context;
    List<Category> menu;


    public CategoryAdapter(Context context, List<Category> menu) {
        this.context = context;
        this.menu = menu;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.menu_item,null);
        return new CategoryViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, final int position) {
        Picasso.with(context)
                .load(menu.get(position).Link)
                .into(holder.img_product);

        holder.menu_name.setText(menu.get(position).Name);

        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View v) {
                Common.currentCategory = menu.get(position);

                //start new Activity
                context.startActivity(new Intent(context, DrinkActivity.class));
            }
        });
    }

    @Override
    public int getItemCount() {
        return menu.size();
    }
}
