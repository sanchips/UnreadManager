package com.zrp.unreadmanager;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.zrp.library.unread.UnreadMgr;
import com.zrp.library.other.BadgeView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button message, looked_me, recent_message, friends, strangers, news, hots, clear_all;
    private BadgeView message_badge, looked_me_badge, recent_message_badge, friends_badge, strangers_badge,
            news_badge, hots_badge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initBadge();

        looked_me.setOnClickListener(this);
        friends.setOnClickListener(this);
        strangers.setOnClickListener(this);
        news.setOnClickListener(this);
        hots.setOnClickListener(this);
        clear_all.setOnClickListener(this);
    }

    private void initView() {
        message = (Button) findViewById(R.id.message);
        looked_me = (Button) findViewById(R.id.looked_me);
        recent_message = (Button) findViewById(R.id.recent_message);
        friends = (Button) findViewById(R.id.friends);
        strangers = (Button) findViewById(R.id.strangers);
        news = (Button) findViewById(R.id.news);
        hots = (Button) findViewById(R.id.hots);
        clear_all = (Button) findViewById(R.id.clear_all);
    }

    private void initBadge() {
        message_badge = (BadgeView) findViewById(R.id.message_badge);
        looked_me_badge = (BadgeView) findViewById(R.id.looked_me_badge);
        recent_message_badge = (BadgeView) findViewById(R.id.recent_message_badge);
        friends_badge = (BadgeView) findViewById(R.id.friends_badge);
        news_badge = (BadgeView) findViewById(R.id.news_badge);
        strangers_badge = (BadgeView) findViewById(R.id.strangers_badge);
        hots_badge = (BadgeView) findViewById(R.id.hots_badge);
    }

    private void registerBadge() {
        UnreadMgr.getInstance().registerBadge(message_badge, false, Constant.MESSAGE);
        UnreadMgr.getInstance().registerBadge(looked_me_badge, false, Constant.LOOKED_ME);
        UnreadMgr.getInstance().registerBadge(recent_message_badge, false, Constant.RECENT_MESSAGE);
        UnreadMgr.getInstance().registerBadge(friends_badge, false, Constant.FRIENDS);
        UnreadMgr.getInstance().registerBadge(strangers_badge, false, Constant.STRANGERS);
        UnreadMgr.getInstance().registerBadge(news_badge, false, Constant.NEWS);
        UnreadMgr.getInstance().registerBadge(hots_badge, true, Constant.HOTS);//注册圆点角标，不关心角标个数
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerBadge();
        UnreadMgr.getInstance().setUnreadListener(new UnreadMgr.UnreadListener() {//注册角标变动通知
            @Override
            public void onUnreadChange(String key, boolean isAdd) {
                registerBadge();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.looked_me:
                UnreadMgr.getInstance().addNumUnread(Constant.LOOKED_ME);
                break;
            case R.id.friends:
                UnreadMgr.getInstance().addNumUnread(Constant.FRIENDS);
                break;
            case R.id.strangers:
                UnreadMgr.getInstance().addNumUnread(Constant.STRANGERS);
                break;
            case R.id.news:
                UnreadMgr.getInstance().addStringUnread(Constant.NEWS, "Hot news!");
                break;
            case R.id.hots:
                UnreadMgr.getInstance().addNumUnread(Constant.HOTS);
                break;
            case R.id.clear_all:
                UnreadMgr.getInstance().resetAllUnread();//清除所有未读的角标

                //或者通过角标存储的key重置该key的角标。切记重置的时候只重置最小元素的未读角标，其父级会自动重置。
//                UnreadMgr.getInstance().resetUnreadByKey(Constant.LOOKED_ME);
//                UnreadMgr.getInstance().resetUnreadByKey(Constant.FRIENDS);
//                UnreadMgr.getInstance().resetUnreadByKey(Constant.STRANGERS);
//                UnreadMgr.getInstance().resetUnreadByKey(Constant.HOTS);
                break;
        }
    }
}
