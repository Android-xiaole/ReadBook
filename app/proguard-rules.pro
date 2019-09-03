# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
-ignorewarnings

-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontskipnonpubliclibraryclassmembers
-dontoptimize

-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*
-keepattributes *Annotation*
-keepattributes Signature
-keepattributes SourceFile,LineNumberTable
-allowaccessmodification
-verbose

-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.support.multidex.MultiDexApplication
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class * extends android.view.View
-keep public class * implements com.jj.base.mvp.IPresenter
-keep public class * implements com.jj.base.mvp.IView
-keep public class * extends android.widget.PopupWindow

-keep class android.support.** {*;}
-keep public class * extends android.support.v4.**
-keep public class * extends android.support.v7.**
-keep public class * extends android.support.annotation.**
-keep public class com.jj.comics.widget.RecycleViewDivider

-keep class com.jj.comics.common.** {
    *;
}
-keep class com.jj.comics.constants.** {
    *;
}
-keep class com.jj.comics.db.** {
    *;
}
-keep class com.jj.comics.net.** {
    *;
}
-keep class com.jj.comics.util.** {
    *;
}
-keep class com.davemorrissey.labs.subscaleview.** {
    *;
}
-keep class jc.pay.** {
    *;
}
-keep class jc.product.** {
    *;
}

-keepclasseswithmembernames class * {
    native <methods>;
}

-keepclassmembers class * extends android.app.Activity{
    public void *(android.view.View);
}

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
-keep public class * extends android.view.View{
    *** get*();
    void set*(***);
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}
# 保留R下面的资源
-keep class **.R$* {
 *;
}
#不混淆资源类下static的
-keepclassmembers class **.R$* {
    public static <fields>;
}

-keepclassmembers class * extends android.webkit.WebViewClient {
    public void *(android.webkit.WebView, jav.lang.String);
    public void *(android.webkit.WebView, java.lang.String, android.graphics.Bitmap);
    public boolean *(android.webkit.WebView, java.lang.String);
}

-assumenosideeffects class android.util.Log {
   public static *** v(...);
   public static *** d(...);
   public static *** i(...);
   public static *** w(...);
 }

## ARouter
-keep public class com.alibaba.android.arouter.routes.**{*;}
-keep public class com.alibaba.android.arouter.facade.**{*;}
-keep class * implements com.alibaba.android.arouter.facade.template.ISyringe{*;}

# If you use the byType method to obtain Service, add the following rules to protect the interface:
-keep interface * implements com.alibaba.android.arouter.facade.template.IProvider


##Glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}

##eventbus
-keepclassmembers class * {
@org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }
# Only required if you use AsyncExecutor
-keepclassmembers class * extends org.greenrobot.eventbus.util.ThrowableFailureEvent {
<init>(java.lang.Throwable);
}


## banner
-keep class com.youth.banner.** {
    *;
}

##Gson
# Gson uses generic type information stored in a class file when working with fields. Proguard
# removes such information by default, so configure it to keep all of it.
# Gson specific classes
-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.stream.** { *; }
# Application classes that will be serialized/deserialized over Gson
-keep class com.jj.comics.model.** { *; }  ##这里需要改成解析到哪个  javabean
-keep class com.jj.base.model.** { *; }


## Butterknife
-keep public class * implements butterknife.Unbinder { public <init>(**, android.view.View); }
-keep class butterknife.** { *; }
-dontwarn butterknife.internal.**
-keep class **$$ViewBinder { *; }
-keepclasseswithmembernames class * {
   @butterknife.* <fields>;
}
-keepclasseswithmembernames class * {
   @butterknife.* <methods>;
}


# Retrofit
-keep class retrofit2.** { *; }
-dontwarn retrofit2.**
-keepattributes Exceptions

-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.annotation.**
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase

# RxJava RxAndroid
-dontwarn sun.misc.**
-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
    long producerIndex;
    long consumerIndex;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode producerNode;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueConsumerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode consumerNode;
}
-dontnote rx.internal.util.PlatformDependent
-dontwarn org.mockito.**
-dontwarn org.junit.**
-dontwarn org.robolectric.**
-keep class io.reactivex.** { *; }
-keep interface io.reactivex.** { *; }
-keep class com.squareup.okhttp.** { *; }
-keep interface com.squareup.okhttp.** { *; }
-dontwarn com.squareup.okhttp.**
-dontwarn io.reactivex.**
-keepclasseswithmembers class * {
    @retrofit.http.* <methods>;
}
-dontwarn java.lang.invoke.*
-keep class io.reactivex.schedulers.Schedulers {
    public static <methods>;
}
-keep class io.reactivex.schedulers.ImmediateScheduler {
    public <methods>;
}
-keep class io.reactivex.schedulers.TestScheduler {
    public <methods>;
}
-keep class io.reactivex.schedulers.Schedulers {
    public static ** test();
}


-dontwarn io.reactivex.internal.util.unsafe.**

#RxLifeCycle
-keep class com.trello.rxlifecycle.** { *; }
-keep interface com.trello.rxlifecycle.** { *; }

##RxCache
-dontwarn io.rx_cache2.internal.**
-keep class io.rx_cache2.internal.Record { *; }
-keepclassmembers enum io.rx_cache2.Source { *; }
-keepclassmembernames class * { @io.rx_cache2.* <methods>; }

-keep class io.victoralbertos.jolyglot.** { *; }
-keep interface io.victoralbertos.jolyglot.** { *; }


#BaseRecyclerViewAdapter 混淆
-keep class com.chad.library.adapter.** {
*;
}
-keep public class * extends com.chad.library.adapter.base.BaseQuickAdapter
-keep public class * extends com.chad.library.adapter.base.BaseViewHolder
-keepclassmembers  class **$** extends com.chad.library.adapter.base.BaseViewHolder {
    <init>(...);
}

-dontwarn org.conscrypt.OpenSSLProvider
-dontwarn javax.lang.model.element.Element
-dontwarn org.conscrypt.Conscrypt

#greendao3.2.0,此是针对3.2.0，如果是之前的，可能需要更换下包名
-keep class org.greenrobot.greendao.**{*;}
-keepclassmembers class * extends org.greenrobot.greendao.AbstractDao {
public static java.lang.String TABLENAME;
}
-keep class **$Properties
-keep public class * implements org.greenrobot.greendao.converter.PropertyConverter

#保留跟 javascript相关的属性
-keepattributes JavascriptInterface

#友盟
-dontwarn com.umeng.**
-dontwarn com.taobao.**
-dontwarn anet.channel.**
-dontwarn anetwork.channel.**
-dontwarn org.android.**
-dontwarn org.apache.thrift.**
-dontwarn com.xiaomi.**
-dontwarn com.huawei.**
-keep class com.taobao.** {*;}
-keep class org.android.** {*;}
-keep class anet.channel.** {*;}
-keep class com.umeng.** {*;}
-keep class com.xiaomi.** {*;}
-keep class com.huawei.** {*;}
-keep class org.apache.thrift.** {*;}
-keep class com.alibaba.sdk.android.**{*;}
-keep class com.ut.**{*;}
-keep class com.ta.**{*;}
-keep public class **.R$*{
    public static final int *;
 }
-keepclassmembers class * {
   public <init> (org.json.JSONObject);
}

#Alipay
-keep class com.alipay.android.app.IAlixPay{*;}
-keep class com.alipay.android.app.IAlixPay$Stub{*;}
-keep class com.alipay.android.app.IRemoteServiceCallback{*;}
-keep class com.alipay.android.app.IRemoteServiceCallback$Stub{*;}
-keep class com.alipay.sdk.app.PayTask{ public *;}
-keep class com.alipay.sdk.app.AuthTask{ public *;}

#bugly
-dontwarn com.tencent.bugly.**
-keep public class com.tencent.bugly.**{*;}
-keep class android.support.**{*;}