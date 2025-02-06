package com.example.petstore.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.petstore.R;
import com.example.petstore.anotherActivity.CustomerServiceActivity;
import com.example.petstore.anotherActivity.GDMapActivity;
import com.example.petstore.anotherActivity.PersonInformationActivity;
import com.example.petstore.anotherActivity.SplashActivity;
import com.example.petstore.dao.NetRequest;
import com.example.petstore.dialogUtils.ShowAddressDialog;
import com.example.petstore.dialogUtils.ShowPayDialog;
import com.example.petstore.pojo.Address;
import com.example.petstore.pojo.User;
import com.example.petstore.utils.JwtUtils;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class PersonActivity extends Fragment implements ShowAddressDialog.ShowAddressListener {
    private View customerService, address, person_information;
    private ImageView profile_image;
    private TextView name_text;
    private ActivityResultLauncher<Intent> activityResultLauncher;
    private NetRequest netRequest;
    private ArrayList<Address> addressList = new ArrayList<>();
    private ShowAddressDialog.ShowAddressListener showAddressListener = this;
    private String username;
    private User user;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.person_activity, container, false);

        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data!= null) {

                            // 地址管理
                            String type = data.getStringExtra("type");
                            if (type!= null && type.equals("add")) {
                                postNetRequest("address");
                            }

                            // 个人信息
                            if (data.getStringExtra("user")!= null) {
                                Gson gson = new Gson();
                                String userJson = data.getStringExtra("user");
                                user = gson.fromJson(userJson, User.class);
                                setData();
                            }
                        }
                    }
                }
        );

        init(view);
        return view;
    }

    private void init(View view) {
        // 获取数据
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        username = sharedPreferences.getString("loggedInUsername", null);

        setNetResponse(getContext());
        postNetRequest("person");

        customerService = view.findViewById(R.id.customerService);
        address = view.findViewById(R.id.address);
        person_information = view.findViewById(R.id.person_information);
        name_text = view.findViewById(R.id.name_text);
        profile_image = view.findViewById(R.id.profile_image);
        listener();
    }

    private void setData() {
        name_text.setText(user.getName());
        Glide.with(getContext())
                .load(user.getImage())
                .into(profile_image);
    }

    private void listener() {
        person_information.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Gson gson = new Gson();
                String userJson = gson.toJson(user);
                Intent intent = new Intent(getActivity(), PersonInformationActivity.class);
                intent.putExtra("user", userJson);
                activityResultLauncher.launch(intent);
            }
        });
        customerService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CustomerServiceActivity.class);
                startActivity(intent);
            }
        });

        address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postNetRequest("address");
            }
        });
    }

    private void showAddressDialog() {
        ShowAddressDialog showAddressDialog = new ShowAddressDialog();
        showAddressDialog.showDialog(activityResultLauncher, getActivity(), getContext(), addressList, showAddressListener);
    }

    //发送网络请求
    private void postNetRequest(String type) {
        if ("address".equals(type)) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("username", username);
            } catch (JSONException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
            // 将最终 JSON 对象转换为字符串
            String jsonPayload = jsonObject.toString();
            netRequest.postAddressRequest("queryAddressByUsername", jsonPayload);
        } else if ("person".equals(type)) {
            netRequest.getUserInformationRequest("queryOnePersonInformation", username);
        }

    }

    //接受网络请求返回数据
    private void setNetResponse(Context context) {
        netRequest = new NetRequest(new JwtUtils(), context, new NetRequest.ResponseCallback() {
            public void successPost(String requestType, String code, String msg, String data) throws JSONException {

                if (requestType.equals("postAddressRequest")) {
                    addressList.clear();
                    // 将 data 转换为 JSONArray
                    JSONArray jsonResponse = new JSONArray(data);

                    // 遍历 JSONArray
                    for (int i = 0; i < jsonResponse.length(); i++) {
                        JSONObject json = jsonResponse.getJSONObject(i);
                        Address newAddress = new Address(
                                json.getString("id"),
                                json.getInt("defaultAddress"),
                                json.getString("name"),
                                json.getInt("gender"),
                                json.getString("phone"),
                                json.getString("addressTitle"),
                                json.getString("addressDetails"),
                                json.getString("door"),
                                json.getDouble("longitude"),
                                json.getDouble("latitude")
                        );
                        addressList.add(newAddress);
                    }
                    showAddressDialog();
                }
                if (requestType.equals("getUserInformationRequest")) {
                    JSONObject json = new JSONObject(data);
                    user = new User(
                            json.getString("id"),
                            json.getString("name"),
                            json.getString("username"),
                            json.getString("password"),
                            json.getString("birth"),
                            json.getString("phone"),
                            json.getInt("gender"),
                            json.getString("image")
                    );
                    setData();
                }
            }

            @Override
            public void failurePost(String requestType, String errorMessage) {
                if (requestType.equals("postAddressRequest")) {
                    Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
                }
                if (requestType.equals("getUserInformationRequest")) {
                    Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void change(Address address) {

    }
}