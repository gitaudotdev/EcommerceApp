package com.ecommerce.ecommerceapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.multidex.MultiDex;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.ecommerce.ecommerceapp.Adapters.CategoryAdapter;
import com.ecommerce.ecommerceapp.Database.DataSource.CartRepository;
import com.ecommerce.ecommerceapp.Database.DataSource.FavoritesRepository;
import com.ecommerce.ecommerceapp.Database.Local.CartDataSource;
import com.ecommerce.ecommerceapp.Database.Local.FavoritesDataSource;
import com.ecommerce.ecommerceapp.Database.Local.GitausRoom;
import com.ecommerce.ecommerceapp.Model.Banner;
import com.ecommerce.ecommerceapp.Model.Category;
import com.ecommerce.ecommerceapp.Model.CheckUserResponse;
import com.ecommerce.ecommerceapp.Model.Drink;
import com.ecommerce.ecommerceapp.Model.User;
import com.ecommerce.ecommerceapp.Retrofit.EcommerceApi;
import com.ecommerce.ecommerceapp.Utils.Common;
import com.ecommerce.ecommerceapp.Utils.ProgressRequestBody;
import com.ecommerce.ecommerceapp.Utils.UploadCallBack;
import com.facebook.accountkit.Account;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitCallback;
import com.facebook.accountkit.AccountKitError;
import com.ipaulpro.afilechooser.utils.FileUtils;
import com.nex3z.notificationbadge.NotificationBadge;
import com.squareup.picasso.Picasso;


import java.io.File;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,UploadCallBack {

    private static final int PICK_FILE_REQUEST_CODE =9999 ;
    TextView tvName,tvPhone;

    SliderLayout sliderLayout;


    EcommerceApi mService;

    RecyclerView menu_list;
    CategoryAdapter adapter;

    NotificationBadge badge;
    ImageView cart_icon;

    CircleImageView avatar;

    Uri selectedFileUri;

    //rxjava
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    protected void attachBaseContext(Context base)
    {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sliderLayout =(SliderLayout)findViewById(R.id.slider);

        mService = Common.getApi();

        menu_list = findViewById(R.id.list_menu);
        menu_list.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
        menu_list.setHasFixedSize(true);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        View headerview = navigationView.getHeaderView(0);
        tvName = headerview.findViewById(R.id.tvName);
        tvPhone= headerview.findViewById(R.id.tvPhone);
        avatar = headerview.findViewById(R.id.avatar_image);


        //Event
        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Common.currentUser !=null)
                     chooseImage();
            }
        });

        if(Common.currentUser !=null) // when we are not logged in current user is obviously null
        {

            //setInfo
            tvName.setText(Common.currentUser.getName());
            tvPhone.setText(Common.currentUser.getPhone());

            //setAvatar image
            if (!TextUtils.isEmpty(Common.currentUser.getAvatarUrl())) {
                Picasso.with(this)
                        .load(new StringBuilder(Common.BASE_URL)
                                .append("user_avatar/")
                                .append(Common.currentUser.getAvatarUrl()).toString())
                        .into(avatar);
            }
        }

        //Get Banner
        getBannerImage();

        //Get Menu
        getMenu();

        //save newest toppings list
        getToppingList();


        //Init Database
        initDb();

        checkSessionLogin(); // if user was already logged , juhs login again since session was already live

    }

    private void checkSessionLogin() {
        if(AccountKit.getCurrentAccessToken() !=null)
        {
            final AlertDialog dialog = new ProgressDialog(this);
            dialog.show();
            dialog.setMessage("Please Wait....");

            //check if user exists on server(MySQL)
            AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {
                @Override
                public void onSuccess(final Account account) {
                    mService.checkUserExists(account.getPhoneNumber().toString())
                            .enqueue(new Callback<CheckUserResponse>() {
                                @Override
                                public void onResponse(Call<CheckUserResponse> call, Response<CheckUserResponse> response) {
                                    CheckUserResponse userResponse = response.body();
                                    if(userResponse.isExists())
                                    {
                                        //Request Information of Current User
                                        mService.getUserInformation(account.getPhoneNumber().toString())
                                                .enqueue(new Callback<User>() {
                                                    @Override
                                                    public void onResponse(Call<User> call, Response<User> response) {
                                                        Common.currentUser = response.body();
                                                        if(Common.currentUser !=null)
                                                            dialog.dismiss();
                                                    }

                                                    @Override
                                                    public void onFailure(Call<User> call, Throwable t) {
                                                        dialog.dismiss();
                                                        Log.d("ERROR",t.getMessage());

                                                    }
                                                });
                                    }else
                                    {
                                        //if user doesnt exist on database we make them login
                                        startActivity(new Intent(HomeActivity.this,MainActivity.class));
                                        finish();
                                    }
                                }

                                @Override
                                public void onFailure(Call<CheckUserResponse> call, Throwable t) {
                                    Log.d("ERROR",t.getMessage());
                                }
                            });
                }

                @Override
                public void onError(AccountKitError accountKitError) {
                    Log.d("ERROR",accountKitError.getErrorType().getMessage());
                }
            });

        }
    }

    private void chooseImage() {
        startActivityForResult(Intent.createChooser(FileUtils.createGetContentIntent(),"Select a File"),PICK_FILE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK)
        {
            if(requestCode == PICK_FILE_REQUEST_CODE)
            {
                if(data != null)
                {
                    selectedFileUri = data.getData();
                    if(selectedFileUri !=null && !selectedFileUri.getPath().isEmpty())
                    {
                        avatar.setImageURI(selectedFileUri);
                        uploadFile();
                    }
                    else
                        Toast.makeText(this, "Cannot Upload File to Server", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void uploadFile() {
     if(selectedFileUri !=null)
     {
         File file = FileUtils.getFile(this,selectedFileUri);

         String fileName = new StringBuilder(Common.currentUser.getPhone())
                 .append(FileUtils.getExtension(file.toString()))
                 .toString();

         ProgressRequestBody requestFile = new ProgressRequestBody(file,this);

         final MultipartBody.Part body = MultipartBody.Part.createFormData("uploaded_file",fileName,requestFile);

         final MultipartBody.Part userPhone = MultipartBody.Part.createFormData("phone",Common.currentUser.getPhone());


         new Thread(new Runnable() {
             @Override
             public void run() {
                 mService.uploadAvatar(userPhone,body)
                         .enqueue(new Callback<String>() {
                             @Override
                             public void onResponse(Call<String> call, Response<String> response) {
                                 Toast.makeText(HomeActivity.this, response.body(), Toast.LENGTH_SHORT).show();
                             }

                             @Override
                             public void onFailure(Call<String> call, Throwable t) {
                                 Toast.makeText(HomeActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                             }
                         });
             }
         }).start();
     }
    }

    private void initDb() {
        Common.sGitausRoom = GitausRoom.getInstance(this);
        Common.sCartRepository = CartRepository.getInstance(CartDataSource.getInstance(Common.sGitausRoom.mCartDAO()));
        Common.sFavoritesRepository = FavoritesRepository.getInstance(FavoritesDataSource.getInstance(Common.sGitausRoom.mFavoritesDao()));
    }

    private void getToppingList() {
        compositeDisposable.add(mService.getDrink(Common.TOPPING_MENU_ID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Drink>>() {
                    @Override
                    public void accept(List<Drink> drinks) throws Exception {
                        Common.toppingsList = drinks;
                    }
                }));
    }

    private void getMenu() {
        compositeDisposable.add(mService.getMenu()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Category>>() {
                    @Override
                    public void accept(List<Category> categories) throws Exception {
                            displayMenu(categories);
                    }
                }));
    }

    private void displayMenu(List<Category> categories) {
        adapter = new CategoryAdapter(this,categories);
        menu_list.setAdapter(adapter);
    }

    private void getBannerImage() {
        compositeDisposable.add(mService.getBanners()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Consumer<List<Banner>>() {
            @Override
            public void accept(List<Banner> banners) throws Exception {
                displayImage(banners);
            }
        }));
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.dispose();
        super.onDestroy();
    }

    private void displayImage(List<Banner> banners) {
        HashMap<String,String> bannerMap = new HashMap<>();
        for(Banner item:banners)
            bannerMap.put(item.getName(),item.getLink());

        for(String name:bannerMap.keySet())
        {
            TextSliderView textSliderView = new TextSliderView(HomeActivity.this);
            textSliderView.description(name)
                    .image(bannerMap.get(name))
                    .setScaleType(BaseSliderView.ScaleType.Fit);

            sliderLayout.addSlider(textSliderView);
        }
    }

    //Exit application when we click back button
    boolean isBackButtonClicked = false;

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if(isBackButtonClicked){
                super.onBackPressed();
                return;
            }
            this.isBackButtonClicked= true;
            Toast.makeText(this, "Your On the Home Page Click BACK again to Exit", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_action_bar, menu);
        View itemview = menu.findItem(R.id.cart_menu).getActionView();
        badge = (NotificationBadge) itemview.findViewById(R.id.badge);
        cart_icon = itemview.findViewById(R.id.cart_icon);
        cart_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this,CartActivity.class));
            }
        });
        updateCartCount();
        return true;
    }

    private void updateCartCount() {
        if(badge==null) return;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(Common.sCartRepository.countCartItems() == 0)
                    badge.setVisibility(View.INVISIBLE);
                else {
                    badge.setVisibility(View.VISIBLE);
                    badge.setText(String.valueOf(Common.sCartRepository.countCartItems()));
                }
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.cart_menu) {
            return true;
        }else if(id==R.id.cart_search){
            startActivity(new Intent(this,SearchActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_log_out) {
            // Create Confirm Dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("EXIT APPLICATION");
            builder.setMessage("ARE YOU SURE YOU WANNA EXIT THIS APP");

            builder.setNegativeButton("YES", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    AccountKit.logOut();

                    //Clear all activity
                    Intent intent = new Intent(HomeActivity.this,MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }
            });
            builder.setPositiveButton("CANCEL", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });

            builder.show();
        }else if(id == R.id.nav_favorites){
            startActivity(new Intent(HomeActivity.this,FavoritesListActivity.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateCartCount();
        isBackButtonClicked = false;
    }


    @Override
    public void onProgressUpdate(int percentage) {

    }
}
