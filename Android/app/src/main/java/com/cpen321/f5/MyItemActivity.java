package com.cpen321.f5;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

public class MyItemActivity extends AppCompatActivity
{
    private static final String TAG = "MyItemActivity";

    Button removeButton;

    RequestQueue requestQueue;

    TextView _itemName;
    TextView _itemPrice;
    TextView _itemCategory;
    TextView _itemDescription;
    TextView _itemLocation;
    TextView _itemNumber;

    String itemName;
    String itemPrice;
    String itemCategory;
    String itemDescription;
    String itemLocation;
    String itemNumber;

    String itemID = MyItemListActivity.MyItemID;

    String GETITEMURL = "http://20.106.78.177:8081/item/getbyid/" + itemID + "/";
    String REMOVEITEMURL = "http://20.106.78.177:8081/item/removeitem/" + itemID;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_item);

        requestQueue = Volley.newRequestQueue(this);

        Log.d(TAG, GETITEMURL);
        GETITEM();

        removeButton = findViewById(R.id.remove_button);
        removeButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                REMOVEITEM();

                Intent removeIntent = new Intent(MyItemActivity.this, MainUI.class);
                startActivity(removeIntent);
            }
        });
    }

    private void GETITEM ()
    {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, GETITEMURL, null, new Response.Listener<JSONObject>()
        {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(JSONObject response)
            {
                Log.d(TAG, "attribute = " + response.toString());

                try
                {
                    itemName = response.getString("name");
                    itemPrice = response.getString("currentPrice");
                    itemCategory = response.getString("name");
                    itemLocation = response.getString("location");
                    itemNumber = response.getString("ItemID");
                    itemDescription = response.getString("description");


                    _itemName = findViewById(R.id.item_name_caption);
                    _itemName.setText(itemName);

                    _itemPrice = findViewById(R.id.item_price_caption);
                    _itemPrice.setText("$ " + itemPrice);

                    _itemCategory = findViewById(R.id.item_category_caption);
                    _itemCategory.setText(itemCategory);

                    _itemLocation = findViewById(R.id.item_location_caption);
                    _itemLocation.setText("Item Location: " + itemLocation);

                    _itemNumber = findViewById(R.id.item_id_caption);
                    _itemNumber.setText("Item ID: " + itemNumber);

                    _itemDescription = findViewById(R.id.item_description_caption);
                    _itemDescription.setText(itemDescription);

                    Toast.makeText(MyItemActivity.this, "CREDENTIALS RETRIEVED", Toast.LENGTH_LONG).show();
                }
                catch (Exception w)
                {
                    Toast.makeText(MyItemActivity.this,w.getMessage(),Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                Toast.makeText(MyItemActivity.this,error.getMessage(),Toast.LENGTH_LONG).show();
            }
        });

        requestQueue.add(jsonObjectRequest);
    }

    private void REMOVEITEM()
    {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.DELETE, REMOVEITEMURL, null, new Response.Listener<JSONObject>()
        {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(JSONObject response)
            {
                Log.d(TAG, "attribute = " + response.toString());

                try
                {
                    Toast.makeText(MyItemActivity.this, "ITEM REMOVED", Toast.LENGTH_LONG).show();
                }
                catch (Exception w)
                {
                    Toast.makeText(MyItemActivity.this,w.getMessage(),Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                Toast.makeText(MyItemActivity.this,error.getMessage(),Toast.LENGTH_LONG).show();
            }
        });

        requestQueue.add(jsonObjectRequest);
    }
}