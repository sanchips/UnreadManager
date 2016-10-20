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

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 存储在本地的所有未读消息类型
 * Created by sanchi3 on 2016/10/18.
 */
public class UnreadMessage extends BaseData {

    private Map<String, Unread> unreadMap = new HashMap<String, Unread>();

    @Override
    public void parseJson(String s) {
        unreadMap.clear();

        JSONObject jsonObject = getJsonObject(s);
        Iterator iterator = jsonObject.keys();
        while (iterator.hasNext()) {
            String valueStr = iterator.next().toString();
            Unread unread = new Unread();
            unread.parseJson(jsonObject.optString(valueStr));
            unreadMap.put(valueStr, unread);
        }
    }

    public Map<String, Unread> getUnreadMap() {
        return unreadMap;
    }

    public void setUnreadMap(Map<String, Unread> unreadMap) {
        this.unreadMap = unreadMap;
    }

    @Override
    public String toString() {
        return "UnreadMessage{" +
                "unreadMap=" + unreadMap +
                '}';
    }
}
