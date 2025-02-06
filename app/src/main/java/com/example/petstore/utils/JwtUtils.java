package com.example.petstore.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class JwtUtils {
    public void saveJwt(Context context,String token){

        // 将 token 存储到 SharedPreferences 中
        SharedPreferences preferences = context.getSharedPreferences("MyJwt", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("token", token);
        editor.apply();
    }

    public String getJwt(Context context){
        SharedPreferences preferences = context.getSharedPreferences("MyJwt", Context.MODE_PRIVATE);
        return preferences.getString("token",null);
    }
}
