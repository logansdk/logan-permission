package com.github.logansdk.permission;

import java.util.ArrayList;

public class PermissionEvent
{
    private final ArrayList<String> granted;
    private final ArrayList<String> denied;
    private final ArrayList<String> rejected;

    public PermissionEvent(ArrayList<String> granted, ArrayList<String> denied, ArrayList<String> rejected)
    {
        this.granted = granted;
        this.denied = denied;
        this.rejected = rejected;
    }

    public ArrayList<String> getGranted()
    {
        return granted;
    }

    public ArrayList<String> getDenied()
    {
        return denied;
    }

    public ArrayList<String> getRejected()
    {
        return rejected;
    }
}