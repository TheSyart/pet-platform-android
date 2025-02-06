package com.example.petstore.dao;

import android.content.Context;

import com.example.petstore.ApiService;
import com.example.petstore.R;
import com.example.petstore.pojo.Dynamics;
import com.example.petstore.utils.JwtUtils;
import com.example.petstore.utils.RetrofitRequestHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;

public class NetRequest {
    private JwtUtils jwtUtils;
    private RetrofitRequestHelper requestHelpers; // 确保名称一致
    private ResponseCallback responseCallback;
    private Context context;

    public NetRequest(JwtUtils jwtUtils, Context context, ResponseCallback responseCallback) {
        this.jwtUtils = jwtUtils;
        this.responseCallback = responseCallback;
        this.context = context;
        this.requestHelpers = new RetrofitRequestHelper(context); // 确保引用的是正确的类
    }

    public interface ResponseCallback {
        void successPost(String requestType, String code, String msg, String data) throws JSONException;

        void failurePost(String requestType, String errorMessage);
    }

    public void transformData(String requestType, ResponseBody responseBody) throws IOException, JSONException {
        // 将响应体转换为字符串
        String responseString = responseBody.string();


        // 打印原始响应数据
        System.out.println("原始响应数据：" + responseString);

        // 将字符串转换为 JSON 对象进行解析
        JSONObject jsonResponse = new JSONObject(responseString);
        String code = jsonResponse.getString("code");
        String msg = jsonResponse.getString("message");
        String data = jsonResponse.getString("data");


        // 登录校验时，保存 JWT token
        if (requestType.equals("postLoginRequest")) {
            String jwt = new JSONObject(data).getString("jwt");
            jwtUtils.saveJwt(context, jwt);
            System.out.println(" JWT token : " + data);
        }

        data = handleLink(List.of("image", "image_path", "messageCover", "message"), data);


        // 调用回调方法传递解析后的数据
        responseCallback.successPost(requestType, code, msg, data);


        // 输出解析结果
        System.out.println(" 当前方法 " + requestType + ":");
        System.out.println("Code: " + code);
        System.out.println("Message: " + msg);
        System.out.println("Data : " + data);
    }


    // TODO 处理link------------------------->
    public String handleLink(List<String> fieldNames, String data) {
        try {
            if (data instanceof String) {
                try {
                    JSONObject dataObj = new JSONObject(data);
                    for (String fieldName : fieldNames) {
                        if (dataObj.has(fieldName)) {
                            String value = dataObj.optString(fieldName);
                            if (!value.isEmpty() && value.contains("server-resource")) {
                                // 修改字段的值
                                value = "http://" + context.getResources().getString(R.string.ip_address) + ":8080" + value;
                                // 将修改后的值存储回 JSONObject
                                dataObj.put(fieldName, value);
                            }
                        }
                    }
                    // 将修改后的 JSONObject 重新赋值给 data
                    data = dataObj.toString();
                } catch (JSONException e) {
                    try {
                        JSONArray dataArray = new JSONArray(data);
                        for (int i = 0; i < dataArray.length(); i++) {
                            Object item = dataArray.get(i);
                            if (item instanceof JSONObject) {
                                JSONObject itemObj = (JSONObject) item;
                                for (String fieldName : fieldNames) {

                                    if (fieldName.equals("message")) {
                                        if (itemObj.optInt("messageType") == 0) {
                                            continue;
                                        }
                                    }

                                    if (itemObj.has(fieldName)) {
                                        String value = itemObj.optString(fieldName);
                                        if (!value.isEmpty() && value.contains("server-resource")) {
                                            // 修改字段的值
                                            value = "http://" + context.getResources().getString(R.string.ip_address) + ":8080" + value;
                                            // 将修改后的值存储回 JSONObject
                                            itemObj.put(fieldName, value);
                                        }
                                    }
                                }
                            }
                        }
                        // 将修改后的 JSONArray 重新赋值给 data
                        data = dataArray.toString();
                    } catch (JSONException ex) {
                        System.err.println("Error parsing data as JSONArray: " + ex.getMessage());
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error handling link: " + e.getMessage());
        }
        return data;
    }


    // TODO 文件上传------------------------>
    public void upLoadFile(byte[] bytes, String dir, String fileType, String type) {
        ApiService apiService = requestHelpers.createService(ApiService.class);
        String token = jwtUtils.getJwt(context);

        File tempFile = null;
        try {
            // 创建临时文件，这里以".jpg"作为示例后缀，可根据实际情况或从uri中判断文件真实类型来调整后缀
            tempFile = File.createTempFile("temp_file", fileType);
            FileOutputStream fos = new FileOutputStream(tempFile);
            fos.write(bytes);
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        RequestBody requestFile;
        MultipartBody.Part fileBody = null;
        // 创建MultipartBody.Part用于表示文件部分
        if (".jpg".equals(fileType)) {
            requestFile = RequestBody.create(tempFile, MediaType.parse("image/jpg"));
            fileBody = MultipartBody.Part.createFormData("file", "1", requestFile);
        } else if (".mp4".equals(fileType)) {
            requestFile = RequestBody.create(tempFile, MediaType.parse("video/mp4"));
            fileBody = MultipartBody.Part.createFormData("file", "1", requestFile);
        } else if (".pcm".equals(fileType)) {
            requestFile = RequestBody.create(tempFile, MediaType.parse("video/pcm"));
            fileBody = MultipartBody.Part.createFormData("file", "1", requestFile);
        }

        RequestBody fileTypeBody = RequestBody.create(MediaType.parse("text/plain"), fileType);
        RequestBody dirBody = RequestBody.create(MediaType.parse("text/plain"), dir);
        Call<ResponseBody> call = null;
        if ("converse".equals(type)){
            call = apiService.uploadFile(token, fileBody, dirBody, fileTypeBody);
        } else if ("common".equals(type)){
            call = apiService.uploadPic(token, fileBody, dir);
        }
        

        requestHelpers.enqueueRequest(call, new RetrofitRequestHelper.ResponseHandler<ResponseBody>() {
            @Override
            public void onSuccess(ResponseBody response) {
                try {
                    transformData("upLoadFile", response);
                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                    responseCallback.failurePost("upLoadFile", "解析响应数据失败: " + e.getMessage());
                }
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
                responseCallback.failurePost("upLoadFile", "网络请求失败: " + t.getMessage());
            }
        });
    }

    // TODO 用户注册------------------------>
    public void postRegisterRequest(String endpoint, String jsonPayload) {
        ApiService apiService = requestHelpers.createService(ApiService.class);
        String token = jwtUtils.getJwt(context);
        // 打印请求的 URL 和请求体
        System.out.println("请求 URL: " + endpoint);
        System.out.println("请求体: " + jsonPayload);

        RequestBody requestBody = RequestBody.create(jsonPayload, okhttp3.MediaType.get("application/json; charset=utf-8"));
        Call<ResponseBody> call = apiService.postRegister(endpoint, requestBody);
        requestHelpers.enqueueRequest(call, new RetrofitRequestHelper.ResponseHandler<ResponseBody>() {
            @Override
            public void onSuccess(ResponseBody response) {
                try {
                    transformData("postRegisterRequest", response);
                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                    responseCallback.failurePost("postRegisterRequest", "解析响应数据失败: " + e.getMessage());
                }
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
                responseCallback.failurePost("postRegisterRequest", "网络请求失败: " + t.getMessage());
            }
        });
    }

    // TODO 登录校验------------------------>
    public void postLoginRequest(String endpoint, String jsonPayload, String loginStatus) {
        ApiService apiService = requestHelpers.createService(ApiService.class);
        String token = jwtUtils.getJwt(context);

        // 打印请求的 URL 和请求体
        System.out.println("请求 URL: " + endpoint);
        System.out.println("请求体: " + jsonPayload);

        RequestBody requestBody = RequestBody.create(jsonPayload, okhttp3.MediaType.get("application/json; charset=utf-8"));
        Call<ResponseBody> call = apiService.postLogin(endpoint, requestBody, loginStatus);

        // 使用 ResponseBody 处理返回结果
        requestHelpers.enqueueRequest(call, new RetrofitRequestHelper.ResponseHandler<ResponseBody>() {
            @Override
            public void onSuccess(ResponseBody responseBody) {
                try {
                    transformData("postLoginRequest", responseBody);

                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                    responseCallback.failurePost("postLoginRequest", "解析响应数据失败: " + e.getMessage());
                }
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
                responseCallback.failurePost("postLoginRequest", "网络请求失败: " + t.getMessage());
            }
        });
    }

    // TODO 得到登陆者信息 name等等------------------------>
    public void getUserInformationRequest(String endpoint, String username) {
        ApiService apiService = requestHelpers.createService(ApiService.class);
        String token = jwtUtils.getJwt(context);

        // 打印请求的 URL 和请求体
        System.out.println("请求 URL: " + endpoint);

        Call<ResponseBody> call = apiService.getUsernameInformation(endpoint, token, username);

        // 使用 ResponseBody 处理返回结果
        requestHelpers.enqueueRequest(call, new RetrofitRequestHelper.ResponseHandler<ResponseBody>() {
            @Override
            public void onSuccess(ResponseBody responseBody) {
                try {
                    transformData("getUserInformationRequest", responseBody);
                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                    responseCallback.failurePost("getUserInformationRequest", "解析响应数据失败: " + e.getMessage());
                }
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
                responseCallback.failurePost("getUserInformationRequest", "网络请求失败: " + t.getMessage());
            }
        });
    }

    // TODO 删除存储的照片文件------------------------>
    public void postDeleteRealFileRequest(String endpoint, String jsonPayload) {
        ApiService apiService = requestHelpers.createService(ApiService.class);
        String token = jwtUtils.getJwt(context);

        // 打印请求的 URL 和请求体
        System.out.println("请求 URL: " + endpoint);
        System.out.println("请求体: " + jsonPayload);

        RequestBody requestBody = RequestBody.create(jsonPayload, okhttp3.MediaType.get("application/json; charset=utf-8"));

        Call<ResponseBody> call = apiService.deleteRealFile(endpoint, token, requestBody);

        // 使用 ResponseBody 处理返回结果
        requestHelpers.enqueueRequest(call, new RetrofitRequestHelper.ResponseHandler<ResponseBody>() {
            @Override
            public void onSuccess(ResponseBody responseBody) {
                try {
                    transformData("postDeleteRealFileRequest", responseBody);
                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                    responseCallback.failurePost("postDeleteRealFileRequest", "解析响应数据失败: " + e.getMessage());
                }
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
                responseCallback.failurePost("postDeleteRealFileRequest", "网络请求失败: " + t.getMessage());
            }
        });
    }

    // TODO 删除数据库照片地址------------------------>
    public void getDeleteSQLFile(String endpoint, String id) {
        ApiService apiService = requestHelpers.createService(ApiService.class);
        String token = jwtUtils.getJwt(context);

        // 打印请求的 URL 和请求体
        System.out.println("请求 URL: " + endpoint);

        Call<ResponseBody> call = apiService.deleteSQLFile(endpoint, token, id);

        // 使用 ResponseBody 处理返回结果
        requestHelpers.enqueueRequest(call, new RetrofitRequestHelper.ResponseHandler<ResponseBody>() {
            @Override
            public void onSuccess(ResponseBody responseBody) {
                try {
                    transformData("getDeleteSQLFile", responseBody);
                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                    responseCallback.failurePost("getDeleteSQLFile", "解析响应数据失败: " + e.getMessage());
                }
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
                responseCallback.failurePost("getDeleteSQLFile", "网络请求失败: " + t.getMessage());
            }
        });
    }

    // TODO 获得动态信息------------------------>
    public void getDynamicsRequest(String endpoint, String dynamicsNum, String username) {
        ApiService apiService = requestHelpers.createService(ApiService.class);
        String token = jwtUtils.getJwt(context);

        // 打印请求的 URL 和请求体
        System.out.println("请求 URL: " + endpoint);
        System.out.println("dynamicsNum: " + dynamicsNum);
        System.out.println("username: " + username);


        Call<ResponseBody> call = apiService.getAllDynamics(endpoint, token, dynamicsNum, username);

        // 使用 ResponseBody 处理返回结果
        requestHelpers.enqueueRequest(call, new RetrofitRequestHelper.ResponseHandler<ResponseBody>() {
            @Override
            public void onSuccess(ResponseBody responseBody) {
                try {
                    transformData("getDynamicsRequest", responseBody);

                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                    responseCallback.failurePost("getDynamicsRequest", "解析响应数据失败: " + e.getMessage());
                }
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
                responseCallback.failurePost("getDynamicsRequest", "网络请求失败: " + t.getMessage());
            }
        });
    }

    // TODO 更新点赞数量和点赞人------------------------>
    public void getUpdateDynamicsRequest(String endpoint, String id, String username, Integer type) {
        ApiService apiService = requestHelpers.createService(ApiService.class);
        String token = jwtUtils.getJwt(context);

        // 打印请求的 URL 和请求体
        System.out.println("请求 URL: " + endpoint);

        Call<ResponseBody> call = apiService.getUpdateDynamicsNumAndPeople(endpoint, token, id, username, type);

        // 使用 ResponseBody 处理返回结果
        requestHelpers.enqueueRequest(call, new RetrofitRequestHelper.ResponseHandler<ResponseBody>() {
            @Override
            public void onSuccess(ResponseBody responseBody) {
                try {
                    transformData("getUpdateDynamicsRequest", responseBody);

                } catch (JSONException e) {
                    e.printStackTrace();
                    responseCallback.failurePost("getUpdateDynamicsRequest", "解析响应数据失败: " + e.getMessage());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
                responseCallback.failurePost("getUpdateDynamicsRequest", "网络请求失败: " + t.getMessage());
            }
        });
    }

    // TODO 发布新动态------------------------>
    public void postNewDynamicsRequest(String endpoint, Dynamics dynamics) {
        ApiService apiService = requestHelpers.createService(ApiService.class);
        String token = jwtUtils.getJwt(context);

        // 打印请求的 URL 和请求体
        System.out.println("请求 URL: " + endpoint);

        Call<ResponseBody> call = apiService.postInsertDynamics(endpoint, token, dynamics);

        // 使用 ResponseBody 处理返回结果
        requestHelpers.enqueueRequest(call, new RetrofitRequestHelper.ResponseHandler<ResponseBody>() {
            @Override
            public void onSuccess(ResponseBody responseBody) throws JSONException, IOException {
                transformData("postNewDynamicsRequest", responseBody);
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
                responseCallback.failurePost("postNewDynamicsRequest", "网络请求失败: " + t.getMessage());
            }
        });
    }

    // TODO 获得宠物百科，宠物分类信息------------------------>
    public void getEncyclopediaRequest(String endpoint, String id) {
        ApiService apiService = requestHelpers.createService(ApiService.class);
        String token = jwtUtils.getJwt(context);

        // 打印请求的 URL 和请求体
        System.out.println("请求 URL: " + endpoint);

        Call<ResponseBody> call = apiService.getEncyclopediaPetSpecies(endpoint, token, id);

        // 使用 ResponseBody 处理返回结果
        requestHelpers.enqueueRequest(call, new RetrofitRequestHelper.ResponseHandler<ResponseBody>() {
            @Override
            public void onSuccess(ResponseBody responseBody) throws JSONException, IOException {
                transformData("getEncyclopediaRequest", responseBody);
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
                responseCallback.failurePost("getEncyclopediaRequest", "网络请求失败: " + t.getMessage());
            }
        });
    }

    // TODO 获得宠物百科，宠物具体信息------------------------>
    public void getPetDetailsRequest(String endpoint, String id) {
        ApiService apiService = requestHelpers.createService(ApiService.class);
        String token = jwtUtils.getJwt(context);

        // 打印请求的 URL 和请求体
        System.out.println("请求 URL: " + endpoint);

        Call<ResponseBody> call = apiService.getEncyclopediaPetDetails(endpoint, token, id);

        // 使用 ResponseBody 处理返回结果
        requestHelpers.enqueueRequest(call, new RetrofitRequestHelper.ResponseHandler<ResponseBody>() {
            @Override
            public void onSuccess(ResponseBody responseBody) throws JSONException, IOException {
                transformData("getPetDetailsRequest", responseBody);
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
                responseCallback.failurePost("getPetDetailsRequest", "网络请求失败: " + t.getMessage());
            }
        });
    }

    // TODO 获得宠物喂养技巧具体信息------------------------>
    public void getPetFeedingSkillRequest(String endpoint, int skillNum) {
        ApiService apiService = requestHelpers.createService(ApiService.class);
        String token = jwtUtils.getJwt(context);

        // 打印请求的 URL 和请求体
        System.out.println("请求 URL: " + endpoint);

        Call<ResponseBody> call = apiService.getPetFeedingSkill(endpoint, token, skillNum);

        // 使用 ResponseBody 处理返回结果
        requestHelpers.enqueueRequest(call, new RetrofitRequestHelper.ResponseHandler<ResponseBody>() {
            @Override
            public void onSuccess(ResponseBody responseBody) throws JSONException, IOException {
                transformData("getPetFeedingSkillRequest", responseBody);
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
                responseCallback.failurePost("getPetFeedingSkillRequest", "网络请求失败: " + t.getMessage());
            }
        });
    }

    // TODO 向用户手机发送message------------------------>
    public void postCustomerRequest(String endpoint, String jsonPayload) {
        ApiService apiService = requestHelpers.createService(ApiService.class);
        String token = jwtUtils.getJwt(context);

        // 打印请求的 URL 和请求体
        System.out.println("请求 URL: " + endpoint);
        System.out.println("请求体: " + jsonPayload);

        RequestBody requestBody = RequestBody.create(jsonPayload, okhttp3.MediaType.get("application/json; charset=utf-8"));

        Call<ResponseBody> call = apiService.postCustomerRequest(endpoint, token, requestBody);

        // 使用 ResponseBody 处理返回结果
        requestHelpers.enqueueRequest(call, new RetrofitRequestHelper.ResponseHandler<ResponseBody>() {
            @Override
            public void onSuccess(ResponseBody responseBody) throws JSONException, IOException {
                transformData("postCustomerRequest", responseBody);
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
                responseCallback.failurePost("postCustomerRequest", "网络请求失败: " + t.getMessage());
            }
        });

    }

    // TODO 宠物日历数据增删改查------------------------>
    public void postPetCalendarRequest(String endpoint, String jsonPayload) {
        ApiService apiService = requestHelpers.createService(ApiService.class);
        String token = jwtUtils.getJwt(context);

        // 打印请求的 URL 和请求体
        System.out.println("请求 URL: " + endpoint);
        System.out.println("请求体: " + jsonPayload);

        RequestBody requestBody = RequestBody.create(jsonPayload, okhttp3.MediaType.get("application/json; charset=utf-8"));

        Call<ResponseBody> call = apiService.postPetCalendar(endpoint, token, requestBody);

        // 使用 ResponseBody 处理返回结果
        requestHelpers.enqueueRequest(call, new RetrofitRequestHelper.ResponseHandler<ResponseBody>() {
            @Override
            public void onSuccess(ResponseBody responseBody) throws JSONException, IOException {
                transformData("postPetCalendarRequest", responseBody);
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
                responseCallback.failurePost("postPetCalendarRequest", "网络请求失败: " + t.getMessage());
            }
        });

    }

    // TODO 我的宠物数据增删改查------------------------>
    public void postMyPetRequest(String endpoint, String jsonPayload) {
        ApiService apiService = requestHelpers.createService(ApiService.class);
        String token = jwtUtils.getJwt(context);

        // 打印请求的 URL 和请求体
        System.out.println("请求 URL: " + endpoint);
        System.out.println("请求体: " + jsonPayload);

        RequestBody requestBody = RequestBody.create(jsonPayload, okhttp3.MediaType.get("application/json; charset=utf-8"));

        Call<ResponseBody> call = apiService.postMyPet(endpoint, token, requestBody);

        // 使用 ResponseBody 处理返回结果
        requestHelpers.enqueueRequest(call, new RetrofitRequestHelper.ResponseHandler<ResponseBody>() {
            @Override
            public void onSuccess(ResponseBody responseBody) throws JSONException, IOException {
                transformData("postMyPetRequest", responseBody);
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
                responseCallback.failurePost("postMyPetRequest", "网络请求失败: " + t.getMessage());
            }
        });

    }

    // TODO 商城数据增删改查------------------------>
    public void postShoppingRequest(String endpoint, String jsonPayload) {
        ApiService apiService = requestHelpers.createService(ApiService.class);
        String token = jwtUtils.getJwt(context);

        // 打印请求的 URL 和请求体
        System.out.println("请求 URL: " + endpoint);
        System.out.println("请求体: " + jsonPayload);

        RequestBody requestBody = RequestBody.create(jsonPayload, okhttp3.MediaType.get("application/json; charset=utf-8"));

        Call<ResponseBody> call = apiService.postPetShopping(endpoint, token, requestBody);

        // 使用 ResponseBody 处理返回结果
        requestHelpers.enqueueRequest(call, new RetrofitRequestHelper.ResponseHandler<ResponseBody>() {
            @Override
            public void onSuccess(ResponseBody responseBody) throws JSONException, IOException {
                transformData("postShoppingRequest", responseBody);
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
                responseCallback.failurePost("postShoppingRequest", "网络请求失败: " + t.getMessage());
            }
        });
    }

    // TODO 订单数据增删改查------------------------>
    public void postOrderRequest(String endpoint, String jsonPayload) {
        ApiService apiService = requestHelpers.createService(ApiService.class);
        String token = jwtUtils.getJwt(context);

        // 打印请求的 URL 和请求体
        System.out.println("请求 URL: " + endpoint);
        System.out.println("请求体: " + jsonPayload);

        RequestBody requestBody = RequestBody.create(jsonPayload, okhttp3.MediaType.get("application/json; charset=utf-8"));

        Call<ResponseBody> call = apiService.postPetOrder(endpoint, token, requestBody);

        // 使用 ResponseBody 处理返回结果
        requestHelpers.enqueueRequest(call, new RetrofitRequestHelper.ResponseHandler<ResponseBody>() {
            @Override
            public void onSuccess(ResponseBody responseBody) throws JSONException, IOException {
                transformData("postOrderRequest", responseBody);
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
                responseCallback.failurePost("postOrderRequest", "网络请求失败: " + t.getMessage());
            }
        });
    }


    // TODO 地址数据增删改查------------------------>
    public void postAddressRequest(String endpoint, String jsonPayload) {
        ApiService apiService = requestHelpers.createService(ApiService.class);
        String token = jwtUtils.getJwt(context);

        // 打印请求的 URL 和请求体
        System.out.println("请求 URL: " + endpoint);
        System.out.println("请求体: " + jsonPayload);

        RequestBody requestBody = RequestBody.create(jsonPayload, okhttp3.MediaType.get("application/json; charset=utf-8"));

        Call<ResponseBody> call = apiService.postPetAddress(endpoint, token, requestBody);

        // 使用 ResponseBody 处理返回结果
        requestHelpers.enqueueRequest(call, new RetrofitRequestHelper.ResponseHandler<ResponseBody>() {
            @Override
            public void onSuccess(ResponseBody responseBody) throws JSONException, IOException {
                transformData("postAddressRequest", responseBody);
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
                responseCallback.failurePost("postAddressRequest", "网络请求失败: " + t.getMessage());
            }
        });
    }

    // TODO 查询全部服务类型------------------------>
    public void postServiceTypeRequest(String endpoint, String jsonPayload) {
        ApiService apiService = requestHelpers.createService(ApiService.class);
        String token = jwtUtils.getJwt(context);

        // 打印请求的 URL 和请求体
        System.out.println("请求 URL: " + endpoint);
        System.out.println("请求体: " + jsonPayload);

        RequestBody requestBody = RequestBody.create(jsonPayload, okhttp3.MediaType.get("application/json; charset=utf-8"));

        Call<ResponseBody> call = apiService.postPetService(endpoint, token, requestBody);

        // 使用 ResponseBody 处理返回结果
        requestHelpers.enqueueRequest(call, new RetrofitRequestHelper.ResponseHandler<ResponseBody>() {
            @Override
            public void onSuccess(ResponseBody responseBody) throws JSONException, IOException {
                transformData("postServiceTypeRequest", responseBody);
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
                responseCallback.failurePost("postServiceTypeRequest", "网络请求失败: " + t.getMessage());
            }
        });
    }

    // TODO 服务的增删改查------------------------>
    public void postServiceInfoRequest(String endpoint, String jsonPayload) {
        ApiService apiService = requestHelpers.createService(ApiService.class);
        String token = jwtUtils.getJwt(context);

        // 打印请求的 URL 和请求体
        System.out.println("请求 URL: " + endpoint);
        System.out.println("请求体: " + jsonPayload);

        RequestBody requestBody = RequestBody.create(jsonPayload, okhttp3.MediaType.get("application/json; charset=utf-8"));

        Call<ResponseBody> call = apiService.postPetService(endpoint, token, requestBody);

        // 使用 ResponseBody 处理返回结果
        requestHelpers.enqueueRequest(call, new RetrofitRequestHelper.ResponseHandler<ResponseBody>() {
            @Override
            public void onSuccess(ResponseBody responseBody) throws JSONException, IOException {
                transformData("postServiceInfoRequest", responseBody);
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
                responseCallback.failurePost("postServiceInfoRequest", "网络请求失败: " + t.getMessage());
            }
        });
    }

    // TODO 获得宠物毛色，具体信息等------------------------>
    public void postPetInfoRequest(String endpoint) {
        ApiService apiService = requestHelpers.createService(ApiService.class);
        String token = jwtUtils.getJwt(context);


        // 打印请求的 URL 和请求体
        System.out.println("请求 URL: " + endpoint);

        Call<ResponseBody> call = apiService.getPetInfo(endpoint, token);

        // 使用 ResponseBody 处理返回结果
        requestHelpers.enqueueRequest(call, new RetrofitRequestHelper.ResponseHandler<ResponseBody>() {
            @Override
            public void onSuccess(ResponseBody responseBody) throws JSONException, IOException {
                transformData("postPetInfoRequest", responseBody);
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
                responseCallback.failurePost("postPetInfoRequest", "网络请求失败: " + t.getMessage());
            }
        });
    }

    // TODO 宠物护理医师的查询------------------------>
    public void postEmpDoctorRequest(String endpoint, String jsonPayload) {
        ApiService apiService = requestHelpers.createService(ApiService.class);
        String token = jwtUtils.getJwt(context);

        // 打印请求的 URL 和请求体
        System.out.println("请求 URL: " + endpoint);
        System.out.println("请求体: " + jsonPayload);

        RequestBody requestBody = RequestBody.create(jsonPayload, okhttp3.MediaType.get("application/json; charset=utf-8"));

        Call<ResponseBody> call = apiService.postEmpDoctor(endpoint, token, requestBody);

        // 使用 ResponseBody 处理返回结果
        requestHelpers.enqueueRequest(call, new RetrofitRequestHelper.ResponseHandler<ResponseBody>() {
            @Override
            public void onSuccess(ResponseBody responseBody) throws JSONException, IOException {
                transformData("postEmpDoctorRequest", responseBody);
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
                responseCallback.failurePost("postEmpDoctorRequest", "网络请求失败: " + t.getMessage());
            }
        });
    }

    // TODO 客服消息查询------------------------>
    public void getMessageRequest(String endpoint, String sender, String receiver, int converseNum) {
        ApiService apiService = requestHelpers.createService(ApiService.class);
        String token = jwtUtils.getJwt(context);

        // 打印请求的 URL 和请求体
        System.out.println("请求 URL: " + endpoint);

        Call<ResponseBody> call = apiService.getMessage(endpoint, token, sender, receiver, converseNum);

        // 使用 ResponseBody 处理返回结果
        requestHelpers.enqueueRequest(call, new RetrofitRequestHelper.ResponseHandler<ResponseBody>() {
            @Override
            public void onSuccess(ResponseBody responseBody) throws JSONException, IOException {
                transformData("getMessageRequest", responseBody);
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
                responseCallback.failurePost("getMessageRequest", "网络请求失败: " + t.getMessage());
            }
        });
    }
}


