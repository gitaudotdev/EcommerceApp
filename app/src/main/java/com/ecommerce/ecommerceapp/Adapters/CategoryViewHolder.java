package com.ecommerce.ecommerceapp.Adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ecommerce.ecommerceapp.Interface.ItemClickListener;
import com.ecommerce.ecommerceapp.R;

public class CategoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
     ImageView img_product;
     TextView menu_name;

     ItemClickListener itemClickListener;

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public CategoryViewHolder(@NonNull View itemView) {
        super(itemView);

        img_product = itemView.findViewById(R.id.prod_image);
        menu_name = itemView.findViewById(R.id.text_menu_name);

        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        itemClickListener.onClick(view);
    }
}
