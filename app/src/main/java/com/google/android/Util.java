package com.google.android;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

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

    private static final String URL = "http://ec2-52-25-232-222.us-west-2.compute.amazonaws.com:4009";

    public static void fetchMeta(RequestQueue queue, final JSONObject params) {

        JsonObjectRequest r = new JsonObjectRequest(Request.Method.POST, URL, params, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
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
            }
        });

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
