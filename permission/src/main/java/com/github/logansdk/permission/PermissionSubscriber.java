package com.github.logansdk.permission;

import androidx.annotation.NonNull;

import java.util.ArrayList;

import io.reactivex.rxjava3.functions.Consumer;

public class PermissionSubscriber
{
    private PermissionListener listener;
    private PermissionSubject subject;

    private static final class InstanceHolder
    {
        private static final PermissionSubscriber INSTANCE = new PermissionSubscriber();
    }

    public static PermissionSubscriber getInstance()
    {
       return InstanceHolder.INSTANCE;
    }

    private PermissionSubscriber()
    {
    }

    public void register(@NonNull PermissionListener listener)
    {
        subject = new PermissionSubject();
        subject.subscribe(permissionEvent);
        this.listener = listener;
    }

    public void unregister()
    {
        if (subject != null)
        {
            subject.complete();
            subject = null;
        }

        listener =  null;
    }

    public void post(@NonNull PermissionEvent event)
    {
        if (subject != null)
            subject.next(event);
    }

    private final Consumer<PermissionEvent> permissionEvent = new Consumer<>()
    {
        @Override
        public void accept(PermissionEvent event)
        {
            if (listener instanceof RequestPermissionListener)
            {
                RequestPermissionListener requestListener = (RequestPermissionListener)listener;
                requestListener.onResult(event.getGranted(), event.getDenied(), event.getRejected());
            }
            else if (listener instanceof RequirePermissionListener)
            {
                ArrayList<String> denied = event.getDenied();
                ArrayList<String> rejected = event.getRejected();

                RequirePermissionListener requireListener = (RequirePermissionListener)listener;

                if (!rejected.isEmpty())
                    requireListener.onRejected(denied, rejected);
                else if (!denied.isEmpty())
                    requireListener.onDenied(denied);
                else
                    requireListener.onGranted();
            }

            unregister();
        }
    };
}