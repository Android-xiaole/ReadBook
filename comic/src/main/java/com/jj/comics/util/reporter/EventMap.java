package com.jj.comics.util.reporter;

import java.util.HashMap;

public class EventMap {
    private HashMap<String, String> map;
    private static EventMap mEventMap;

    private EventMap() {
        map = new HashMap<>();
    }

    public static EventMap getInstance() {
        if (mEventMap == null) {
            synchronized (EventMap.class) {
                if (mEventMap == null)
                    mEventMap = new EventMap();
            }
        }
        return new EventMap();
    }

    public EventMap clear() {
        if (map != null && !map.isEmpty()) map.clear();
        return this;
    }

    public EventMap setNew(String key, String value) {
        clear();
        return put(key, value);
    }

    public EventMap put(String key, String value) {
        map.put(key, value);
        return this;
    }

    public HashMap<String, String> getMap() {
        return map;
    }
}
