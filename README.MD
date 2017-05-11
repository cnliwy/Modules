android 组件化
===

###一、组件化的意义
代码级解耦我们可以通过MVP、MVVM等去实现，但是随着公司业务的逐步增多，项目体积也在不断增长，这时候就需要进行一种项目级的解耦了，把各个业务单独出一个组件，既方便调试又可以做到分工合作，否则一个项目无论前期设计多合理多优雅也会慢慢写成一坨屎的。。。

###二、组件化的实现
组件化基本实现原理就是编译期间各个组件的依赖整合，apk还是一个apk。具体的实现思路就是将一个项目分离成一个容器APP（用来加载各个组件），一个基础Library（组件所需要的一些基类、公用第三方库和资源文件等），和多个组件Module（具体的业务实现）的形式。
好，下面开始简单的模拟下如何一步一步实现组件化。

###三、Music!Action!
假设我们要有一个叫做Modules的项目，Modules有两个主要功能：音乐和读书，我们就先按这个去创建项目。
####1、新建项目Modules
####2、新建Library
    右键工程，new->module->Android Library  名称就叫library好了
    在library里我们可以引入一些第三方库，比如Okhttp,Rxjava,Logger.Retrofit等等，也可以引入一些Utils工具类等等。library里的资源可以被依赖它的Moudle所引用的，包括string、color、drawable等等。
####3、新建音乐Module和读书Module
    右键工程，new->module->Phone & Tablet Module,module就叫music吧
    同理，新建一个叫book的module
###4、更改gradle配置
    每一个Module都需要两种形态，分别是：测试阶段可以单独运行的Application形态，以及发布版作为library融入app的library形态。为了区分这两种形态，按理说我们应该需要两套配置文件，为了方便起见，我们通过动态修改build配置文件来达到此目的。
    1）、在gradle.properties（Project Properties）里新建一个boolean值的变量isDebug用以标识组件的形态。代码如下：
    org.gradle.jvmargs=-Xmx1536m
    #将Module作为Application还是library, false->library  true->Application
    isDebug=false
    2)修改music的build.gradle文件,内容如下：
    
       if (isDebug.toBoolean()){
        // 为true时表示此Module是一个独立的应用
            apply plugin: 'com.android.application'
        }else{
        // 为false时表示此Module是一个library
            apply plugin: 'com.android.library'
        }
    android {
        compileSdkVersion 25
        buildToolsVersion "25.0.2"
    
        defaultConfig {
            // 作为library时不能有applicationId,只有作为一个独立应用时才能够如下设置
            if (isDebug.toBoolean()){
                applicationId "com.liwy.music"
            }
            minSdkVersion 14
            targetSdkVersion 25
            versionCode 1
            versionName "1.0"
    
            testInstrumentationRunner "android.support.test.runner.AndroidJUnitRunner"
    
        }
        buildTypes {
            release {
                minifyEnabled false
                proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.pro'
            }
        }
        sourceSets {
            main {
                if (isDebug.toBoolean()) {
                    manifest.srcFile 'src/main/debug/AndroidManifest.xml'
                    java.srcDirs += "src/main/debug/java/";
                } else {
                    manifest.srcFile 'src/main/release/AndroidManifest.xml'
                    java.srcDirs += "src/main/release/java/";
                }
            }
        }
        // 为防止资源冲突，设置资源别名，music下所有的资源名称都要以“music_”开头，否则会报错的哦！
        resourcePrefix "music_"
    }
    
    dependencies {
        // 加载公用library
        compile project(':library')
    }
    
    
    
    同理，按照此方式设置Book的build.gradle。
    
    3) 配置AndroidManifest.xml文件
        由于不同形态加载不同的AndroidManifest.xml文件，切换至Project目录状态下，在src/main/下新建两个目录：debug和release，并分别拷入AndroidManifest.xml，两种形态下的AndroidManifest.xml的配置是不一样的哦，至少library形态下的.xml没有启动activity没有theme没有icon等等， 内容如下：
        library状态下的AndroidManifest.xml文件内容：
            <?xml version="1.0" encoding="utf-8"?>
            <manifest xmlns:android="http://schemas.android.com/apk/res/android" package="com.liwy.music">
                <application 
                    android:allowBackup="true"
                    android:supportsRtl="true">
                    <activity android:name=".MusicActivity"></activity>
                </application>
            </manifest>
      4）根据build.gradle里配置的资源别名，修改module下的资源名称，包括layout,drawable,string,color等属性，反正res文件夹下面的资源名称都改掉好了，反正不改会报错。。。
      5）按2）-> 4）的步骤设置book。
      
###5、在app里引入这两个Module
    1)修改app的build.gradle文件
    dependencies {
        if (!isDebug.toBoolean()) {
            compile project(':music')
            compile project(':book')
        } else {
            compile project(':library')
        }
    }
    2)在App的MainActivity里增加两个按钮，并设置其点击事件，使其一个跳转至music下的MusicActivity，一个跳转至book下的BookAcitivity。
    
    package com.liwy.modules;
    import android.content.Intent;
    import android.support.v7.app.AppCompatActivity;
    import android.os.Bundle;
    import android.view.View;
    
    import com.liwy.book.BookActivity;
    import com.liwy.music.MusicActivity;
    
    public class MainActivity extends AppCompatActivity {
    
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
            findViewById(R.id.btn_music).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent  = new Intent(MainActivity.this, MusicActivity.class);
                    startActivity(intent);
                }
            });
    
            findViewById(R.id.btn_book).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent  = new Intent(MainActivity.this, BookActivity.class);
                    startActivity(intent);
                }
            });
        }
    }
    
###6、运行
    如果没有搞错的话，是可以正常跳转至MusicActivity和BookActivity页面。
    如果搞错了的话，那你还犹豫什么...查去吧...肯定上面步骤有遗漏的地方...
###7、引入路由框架
     细心的同学可能会发现一个问题，那就是在app的MainActivity为了跳转引用了book和music两个组件的页面类BookActivity和MusicActivity。如果此时我将gradle.properties(Project Properties)里的isDebug设置为true，表示两个组件都是独立的application了，而app也不会再去依赖这两个Module，那么问题就来了，在NainActivity里引入的类将无法找到路径，从而导致报错。
     为了解决这个问题，达到组件间解耦的目的，我们引入一个开源的框架ActivityRouter。
     1）在library的build.gradle里引入activityrouter:
     compile 'com.github.mzule.activityrouter:activityrouter:1.2.2'
     2)在app，book和music的build.gradle里分别引入activityrouter的编译组件
      annotationProcessor 'com.github.mzule.activityrouter:compiler:1.1.7'
     3）在app的AndroidManifest.xml里加入如下代码：
     
      <activity
                 android:name="com.github.mzule.activityrouter.router.RouterActivity"
                 android:theme="@android:style/Theme.NoDisplay">
                 <intent-filter>
                     <action android:name="android.intent.action.VIEW" />
     
                     <category android:name="android.intent.category.DEFAULT" />
                     <category android:name="android.intent.category.BROWSABLE" />
                       <!-- jump_scheme = web -->
                     <data android:scheme="@string/jump_scheme" />
                 </intent-filter>
             </activity>
     
     4)在app,book和music各自的包下分别创建一个空类，在类名上方加入注解@Module("app")，如下：
     
     // 表示此类所在的Module的组件名称为app
     @Module("app")
     public class AppModule {
     }
     
     5）在app所在的Module的Application类上增加注解：@Modules({"app","music","book"})
     如果你没有重写Application类，那就新建一个吧...别忘了在app的AndroidManifest.xml里引入哦！
     
     6）配置页面类的跳转路径
     此次先拿BookActivity试水吧，在类名上方增加注解：@Router("book")
     
     @Router("book")
     public class BookActivity extends AppCompatActivity {
     
         @Override
         protected void onCreate(Bundle savedInstanceState) {
             super.onCreate(savedInstanceState);
             setContentView(R.layout.book_activity_book);
         }
     }
     
     7）修改app的MainActivity下前往BookActivity页面的跳转方式，修改为：
     
      findViewById(R.id.btn_book).setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {
                     Routers.open(MainActivity.this, Uri.parse("web://book"));
                 }
             });
       跳转路径的组成方式"web://book" = (AndroidManifest.xml里的jump_scheme值web) + :// + (页面Activity上增加的Router注解值book)
     
  ###8、最终幻想
    ok!反正我是完美运行啊，这样app里页面访问组件类的页面就无需引入其类路径了，传值也可通过注解配置进行传递，activityrouter的传值使用详情大家还是百度一下吧，网上资料还是很多的。好了，整个组件化的简单使用就介绍到这里，具体的运用还是要仁者见仁智者见智，尽情的翱翔吧！该下班吃饭了，走起！
     
        
    