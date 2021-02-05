package com.ecommerce.ecommerceapp.Adapters;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ecommerce.ecommerceapp.Interface.ItemClickListener;
import com.ecommerce.ecommerceapp.R;

public class DrinkViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    ImageView img_product;
    TextView txt_drink,txt_price;
    ImageView btn_add,btn_fave;

    ItemClickListener itemClickListener;

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public DrinkViewHolder(@NonNull View itemView) {
        super(itemView);

        img_product = itemView.findViewById(R.id.drink_image);
        txt_drink = itemView.findViewById(R.id.text_drink_name);
        txt_price = itemView.findViewById(R.id.txt_price);
        btn_add = itemView.findViewById(R.id.btn_add_cart);
        btn_fave = itemView.findViewById(R.id.btn_fav);

        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        itemClickListener.onClick(view);
    }
}
