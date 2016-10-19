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
package com.zrp.library.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.Map;

/**
 * sharedPreferences管理类
 * Created by sanchi3 on 2016/10/18.
 */
public class PSP {

    private volatile static PSP instance = null;

    private PSP() {
    }

    public static PSP getInstance() {
        if (instance == null) {
            synchronized (PSP.class) {
                if (instance == null) {
                    instance = new PSP();
                }
            }
        }
        return instance;
    }

    private SharedPreferences sharedPreferences;

    public void init(Context context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    /**
     * 保存数据的方法，我们需要拿到保存数据的具体类型，然后根据类型调用不同的保存方法
     */
    public synchronized void put(String key, Object object) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (object instanceof String) {
            editor.putString(key, (String) object);
        } else if (object instanceof Integer) {
            editor.putInt(key, (Integer) object);
        } else if (object instanceof Boolean) {
            editor.putBoolean(key, (Boolean) object);
        } else if (object instanceof Float) {
            editor.putFloat(key, (Float) object);
        } else if (object instanceof Long) {
            editor.putLong(key, (Long) object);
        } else {
            editor.putString(key, object.toString());
        }
        editor.apply();
    }

    public synchronized String getString(String key, String defaultValue) {
        return sharedPreferences.getString(key, defaultValue);
    }

    public synchronized int getInt(String key, int defaultValue) {
        return sharedPreferences.getInt(key, defaultValue);
    }

    public synchronized boolean getBoolean(String key, boolean defaultValue) {
        return sharedPreferences.getBoolean(key, defaultValue);
    }

    public synchronized float getFloat(String key, float defaultValue) {
        return sharedPreferences.getFloat(key, defaultValue);
    }

    public synchronized long getLong(String key, long defaultValue) {
        return sharedPreferences.getLong(key, defaultValue);
    }

    /**
     * 移除某个key值已经对应的值
     */
    public synchronized void remove(String key) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(key);
        editor.apply();
    }

    /**
     * 清除所有数据
     */
    public synchronized void clear(Context context) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    /**
     * 查询某个key是否已经存在
     */
    public synchronized boolean contains(String key) {
        return sharedPreferences.contains(key);
    }

    /**
     * 返回所有的键值对
     */
    public synchronized Map<String, ?> getAll(Context context) {
        return sharedPreferences.getAll();
    }
}
