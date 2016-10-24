package com.zs.cacheannotation.data.model;

import com.google.auto.value.AutoValue;

/**
 * 描述：
 *
 * @author zhj on 16/10/24.
 */
@AutoValue
public abstract class UserCache implements UserCacheModel {
    public static final Mapper<UserCache> MAPPER =
            new Mapper<>(AutoValue_UserCache::new);

    public static final class Marshal extends UserCacheModel.UserCacheMarshal<Marshal> {
    }
}
