package com.ecommerce.ecommerceapp.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.ecommerce.ecommerceapp.Database.ModelDb.Cart;
import com.ecommerce.ecommerceapp.Database.ModelDb.Favorites;
import com.ecommerce.ecommerceapp.Interface.ItemClickListener;
import com.ecommerce.ecommerceapp.Model.Drink;
import com.ecommerce.ecommerceapp.R;
import com.ecommerce.ecommerceapp.Utils.Common;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.List;

public class DrinkAdapter extends RecyclerView.Adapter<DrinkViewHolder> {

    Context context;
    List<Drink> drinkList;

    public DrinkAdapter(Context context, List<Drink> drinkList) {
        this.context = context;
        this.drinkList = drinkList;
    }

    @NonNull
    @Override

    public DrinkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rootview = LayoutInflater.from(context).inflate(R.layout.drink_item_layout,null);
        return new DrinkViewHolder(rootview);
    }

    @Override
    public void onBindViewHolder(@NonNull final DrinkViewHolder holder, final int position) {

        holder.btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAdToCartDialog(position);
            }
        });

        holder.txt_price.setText(new StringBuilder("$").append(drinkList.get(position).Price).toString());
        holder.txt_drink.setText(drinkList.get(position).Name);

        Picasso.with(context)
                .load(drinkList.get(position).Link)
                .into(holder.img_product);

        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "clicked", Toast.LENGTH_SHORT).show();
            }
        });

        //favorites System
        if(Common.sFavoritesRepository.isFavorite(Integer.parseInt(drinkList.get(position).ID))==1)
            holder.btn_fave.setImageResource(R.drawable.ic_favorite_white_24dp);
        else
            holder.btn_fave.setImageResource(R.drawable.ic_favorite_border_white_24dp);


        holder.btn_fave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Common.sFavoritesRepository.isFavorite(Integer.parseInt(drinkList.get(position).ID))!=1)
                {
                    addOrRemoveFavorites(drinkList.get(position),true);
                    holder.btn_fave.setImageResource(R.drawable.ic_favorite_white_24dp);
                }
                else {
                    addOrRemoveFavorites(drinkList.get(position),false);
                    holder.btn_fave.setImageResource(R.drawable.ic_favorite_border_white_24dp);

                }

//                Toast.makeText(context, "Clicked", Toast.LENGTH_SHORT).show();
            }
        });



    }

    private void addOrRemoveFavorites(Drink drink, boolean isAdd) {
        Favorites favorites = new Favorites();
        favorites.id= drink.ID;
        favorites.link = drink.Link;
        favorites.name = drink.Name;
        favorites.price = drink.Price;
        favorites.menuId = drink.MenuId;

        if(isAdd)
            Common.sFavoritesRepository.insertFav(favorites);
        else
            Common.sFavoritesRepository.delete(favorites);
    }


    private void showAdToCartDialog(final int position) {
        AlertDialog.Builder alertdialog = new AlertDialog.Builder(context);
        View itemView =LayoutInflater.from(context)
                .inflate(R.layout.add_to_cart_layout,null);

        //View
        ImageView prod_image = itemView.findViewById(R.id.img_cart_product);
        final ElegantNumberButton txt_count = itemView.findViewById(R.id.txt_count);
        TextView txt_prod_dialog = itemView.findViewById(R.id.txt_cart_product_name);
        EditText edt_Comment = itemView.findViewById(R.id.edt_comment);

        RadioButton rdi_sizem = itemView.findViewById(R.id.rdi_sizeM);
        RadioButton rdi_sizeL = itemView.findViewById(R.id.rdi_sizeL);
        RadioButton rdi_sizeX = itemView.findViewById(R.id.rdi_sizeX);

        rdi_sizem.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked)
                    Common.sizeOfCup=0;
            }
        });

        rdi_sizeL.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked)
                    Common.sizeOfCup=1;
            }
        });
        rdi_sizeX.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked)
                    Common.sizeOfCup=2;
            }
        });

        RadioButton rdi_sugar_100 = itemView.findViewById(R.id.rdi_sugar_100);
        RadioButton rdi_sugar_70 = itemView.findViewById(R.id.rdi_sugar_70);
        RadioButton rdi_sugar_50 = itemView.findViewById(R.id.rdi_sugar_50);
        RadioButton rdi_sugar_30 = itemView.findViewById(R.id.rdi_sugar_30);
        RadioButton rdi_sugar_free = itemView.findViewById(R.id.rdi_sugar_free);


        rdi_sugar_free.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked)
                    Common.sugar = 0;
            }
        });
        rdi_sugar_30.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked)
                    Common.sugar = 30;
            }
        });
        rdi_sugar_50.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked)
                    Common.sugar = 50;
            }
        });
        rdi_sugar_70.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked)
                    Common.sugar = 70;
            }
        });
        rdi_sugar_100.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked)
                    Common.sugar = 100;
            }
        });

        RadioButton rdi_ice_100 = itemView.findViewById(R.id.rdi_ice_100);
        RadioButton rdi_ice_70 = itemView.findViewById(R.id.rdi_ice_70);
        RadioButton rdi_ice_50 = itemView.findViewById(R.id.rdi_ice_50);
        RadioButton rdi_ice_30 = itemView.findViewById(R.id.rdi_ice_30);
        RadioButton rdi_ice_free = itemView.findViewById(R.id.rdi_ice_free);


        rdi_ice_free.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked)
                    Common.ice =0;
            }
        });
        rdi_ice_30.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked)
                    Common.ice =30;
            }
        });
        rdi_ice_50.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked)
                    Common.ice =50;
            }
        });
        rdi_ice_70.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked)
                    Common.ice =70;
            }
        });
        rdi_ice_100.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked)
                    Common.ice =100;
            }
        });

        RecyclerView toppings_recycler = itemView.findViewById(R.id.recycler_toppings);
        toppings_recycler.setLayoutManager(new LinearLayoutManager(context));
        toppings_recycler.setHasFixedSize(true);

        MultiChoiceAdapter adapter = new MultiChoiceAdapter(context, Common.toppingsList);
        toppings_recycler.setAdapter(adapter);


        //Set data
        Picasso.with(context)
                .load(drinkList.get(position).Link)
                .into(prod_image);

        alertdialog.setView(itemView);

        alertdialog.setNegativeButton("ADD TO CART", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                if(Common.sizeOfCup ==-1){
                    Toast.makeText(context, "Please choose size of cup", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(Common.ice ==-1){
                    Toast.makeText(context, "Please choose amount of ice", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(Common.sugar ==-1){
                    Toast.makeText(context, "Please choose amount of sugar", Toast.LENGTH_SHORT).show();
                    return;
                }

                showConfirmDialog(position,txt_count.getNumber());
                dialogInterface.dismiss();
            }
        });

        alertdialog.show();
    }

    private void showConfirmDialog(final int position, final String number) {
        AlertDialog.Builder alertdialog = new AlertDialog.Builder(context);
        View itemView =LayoutInflater.from(context).inflate(R.layout.confirm_add_to_cart_layout,null);

        //view
        ImageView prod_image = itemView.findViewById(R.id.image_product);
        final TextView txt_prod_dialog = itemView.findViewById(R.id.txt_product_name);
        final TextView txt_prod_price = itemView.findViewById(R.id.txt_cart_product_price);
        TextView txt_sugar = itemView.findViewById(R.id.txt_sugar);
        TextView txt_ice = itemView.findViewById(R.id.txt_ice);
        final TextView txt_toppings = itemView.findViewById(R.id.topping_extra);


        //set data
        Picasso.with(context).load(drinkList.get(position).Link).into(prod_image);
        txt_prod_dialog.setText(new StringBuilder(drinkList.get(position).Name).append("x")
                .append(Common.sizeOfCup == 0 ? "Size M":"Size L").append(number).toString());

        txt_ice.setText(new StringBuilder("Ice: ").append(Common.ice).append("%").toString());
        txt_sugar.setText(new StringBuilder("Sugar: ").append(Common.sugar).append("%").toString());

        double price = (Double.parseDouble(drinkList.get(position).Price)*Double.parseDouble(number))+ Common.toppingsPrice;

        if(Common.sizeOfCup ==1)
            price+=100.0*(Double.parseDouble(number));




        StringBuilder topping_final_comment = new StringBuilder("");
        for(String line:Common.toppingsAdded)
            topping_final_comment.append(line).append("\n");

        txt_toppings.setText(topping_final_comment);


        final double finalPrice = Math.round(price);

        txt_prod_price.setText(new StringBuilder("Kshs.").append(finalPrice));

        alertdialog.setNegativeButton("CONFIRM", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                dialogInterface.dismiss();
                try {
                    //add to sqlite
                    //create new Cart item
                    Cart cart_item = new Cart();
                    cart_item.name = drinkList.get(position).Name;
                    cart_item.amount = Integer.parseInt(number);
                    cart_item.ice = Common.ice;
                    cart_item.sugar = Common.sugar;
                    cart_item.price = finalPrice;
                    cart_item.size = Common.sizeOfCup;
                    cart_item.toppingExtras = txt_toppings.getText().toString();
                    cart_item.link = drinkList.get(position).Link;


                    //Add to Db
                    Common.sCartRepository.insertToCart(cart_item);

                    Log.d("GITAU_DEBUG",new Gson().toJson(cart_item));

                    Toast.makeText(context, "Save item to  Successful...", Toast.LENGTH_SHORT).show();

                }
                catch (Exception ex)
                {
                    Toast.makeText(context, ex.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        });

        alertdialog.setView(itemView);
        alertdialog.show();
    }

    @Override
    public int getItemCount() {
        return drinkList.size();
    }


}
