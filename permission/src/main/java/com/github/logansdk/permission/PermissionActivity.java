package com.github.logansdk.permission;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class PermissionActivity extends AppCompatActivity
{
    private final static int REQUEST_PERMISSION_CODE = 1000;
    private final static long PERMISSION_REJECT_TIME = 250;

    private long permissionDlgShowTime = 0;
    private String[] grantedPermissions;
    private Map<String, Boolean> rationalePermissions;
    private PermissionSubscriber subscriber;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        overridePendingTransition(0, 0);

        subscriber = PermissionSubscriber.getInstance();
        rationalePermissions = new HashMap<>();

        grantedPermissions = getIntent().getStringArrayExtra(PermissionManager.EXTRA_GRANTED_PERMISSIONS);
        String[] deniedPermissions = getIntent().getStringArrayExtra(PermissionManager.EXTRA_DENIED_PERMISSIONS);

        for (String permission : deniedPermissions)
            rationalePermissions.put(permission, ActivityCompat.shouldShowRequestPermissionRationale(this, permission));

        ActivityCompat.requestPermissions(this, deniedPermissions, REQUEST_PERMISSION_CODE);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        permissionDlgShowTime = System.currentTimeMillis();
    }

    @Override
    public void finish()
    {
        super.finish();
        overridePendingTransition(0, 0);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_PERMISSION_CODE && permissions.length > 0)
        {
            long permissionDlgViewTime = System.currentTimeMillis() - permissionDlgShowTime;

            ArrayList<String> granted = new ArrayList<>(Arrays.asList(grantedPermissions));
            ArrayList<String> denied = new ArrayList<>();
            ArrayList<String> rejected = new ArrayList<>();

            for (int i = 0; i < permissions.length; i++)
            {
                String permission = permissions[i];

                if (grantResults[i] == PackageManager.PERMISSION_GRANTED)
                {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
                        setRejectedPermission(this, permission, false);

                    granted.add(permission);
                }
                else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R)
                {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission))
                        denied.add(permission);
                    else
                        rejected.add(permission);
                }
                else
                {
                    boolean beforePermissionRationale = Boolean.TRUE.equals(rationalePermissions.get(permission));
                    boolean afterPermissionRationale = ActivityCompat.shouldShowRequestPermissionRationale(this, permission);
                    boolean rejectedCase1 = beforePermissionRationale && !afterPermissionRationale;
                    boolean rejectedCase2 = !beforePermissionRationale && !afterPermissionRationale;

                    if (rejectedCase1)
                    {
                        setRejectedPermission(this, permission, true);
                        rejected.add(permission);
                    }
                    else if (rejectedCase2 && getRejectedPermission(this, permission))
                    {
                        rejected.add(permission);
                    }
                    else
                    {
                        if (permissionDlgViewTime < PERMISSION_REJECT_TIME)
                        {
                            setRejectedPermission(this, permission, true);
                            rejected.add(permission);
                        }
                        else
                        {
                            denied.add(permission);
                        }
                    }
                }
            }

            subscriber.post(new PermissionEvent(granted, denied, rejected));
        }
        else
        {
            subscriber.unregister();
        }

        finish();
    }

    private void setRejectedPermission(Context context, String permission, boolean value)
    {
        SharedPreferences sp = context.getSharedPreferences("logan-permission", Context.MODE_PRIVATE);
        SharedPreferences.Editor spEditor = sp.edit();
        spEditor.putBoolean(permission, value);
        spEditor.apply();
    }

    private boolean getRejectedPermission(Context context, String permission)
    {
        SharedPreferences sp = context.getSharedPreferences("logan-permission", Context.MODE_PRIVATE);
        return sp.getBoolean(permission, false);
    }
}