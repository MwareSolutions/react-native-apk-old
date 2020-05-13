package be.skyzohlabs.rnapk;

import android.content.pm.PackageManager;
import android.content.pm.PackageInfo;
import android.content.pm.ApplicationInfo;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Binder;
import android.provider.Settings;
import androidx.core.content.FileProvider;
import android.util.Log;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeArray;
import com.facebook.react.bridge.WritableNativeMap;

import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.io.File;

import javax.annotation.Nullable;

public class ReactNativeAPKModule extends ReactContextBaseJavaModule {

  private final ReactApplicationContext reactContext;

  public ReactNativeAPKModule(ReactApplicationContext reactContext) {
    super(reactContext);
    this.reactContext = reactContext;
  }

  @Override
  public String getName() {
    return "ReactNativeAPK";
  }

  @ReactMethod
  public void isAppInstalled(String packageName, Callback cb) {
    try {
      PackageInfo pInfo = this.reactContext.getPackageManager().getPackageInfo(packageName,
          PackageManager.GET_ACTIVITIES);

      cb.invoke(true);
    } catch (PackageManager.NameNotFoundException e) {
      cb.invoke(false);
    }
  }

  @ReactMethod
  public void installApp(String packagePath) {
    try {
      String permission="666";
      String command = "chmod " + permission + " " + packagePath;
      Runtime runtime = Runtime.getRuntime();
      runtime.exec(command);
    } catch (IOException e) {
      e.printStackTrace();
    }
    File toInstall = new File(packagePath);
    toInstall.setExecutable(true);
    if(toInstall.exists() == true &&  toInstall.canExecute() == true) {
      if (Build.VERSION.SDK_INT >= 24) {
          String callingPackageName = this.reactContext.getPackageManager().getNameForUid(Binder.getCallingUid());
          Uri apkUri = FileProvider.getUriForFile(this.reactContext, callingPackageName + ".fileprovider", toInstall);
          Intent intent = new Intent(Intent.ACTION_INSTALL_PACKAGE);
          intent.setData(apkUri);
          intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
          intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
          this.reactContext.startActivity(intent);
      } else {
//          Uri apkUri = Uri.fromFile(toInstall);
//         // Intent intent = new Intent(Intent.ACTION_VIEW);
//        Intent intent = new Intent(Intent.ACTION_INSTALL_PACKAGE);
//          intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
//          intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//          this.reactContext.startActivity(intent);

        Uri apkUri = Uri.fromFile(toInstall);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        this.reactContext.startActivity(intent);
      }
    }
  }

  @ReactMethod
  public void uninstallApp(String packageName, Callback cb) {
    Intent intent = new Intent(Intent.ACTION_DELETE);
    intent.setData(Uri.parse("package:" + packageName));
    this.reactContext.startActivity(intent);
    cb.invoke(true);
  }

  @ReactMethod
  public void getAppVersion(String packageName, Callback cb) {
    try {
      PackageInfo pInfo = this.reactContext.getPackageManager().getPackageInfo(packageName, 0);

      cb.invoke(pInfo.versionName);
    } catch (PackageManager.NameNotFoundException e) {
      cb.invoke(false);
    }
  }

  @ReactMethod
  public void getApps(Promise cb) {
    WritableArray resultData = new WritableNativeArray();
    PackageManager pm = getReactApplicationContext().getPackageManager();
    List<ApplicationInfo> packages = pm.getInstalledApplications(0);
    for (ApplicationInfo packageInfo : packages) {
      try {

        if (getReactApplicationContext().getPackageManager().getLaunchIntentForPackage(packageInfo.packageName) != null) {

          Drawable appIcon = packageInfo.loadIcon(pm);
          Bitmap appIconBitmap = ((BitmapDrawable)appIcon).getBitmap();
          WritableMap info = new WritableNativeMap();

          File mypath = new File( this.reactContext.getFilesDir(), packageInfo.loadLabel(pm).toString().replace(" ","_")  + ".png");
          if(!mypath.exists()) {
            FileOutputStream fos = null;
            info.putString("status", "create image");
            fos = new FileOutputStream(mypath);
            appIconBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
          }else{
            info.putString("status", "image existed");
          }

          info.putString("appname", packageInfo.loadLabel(pm).toString());
          info.putString("icon", this.reactContext.getFilesDir() + packageInfo.loadLabel(pm).toString().replace(" ","_")  + ".png");
          info.putString("url", Uri.fromFile(mypath).toString());
          info.putString("packagename", packageInfo.packageName);
          resultData.pushMap(info);
        }
      } catch (Exception ex) {
        System.err.println("Exception: " + ex.getMessage());
      }
    }
    cb.resolve(resultData);
    //cb.invoke(resultData);
  }
  @ReactMethod
  public void getNonSystemApps(Callback cb) {
    WritableArray resultData = new WritableNativeArray();
    PackageManager pm = getReactApplicationContext().getPackageManager();
    List<ApplicationInfo> packages = pm.getInstalledApplications(0);
    for (ApplicationInfo packageInfo : packages) {
      try {

        if (getReactApplicationContext().getPackageManager().getLaunchIntentForPackage(packageInfo.packageName) != null) {

          Drawable appIcon = packageInfo.loadIcon(pm);
          Bitmap appIconBitmap = ((BitmapDrawable)appIcon).getBitmap();
          WritableMap info = new WritableNativeMap();

          File mypath = new File( this.reactContext.getFilesDir(), packageInfo.loadLabel(pm).toString().replace(" ","_")  + ".png");
          if(!mypath.exists()) {
            FileOutputStream fos = null;
            info.putString("status", "create image");
            fos = new FileOutputStream(mypath);
            appIconBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
          }else{
            info.putString("status", "image existed");
          }
          //saveToInternalStorage(appIconBitmap, packageInfo.loadLabel(pm).toString().replace(" ","_"));


          info.putString("appname", packageInfo.loadLabel(pm).toString());
          info.putString("url", packageInfo.packageName);
          info.putString("icon",this.reactContext.getFilesDir() + packageInfo.loadLabel(pm).toString().replace(" ","_")  + ".png");

          resultData.pushMap(info);
        }
      } catch (Exception ex) {
        System.err.println("Exception: " + ex.getMessage());
      }
    }
    cb.invoke(resultData);
  }
  public void saveToInternalStorage(Bitmap bitmapImage, String imageName){
    File mypath = new File( this.reactContext.getFilesDir(), imageName + ".png");
    if(!mypath.exists()) {
      FileOutputStream fos = null;
      try {
        fos = new FileOutputStream(mypath);
        bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        // Log.d(TAG, "image created");
      } catch (Exception e) {
        e.printStackTrace();
      } finally {
        try {
          fos.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }else{
      // Log.d(TAG, "Image exists ===============================>" + imageName);
    }
  }
  @ReactMethod
  public void runApp(String packageName) {
    // TODO: Allow to pass Extra's from react.
    Intent launchIntent = this.reactContext.getPackageManager().getLaunchIntentForPackage(packageName);
    //launchIntent.putExtra("test", "12331");
    this.reactContext.startActivity(launchIntent);
  }

  /*@Override
  public @Nullable Map<String, Object> getConstants() {
      Map<String, Object> constants = new HashMap<>();
  
      constants.put("getApps", getApps());
      constants.put("getNonSystemApps", getNonSystemApps());
      return constants;
  }*/
}