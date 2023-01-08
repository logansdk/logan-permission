package com.github.logansdk.permission;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.Objects;

public class PermissionManager
{
    static final String EXTRA_GRANTED_PERMISSIONS = "GRANTED_PERMISSIONS";
    static final String EXTRA_DENIED_PERMISSIONS = "DENIED_PERMISSIONS";

    private final Context context;
    private final String[] permissions;

    public static PermissionManager with(@NonNull Context context, @NonNull String[] permissions)
    {
        return new PermissionManager(context, permissions);
    }

    public static PermissionManager with(@NonNull Activity activity, @NonNull String[] permissions)
    {
        return new PermissionManager(activity, permissions);
    }

    public static PermissionManager with(@NonNull Fragment fragment, @NonNull String[] permissions)
    {
        return new PermissionManager(fragment.requireActivity(), permissions);
    }

    private PermissionManager(@NonNull Context context, @NonNull String[] permissions)
    {
        Objects.requireNonNull(permissions, "permission must be non-null");

        this.context = context;
        this.permissions = permissions;
    }

    public void check(@NonNull CheckPermissionListener listener)
    {
        ArrayList<String> granted = new ArrayList<>();
        ArrayList<String> denied = new ArrayList<>();

        for (String permission : permissions)
        {
            if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED)
                granted.add(permission);
            else
                denied.add(permission);
        }

        listener.onResult(granted, denied);
    }

    public void check(@NonNull RequestPermissionListener listener)
    {
        ArrayList<String> granted = new ArrayList<>();
        ArrayList<String> denied = new ArrayList<>();

        for (String permission : permissions)
        {
            if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED)
                granted.add(permission);
            else
                denied.add(permission);
        }

        if (denied.isEmpty())
            listener.onResult(granted, denied, new ArrayList<>());
        else
            showPermissionActivity(context, granted, denied, listener);
    }

    public void check(@NonNull RequirePermissionListener listener)
    {
        ArrayList<String> granted = new ArrayList<>();
        ArrayList<String> denied = new ArrayList<>();

        for (String permission : permissions)
        {
            if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED)
                granted.add(permission);
            else
                denied.add(permission);
        }

        if (denied.isEmpty())
            listener.onGranted();
        else
            showPermissionActivity(context, granted, denied, listener);
    }

    private void showPermissionActivity(Context context, ArrayList<String> granted, ArrayList<String> denied, PermissionListener listener)
    {
        if (context instanceof Activity)
        {
            PermissionSubscriber subscriber = PermissionSubscriber.getInstance();
            subscriber.register(listener);

            Activity activity = (Activity)context;
            Intent intent = new Intent(activity, PermissionActivity.class);
            intent.putExtra(EXTRA_GRANTED_PERMISSIONS, granted.toArray(new String[]{}));
            intent.putExtra(EXTRA_DENIED_PERMISSIONS, denied.toArray(new String[]{}));
            activity.startActivity(intent);
        }
    }
}