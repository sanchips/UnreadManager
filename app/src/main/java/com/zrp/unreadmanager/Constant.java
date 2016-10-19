package com.zrp.unreadmanager;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by sanchi3 on 2016/10/19.
 */
public class Constant {

    //一级角标
    public static final String MESSAGE = "MESSAGE";
    //二级角标
    public static final String RECENT_MESSAGE = "RECENT_MESSAGE";
    public static final String LOOKED_ME = "LOOKED_ME";
    //依附于二级角标RECENT_MESSAGE的三级角标
    public static final String FRIENDS = "FRIENDS";
    public static final String STRANGERS = "STRANGERS";
    public static final String NEWS = "NEWS";
    //游离无层联关系的角标
    public static final String HOTS = "HOTS";

    //分级级联关系
    public static final Map<String, String[]> parentMap = new HashMap<String, String[]>() {
        {
            put(LOOKED_ME, new String[]{MESSAGE});
            put(FRIENDS, new String[]{MESSAGE, RECENT_MESSAGE});
            put(STRANGERS, new String[]{MESSAGE, RECENT_MESSAGE});
            put(NEWS, new String[]{MESSAGE, RECENT_MESSAGE});
        }
    };
}
