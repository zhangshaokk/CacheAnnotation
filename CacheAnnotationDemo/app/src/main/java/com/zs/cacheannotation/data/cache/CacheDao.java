package com.zs.cacheannotation.data.cache;

import com.cacheannotation.annotaion.AutoCache;
import com.cacheannotation.annotaion.InserBean;
import com.cacheannotation.annotaion.InsertOrUpdateCache;
import com.cacheannotation.annotaion.QueryCache;
import com.cacheannotation.annotaion.SQLKey;
import com.cacheannotation.annotaion.UseCache;
import com.zs.cacheannotation.data.model.UserCache;
import com.zs.cacheannotation.data.model.UserModel;

import rx.Observable;

/**
 * Created by zhj on 2016/10/23.
 */
@AutoCache(tagName = "CacheDao") //声明需要生成此接口的实现类
public interface CacheDao {

    //添加一个需要实现为查询的新方法
    @QueryCache(querySql = UserCache.SELECT_BY_USERID //需要执行的查询sql的语句
            , cacheModel = UserCache.class // UserCacheModel是由SQLDelihgt插件生成的代码，配合AutoValue可以有妙用
            , returnBean = UserModel.class)
    //需要返回的数据实体类
    Observable<UserModel> getUserCache(@SQLKey String... key);


    /**
     * 刷新用户缓存
     * @param model
     * @param usecache
     * @param key
     */
    @InsertOrUpdateCache(cacheModel = UserCache.class
            , whereClause = UserCache.USERID + "=?")
    void freshUserCache(@InserBean UserModel model
            , @UseCache int usecache, @SQLKey String... key);
}
