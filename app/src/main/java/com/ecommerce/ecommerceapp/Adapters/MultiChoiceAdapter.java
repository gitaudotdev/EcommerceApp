package com.ecommerce.ecommerceapp.Adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.ecommerce.ecommerceapp.Model.Drink;
import com.ecommerce.ecommerceapp.R;
import com.ecommerce.ecommerceapp.Utils.Common;

import java.util.List;

public class MultiChoiceAdapter extends RecyclerView.Adapter<MultiChoiceAdapter.MultichoiceViewHolder> {

    Context mContext;
    List<Drink> optionsList;

    public MultiChoiceAdapter(Context context, List<Drink> optionsList) {
        mContext = context;
        this.optionsList = optionsList;
    }

    @NonNull
    @Override
    public MultichoiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.multi_check_layout,null);
        return new MultichoiceViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MultichoiceViewHolder holder, final int position) {
        holder.mCheckBox.setText(optionsList.get(position).Name);
        holder.mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked)
                {
                    Common.toppingsAdded.add(compoundButton.getText().toString());
                    Common.toppingsPrice+=Double.parseDouble(optionsList.get(position).Price);
                }
                else
                {
                    Common.toppingsAdded.remove(compoundButton.getText().toString());
                    Common.toppingsPrice-=Double.parseDouble(optionsList.get(position).Price);
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return optionsList.size();
    }

    class MultichoiceViewHolder extends RecyclerView.ViewHolder{

        CheckBox mCheckBox;

        public MultichoiceViewHolder(@NonNull View itemView) {
            super(itemView);
            mCheckBox = itemView.findViewById(R.id.ckb_topping);
        }
    }
}
