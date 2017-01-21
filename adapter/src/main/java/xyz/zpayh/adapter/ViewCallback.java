package xyz.zpayh.adapter;

import android.support.annotation.NonNull;
import android.view.View;

public interface ViewCallback<T extends View> {
     void callback(@NonNull T view);
}
