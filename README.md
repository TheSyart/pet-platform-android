# 宠物护理平台 - Android 移动端💯💯💯

[Github链接,查看springboot后端源码\~\~\~](https://github.com/TheSyart/pet-platform-springboot.git)  
[Github链接,查看web网页端源码\~\~\~](https://github.com/TheSyart/pet-platform-web.git)

## 1.简介
Android 移动端为爱宠人士提供一站式服务。用户不仅能通过平台便捷购买宠物用品，还可预约宠物服务、了解宠物护理知识、发布动态与其他爱宠人士交流。若遇到问题，可与客服沟通及时解决订单问题。

## 2.技术栈
首先，采用账户密码与手机验证码登录两种方式，方便用户自主选择登录;然后使用Frame加导航菜单，保障移动端用户页面切换的流畅;其次，在不同页面中也使用了多个循环嵌套的RecyclerView，并且在商城中实现了悬浮吸顶和二级菜单的导航功能，大大提高用户体验;然后为了方便用户选择用户地址，集成高德Android SDK提供定位服务生成用户的具体地址。最后，为了保障用户权益，提供了客服咨询服务，让用户发声。

## 3.照片展示  
登录  
<img src="https://github.com/user-attachments/assets/6730fa93-2758-4fe8-9d12-b3e63fc46dbb" width="210px" alt="登录页面截图">  
注册  
<img src="https://github.com/user-attachments/assets/6b304ad6-bfb1-4879-bfbb-1e492adf2ecc" width="210px" alt="注册页面截图">  
主页面功能展示  
<img src="https://github.com/user-attachments/assets/70babe15-135a-4a62-9b4d-d71ebef77397" width="210px" alt="主页面功能截图 1">
<img src="https://github.com/user-attachments/assets/12c8134e-adad-4b8d-a6e0-f3b73f928717" width="210px" alt="主页面功能截图 2">
<img src="https://github.com/user-attachments/assets/8b8c23d1-8075-4ad4-accd-67da1c476f17" width="210px" alt="主页面功能截图 3">
<img src="https://github.com/user-attachments/assets/a502e3cc-fb87-4feb-8cfa-c0c8fef0538e" width="210px" alt="主页面功能截图 4">
<img src="https://github.com/user-attachments/assets/622f4099-8b25-4ff2-8997-40e383351548" width="210px" alt="主页面功能截图 5">
<img src="https://github.com/user-attachments/assets/3548b397-64b4-48cc-90e1-9dca94202e23" width="210px" alt="主页面功能截图 6">
<img src="https://github.com/user-attachments/assets/9c67fb30-fb24-48a2-b0f2-1b08970e551b" width="210px" alt="主页面功能截图 7">

<img src="https://github.com/user-attachments/assets/a6f4d999-87b5-4d66-8d00-fe5c0b590635" width="210px" alt="主页面功能截图 8">
<img src="https://github.com/user-attachments/assets/09847bfe-9a38-4b8f-84b0-c0064edfd097" width="210px" alt="主页面功能截图 9">
<img src="https://github.com/user-attachments/assets/7b218753-0c66-47b2-8a86-c8939a3cc188" width="210px" alt="主页面功能截图 10">
<img src="https://github.com/user-attachments/assets/1b0e2894-5634-451f-9715-efe7c0415851" width="210px" alt="主页面功能截图 11">

地址  
<img src="https://github.com/user-attachments/assets/a28d662b-6834-4817-a5ba-90e330dead4a" width="210px" alt="地址页面截图 1">
<img src="https://github.com/user-attachments/assets/a78f7f39-c440-437c-96e1-97d48f300442" width="210px" alt="地址页面截图 2">

客服咨询  
<img src="https://github.com/user-attachments/assets/5e9945da-e496-420e-b8b0-601ca8331e32" width="210px" alt="客服咨询页面截图 1">
<img src="https://github.com/user-attachments/assets/6797a25b-1fd6-4411-9d23-bad199fe42f1" width="210px" alt="客服咨询页面截图 2">
<img src="https://github.com/user-attachments/assets/76266bb2-1bfb-4d46-9822-e0a1f2f38820" width="210px" alt="客服咨询页面截图 3">  


## 4.配置！  

### 1.AndroidMainifest.xml文件中  
<img src="https://github.com/user-attachments/assets/9dbce39e-29cb-4189-b61f-a65f537de5b0" width="210px" alt="高德地图秘钥"> 

### 2.网络配置（将下列文件中，使用自己的ip地址补全)

app\src\main\res\xml\network_security_config.xml文件下  
<img src="https://github.com/user-attachments/assets/32d94cf9-6c5e-4955-974e-b12ac63890a6" width="210px" alt="网络配置1"> 

app\src\main\res\values\strings.xml文件下  
<img src="https://github.com/user-attachments/assets/49bfce54-9f43-4da5-bc2c-1a1cbf95dfb5" width="210px" alt="网络配置2"> 

