-keep public class * extends android.app.Activity {
   *;
}
-keep public class * extends android.app.Service {
   *;
}
-keep public class * extends android.content.BroadcastReceiver {
   *;
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
-keep public class **.R {
  public *;
}
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
-keep public class com.nao20010128nao.Wisecraft.TheApplication {
   public static final android.graphics.Typeface **;
}
-keep public class com.nao20010128nao.Wisecraft.misc.Server {
   public <fields>;
}
-keep public class com.nao20010128nao.WRcon.misc.Server {
   public <fields>;
}
-keep public class com.nao20010128nao.Wisecraft.collector.CollectorMain {
   public <fields>;
}
-keep public class com.nao20010128nao.WRcon.collector.CollectorMain {
   public <fields>;
}
-keep public class * implements com.nao20010128nao.Wisecraft.misc.pinger.ServerPingResult {
   *;
}
-keep public class com.nao20010128nao.Wisecraft.misc.pinger.** {
   *;
}
-keep public class com.nao20010128nao.Wisecraft.collector.CollectorMain$** {
   public <fields>;
}
-keep public class com.nao20010128nao.WRcon.collector.CollectorMain$** {
   public <fields>;
}
