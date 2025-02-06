package com.example.petstore.utils;

import android.content.Context;

import com.example.petstore.R;

import org.json.JSONException;
import java.io.IOException;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RetrofitRequestHelper {
    private static String ipAddress;
    private static String BASE_URL;
    private Retrofit retrofit;

    public RetrofitRequestHelper(Context context) {
        ipAddress = context.getResources().getString(R.string.ip_address);
        BASE_URL = "http://" + ipAddress + ":8080";
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create()) // 使用 Gson 转换器
                .build();
    }



    public <T> T createService(Class<T> service) {
        return retrofit.create(service);
    }

    public <T> void enqueueRequest(Call<T> call, final ResponseHandler<T> handler) {
        call.enqueue(new Callback<T>() {
            @Override
            public void onResponse(Call<T> call, Response<T> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        handler.onSuccess(response.body());
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    // 打印详细错误信息
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "无错误信息";
                        handler.onFailure(new Throwable("请求失败 - 状态码: " + response.code() + ", 错误信息: " + errorBody));
                    } catch (IOException e) {
                        e.printStackTrace();
                        handler.onFailure(new Throwable("请求失败，无法读取错误信息: " + e.getMessage()));
                    }
                }
            }


            @Override
            public void onFailure(Call<T> call, Throwable t) {
                handler.onFailure(t);
            }
        });
    }

    public interface ResponseHandler<T> {
        void onSuccess(T response) throws JSONException, IOException;
        void onFailure(Throwable t);
    }
}

