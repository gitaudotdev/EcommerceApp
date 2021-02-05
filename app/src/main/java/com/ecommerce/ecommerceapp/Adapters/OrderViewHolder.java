package com.ecommerce.ecommerceapp.Adapters;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ecommerce.ecommerceapp.Interface.ItemClickListener;
import com.ecommerce.ecommerceapp.R;

public class OrderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    public TextView txtOrderId,txtOrderPrice,txtOrderAddress,
    txtOrderComment,txtOrderStatus;

    ItemClickListener clickListener;

    public void setClickListener(ItemClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public OrderViewHolder(@NonNull View itemView) {
        super(itemView);

        txtOrderAddress = itemView.findViewById(R.id.txt_orderAddress);
        txtOrderId = itemView.findViewById(R.id.txt_orderId);
        txtOrderPrice = itemView.findViewById(R.id.txt_order_price);
        txtOrderComment = itemView.findViewById(R.id.txt_orderComment);
        txtOrderStatus = itemView.findViewById(R.id.txt_orderStatus);

        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        clickListener.onClick(v);
    }
}
