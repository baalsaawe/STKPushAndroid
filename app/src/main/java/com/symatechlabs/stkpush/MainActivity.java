package com.symatechlabs.stkpush;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


//======================================================================
// SAFARICOM STK PUSH ANDROID APP
//======================================================================
//-----------------------------------------------------
// Author   : Brian Osoro
// Company  : Symatech Labs Ltd
// Website  : www.symatechlabs.com
// Blog     : www.brianosoro.com
// Twitter  : @brayanosoro
// Email    : info@brianosoro.com / brianosoroinc@gmail.com
//-----------------------------------------------------



public class MainActivity extends AppCompatActivity {

    EditText phone, amount;
    Button submit;
    public ConnectivityManager conMgr;
    public NetworkInfo netInfo;
    int PERMISSION_ALL = 1;
    private static final String BASE_URL = "http://localhost/stkpush/sendSTKPush.php";
    private OkHttpClient client = new OkHttpClient();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        askPermissions();

        phone = (EditText) findViewById(R.id.phone);
        amount = (EditText) findViewById(R.id.amount);
        submit = (Button) findViewById(R.id.submit);


        submit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                if (checkConnectivity()) {

                    new sendSTKPush().execute();

                } else {

                    Toast.makeText(MainActivity.this, "No Internet Connection", Toast.LENGTH_LONG).show();

                }

            }
        });

    }


    public boolean checkConnectivity() {

        this.conMgr = (ConnectivityManager) MainActivity.this
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        this.netInfo = this.conMgr.getActiveNetworkInfo();

        if (this.netInfo != null && this.netInfo.isConnectedOrConnecting() && this.netInfo.isAvailable()) {
            return true;
        } else {

            return false;
        }

    }


    public static boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }


    public void askPermissions() {

        String[] PERMISSIONS = {Manifest.permission.INTERNET, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.BLUETOOTH_PRIVILEGED, Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN};

        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }

    }


    class sendSTKPush extends AsyncTask<Void, Void, Void> {

        ProgressDialog progressDialog;
        String response_;
        JSONObject jsonObject;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("Working...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            FormBody.Builder formBuilder = new FormBody.Builder().add("phone", phone.getText().toString().trim()).add("amount", amount.getText().toString().trim());

            RequestBody formBody = formBuilder.build();
            Response response = null;
            Request request = null;

            try {

                request = new Request.Builder()
                        .url(BASE_URL)
                        .post(formBody)
                        .build();
            } catch (Exception e) {

            }


            try {
                response = client.newCall(request).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                jsonObject = new JSONObject(response.body().string());
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressDialog.dismiss();
            if (jsonObject != null) {
                try {

                    if (jsonObject.get("ResponseCode").toString().equalsIgnoreCase("0")) {
                        Toast.makeText(MainActivity.this, "SUCCESS", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(MainActivity.this, "ERROR", Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    Toast.makeText(MainActivity.this, "ERROR", Toast.LENGTH_LONG).show();
                }
            } else {

                Toast.makeText(MainActivity.this, "ERROR", Toast.LENGTH_LONG).show();

            }


        }
    }

}
