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

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.ecommerce.ecommerceapp.Database.ModelDb.Cart;
import com.ecommerce.ecommerceapp.R;
import com.ecommerce.ecommerceapp.Utils.Common;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    Context mContext;
    List<Cart> mCartList;


    public CartAdapter(Context context, List<Cart> cartList) {
        mContext = context;
        mCartList = cartList;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.cart_item_layout,parent,false);
        return new CartViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final CartViewHolder holder, final int position) {
        Picasso.with(mContext)
                .load(mCartList.get(position).link)
                .into(holder.prod_image);

        holder.txt_amount.setNumber(String.valueOf(mCartList.get(position).amount));
        holder.txt_price.setText(new StringBuilder("Kshs").append(mCartList.get(position).price));
        holder.txt_product_name.setText(new StringBuilder(mCartList.get(position).name)
                .append(" x")
                .append(mCartList.get(position).amount)
                .append(mCartList.get(position).size ==0 ? "SIZE M":"SIZE L"));

        holder.txt_sugar_ice.setText(new StringBuilder("Sugar: ")
        .append(mCartList.get(position).sugar).append("%").append("\n")
        .append("Ice:").append(mCartList.get(position).ice)
        .append("%").toString());


        //Get price of one cup with all options
        final double priceOneCup = mCartList.get(position).price /mCartList.get(position).amount;

        //Auto save item when user changes amount
        holder.txt_amount.setOnValueChangeListener(new ElegantNumberButton.OnValueChangeListener() {
            @Override
            public void onValueChange(ElegantNumberButton view, int oldValue, int newValue) {
                Cart cart = mCartList.get(position);
                cart.amount = newValue;
                cart.price = Math.round(priceOneCup*newValue);

                Common.sCartRepository.updateCart(cart);

                holder.txt_price.setText(new StringBuilder("kshs").append(mCartList.get(position).price));
            }
        });
    }

    @Override
    public int getItemCount() {
        return mCartList.size();
    }


    public static class CartViewHolder extends RecyclerView.ViewHolder
    {
        ImageView prod_image;
        TextView txt_product_name,txt_sugar_ice,txt_price;
        ElegantNumberButton txt_amount;

       public RelativeLayout background_view;
       public LinearLayout foregroundView;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            prod_image = itemView.findViewById(R.id.img_product);
            txt_product_name = itemView.findViewById(R.id.product_name_txt);
            txt_sugar_ice = itemView.findViewById(R.id.sugar_ice_txt);
            txt_price = itemView.findViewById(R.id.price_txt);
            txt_amount = itemView.findViewById(R.id.amount_txt);

            background_view = (RelativeLayout)itemView.findViewById(R.id.background);
            foregroundView =(LinearLayout)itemView.findViewById(R.id.foreground);
        }
    }

    public void removeItem(int position)
    {
        mCartList.remove(position);
        notifyItemRemoved(position);
    }
    public void restoreItem(Cart item, int position)
    {
        mCartList.add(position,item);
        notifyItemInserted(position);
    }
}
