package org.firebase.debug;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.concurrent.Executor;

public class FalseTask extends Task<Boolean> {

    @Override
    public boolean isComplete() {
        return false;
    }

    @Override
    public boolean isSuccessful() {
        return false;
    }

    @Override
    public boolean isCanceled() {
        return false;
    }

    @Override
    public Boolean getResult() {
        return null;
    }

    @Override
    public <X extends Throwable> Boolean getResult(@NonNull Class<X> aClass) throws X {
        return null;
    }

    @Nullable
    @Override
    public Exception getException() {
        return null;
    }

    @NonNull
    @Override
    public Task<Boolean> addOnSuccessListener(@NonNull OnSuccessListener<? super Boolean> onSuccessListener) {
        return null;
    }

    @NonNull
    @Override
    public Task<Boolean> addOnSuccessListener(@NonNull Executor executor, @NonNull OnSuccessListener<? super Boolean> onSuccessListener) {
        return null;
    }

    @NonNull
    @Override
    public Task<Boolean> addOnSuccessListener(@NonNull Activity activity, @NonNull OnSuccessListener<? super Boolean> onSuccessListener) {
        return null;
    }

    @NonNull
    @Override
    public Task<Boolean> addOnFailureListener(@NonNull OnFailureListener onFailureListener) {
        return null;
    }

    @NonNull
    @Override
    public Task<Boolean> addOnFailureListener(@NonNull Executor executor, @NonNull OnFailureListener onFailureListener) {
        return null;
    }

    @NonNull
    @Override
    public Task<Boolean> addOnFailureListener(@NonNull Activity activity, @NonNull OnFailureListener onFailureListener) {
        return null;
    }

    @NonNull
    @Override
    public Task<Boolean> addOnCompleteListener(@NonNull OnCompleteListener<Boolean> onCompleteListener) {
        onCompleteListener.onComplete(this);
        return this;
    }

    @NonNull
    @Override
    public Task<Boolean> addOnCompleteListener(@NonNull Activity activity, @NonNull OnCompleteListener<Boolean> onCompleteListener) {
        onCompleteListener.onComplete(this);
        return this;
    }
}
