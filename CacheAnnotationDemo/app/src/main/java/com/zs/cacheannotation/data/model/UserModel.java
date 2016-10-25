package com.zs.cacheannotation.data.model;

import android.os.Parcelable;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;

/**
 * 用户类实体,只需要添加Autovalue注解，并把实体类声明为抽象的，
 * 剩下的就可以交由autovalue帮我们进行处理了
 * Created by zhj on 2016/10/23.
 */
@AutoValue
public abstract class UserModel implements Parcelable {
    public abstract int age();

    public abstract int sex();

    public abstract String name();

    public abstract int userid();

    public long cache_time;//用于记录此数据上次刷新数据库的时间点

    //Autovalue配合gson使用时，需要提供这个静态方法
    public static TypeAdapter<UserModel> typeAdapter(Gson gson) {
        return new AutoValue_UserModel.GsonTypeAdapter(gson);
    }
}
