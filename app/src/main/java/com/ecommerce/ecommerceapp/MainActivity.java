package com.ecommerce.ecommerceapp;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

import com.ecommerce.ecommerceapp.Model.CheckUserResponse;
import com.ecommerce.ecommerceapp.Model.User;
import com.ecommerce.ecommerceapp.Retrofit.EcommerceApi;
import com.ecommerce.ecommerceapp.Utils.Common;
import com.facebook.accountkit.Account;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitCallback;
import com.facebook.accountkit.AccountKitError;
import com.facebook.accountkit.AccountKitLoginResult;
import com.facebook.accountkit.ui.AccountKitActivity;
import com.facebook.accountkit.ui.AccountKitConfiguration;
import com.facebook.accountkit.ui.LoginType;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.szagurskii.patternedtextwatcher.PatternedTextWatcher;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 1000;
    private static final int REQUEST_PERMISSION =9087 ;
    Button btnContinue;
    EcommerceApi mService;

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

        btnContinue = findViewById(R.id.btn_continue);
        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startLoginPage(LoginType.PHONE);
            }
        });

        //check session
        if(AccountKit.getCurrentAccessToken() !=null)
        {
            final AlertDialog dialog = new ProgressDialog(MainActivity.this);
            dialog.show();
            dialog.setMessage("Please Wait....");
            //Auto Login
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
                                        //fetch info

                                        mService.getUserInformation(account.getPhoneNumber().toString())
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

                                        showRegisterDialog(account.getPhoneNumber().toString());
                                    }
                                }

                                @Override
                                public void onFailure(Call<CheckUserResponse> call, Throwable t) {

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

    private void startLoginPage(LoginType loginType) {
        Intent intent = new Intent(this, AccountKitActivity.class);
        AccountKitConfiguration.AccountKitConfigurationBuilder builder=
                new AccountKitConfiguration.AccountKitConfigurationBuilder(loginType,AccountKitActivity.ResponseType.TOKEN);
        intent.putExtra(AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION,builder.build());
        startActivityForResult(intent,REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode ==REQUEST_CODE)
        {
            AccountKitLoginResult result = data.getParcelableExtra(AccountKitLoginResult.RESULT_KEY);

            if(result.getError() !=null)
            {
                Toast.makeText(this, ""+result.getError().getErrorType().getMessage(), Toast.LENGTH_SHORT).show();
            }
            else if(result.wasCancelled())
            {
                Toast.makeText(this, "Cancel", Toast.LENGTH_SHORT).show();
            }
            else{
                if(result.getAccessToken() != null)
                {
                    final AlertDialog dialog = new ProgressDialog(MainActivity.this);
                    dialog.show();
                    dialog.setMessage("Please Wait....");


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
                                                    //fetch info

                                                    mService.getUserInformation(account.getPhoneNumber().toString())
                                                            .enqueue(new Callback<User>() {
                                                                @Override
                                                                public void onResponse(Call<User> call, Response<User> response) {
                                                                    //if user exists just start a new activity
                                                                    dialog.dismiss();

                                                                    //fix  for first time login crash
                                                                    Common.currentUser = response.body();

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

                                                    showRegisterDialog(account.getPhoneNumber().toString());
                                                }
                                            }

                                            @Override
                                            public void onFailure(Call<CheckUserResponse> call, Throwable t) {

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
                } if(TextUtils.isEmpty(edtbirth.getText().toString()))
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

                mService.registerNewUser(phone,edtName.getText().toString(),edtaddress.getText().toString(),edtbirth.getText().toString())
                        .enqueue(new Callback<User>() {
                            @Override
                            public void onResponse(Call<User> call, Response<User> response) {
                                waitingdialog.dismiss();
                                User user = response.body();

                                if(TextUtils.isEmpty(user.getError_msg()))
                                {
                                    Toast.makeText(MainActivity.this, "User Registered Successfully!!....", Toast.LENGTH_SHORT).show();

                                    Common.currentUser= response.body();
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

    private void printKeyHash() {
        try{
            PackageInfo info = getPackageManager().getPackageInfo("com.ecommerce.ecommerceapp", PackageManager.GET_SIGNATURES);
            for(Signature signature:info.signatures)
            {
                MessageDigest md= MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KEYHASH", Base64.encodeToString(md.digest(),Base64.DEFAULT));
            }

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
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
