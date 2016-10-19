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
package com.zrp.library.bean;

import org.json.JSONObject;

/**
 * 未读角标数据解析类
 * Created by sanchi3 on 2016/10/18.
 */
public class Unread extends BaseData {

    private String key;     //角标的key值
    private int num;        //角标显示的数字
    private String show;    //角标显示的文字

    public Unread() {
    }

    @Override
    public void parseJson(String jsonStr) {
        JSONObject jsonObject = getJsonObject(jsonStr);
        this.setKey(jsonObject.optString("key"));
        this.setNum(jsonObject.optInt("num"));
        this.setShow(jsonObject.optString("show"));
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public String getShow() {
        return show;
    }

    public void setShow(String show) {
        this.show = show;
    }

    @Override
    public String toString() {
        return "Unread{" +
                "key='" + key + '\'' +
                ", num=" + num +
                ", show='" + show + '\'' +
                '}';
    }
}
