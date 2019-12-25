package cn.edu.fan.himalaya.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import cn.edu.fan.himalaya.utils.Constants;



//
public class XimalayaDBHelper extends SQLiteOpenHelper {

    private static final String TAG = "XimalayaDBHelper";

    public XimalayaDBHelper(@Nullable Context context) {
        //name数据库的名字， factory游标工厂，version版本号
        super(context, Constants.DB_NAME, null, Constants.DB_VERSION_CODE);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG,"onCreate");
        //创建数据表
        //订阅相关的字段
        //图片、title、描述、播放量、节目数量、作者名称（详情界面）专辑id
        String subTbSql = "create table " + Constants.SUB_TB_NAME + "(\n" +
                Constants.SUB_ID + "\t integer primary key autoincrement,\n" +
                Constants.SUB_COVER_URL + "\t varchar,\n" +
                Constants.SUB_TITLE + "\t varchar,\n" +
                Constants.SUB_DESCRIPTION + "\t varchar,\n" +
                Constants.SUB_PLAY_COUNT + "\t integer,\n" +
                Constants.SUB_TRACKS_COUNT + "\t integer,\n" +
                Constants.SUB_AUTHOR_NAME + "\t varchar,\n" +
                Constants.SUB_ALBUM_ID + "\t integer\n" +
                ");";
        //执行
        db.execSQL(subTbSql);

        //创建历史记录表
        String historyTbSql = "create table " + Constants.HISTORY_TB_NAME + "(\n" +
                Constants.HISTORY_ID + "\t integer primary key autoincrement,\n" +
                Constants.HISTORY_TRACK_ID + "\t integer,\n" +
                Constants.HISTORY_TITLE + "\t varchar,\n" +
                Constants.HISTORY_COVER + "\t varchar,\n" +
                Constants.HISTORY_PLAY_COUNT + "\t integer,\n" +
                Constants.HISTORY_DURATION + "\t integer,\n" +
                Constants.HISTORY_AUTHOR + "\t varchar,\n" +
                Constants.HISTORY_UPDATE_TIME + "\t integer \n" +

                ")";
        db.execSQL(historyTbSql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
