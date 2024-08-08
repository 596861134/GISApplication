package com.gis.common.permissions;

import android.content.Context;

import androidx.annotation.NonNull;


import com.gis.common.R;

import java.util.List;

/**
 * author : Android 轮子哥
 * github : https://github.com/getActivity/XXPermissions
 * time   : 2023/01/02
 * desc   : 权限描述转换器
 */
public final class PermissionDescriptionConvert {

    /**
     * 获取权限描述
     */
    public static String getPermissionDescription(Context context, List<String> permissions) {
        StringBuilder stringBuilder = new StringBuilder();
        List<String> permissionNames = PermissionNameConvert.permissionsToNames(context, permissions);
        for (String permissionName : permissionNames) {
            stringBuilder.append(permissionName)
                    .append(context.getString(R.string.common_permission_colon))
                    .append(permissionsToDescription(context, permissionName))
                    .append("\n");
        }
        return stringBuilder.toString().trim();
    }

    /**
     * 将权限名称列表转换成对应权限描述
     */
    @NonNull
    public static String permissionsToDescription(Context context, String permissionName) {
        // 请根据权限名称转换成对应权限说明
        String desc = "";
        switch (permissionName) {
            case "日历权限": {
                desc = "用于日历业务";
                break;
            }
            case "相机权限": {
                desc = "用于相机业务";
                break;
            }
            case "通讯录权限": {
                desc = "用于通讯业务";
                break;
            }
            case "定位权限": {
                desc = "用于定位业务";
                break;
            }
            case "后台定位权限": {
                desc = "用于后台定位业务";
                break;
            }
            case "附近设备权限": {
                desc = "用于获取附近设备业务";
                break;
            }
            case "麦克风权限": {
                desc = "用于麦克风业务";
                break;
            }
            case "电话权限": {
                desc = "用于拨打电话业务";
                break;
            }
            case "通话记录权限": {
                desc = "用于通讯录业务";
                break;
            }
            case "身体传感器权限": {
                desc = "用于身体传感器业务";
                break;
            }
            case "后台身体传感器权限": {
                desc = "用于后台身体传感器业务";
                break;
            }
            case "健身运动权限": {
                desc = "用于健身运动业务";
                break;
            }
            case "身体活动权限": {
                desc = "用于身体活动业务";
                break;
            }
            case "读取媒体文件位置权限": {
                desc = "用于文件读写业务";
                break;
            }
            case "短信权限": {
                desc = "用于短信业务";
                break;
            }
            case "存储权限": {
                desc = "用于文件读写业务";
                break;
            }
            case "发送通知权限": {
                desc = "用于通知业务";
                break;
            }
            case "照片和视频权限":
            case "音乐和音频权限": {
                desc = "用于多媒体业务";
                break;
            }
            case "读取应用列表权限": {
                desc = "用于读取应用列表业务";
                break;
            }
            case "所有文件访问权限": {
                desc = "用于文件读写业务";
                break;
            }
            case "安装应用权限": {
                desc = "用于应用安装业务";
                break;
            }
            case "悬浮窗权限": {
                desc = "用于悬浮窗业务";
                break;
            }
            case "修改系统设置权限": {
                desc = "用于系统设置业务";
                break;
            }
            case "通知权限": {
                desc = "用于通知业务";
                break;
            }
            case "通知栏监听权限": {
                desc = "用于通知业务";
                break;
            }
            case "查看使用情况权限": {
                desc = "用于查看使用情况业务";
                break;
            }
            case "查看闹钟提醒权限": {
                desc = "用于闹钟业务";
                break;
            }
            case "勿扰权限":
            case "忽略电池优化权限":
            case "画中画权限":
            case "\tVPN\t权限": {
                desc = "用于系统设置业务";
                break;
            }
            default:
                desc = "用于系统设置业务";
                break;
        }
        return desc;
    }
}