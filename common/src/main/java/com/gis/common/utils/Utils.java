package com.gis.common.utils;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.Settings;
import android.text.TextUtils;

import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.FileProvider;

import com.hjq.toast.Toaster;

import java.io.File;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.net.URLEncoder;


/**
 * @author czf
 */
public class Utils {

    private static Utils utils;

    public static Utils getInstance() {
        if (utils == null) {
            synchronized (Utils.class) {
                if (utils == null) {
                    utils = new Utils();
                }
            }
        }
        return utils;
    }

    /**
     * url 编码
     * @param input
     * @param charsetName
     * @return
     */
    public String encode(String input, String charsetName){
        try {
            return URLEncoder.encode(input, charsetName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * url 解码
     * @param input
     * @param charsetName
     * @return
     */
    public String decode(String input, String charsetName){
        try {
            return URLDecoder.decode(input, charsetName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 获取文件Uri
     *
     * @param context
     * @param file
     * @return
     */
    public Uri getUriForFile(Context context, File file) {
        if (context == null || file == null) {
            return null;
        }
        Uri fileUri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            fileUri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", file);
        } else {
            fileUri = Uri.fromFile(file);
        }
        // 处理中文转义
        String uriPath = Uri.decode(fileUri.toString());
        Uri decodeUri = Uri.parse(uriPath);
        context.grantUriPermission(context.getPackageName(), decodeUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        context.grantUriPermission(context.getPackageName(), decodeUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
        return decodeUri;
    }

    /**
     * 通过指定pkg获取文件Uri，允许其访问应用内部文件
     *
     * @param context
     * @param file
     * @return
     */
    public Uri getUriForFile(Context context, File file, String pkg) {
        if (context == null || file == null) {
            return null;
        }
        Uri fileUri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            fileUri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", file);
        } else {
            fileUri = Uri.fromFile(file);
        }
        // 处理中文转义
        String uriPath = Uri.decode(fileUri.toString());
        Uri decodeUri = Uri.parse(uriPath);
        context.grantUriPermission(pkg, decodeUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        context.grantUriPermission(pkg, decodeUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
        return decodeUri;
    }

    /**
     * 根据文件后缀获取文件MIME类型
     * MediaMetadataRetriever 不能处理访问本地文件的权限，如要处理本地文件请使用 getFileContentType
     * MediaMetadataRetriever 类只支持从特定来源（例如网络地址或媒体播放器）获取多媒体数据，而不能直接访问本地文件系统
     * @param path
     * @return
     */
    public String getMimeType(Context context, Uri path) {
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        String mime = "*/*";
        if (path != null) {
            try {
                mmr.setDataSource(context, path);
                mime = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE);
            } catch (Exception e) {
                e.printStackTrace();
                return mime;
            }
        }
        return mime;
    }

    /**
     * 根据文件后缀获取文件ContentType，不推荐使用，因为不是通过provider获取的URI
     *
     * @param filePath 获取本地文件路径
     * @return ContentType
     */
    public String getFileContentType(String filePath) {
        String contentType = "*/*";
        URLConnection urlConnection = null;
        try {
            File file = new File(filePath);
            URL fileUrl = file.toURI().toURL();
            urlConnection = fileUrl.openConnection();
            contentType = urlConnection.getContentType();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != urlConnection){
                    urlConnection.getInputStream().close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return contentType;
    }

    /**
     * 根据文件Uri获取文件ContentType
     * @param context
     * @param uri
     * @return  MIME
     */
    public String getFileContentType(Context context, Uri uri){
        return context.getContentResolver().getType(uri);
    }

    /**
     * 打开系统中安装的对应程序预览
     * @param path 文件路径
     */
    public void openReaderByMIME(Context context, String path){
        File file = new File(path);
        if (file.exists()) {
            try {
                String uriPath = Uri.decode(getUriForFile(context, file).toString());
                Uri uri = Uri.parse(uriPath);
                String mimeType = getFileContentType(context, uri);
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setDataAndType(uri, mimeType);
                context.startActivity(intent);
            }
            catch (Exception e) {
                e.printStackTrace();
                Toaster.showShort("没有可查看\""+file.getName()+"\"的应用程序");
            }
        }
    }

    /**
     * 跳转到应用详情界面
     *
     * @param context
     */
    public void startAppDetailSetting(Context context) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setData(Uri.parse("package:" + context.getPackageName()));
        } else if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.FROYO) {
            intent.setAction(Intent.ACTION_VIEW);
            intent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
            intent.putExtra("com.android.settings.ApplicationPkgName", context.getPackageName());
        }
        context.startActivity(intent);
    }

    /**
     * 跳转到应用通知权限页面
     *
     * @param context
     */
    public void startNotificationSetting(Context context) {
        try {
            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                //这种方案适用于 API 26, 即8.0（含8.0）以上可以用
                intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
                intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.getPackageName());
                intent.putExtra(Notification.EXTRA_CHANNEL_ID, context.getApplicationInfo().uid);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                //这种方案适用于 API21——25，即 5.0——7.1 之间的版本可以使用
                intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
                intent.putExtra("app_package", context.getPackageName());
                intent.putExtra("app_uid", context.getApplicationInfo().uid);
            } else {
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.setData(Uri.parse("package:" + context.getPackageName()));
            }
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            startAppDetailSetting(context);
        }
    }

    /**
     * Android判断APP通知权限是否打开
     * 场景：判断用户是否屏蔽了应用的推送权限
     *
     * @param context
     * @return
     */
    public boolean isOpenNotification(Context context) {
        return NotificationManagerCompat.from(context).areNotificationsEnabled();
    }

    /**
     * 打开系统文件管理器
     *
     * @param context
     */
    public void startSystemFileManager(Context context) {
        String path = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
        Uri fileURI = getUriForFile(context, new File(path));
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //想要展示的文件类型
        intent.setType("*/*");
        intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, fileURI);
        context.startActivity(intent);
    }

    /**
     * 位置信息是否关闭
     * 是否将访问我的位置信息总开关关闭
     * @param context
     * @return
     */
    public final boolean isPhoneLocationOpen(Context context) {
        if (context == null) {
            return true;
        }

        int locationMode = 0;
        String locationProviders;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);
            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
                return false;
            }
            return locationMode != Settings.Secure.LOCATION_MODE_OFF;
        } else {
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }
    }

    /**
     * 获取应用详情页面intent（如果找不到要跳转的界面，也可以先把用户引导到系统设置页面）
     *
     * @return
     */
    private Intent getAppDetailSettingIntent(Context context) {
        Intent localIntent = new Intent();
        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= 9) {
            localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            localIntent.setData(Uri.fromParts("package", context.getPackageName(), null));
        } else if (Build.VERSION.SDK_INT <= 8) {
            localIntent.setAction(Intent.ACTION_VIEW);
            localIntent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
            localIntent.putExtra("com.android.settings.ApplicationPkgName", context.getPackageName());
        }
        return localIntent;
    }

    /**
     * 获取当前手机系统版本号
     *
     * @return  系统版本号
     */
    public String getSystemVersion() {
        return Build.VERSION.RELEASE;
    }

    /**
     * 获取手机型号
     *
     * @return  手机型号
     */
    public String getSystemModel() {
        return Build.MODEL;
    }

    /**
     * 获取手机厂商
     *
     * @return  手机厂商
     */
    public String getDeviceBrand() {
        return Build.BRAND;
    }
}
