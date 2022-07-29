package com.cpen321.f5;


import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.Formatter;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class PostActivity extends AppCompatActivity implements LocationListener{
    String title;
    String description;
    String location;
    String startPrice;
    String deposit;
    String stepPrice;
    String timeLast;
    String postTime;
    String timeExpire;

    String[] uploadedImages = new String[5];

    public static double lat;
    public static double lon;

    private final String TAG = "PostActivity";

    private TextView showLocation;

    LocationManager locationManager;

    JSONObject jsonObject = new JSONObject();


    private View imageButton;
    private View cameraButton;


    private int imgCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_items);



        showLocation = findViewById(R.id.show_location);



        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, this);


        Button postButton = findViewById(R.id.post_post);
        postButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                title = ((EditText)findViewById(R.id.post_title)).getText().toString().trim();
                description = ((EditText)findViewById(R.id.post_description)).getText().toString().trim();
                location = ((TextView)findViewById(R.id.post_location)).getText().toString().trim();
                startPrice = ((EditText)findViewById(R.id.post_start_price)).getText().toString().trim();
                deposit = ((EditText)findViewById(R.id.post_deposit)).getText().toString().trim();
                stepPrice = ((EditText)findViewById(R.id.post_step_price)).getText().toString().trim();
                timeLast = ((EditText)findViewById(R.id.post_time_last)).getText().toString().trim();
                postTime = getTime();



                Log.d(TAG, "title = " + title);
                Log.d(TAG, "description = " + description);
                Log.d(TAG, "startP = " + startPrice);
                Log.d(TAG, "stepP = " + stepPrice);
                Log.d(TAG, "deposit = " + deposit);
                Log.d(TAG, "how lone = " + timeLast);
                Log.d(TAG, "post time = " + postTime);
                Log.d(TAG, "image_0 = " + uploadedImages[0]);
                Log.d(TAG, "image_4 = " + uploadedImages[4]);

                if (validCheck()){
                    long currentTime = Instant.now().toEpochMilli() / 1000;
                    long expireTime = currentTime + Integer.parseInt(timeLast) * 3600;
                    timeExpire = Long.toString(expireTime);
                    Log.d(TAG, "expire time = " + timeExpire);
                    postDataToServer();
                    Intent MainUI = new Intent(PostActivity.this, MainUI.class);
                    startActivity(MainUI);
                }
            }
        });

        Button cancelButton = findViewById(R.id.post_cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent MainUI = new Intent(PostActivity.this, MainUI.class);
                startActivity(MainUI);
            }
        });

        imageButton = findViewById(R.id.image_add_button);
        imageButton.setOnClickListener(v ->{
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            requestForAlbum.launch(intent);
            Toast.makeText(PostActivity.this, "Trying to open album", Toast.LENGTH_SHORT).show();
        });

        cameraButton = findViewById(R.id.post_camera_button);

    }


    private void postDataToServer(){
        String url = getString(R.string.url_post);
        RequestQueue queue = Volley.newRequestQueue(PostActivity.this);
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Toast.makeText(PostActivity.this, "Data added to API", Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Toast.makeText(PostActivity.this, "Fail to get response = " + error, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();

                params.put("sellerID", MainActivity.idOfUser);
                params.put("name", title);
                params.put("description", description);

                params.put("location_lat", Double.toString(lat));
                params.put("location_lon", Double.toString(lon));

                params.put("startPrice", startPrice);
                params.put("deposit", deposit);
                params.put("stepPrice", stepPrice);
                params.put("postTime", postTime);
                params.put("timeLast", timeLast);
                params.put("timeExpire", timeExpire);

                params.put("currentPriceHolder", "no one bid yet");
                params.put("currentPrice", startPrice);
                params.put("needAdmin", "false");
                params.put("refundDescrition", "");
                params.put("expired", "false");
                params.put("adminResponse", "Waiting For Admin To Resolve Dispute!");

                params.put("image_0", uploadedImages[0]);
                params.put("image_1", uploadedImages[1]);
                params.put("image_2", uploadedImages[2]);
                params.put("image_3", uploadedImages[3]);
                params.put("image_4", uploadedImages[4]);

                return params;
            }
        };
        queue.add(postRequest);
    }

    private boolean validCheck(){
        if (startPrice.equals("")){
            Toast.makeText(PostActivity.this, "Fail, Start Price is not set yet", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (deposit.equals("")){
            Toast.makeText(PostActivity.this, "Fail, Deposit is not set yet", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (stepPrice.equals("")){
            Toast.makeText(PostActivity.this, "Fail, Step Price is not set yet", Toast.LENGTH_SHORT).show();
            return false;
        }


        if (timeLast.equals("")){
            Toast.makeText(PostActivity.this, "Fail, the post last hour is not set yet", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (Integer.parseInt(timeLast) > 168){
            Toast.makeText(PostActivity.this, "Fail, the post should last less than 7 days", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private String getTime(){
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        return formatter.format(date);
    }


    @Override
    public void onLocationChanged(@NonNull Location location) {
        Log.d(TAG, "Lat: " + location.getLatitude() + " | Long: " + location.getLongitude());
        lat = location.getLatitude();
        lon = location.getLongitude();

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lon, 1);
            //Log.d(TAG, "debug sign: " + addresses.size() + "---" + lat + "---" + lon);
            String cityName = addresses.get(0).getLocality();
            Log.d(TAG, "*************city name: " + cityName);

            Formatter formatter = new Formatter();
            formatter.format("%.5f", lat);
            formatter.format("%.5f", lon);
            showLocation.setText("Lat: " + location.getLatitude() + "\n" + " | Long: " + location.getLongitude());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLocationChanged(@NonNull List<Location> locations) {
        LocationListener.super.onLocationChanged(locations);
    }

    private final ActivityResultLauncher<Intent> requestForAlbum =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result){
                    if(result.getData() != null && result.getResultCode() == RESULT_OK){
                        try{
                            InputStream Is = getContentResolver().openInputStream(result.getData().getData());
                            Bitmap image = BitmapFactory.decodeStream(Is);



                            Toast.makeText(PostActivity.this, "work", Toast.LENGTH_SHORT).show();
                            uploadedImages[imgCount] = sendImage(image);
                            displayImage(image);
                        }catch (FileNotFoundException error){
                            Toast.makeText(PostActivity.this, "error", Toast.LENGTH_SHORT).show();
                            error.printStackTrace();
                        }
                    }
                }
            });


    private String sendImage(Bitmap image) {
        ByteArrayOutputStream Os = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG,50, Os);
        String b64String = Base64.encodeToString(Os.toByteArray(), Base64.DEFAULT);

        return b64String;
    }



    private void displayImage(Bitmap myBitmap){
        ImageView img = findViewById(R.id.post_item_img0 + imgCount);
        img.setImageBitmap(myBitmap);
        imgCount++;

        if (imgCount == 5){
            imageButton.setVisibility(View.INVISIBLE);
            cameraButton.setVisibility(View.INVISIBLE);
        }
    }

}