/*
 * Copyright (C) 2016 sanchi3 Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.zrp.library.unread;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zrp.library.other.BadgeView;
import com.zrp.library.other.PSP;

import java.util.HashMap;
import java.util.Map;

/**
 * 未读角标管理manager
 * Created by sanchi3 on 2016/10/18.
 */
public class UnreadMgr {

    private volatile static UnreadMgr instance = null;

    private UnreadMgr() {
    }

    public static UnreadMgr getInstance() {
        if (instance == null) {
            synchronized (UnreadMgr.class) {
                if (instance == null) {
                    instance = new UnreadMgr();
                }
            }
        }
        return instance;
    }

    // =================================== init ===================================

    private static final String TAG = "UnreadMgr";

    /* 消息存储的SP-key，用以区分不同的用户 */
    private String storeTag;
    /* 未读消息的级联关系，每次添加新的层级角标之后在此进行配置 */
    private Map<String, String[]> parentMap;

    /* 对应未读消息的存储对象 */
    private Map<String, Unread> unreadMap = new HashMap<String, Unread>();

    /**
     * 初始化角标系统
     *
     * @param storeTag  用户标签。如果切换用户，需要重新调用该方法进行初始化
     * @param parentMap 子级和父级的级联关系。只关心最小元素的子级，如：消息tab中有一个最近消息栏目，最近消息中又包括好友和陌生人，
     *                  这时候好友和陌生人就是最小的元素，由这两个最小元素的添加引起了最近消息和消息总数的添加。<p>
     *                  Map<String, String[]> parentMap = new HashMap<String, String[]>();<br>
     *                  parentMap.put("friends",new String[]{"message","recentMessage"});<br>
     *                  parentMap.put("strangers",new String[]{"message","recentMessage"});<p>
     *                  以上演示了一个三级标示消息的结构，二级的结构如下：<br>
     *                  parentMap.put("friends",new String[]{"message","lookedMe"});<p>
     *                  只有一级的标示消息无需添加父级层联关系的map。
     */
    public void init(Context context, String storeTag, Map<String, String[]> parentMap) {
        PSP.getInstance().init(context);
        this.storeTag = storeTag;
        this.parentMap = (parentMap == null ? new HashMap<String, String[]>() : parentMap);

        unreadMap.clear();
        unreadMap = getUnreadMessage();
    }

    /**
     * 从sharedPreferences中读取存储的未读信息
     *
     * @return 存储的未读信息
     */
    public Map<String, Unread> getUnreadMessage() {
        Log.d(TAG, "getUnreadMessage: --------->stored tag：" + getStoredTag() +
                "，stored string：" + PSP.getInstance().getString(getStoredTag(), ""));
        return new Gson().fromJson(PSP.getInstance().getString(getStoredTag(), ""),
                new TypeToken<Map<String, Unread>>() {
                }.getType());
    }

    /**
     * 获取在SharedPreferences中存储的key，以uid进行存储，便于用户切换帐号处理
     *
     * @return 获取最终存储用户角标信息的标签
     */
    private String getStoredTag() {
        return "unread_" + (TextUtils.isEmpty(storeTag) ? "default" : storeTag);
    }

    // =================================== 外部调用 ===================================

    /**
     * 设置未读角标变动监听
     */
    public void setUnreadListener(UnreadListener unreadListener) {
        this.unreadListener = unreadListener;
    }

    /**
     * 通过角标的key获取存储的角标对象的显示数目。只针对数值类角标
     *
     * @param key 角标key值
     * @return 存储的单个角标对象
     */
    public int getUnreadNumByKey(String key) {
        Unread unread = unreadMap.get(key);
        return unread == null ? 0 : unread.getNum();
    }

    /**
     * 注册角标view，写在页面resume的位置，保证每次用户可见都会更新角标
     *
     * @param badge   注册角标的view
     * @param isPoint 是否只显示角标点
     * @param key     注册角标的key
     */
    public void registerBadge(BadgeView badge, boolean isPoint, String key) {
        if (TextUtils.isEmpty(key)) return;

        Unread unread = unreadMap.get(key);
        Log.d(TAG, "registerView：key：" + key + "，isPoint：" + isPoint + "，unread：" + unread);

        if (unread == null) {
            badge.setVisibility(View.GONE);
        } else {
            int num = unread.getNum();
            String show = unread.getShow();
            if (num < 1 && TextUtils.isEmpty(show)) {//空内容
                badge.setVisibility(View.GONE);
            } else {
                badge.setVisibility(View.VISIBLE);
                if (isPoint) {
                    badge.setPoint();
                } else {
                    if (isNum(key)) {
                        badge.setText(num > 99 ? "99+" : String.valueOf(num));
                    } else {
                        badge.setText(show);
                    }
                }
            }
        }
    }

    /**
     * 外部添加一个未读消息类型
     *
     * @param unread 拼接的未读消息体
     */
    public void addUnread(Unread unread) {
        if (unread == null) return;
        unreadMap.put(unread.getKey(), unread);

        addParent(unread.getKey());
        Log.d(TAG, "addUnread: --------->unread：" + unread.toString() + "，unreadMap：" + unreadMap.toString());

        PSP.getInstance().put(getStoredTag(), new Gson().toJson(unreadMap));
        castUnreadMsg(unread.getKey(), true);
    }

    /**
     * 添加数字角标，每次单独+1
     *
     * @param key 角标的key
     */
    public void addNumUnread(String key) {
        if (TextUtils.isEmpty(key)) return;

        Unread unread = unreadMap.get(key);
        if (unread == null) unread = new Unread();
        unread.setKey(key);
        unread.setNum(unread.getNum() + 1);
        unreadMap.put(key, unread);//存储子角标

        addParent(key);
        Log.d(TAG, "addNumUnread: --------->key：" + key + "，unreadMap：" + unreadMap.toString());

        PSP.getInstance().put(getStoredTag(), new Gson().toJson(unreadMap));
        castUnreadMsg(key, true);
    }

    /**
     * 添加字符串角标，该类型的服级角标只添加一次
     *
     * @param key 角标的key
     */
    public void addStringUnread(String key, String show) {
        if (TextUtils.isEmpty(key) || TextUtils.isEmpty(show)) return;

        Unread unread = unreadMap.get(key);
        if (unread == null) {
            addParent(key);
            unread = new Unread();
        }
        unread.setKey(key);
        unread.setShow(show);
        unreadMap.put(key, unread);//存储子角标

        Log.d(TAG, "addStringUnread: --------->key：" + key + "，unreadMap：" + unreadMap.toString());

        PSP.getInstance().put(getStoredTag(), new Gson().toJson(unreadMap));
        castUnreadMsg(key, true);
    }

    /**
     * 从存储中移除指定key值的一个未读值
     *
     * @param key 未读key
     */
    public void reduceUnreadByKey(String key) {
        Unread unread = unreadMap.get(key);
        if (TextUtils.isEmpty(key) || unread == null) return;

        reduceParent(key);//父级角标递减

        //子级角标递减
        int num = unread.getNum();
        if (isNum(key)) {//数字角标
            if (num <= 1) {
                unreadMap.remove(key);
            } else {
                unread.setNum(num - 1);
                unreadMap.put(key, unread);
            }
        } else {//文字角标
            unreadMap.remove(key);
        }
        Log.d(TAG, "reduceUnreadByKey: --------->key：" + key + "，unreadMap：" + unreadMap.toString());

        PSP.getInstance().put(getStoredTag(), new Gson().toJson(unreadMap));
        castUnreadMsg(key, false);
    }

    /**
     * 从存储中移除指定key值的所有未读信息。
     *
     * @param key 未读key
     */
    public void resetUnreadByKey(String key) {
        if (TextUtils.isEmpty(key)) return;

        clearChildInParent(key);//先移除父级再移除子级，因为父级的计算要依赖于子级
        unreadMap.remove(key);
        Log.d(TAG, "resetUnreadByKey: --------->key：" + key + "，unreadMap：" + unreadMap.toString());

        PSP.getInstance().put(getStoredTag(), new Gson().toJson(unreadMap));
        castUnreadMsg(key, false);
    }

    /**
     * 清除所有存储的未读角标
     */
    public void resetAllUnread() {
        unreadMap.clear();
        Log.d(TAG, "resetAllUnread: --------->" + unreadMap.toString());
        PSP.getInstance().put(getStoredTag(), new Gson().toJson(unreadMap));
        castUnreadMsg(null, false);
    }


    // =================================== 内部调用 ===================================

    private UnreadListener unreadListener;

    /**
     * 抛出角标变动消息的监听
     */
    public interface UnreadListener {
        /**
         * 角标变动
         *
         * @param key   角标类型的key值
         * @param isAdd 是否为角标添加消息：true[添加]，false[减少]
         */
        void onUnreadChange(String key, boolean isAdd);
    }

    /**
     * 抛出角标变动消息
     *
     * @param key   角标类型的key值
     * @param isAdd 是否为角标添加消息：true[添加]，false[减少]
     */
    private void castUnreadMsg(String key, boolean isAdd) {
        if (unreadListener != null) unreadListener.onUnreadChange(key, isAdd);
    }

    /**
     * 判断该角标是否是数字角标
     *
     * @param key 角标key值
     * @return 数字角标：true，文字角标：false
     */
    private boolean isNum(String key) {
        if (TextUtils.isEmpty(key)) return false;
        Unread unread = unreadMap.get(key);
        return TextUtils.isEmpty(unread.getShow());
    }

    /**
     * 根据子级角标的key递增父级角标。父级角标只关心num，不关心其他字段，isPoint默认为false，show默认为""
     *
     * @param key 子级角标的key
     */
    private void addParent(String key) {
        String[] parent = parentMap.get(key);
        if (parent == null) return;//如果没有父级角标，就返回

        for (String s : parent) {
            Unread parentUnread = unreadMap.get(s);
            if (parentUnread == null) parentUnread = new Unread();//如果父级角标为空，就创建
            parentUnread.setKey(s);
            parentUnread.setNum(parentUnread.getNum() + 1);
            unreadMap.put(s, parentUnread);//存储父角标
        }
    }

    /**
     * 根据子级角标的key递减父级角标.。父级角标只关心num，不关心其他字段，isPoint默认为false，show默认为""
     *
     * @param key 子级角标的key
     */
    private void reduceParent(String key) {
        Unread unread = unreadMap.get(key);
        String[] parent = parentMap.get(key);
        if (unread == null || parent == null) return;//如果没有该类型角标或者没有该类型的父级角标，就返回

        for (String s : parent) {
            Unread parentUnread = unreadMap.get(s);
            if (parentUnread != null) {
                int parentNum = parentUnread.getNum();
                if (isNum(key)) {//数字角标：如果父级角标的个数<=1，就从集合中移除该父级角标。否则角标-1
                    if (parentNum <= 1) {
                        unreadMap.remove(s);
                    } else {
                        parentUnread.setNum(parentUnread.getNum() - 1);
                        unreadMap.put(s, parentUnread);
                    }
                } else {//如果是文字角标：如果父级角标个数<=1就从集合中移除该父级角标。否则角标-1
                    if (parentNum <= 1) {
                        unreadMap.remove(s);
                    } else {
                        parentUnread.setNum(parentUnread.getNum() - 1);
                        unreadMap.put(s, parentUnread);
                    }
                }
            }
        }
    }

    /**
     * 在父级角标中清除指定的子级角标
     *
     * @param key 子级角标的key
     */
    private void clearChildInParent(String key) {
        Unread unread = unreadMap.get(key);
        String[] parent = parentMap.get(key);
        if (unread == null || parent == null) return;//如果没有该类型角标或者没有该类型的父级角标，就返回

        int num = unread.getNum();
        for (String s : parent) {
            Unread parentUnread = unreadMap.get(s);
            if (parentUnread != null) {
                int parentNum = parentUnread.getNum();
                if (isNum(key)) {//数字角标
                    if (num >= parentNum) {//如果子级角标的个数>=父级角标的个数，就从集合中移除该父级角标
                        unreadMap.remove(s);
                    } else {
                        parentUnread.setNum(parentUnread.getNum() - num);
                        unreadMap.put(s, parentUnread);
                    }
                } else {//如果是文字角标，父级角标个数-1
                    if (parentNum <= 1) {
                        unreadMap.remove(s);
                    } else {
                        parentUnread.setNum(parentUnread.getNum() - 1);
                        unreadMap.put(s, parentUnread);
                    }
                }
            }
        }
    }
}