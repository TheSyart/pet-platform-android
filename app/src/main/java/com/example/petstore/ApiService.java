package com.example.petstore;

import com.example.petstore.pojo.Dynamics;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    //处理聊天上传文件
    @Multipart
    @POST("/handlePic/converseUpload")
    Call<ResponseBody> uploadFile(
            @Header("token") String header,
            @Part MultipartBody.Part file,
            @Part("dir") RequestBody dir,
            @Part("fileType") RequestBody fileType);

    // 普通上传照片
    @Multipart
    @POST("/handlePic/uploadPic")
    Call<ResponseBody> uploadPic(
            @Header("token") String header,
            @Part MultipartBody.Part file,
            @Part("dir") String dir);

    //用户注册
    @POST("/customer/{endpoint}")
    Call<ResponseBody> postRegister(
            @Path("endpoint") String endpoint,
            @Body RequestBody body);

    //向手机号发送验证码 和 更新用户信息
    @POST("/customer/{endpoint}")
    Call<ResponseBody> postCustomerRequest(
            @Path("endpoint") String endpoint,
            @Header("token") String Header,
            @Body RequestBody body);

    //得到用户信息
    @GET("/customer/{endpoint}")
    Call<ResponseBody> getUsernameInformation(
            @Path("endpoint") String endpoint,
            @Header("token") String Header,
            @Query("username") String username);

    //删除用户数据库照片
    @GET("/customer/{endpoint}")
    Call<ResponseBody> deleteSQLFile(
            @Path("endpoint") String endpoint,
            @Header("token") String Header,
            @Query("id") String id);

    //删除用户照片文件
    @POST("/handlePic/{endpoint}")
    Call<ResponseBody> deleteRealFile(
            @Path("endpoint") String endpoint,
            @Header("token") String Header,
            @Body RequestBody body);

    //登录校验
    @POST("/login/{endpoint}")
    Call<ResponseBody> postLogin(
            @Path("endpoint") String endpoint,
            @Body RequestBody body,
            @Query("loginStatus") String loginStatus);

    //获得动态信息
    @GET("/dynamics/{endpoint}")
    Call<ResponseBody> getAllDynamics(
            @Path("endpoint") String endpoint,
            @Header("token") String Header,
            @Query("dynamicsNum") String dynamicsNum,
            @Query("username") String username);

    //更新点赞数量和点赞人
    @GET("/dynamics/{endpoint}")
    Call<ResponseBody> getUpdateDynamicsNumAndPeople(
            @Path("endpoint") String endpoint,
            @Header("token") String Header,
            @Query("id") String id,
            @Query("username") String username,
            @Query("type") Integer type
    );

    //发布新动态
    @POST("/dynamics/{endpoint}")
    Call<ResponseBody> postInsertDynamics(
            @Path("endpoint") String endpoint,
            @Header("token") String Header,
            @Body Dynamics dynamics);


    //获得宠物百科，宠物分类信息
    @GET("/encyclopedia/{endpoint}")
    Call<ResponseBody> getEncyclopediaPetSpecies(
            @Path("endpoint") String endpoint,
            @Header("token") String Header,
            @Query("id") String id);

    //获得宠物百科下,一个宠物的具体信息
    @GET("/encyclopedia/{endpoint}")
    Call<ResponseBody> getEncyclopediaPetDetails(
            @Path("endpoint") String endpoint,
            @Header("token") String Header,
            @Query("id") String id);

    //获得宠物喂养技巧具体信息
    @GET("/feedingSkill/{endpoint}")
    Call<ResponseBody> getPetFeedingSkill(
            @Path("endpoint") String endpoint,
            @Header("token") String Header,
            @Query("skillNum") Integer skillNum);

    //宠物日历数据的增删改查
    @POST("/petCalendar/{endpoint}")
    Call<ResponseBody> postPetCalendar(
            @Path("endpoint") String endpoint,
            @Header("token") String Header,
            @Body RequestBody body);


    //我的宠物数据的增删改查
    @POST("/myPet/{endpoint}")
    Call<ResponseBody> postMyPet(
            @Path("endpoint") String endpoint,
            @Header("token") String Header,
            @Body RequestBody body);


    //商城的增删改查
    @POST("/petShopping/{endpoint}")
    Call<ResponseBody> postPetShopping(
            @Path("endpoint") String endpoint,
            @Header("token") String Header,
            @Body RequestBody body);

    //订单的增删改查
    @POST("/petOrder/{endpoint}")
    Call<ResponseBody> postPetOrder(
            @Path("endpoint") String endpoint,
            @Header("token") String Header,
            @Body RequestBody body
    );

    //地址的增删改查
    @POST("/petAddress/{endpoint}")
    Call<ResponseBody> postPetAddress(
            @Path("endpoint") String endpoint,
            @Header("token") String Header,
            @Body RequestBody body);

    //服务的增删改查
    @POST("/petService/{endpoint}")
    Call<ResponseBody> postPetService(
            @Path("endpoint") String endpoint,
            @Header("token") String Header,
            @Body RequestBody body);

    //获得宠物毛色，具体信息等
    @GET("/myPet/{endpoint}")
    Call<ResponseBody> getPetInfo(
            @Path("endpoint") String endpoint,
            @Header("token") String Header);

    //获得宠物毛色，具体信息等
    @POST("/emp/{endpoint}")
    Call<ResponseBody> postEmpDoctor(
            @Path("endpoint") String endpoint,
            @Header("token") String Header,
            @Body RequestBody body);

    @GET("/converse/{endpoint}")
    Call<ResponseBody> getMessage(
            @Path("endpoint") String endpoint,
            @Header("token") String Header,
            @Query("sender") String sender,
            @Query("receiver") String receiver,
            @Query("converseNum") Integer converseNum);
}

