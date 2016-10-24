package com.zs.cacheannotation.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.squareup.sqlbrite.BriteDatabase;
import com.squareup.sqlbrite.SqlBrite;
import com.zs.cacheannotation.sql.UserCacheModel;

import rx.schedulers.Schedulers;

/**
 * 创建数据库
 * @author zhj
 */
public class DataBaseManager {
    private static SQLiteOpenHelper mSqLiteOpenHelper;
    private static DataBaseManager mDataBaseManager;
    private static final String DB_NAME = "cache";
    private static SqlBrite mSqlBrite;
    private static BriteDatabase mBriteDatabase;

    public static BriteDatabase getBriteDatabase(Context context) {
        if (mBriteDatabase == null) {
            synchronized (DataBaseManager.class) {
                if (mDataBaseManager == null) {
                    mDataBaseManager = new DataBaseManager();
                    mDataBaseManager.mSqLiteOpenHelper = new SQLiteOpenHelper(context, DB_NAME, null, 2) {
                        @Override
                        public void onCreate(SQLiteDatabase db) {
                            init(db);
                        }

                        @Override
                        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
                        }
                    };
                    mSqlBrite = SqlBrite.create();
                    mBriteDatabase = mSqlBrite.wrapDatabaseHelper(mSqLiteOpenHelper, Schedulers.io());
                    mBriteDatabase.setLoggingEnabled(true);
                }
            }
        }
        return mBriteDatabase;
    }

    private static void init(SQLiteDatabase db) {
        db.execSQL(UserCacheModel.CREATE_TABLE);
    }
}
