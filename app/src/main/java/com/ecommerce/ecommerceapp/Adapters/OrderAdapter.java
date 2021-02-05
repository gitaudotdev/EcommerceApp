package com.ecommerce.ecommerceapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ecommerce.ecommerceapp.Interface.ItemClickListener;
import com.ecommerce.ecommerceapp.Model.Order;
import com.ecommerce.ecommerceapp.OrderDetailActivity;
import com.ecommerce.ecommerceapp.R;
import com.ecommerce.ecommerceapp.Utils.Common;

import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderViewHolder> {

    Context mContext;
    List<Order> orderList;

    public OrderAdapter(Context mContext, List<Order> orderList) {
        this.mContext = mContext;
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.order_display,parent,false);
        return new OrderViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        holder.txtOrderId.setText(new StringBuilder("#").append(orderList.get(position).getOrderId()));
        holder.txtOrderStatus.setText(new StringBuilder("Order Status: ").append(Common.convertCodeToStatus(orderList.get(position).getOrderStatus())));
        holder.txtOrderComment.setText(orderList.get(position).getOrderComment());
        holder.txtOrderAddress.setText(orderList.get(position).getOrderAddress());
        holder.txtOrderPrice.setText(new StringBuilder("Ksh.").append(orderList.get(position).getOrderPrice()));

        holder.setClickListener(new ItemClickListener(){
            @Override
            public void onClick(View v) {
                mContext.startActivity(new Intent(mContext, OrderDetailActivity.class));
            }
        });
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }
}
