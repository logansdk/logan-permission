package com.github.logansdk.samples.permission;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;

import androidx.appcompat.app.AlertDialog;

import java.util.ArrayList;

public class PermissionUtils
{
    public enum PermissionValue
    {
        PermissionValue1 (Manifest.permission.CAMERA, "Camera"),
        PermissionValue2 (Manifest.permission.RECORD_AUDIO, "Microphone"),
        PermissionValue3 (Manifest.permission.ACCESS_COARSE_LOCATION, "Location"),
        PermissionValue4 (Manifest.permission.ACCESS_FINE_LOCATION, "Location"),
        PermissionValue5 (Manifest.permission.READ_EXTERNAL_STORAGE, "Storage"),
        PermissionValue6 (Manifest.permission.WRITE_EXTERNAL_STORAGE, "Storage"),
        PermissionValue7 (Manifest.permission.READ_MEDIA_IMAGES, "Photos and videos");

        private final String permission;
        private final String name;

        PermissionValue(String permission, String name)
        {
            this.permission = permission;
            this.name = name;
        }

        public String getPermission()
        {
            return permission;
        }

        public String getName()
        {
            return name;
        }

        private static PermissionValue get(int index)
        {
            PermissionValue[] values = PermissionValue.values();
            return values[index];
        }
    }

    public static String getPermissionName(String permission)
    {
        String result = "";

        if (!TextUtils.isEmpty(permission))
        {
            for (int i = 0; i < PermissionValue.values().length; i++)
            {
                String comparePermission = PermissionValue.get(i).getPermission();

                if (permission.equals(comparePermission))
                {
                    result = PermissionValue.get(i).getName();
                    break;
                }
            }
        }

        return result;
    }

    public static void showPermissionInfo(Context context, ArrayList<String> rejected, DialogInterface.OnClickListener listener)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Go to the path below and allow the denied permission.");
        builder.setMessage(String.format("\nSettings -> Apps -> LoganPermission -> Permissions\n\n%s", getPermissionName(rejected)));
        builder.setPositiveButton("Setting", listener);
        builder.setNegativeButton("Close", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    public static String getPermissionName(ArrayList<String> permissions)
    {
        StringBuilder sb = new StringBuilder();

        String compareName = "";

        for (String permission : permissions)
        {
            String permissionName = PermissionUtils.getPermissionName(permission);

            if (!permissionName.equals(compareName))
                sb.append(" * ").append(permissionName).append("\n");

            compareName = permissionName;
        }

        return sb.toString();
    }
}