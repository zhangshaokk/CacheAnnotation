package com.zs.cacheannotation.data.model;

import android.os.Parcelable;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;

/**
 * 用户类实体
 * Created by zhj on 2016/10/23.
 */
@AutoValue
public abstract class UserModel implements Parcelable {
    public abstract int age();

    public abstract int sex();

    public abstract String name();

    //Autovalue配合gson使用时，需要提供这个静态方法
    public static TypeAdapter<UserModel> typeAdapter(Gson gson) {
        return new AutoValue_UserModel.GsonTypeAdapter(gson);
    }

}
