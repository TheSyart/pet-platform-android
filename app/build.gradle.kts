import com.android.ide.common.repository.main

plugins {
    id("com.android.application")
}


android {
    namespace = "com.example.petstore"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.petstore"
        minSdk = 29
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    sourceSets {
        getByName("main") {
            jniLibs.srcDirs("src/main/jniLibs")
        }
    }



    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}


dependencies {
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.8.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    implementation("com.google.ai.edge.litert:litert:1.0.1")
    implementation("androidx.emoji:emoji-appcompat:1.1.0")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    // 网络请求
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.9.0")

    // Android原生ui下拉刷新
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.0.0")

    // 下拉刷新，上拉加载更多
    implementation("io.github.scwang90:refresh-layout-kernel:2.1.0")
    implementation("io.github.scwang90:refresh-header-classics:2.1.0")
    implementation("io.github.scwang90:refresh-footer-classics:2.1.0")

    // ViewPager 控件
    implementation("androidx.viewpager2:viewpager2:1.0.0")

    // 设置图片
    implementation("com.github.bumptech.glide:glide:4.12.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.12.0")

    // 高德地图
    implementation("com.amap.api:search:9.7.0")
    implementation("com.amap.api:3dmap:9.2.0")
    implementation("com.amap.api:location:6.4.2")

    // XXPermissions 库依赖
    implementation("com.github.getActivity:XXPermissions:18.3")

    // circlemenu 库依赖
    implementation("com.github.imangazalievm:circlemenu:3.0.0")


    // 悬浮球
    implementation("com.github.princekin-f:EasyFloat:2.0.4")

    // 用于感知生命周期等相关操作
    implementation ("androidx.lifecycle:lifecycle-runtime-ktx:2.4.0")

    // 监听软键盘
    implementation ("net.yslibrary.keyboardvisibilityevent:keyboardvisibilityevent:3.0.0-RC1")

    // 谷歌视频播放器
    implementation ("com.google.android.exoplayer:exoplayer-core:2.15.1")
    implementation ("com.google.android.exoplayer:exoplayer-ui:2.15.1")

    // 一个组件库
    implementation ("io.github.lilongweidev:easyview:1.0.5")

}