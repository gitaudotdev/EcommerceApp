package com.ecommerce.ecommerceapp;

import android.Manifest;
import android.accounts.Account;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.ecommerce.ecommerceapp.Model.CheckUserResponse;
import com.ecommerce.ecommerceapp.Model.User;
import com.ecommerce.ecommerceapp.Retrofit.EcommerceApi;
import com.ecommerce.ecommerceapp.Utils.Common;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.szagurskii.patternedtextwatcher.PatternedTextWatcher;


import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 1000;
    private static final int REQUEST_PERMISSION =9087 ;
    Button btnContinue;
    EcommerceApi mService; //change and use firebase


    FirebaseAuth firebaseAuth;
    FirebaseAuth.AuthStateListener listener;
    List<AuthUI.IdpConfig> providers;

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(listener);
    }

    @Override
    protected void onStop() {
        if(listener != null)
            firebaseAuth.removeAuthStateListener(listener);
        super.onStop();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode)
        {
            case REQUEST_PERMISSION:
            {
                if(grantResults.length > 0 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
                    Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
            break;
            default:
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this,new String[]{
                            Manifest.permission.READ_EXTERNAL_STORAGE
                    },REQUEST_PERMISSION
            );

        mService = Common.getApi();

        firebaseAuth = FirebaseAuth.getInstance();
        providers = Arrays.asList(new AuthUI.IdpConfig.PhoneBuilder().build());

        listener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user != null){
                    final AlertDialog dialog = new ProgressDialog(MainActivity.this);
                    dialog.show();
                    dialog.setMessage("Please Wait....");
                    dialog.setCancelable(false);

                    mService.checkUserExists(user.getPhoneNumber())
                            .enqueue(new Callback<CheckUserResponse>() {
                                @Override
                                public void onResponse(Call<CheckUserResponse> call, Response<CheckUserResponse> response) {
                                    CheckUserResponse userResponse = response.body();
                                    if(userResponse.isExists())
                                    {
                                        //fetch info

                                        mService.getUserInformation(user.getPhoneNumber())
                                                .enqueue(new Callback<User>() {
                                                    @Override
                                                    public void onResponse(Call<User> call, Response<User> response) {
                                                        //if user exists just start a new activity
                                                        dialog.dismiss();

                                                        Common.currentUser= response.body();


                                                        startActivity(new Intent(MainActivity.this,HomeActivity.class));
                                                        finish();
                                                    }

                                                    @Override
                                                    public void onFailure(Call<User> call, Throwable t) {
                                                        Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                                                    }
                                                });

                                    }
                                    else
                                    {
                                        //else we perform registration
                                        dialog.dismiss();

                                        showRegisterDialog(user.getPhoneNumber());
                                    }
                                }

                                @Override
                                public void onFailure(Call<CheckUserResponse> call, Throwable t) {

                                }
                            });
                }
            }
        };

        btnContinue = findViewById(R.id.btn_continue);
        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startLoginPage();
            }
        });



    }

    private void startLoginPage() {

        startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder()
                .setAvailableProviders(providers).build(),REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode ==REQUEST_CODE)
        {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if(resultCode == RESULT_OK){
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            }else {
                Toast.makeText(this, "Failed to Sign In", Toast.LENGTH_SHORT).show();
            }


        }
    }

    private void showRegisterDialog(final String phone) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("REGISTER");


        LayoutInflater inflater =this.getLayoutInflater();
        View register_layout = inflater.inflate(R.layout.register_layout,null);

        final MaterialEditText edtName = register_layout.findViewById(R.id.edtName);
        final MaterialEditText edtbirth = register_layout.findViewById(R.id.edtbirthdate);
        final MaterialEditText edtaddress = register_layout.findViewById(R.id.edtaddress);

        Button register  = register_layout.findViewById(R.id.btn_register);

        edtbirth.addTextChangedListener(new PatternedTextWatcher("####-##-##"));

        builder.setView(register_layout);
        final AlertDialog dialog = builder.create();

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.dismiss();


                if(TextUtils.isEmpty(edtName.getText().toString()))
                {
                    Toast.makeText(MainActivity.this, "Please Enter your Name ", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(edtbirth.getText().toString()))
                {
                    Toast.makeText(MainActivity.this, "Please Enter your Date of Birth ", Toast.LENGTH_SHORT).show();
                    return;
                } if(TextUtils.isEmpty(edtaddress.getText().toString()))
                {
                    Toast.makeText(MainActivity.this, "Please Enter your address ", Toast.LENGTH_SHORT).show();
                    return;
                }
                final AlertDialog waitingdialog = new ProgressDialog(MainActivity.this);
                waitingdialog.show();
                waitingdialog.setMessage("Please wait....");
                waitingdialog.setCancelable(false);

                mService.registerNewUser(phone,edtName.getText().toString(),
                        edtaddress.getText().toString(),
                        edtbirth.getText().toString())
                        .enqueue(new Callback<User>() {
                            @Override
                            public void onResponse(Call<User> call, Response<User> response) {
                                waitingdialog.dismiss();
                                User user = response.body();

                                if(TextUtils.isEmpty(user.getError_msg()))
                                {
                                    Toast.makeText(MainActivity.this, "User Registered Successfully!!....", Toast.LENGTH_SHORT).show();

                                    Common.currentUser= response.body();
                                    Log.d("user response", String.valueOf(response.body()));
                                    //start new Activity
                                    startActivity(new Intent(MainActivity.this,HomeActivity.class));
                                    finish();
                                }

                            }

                            @Override
                            public void onFailure(Call<User> call, Throwable t) {
                                waitingdialog.dismiss();

                            }
                        });
            }
        });

        dialog.show();

    }


    //Exit application when we click back button
    boolean isBackButtonClicked = false;

    //Ctrl+O
    @Override
    public void onBackPressed() {
        if(isBackButtonClicked){
            super.onBackPressed();
            return;
        }
        this.isBackButtonClicked= true;
        Toast.makeText(this, "Click BACK again to Exit", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        isBackButtonClicked= false;
        super.onResume();
    }
}
