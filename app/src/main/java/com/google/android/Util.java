package com.google.android;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;


public class Util {
    private static final String TAG = "FaceUtils";

    private static final String URL = "http://fabebeb6.ngrok.io/api/match_image";

    public static void fetchMeta(RequestQueue queue, final JSONObject params) {
        try {
            Log.v(TAG, "sending data: (left, top, width, height): "
                    + params.getString("left") + " " + params.getString("top") + " " + params.getString("width") + " " + params.getString("height"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest r = new JsonObjectRequest(Request.Method.POST, URL, params, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.v(TAG, "received data: " + response.toString());
                // Put face into array
                try {
                    int id = (int)params.get("id");
                    faces.put(""+id, (JSONObject)response.get("match"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }) {
            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };

        r.setRetryPolicy(new DefaultRetryPolicy(30000, 1, 1));

        queue.add(r);
    }

    public static String encodeImagetoString(String imagePath) {
        Bitmap bm = BitmapFactory.decodeFile(imagePath);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        return Base64.encodeToString(b, Base64.DEFAULT);
    }

    public static HashMap<String,JSONObject> faces = new HashMap<String, JSONObject>();

}
