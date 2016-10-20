package com.zrp.unreadmanager;

import android.app.Application;

import com.zrp.library.unread.UnreadMgr;

/**
 * Created by sanchi3 on 2016/10/19.
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        //进行未读初始化，用户id等作为storeTag。如果进行了用户的切换，在切换完成之后重新调用该初始化
        UnreadMgr.getInstance().init(this, "user", Constant.parentMap);
    }
}
