package com.zs.cacheannotation;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.zs.cacheannotation.data.cache.CacheDaoImpl;
import com.zs.cacheannotation.data.model.UserModel;
import com.zs.cacheannotation.injection.component.ActivityComponent;
import com.zs.cacheannotation.injection.component.DaggerActivityComponent;
import com.zs.cacheannotation.injection.module.ActivityModule;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private ActivityComponent mActivityComponent;

    @Inject
    CacheDaoImpl mDaoImpl;

    @Inject
    Gson mGson;

    private String testJson = "{\"name\":\"zhj\",\"age\":1,\"sex\":1,\"userid\":123456}";

    @BindView(R.id.tv_cache_username)
    TextView mTvCacheUsername;
    @BindView(R.id.tv_cache_time)
    TextView mTvCacheTime;

    /**
     * 缓存监听
     */
    private Subscription cacheSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        activityComponent().inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        //假设从网络获取到json
        getTestData();
    }

    @OnClick(R.id.btn)
    void doOnClick() {
        if (cacheSubscription != null && !cacheSubscription.isUnsubscribed()) {
            cacheSubscription.unsubscribe();
            cacheSubscription = null;
        }
        cacheSubscription = mDaoImpl.getUserCache(new String[]{"123456"})
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onCacheDataLoad,
                        this::onLoadCacheError,
                        this::onLoadCacheCompleted);
    }

    //缓存加载返回数据
    private void onCacheDataLoad(UserModel userModel) {
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        mTvCacheTime.setText("缓存时间:"+sdf.format(new Date(userModel.cache_time)));
        mTvCacheUsername.setText("用户名:"+userModel.name());
    }

    //缓存加载失败
    private void onLoadCacheError(Throwable e) {
        e.printStackTrace();
    }

    private void onLoadCacheCompleted() {
        // do nothing
    }


    private void getTestData() {
        Observable.create(new Observable.OnSubscribe<UserModel>() {
            @Override
            public void call(Subscriber<? super UserModel> subscriber) {
                UserModel user;
                try {
                    user = UserModel.typeAdapter(mGson).fromJson(testJson);
                    subscriber.onNext(user);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                subscriber.onCompleted();
            }
        }).delay(100, TimeUnit.MILLISECONDS)//延迟100ms
                .doOnNext(this::doOnNext)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onNetDataLoad);
    }

    //网络数据返回
    private void onNetDataLoad(UserModel user) {
        Toast.makeText(MainActivity.this, "获取到user:" + user.name(), Toast.LENGTH_SHORT).show();
    }

    //执行网络数据返回前执行的一步，用来刷新数据库缓存
    private void doOnNext(UserModel userModel) {
        //注意，此处如果不在createWorker里操作，则虽然刷新数据库的动作会在子线程中执行
        //但是一定会在刷新数据库的动作执行完成后，才会相应主线程的subscribe
        //这样处理，才是真正意义上的异步
        Schedulers.computation().createWorker().schedule(() ->
                mDaoImpl.freshUserCache(userModel
                        , 0, new String[]{userModel.userid() + ""}));
    }


    public ActivityComponent activityComponent() {
        if (mActivityComponent == null) {
            mActivityComponent = DaggerActivityComponent.builder()
                    .activityModule(new ActivityModule(this))
                    .applicationComponent(DemoApplication.get(this).getComponent())
                    .build();
        }
        return mActivityComponent;
    }
}
