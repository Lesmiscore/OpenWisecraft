-keepnames public class * extends android.app.Activity {
}
-keepnames public class * extends android.app.Service {
}
-keepnames public class * extends android.content.BroadcastReceiver {
}
-dontwarn com.google.common.**
-dontwarn com.google.appengine.**
-dontwarn com.google.android.gms.**
-dontwarn com.google.api.client.**
-assumenosideeffects class android.util.Log {
    public static *** v(...);
    public static *** d(...);
    public static *** i(...);
    public static *** w(...);
    public static *** e(...);
}
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}
-keep public class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
    public void set*(...);
}
#-keep public class **.R {
#  public *;
#}
#-keep public class **.BuildConfig {
#  public *;
#}
-keep public class org.jsoup.** {
  public *;
}
-keepattributes Signature
-keepattributes *Annotation*
-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.stream.** { *; }
-keep class com.google.gson.examples.android.model.** { *; }
-dontwarn org.apache.**
-keep class org.apache.** {
  *;
}
-dontwarn android.support.v4.app.**
-dontwarn com.nao20010128nao.McServerList.sites.**
-keep class uk.co.chrisjenx.calligraphy.** {
  *;
}
-keep class android.support.** {
  *;
}

-keep class com.firebase.** { *; }
-keep class org.apache.** { *; }
-keepnames class com.fasterxml.jackson.** { *; }
-keepnames class javax.servlet.** { *; }
-keepnames class org.ietf.jgss.** { *; }
-dontwarn org.apache.**
-dontwarn org.w3c.dom.**
-dontwarn android.**

-keepattributes InnerClasses,EnclosingMethod

-keep class ** {
	@com.google.gson.annotations.** <fields>;
}

-keep class com.nao20010128nao.McServerList.sites.Pmmp_Jp_Net$** {
 @com.google.gson.annotations.** <fields>;
}
