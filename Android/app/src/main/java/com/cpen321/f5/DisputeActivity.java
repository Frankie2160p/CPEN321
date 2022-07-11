package com.cpen321.f5;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class DisputeActivity extends AppCompatActivity
{
    private static final String TAG = "DisputeActivity";

    private Button submitButton;
    private Button checkButton;

    RequestQueue requestQueue;


    EditText _orderItemID;
    EditText _reason;
    EditText _refund;
    EditText _admin;

    String orderItemID;
    String reason;
    String refund;
    String admin;

    public static String adminConclusion;
    public static String ifDisputed;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dispute);

        requestQueue = Volley.newRequestQueue(this);

        submitButton = findViewById(R.id.submit_button);
        submitButton.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                _orderItemID = findViewById(R.id.order_caption);
                _reason = findViewById(R.id.reason_caption);
                _refund = findViewById(R.id.refund_caption);
                _admin = findViewById(R.id.admin_caption);

                orderItemID = _orderItemID.getText().toString();
                reason = _reason.getText().toString();
                refund = _refund.getText().toString();
                admin = _admin.getText().toString();

                Log.d(TAG, "ID = " + orderItemID);
                Log.d(TAG, "REASON = " + reason);
                Log.d(TAG, "REFUND = " + refund);
                Log.d(TAG, "ADMIN = " + admin);

                //missing error checking pop out error box
                if (validCheck())
                {
                    GETDISPUTEUPDATE();
                }
            }
        });

        checkButton = findViewById(R.id.check_button);
        checkButton.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                _orderItemID = findViewById(R.id.order_caption);
                orderItemID = _orderItemID.getText().toString();
                Log.d(TAG, "ID = " + orderItemID);

                if (validCheck1())
                {
                    GETDISPUTEADMIN();
                }
            }
        });
    }

    private void GETDISPUTEUPDATE ()
    {
        String DISPUTEUPDATEURL = "http://20.106.78.177:8081/item/updateitem/";

        JSONObject jsonObject = new JSONObject();
        RequestQueue queue = Volley.newRequestQueue(DisputeActivity.this);

        StringRequest postRequest = new StringRequest(Request.Method.PUT, DISPUTEUPDATEURL,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response)
                    {
                        Toast.makeText(DisputeActivity.this, "DATA SEND TO DB", Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        Toast.makeText(DisputeActivity.this, "FAILED TO SEND DATA: " + error, Toast.LENGTH_SHORT).show();
                    }
                })

        {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();

                params.put("ItemID", orderItemID);
                params.put("refundDescription", reason);
                params.put("refund", refund);
                params.put("needAdmin", admin);

                return params;
            }
        };
        queue.add(postRequest);
    }

    private boolean validCheck()
    {
        if (orderItemID.equals(""))
        {
            Toast.makeText(DisputeActivity.this, "ID Cannot Be Empty", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (reason.equals(""))
        {
            Toast.makeText(DisputeActivity.this, "Reason Cannot Be Empty", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (refund.equals(""))
        {
            Toast.makeText(DisputeActivity.this, "Refund Needed Cannot Be Empty", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (admin.equals(""))
        {
            Toast.makeText(DisputeActivity.this, "Admin Needed Cannot Be Empty", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!refund.equals("false") && !refund.equals("true"))
        {
            Toast.makeText(DisputeActivity.this, "Must Enter ture or false for Refund Needed", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!admin.equals("false") && !admin.equals("true"))
        {
            Toast.makeText(DisputeActivity.this, "Must Enter ture or false for Admin Needed", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private boolean validCheck1()
    {
        if (orderItemID.equals(""))
        {
            Toast.makeText(DisputeActivity.this, "ID Cannot Be Empty", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void GETDISPUTEADMIN ()
    {
        String GETDISPUTEADMINURL = "http://20.106.78.177:8081/item/getbyid/" + orderItemID + "/";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, GETDISPUTEADMINURL, null, new Response.Listener<JSONObject>()
        {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(JSONObject response)
            {
                Log.d(TAG, "attribute = " + response.toString());

                try
                {
                    adminConclusion = response.getString("adminResponse");
                    ifDisputed = response.getString("needAdmin");

                    if (ifDisputed.equals("false"))
                    {
                        Toast.makeText(DisputeActivity.this, "DISPUTE NOT YET FILED FOR THIS ITEM", Toast.LENGTH_LONG).show();
                    }

                    else
                    {
                        Toast.makeText(DisputeActivity.this, "CREDENTIALS RETRIEVED", Toast.LENGTH_LONG).show();

                        Intent disputeCheckIntent = new Intent(DisputeActivity.this, DisputeCheckActivity.class);
                        startActivity(disputeCheckIntent);
                    }
                }
                catch (Exception w)
                {
                    Toast.makeText(DisputeActivity.this,w.getMessage(),Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                Toast.makeText(DisputeActivity.this,error.getMessage(),Toast.LENGTH_LONG).show();
            }
        });

        requestQueue.add(jsonObjectRequest);
    }
}