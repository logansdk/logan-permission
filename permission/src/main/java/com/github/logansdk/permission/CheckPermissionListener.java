package com.github.logansdk.permission;

import java.util.ArrayList;

public interface CheckPermissionListener extends PermissionListener
{
    void onResult(ArrayList<String> granted, ArrayList<String> denied);
}