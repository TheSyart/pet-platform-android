package com.example.petstore.dialogUtils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import com.example.petstore.R;
import com.example.petstore.dao.NetRequest;
import com.example.petstore.interfaces.AddPetInterface;
import com.example.petstore.utils.JwtUtils;
import com.example.petstore.utils.LoadPicture;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Base64;

public class ShowAddMyPetDialog {
    private AlertDialog dialog;
    private NetRequest netRequest;
    //各个控件
    private ImageView pet_image;
    private EditText pet_name;
    private TextView pet_breed;
    private Spinner spinner_breed;
    private Spinner spinner_color;
    private RadioGroup radioGroupGender;
    private RadioButton isMale;
    private RadioButton isFemale;
    private TextView pet_birth;
    private EditText pet_weight;
    private CheckBox isSterilize;
    private CheckBox isVaccine;
    private Button save;

    //传递后端的值
    private String sexText = "";
    private String[] detailsText = {"0","0"};
    private String details_pass = "";
    private String image_pass;
    private String birth_pass = "";

    private Integer selectedBreed = 0;
    private Integer selectedColor = 0;

    //宠物全部分类
    private String[] breed;

    //宠物全部毛色
    private String[] colors;

    private ArrayAdapter<String> breedAdapter;
    private ArrayAdapter<String> colorAdapter;
    private AddPetInterface listener;  // 用于回调接口

    public void setOnReloadable(AddPetInterface listener) {
        this.listener = listener;  // 设置回调监听
    }

    public void showDialog(Context context, ActivityResultLauncher<Intent> launcher) {
        setNetResponse(context);
        postNetRequest(context);

        // 创建一个AlertDialog.Builder对象
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.add_mypet_dialog, null);

        // 初始化控件
        pet_image = dialogView.findViewById(R.id.pet_image);
        pet_name = dialogView.findViewById(R.id.pet_name);
        radioGroupGender = dialogView.findViewById(R.id.radioGroupGender);
        isMale = dialogView.findViewById(R.id.isMale);
        isFemale = dialogView.findViewById(R.id.isFemale);
        pet_birth = dialogView.findViewById(R.id.pet_birth);
        pet_weight = dialogView.findViewById(R.id.pet_weight);
        isSterilize = dialogView.findViewById(R.id.isSterilize);
        isVaccine = dialogView.findViewById(R.id.isVaccine);
        save = dialogView.findViewById(R.id.save);


        // 宠物品种 Spinner 设置
        spinner_breed = dialogView.findViewById(R.id.spinner_breed);

        spinner_breed.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedBreed = position;
                Toast.makeText(context, "选中: " + breed[position], Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // 无操作
            }
        });

        // 宠物颜色 Spinner 设置
        spinner_color = dialogView.findViewById(R.id.spinner_color);

        // 设置颜色 Spinner 的监听器
        spinner_color.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedColor = position;
                Toast.makeText(context, "选中: " +  colors[position], Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // 无操作
            }
        });

        //各个监视器
        click(launcher,context);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getTextInformation(context);
            }
        });

        // 将自定义布局设置到对话框中
        builder.setView(dialogView);

        // 创建AlertDialog对象并显示
        dialog = builder.create();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);//去除标题栏
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));//去除边框
        dialog.show();

    }

    private void postNetRequest(Context context) {
        //获取所有宠物分类
        netRequest.getEncyclopediaRequest("allBreed","");
        //获取所有宠物毛色
        netRequest.postPetInfoRequest("queryPetCoat");
    }

    public void getImage(byte[] imageBytes, Context context){
        // 将字节数组转换为 Bitmap 并显示在 ImageView 上
        if (imageBytes != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            pet_image.setImageBitmap(bitmap);
        }

        netRequest.upLoadFile(imageBytes,"mypet", ".jpg", "common");
    }


    public void getTextInformation(Context context){
        // 获取username
        SharedPreferences sharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String username = sharedPreferences.getString("loggedInUsername", null);

        if (!sexText.equals("")){
            Integer.valueOf(sexText);
        }

        if (detailsText[0].equals("0") && detailsText[1].equals("0")) {
            details_pass = "0";
        } else if (detailsText[0].equals("1") && detailsText[1].equals("0")) {
            details_pass = "1";
        } else if (detailsText[0].equals("0") && detailsText[1].equals("1")) {
            details_pass = "2";
        } else if (detailsText[0].equals("1") && detailsText[1].equals("1")) {
            details_pass = "3";
        }
        System.out.println("宠物性别：" + details_pass);

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("username", username);
            jsonObject.put("pet_name", pet_name.getText().toString());
            jsonObject.put("pet_breed", selectedBreed);
            jsonObject.put("pet_sex", sexText);
            jsonObject.put("pet_birth", birth_pass);
            jsonObject.put("pet_coat", selectedColor);
            jsonObject.put("pet_weight", pet_weight.getText().toString());
            jsonObject.put("image", image_pass);
            jsonObject.put("pet_details", Integer.valueOf(details_pass));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        String jsonPayload = jsonObject.toString();
        netRequest.postMyPetRequest("insertMyPet", jsonPayload);
    }



    public void click(ActivityResultLauncher<Intent> launcher, Context context){
        //获取我的宠物头像
        pet_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoadPicture loadPicture = new LoadPicture();
                loadPicture.getPictureByPhone(launcher,"Images");
            }
        });

        //性别选择
        radioGroupGender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.isMale){
                    sexText = "0";
                }else if (checkedId == R.id.isFemale){
                    sexText = "1";
                }
            }
        });
        //是否绝育
        isSterilize.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    detailsText[0] = "1";
                }else {
                    detailsText[0] = "0";
                }
            }
        });
        //是否打疫苗
        isVaccine.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    detailsText[1] = "1";
                }else {
                    detailsText[1] = "0";
                }
            }
        });

        //打开宠物生日选择对话框
        pet_birth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowDatePiker showDatePiker = new ShowDatePiker();
                showDatePiker.showDialog(context, new ShowDatePiker.OnDateSelectedListener() {
                    @Override
                    public void onDateSelected(String date) {
                        // 在这里处理接收到的日期
                        birth_pass = date;
                        pet_birth.setText(date);
                        System.out.println("接收到的日期: " + date);

                    }
                });
            }
        });



    }

    private void setNetResponse(Context context){
        //发送动态到服务器后返回数据
        netRequest = new NetRequest(new JwtUtils(), context, new NetRequest.ResponseCallback() {
            @Override
            public void successPost(String requestType, String code, String msg, String data) throws JSONException {
                if (requestType.equals("postMyPetRequest")) {
                    dialog.dismiss();
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();

                    if (listener != null) {
                        listener.reloadPet();
                    }

                }

                if (requestType.equals("upLoadFile")) {
                    //照片上传，返回的地址
                    System.out.println("照片返回地址：" + data);
                    image_pass = data;

                }

                //获取所有宠物分类
                if (requestType.equals("getEncyclopediaRequest")) {
                    JSONArray breedArray = new JSONArray(data); // 直接解析为 JSONArray
                    breed = new String[breedArray.length()];
                    for (int i = 0; i < breedArray.length(); i++) {
                        breed[i] = breedArray.getString(i); // 填充 breed 数组
                    }
                    breedAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, breed);
                    breedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner_breed.setAdapter(breedAdapter);
                }

                //获取所有宠物毛色
                if (requestType.equals("postPetInfoRequest")) {
                    JSONArray breedArray = new JSONArray(data); // 直接解析为 JSONArray
                    colors = new String[breedArray.length()];
                    for (int i = 0; i < breedArray.length(); i++) {
                        colors[i] = breedArray.getString(i); // 填充 colors 数组
                    }
                    colorAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, colors);
                    colorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner_color.setAdapter(colorAdapter);
                }



            }

            @Override
            public void failurePost(String requestType, String errorMessage) {
                if (requestType.equals("postMyPetRequest")) {
                    Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
                }
                if (requestType.equals("upLoadFile")) {
                    Toast.makeText(context, "上传图片失败: " + errorMessage, Toast.LENGTH_SHORT).show();
                }
                if (requestType.equals("getEncyclopediaRequest")){
                    Toast.makeText(context, "所有宠物分类获取失败: " + errorMessage, Toast.LENGTH_SHORT).show();
                }
                if (requestType.equals("postPetInfoRequest")){
                    Toast.makeText(context, "所有宠物毛色获取失败: " + errorMessage, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
