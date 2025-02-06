package com.example.petstore.anotherActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.petstore.R;
import com.example.petstore.dao.NetRequest;
import com.example.petstore.pojo.User;
import com.example.petstore.utils.DialogUtils;
import com.example.petstore.utils.JwtUtils;
import com.example.petstore.utils.LoadPicture;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

public class PersonInformationActivity extends AppCompatActivity {
    private View avatar_container, name_container, sex_container, birth_container, phone_container, password_container;
    private NetRequest netRequest;
    private ImageView avatar, sex, back;
    private TextView name, birth, phone, username;
    private User user;
    private User updateUser = new User();
    private ActivityResultLauncher<Intent> pickImageLauncher;
    private byte[] imageByte;

    // 记录更新哪个字段
    private String updateType = "";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.person_information_activity);

        // 初始化 ActivityResultLauncher
        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        LoadPicture loadPicture = new LoadPicture();
                        loadPicture.onActivityResult(result.getResultCode(), data, this, "Images");

                        handleAvatar(loadPicture.getImageBytes());


                    }
                });

        init();
    }

    private void init() {
        setNetResponse(getApplicationContext());

        back = findViewById(R.id.back);

        avatar = findViewById(R.id.avatar);
        sex = findViewById(R.id.sex);
        name = findViewById(R.id.name);
        birth = findViewById(R.id.birth);
        phone = findViewById(R.id.phone);
        username = findViewById(R.id.username);

        avatar_container = findViewById(R.id.avatar_container);
        name_container = findViewById(R.id.name_container);
        sex_container = findViewById(R.id.sex_container);
        birth_container = findViewById(R.id.birth_container);
        phone_container = findViewById(R.id.phone_container);
        password_container = findViewById(R.id.password_container);

        Intent data = getIntent();
        Gson gson = new Gson();
        String userJson = data.getStringExtra("user");
        user = gson.fromJson(userJson, User.class);
        initData(user);
        initListener();
    }

    private void initListener() {
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Gson gson = new Gson();
                String userJson = gson.toJson(user);

                Intent resultIntent = new Intent();
                resultIntent.putExtra("user", userJson);
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
            }
        });
        avatar_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoadPicture loadPicture = new LoadPicture();
                loadPicture.getPictureByPhone(pickImageLauncher, "Images");
            }
        });
        name_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogUtils.showInputDialog(PersonInformationActivity.this, "名字", "请更改名字", user.getName(), new DialogUtils.InputDialogCallback() {
                    @Override
                    public void onInputEntered(String input) {
                        handleModify("name", String.valueOf(input));
                    }
                });
            }
        });
        sex_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogUtils.selectGender(PersonInformationActivity.this, user.getGender(), new DialogUtils.GenderSelectCallback() {
                    @Override
                    public void onGenderSelected(int index) {
                        handleModify("gender", String.valueOf(index));
                    }
                });
            }
        });
        birth_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogUtils.showDatePickerDialog(PersonInformationActivity.this, new DialogUtils.DateSelectCallback() {
                    @Override
                    public void onDateSelected(String data) {
                        handleModify("birth", data);
                    }
                });
            }
        });
        phone_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogUtils.showPhoneDialog(PersonInformationActivity.this, user.getPhone(), new DialogUtils.PhoneCallback() {
                    @Override
                    public void onPhoneInput(String newPhone, String code) {
                        user.setCode(code);
                        handleModify("phone", newPhone);
                    }
                });
            }
        });
        password_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogUtils.showPasswordDialog(PersonInformationActivity.this, user.getPassword(), new DialogUtils.PasswordCallback() {
                    @Override
                    public void onPasswordInput(String newPassword) {
                        handleModify("password", newPassword);
                    }
                });
            }
        });
    }

    private void initData(User user) {
        //用户头像
        Glide.with(getApplicationContext())
                .load(user.getImage())
                .into(avatar);

        sex.setImageResource(user.getGender() == 0 ? R.drawable.male_sign : R.drawable.female_sign);
        name.setText(user.getName());
        birth.setText(user.getBirth());
        phone.setText(user.getPhone());
        username.setText(user.getUsername());
    }

    private void handleAvatar(byte[] avatarByte) {
        imageByte = avatarByte;
        postNetRequest("deleteFile");
    }


    private void handleModify(String type, String value) {

        updateUser = new User();
        if (type.equals("password")) {
            user.setPassword(value);
            updateUser.setPassword(value);
        } else if (type.equals("name")) {
            user.setName(value);
            updateUser.setName(value);
        } else if (type.equals("username")) {
            user.setUsername(value);
            updateUser.setUsername(value);
        } else if (type.equals("birth")) {
            user.setBirth(value);
            updateUser.setBirth(value);
        } else if (type.equals("phone")) {
            user.setPhone(value);
            updateUser.setPhone(value);
        } else if (type.equals("gender")) {
            user.setGender(Integer.valueOf(value));
            updateUser.setGender(Integer.valueOf(value));
        } else if (type.equals("image")) {
            user.setImage(value);
            updateUser.setImage(value);
        }
        updateType = type;
        initData(user);
        postNetRequest("update");
    }

    //发送网络请求
    private void postNetRequest(String type) {
        if ("update".equals(type)) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("id", user.getId());
                jsonObject.put("name", updateUser.getName());
                jsonObject.put("phone", updateUser.getPhone());
                jsonObject.put("code", user.getCode());
                jsonObject.put("gender", updateUser.getGender());
                jsonObject.put("birth", updateUser.getBirth());
                jsonObject.put("image", updateUser.getImage());
                jsonObject.put("password", updateUser.getPassword());
            } catch (JSONException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
            // 将最终 JSON 对象转换为字符串
            String jsonPayload = jsonObject.toString();
            netRequest.postCustomerRequest("updateCustomer", jsonPayload);
        } else if ("delete".equals(type)) {
            netRequest.getDeleteSQLFile("deleteCustomerImage", user.getId());
        } else if ("uploadImage".equals(type)) {
            netRequest.upLoadFile(imageByte, "customer", ".jpg", "common");
        } else if ("deleteFile".equals(type)) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("dir", user.getImage());
            } catch (JSONException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
            // 将最终 JSON 对象转换为字符串
            String jsonPayload = jsonObject.toString();
            netRequest.postDeleteRealFileRequest("delete", jsonPayload);
        }
    }

    //接受网络请求返回数据
    private void setNetResponse(Context context) {
        netRequest = new NetRequest(new JwtUtils(), context, new NetRequest.ResponseCallback() {
            public void successPost(String requestType, String code, String msg, String data) throws JSONException {
                if (requestType.equals("postCustomerRequest")) {
                    if (updateType.equals("name")){
                        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        // 修改 loggedInName 的值
                        editor.putString("loggedInName", user.getName());
                        // 提交修改
                        editor.apply();
                    }
                    if (updateType.equals("phone")){

                    }
                }
                if (requestType.equals("upLoadFile")) {


                    System.out.println("================================" + data);



                    handleModify("image", data);
                }
                if (requestType.equals("getDeleteSQLFile")) {
                    postNetRequest("uploadImage");
                }
                if (requestType.equals("postDeleteRealFileRequest")) {
                    postNetRequest("delete");
                }
            }

            @Override
            public void failurePost(String requestType, String errorMessage) {
                if (requestType.equals("postCustomerRequest")) {
                    Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
                }
                if (requestType.equals("upLoadFile")) {
                    Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
                }
                if (requestType.equals("getDeleteSQLFile")) {
                    Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
                }
                if (requestType.equals("postDeleteRealFileRequest")) {
                    Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
