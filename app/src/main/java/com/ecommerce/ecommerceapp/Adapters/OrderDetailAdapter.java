package com.ecommerce.ecommerceapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.ecommerce.ecommerceapp.Database.ModelDb.Cart;
import com.ecommerce.ecommerceapp.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class OrderDetailAdapter extends RecyclerView.Adapter<OrderDetailAdapter.OrderDetailViewHolder> {



    Context mContext;
    List<Cart> mCartList;


    public OrderDetailAdapter(Context context, List<Cart> cartList) {
        mContext = context;
        mCartList = cartList;
    }

    @NonNull
    @Override
    public OrderDetailAdapter.OrderDetailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.cart_item_layout,parent,false);
        return new OrderDetailAdapter.OrderDetailViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final OrderDetailAdapter.OrderDetailViewHolder holder, final int position) {
        Picasso.with(mContext)
                .load(mCartList.get(position).link)
                .into(holder.prod_image);

        holder.txt_price.setText(new StringBuilder("Kshs").append(mCartList.get(position).price));
        holder.txt_product_name.setText(new StringBuilder(mCartList.get(position).name)
                .append(" x")
                .append(mCartList.get(position).amount)
                .append(mCartList.get(position).size ==0 ? "SIZE M":"SIZE L"));

        holder.txt_sugar_ice.setText(new StringBuilder("Sugar: ")
                .append(mCartList.get(position).sugar).append("%").append("\n")
                .append("Ice:").append(mCartList.get(position).ice)
                .append("%").toString());



    }

    @Override
    public int getItemCount() {
        return mCartList.size();
    }


    public static class OrderDetailViewHolder extends RecyclerView.ViewHolder
    {
        ImageView prod_image;
        TextView txt_product_name,txt_sugar_ice,txt_price;


        public RelativeLayout background_view;
        public LinearLayout foregroundView;

        public OrderDetailViewHolder(@NonNull View itemView) {
            super(itemView);
            prod_image = itemView.findViewById(R.id.img_product);
            txt_product_name = itemView.findViewById(R.id.product_name_txt);
            txt_sugar_ice = itemView.findViewById(R.id.sugar_ice_txt);
            txt_price = itemView.findViewById(R.id.price_txt);

            background_view = (RelativeLayout)itemView.findViewById(R.id.background);
            foregroundView =(LinearLayout)itemView.findViewById(R.id.foreground);
        }
    }
}
