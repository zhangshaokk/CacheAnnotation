package com.zs.cacheannotation.cache;

import com.cacheannotation.annotaion.AutoCache;
import com.cacheannotation.annotaion.QueryCache;
import com.zs.cacheannotation.data.model.UserModel;
import com.zs.cacheannotation.sql.UserCacheModel;

/**
 * Created by zhj on 2016/10/23.
 */
@AutoCache //声明需要生成此接口的实现类
public interface CacheDao {

    //添加一个需要实现为查询的新方法
    @QueryCache(querySql = UserCacheModel.SELECT_BY_USERNAME //需要执行的查询sql的语句
            ,cacheModel =UserCacheModel.class // UserCacheModel是由SQLDelihgt插件生成的代码，配合AutoValue可以有妙用
    ,returnBean = UserModel.class)//需要返回的数据实体类
    void getUserCache();
}
