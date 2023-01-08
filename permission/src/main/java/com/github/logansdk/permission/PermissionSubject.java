package com.github.logansdk.permission;

import androidx.annotation.NonNull;

import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.subjects.PublishSubject;
import io.reactivex.rxjava3.subjects.Subject;

public class PermissionSubject
{
    private final Subject<PermissionEvent> subject;
    private Disposable disposable;

    public PermissionSubject()
    {
        subject = PublishSubject.create();
    }

    public void next(@NonNull PermissionEvent event)
    {
        subject.onNext(event);
    }

    public void subscribe(@NonNull Consumer<? super PermissionEvent> onNext)
    {
        disposable = subject.subscribe(onNext, Throwable::printStackTrace);
    }

    public void complete()
    {
        if (disposable != null)
            disposable.dispose();
    }
}