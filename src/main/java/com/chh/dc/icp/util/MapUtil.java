package com.chh.dc.icp.util;

import java.util.Iterator;
import java.util.Map;

public class MapUtil {

    public static String mapToString(Map<?, ?> map) {
        if (map == null || map.size() == 0) {
            return "";
        }
        String str = "";

        Iterator<?> iterator = map.keySet().iterator();
        while (iterator.hasNext()) {
            Object key = iterator.next();
            Object value = map.get(key);
            str += "[" + key + " : " + value + "]";
        }
        return str;
    }
}
