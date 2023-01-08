package com.github.logansdk.permission;

import java.util.ArrayList;

public interface RequestPermissionListener extends PermissionListener
{
    void onResult(ArrayList<String> granted, ArrayList<String> denied, ArrayList<String> rejected);
}