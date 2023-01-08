package com.github.logansdk.permission;

import java.util.ArrayList;

public interface RequirePermissionListener extends PermissionListener
{
    void onGranted();
    void onDenied(ArrayList<String> denied);
    void onRejected(ArrayList<String> rejected);
}