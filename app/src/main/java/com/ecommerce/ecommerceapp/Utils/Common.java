package com.ecommerce.ecommerceapp.Utils;

import com.ecommerce.ecommerceapp.Database.DataSource.CartRepository;
import com.ecommerce.ecommerceapp.Database.DataSource.FavoritesRepository;
import com.ecommerce.ecommerceapp.Database.Local.GitausRoom;
import com.ecommerce.ecommerceapp.Model.Category;
import com.ecommerce.ecommerceapp.Model.Drink;
import com.ecommerce.ecommerceapp.Model.User;
import com.ecommerce.ecommerceapp.Retrofit.EcommerceApi;
import com.ecommerce.ecommerceapp.Retrofit.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

public class Common {
//    For emulator ,localhost = 10.0.2.2
//    ip address , localst = 192.168.237.2,192.168.43.183,192.168.56.1
    public static final String BASE_URL = "http://192.168.9.101/ecommerce/";

    public static final String TOPPING_MENU_ID="7";


    public static User currentUser = null;
    public static Category currentCategory= null;

    public static List<Drink> toppingsList = new ArrayList<>();

    public static double toppingsPrice = 0.0;
    public static List<String> toppingsAdded = new ArrayList<>();

    //Hold field
    public static int sizeOfCup = -1; //-1 : throws error wen nothing is chosen, O : M ,1 : L
    public static int sugar = -1; // -1 : throws error wen nothing is chosen
    public static int ice =-1;


    //Database
    public static FavoritesRepository sFavoritesRepository;
    public static CartRepository sCartRepository;
    public static GitausRoom sGitausRoom;

    public static EcommerceApi getApi()
    {
        return RetrofitClient.getClient(BASE_URL).create(EcommerceApi.class);

    }

}
