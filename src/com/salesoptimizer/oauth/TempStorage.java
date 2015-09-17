package com.salesoptimizer.oauth;

import java.util.HashMap;
import java.util.Map;

public class TempStorage {
    
    public static final String KEY_SETTINGS = "settings";
    public static final String KEY_PARAMS = "params";
    
    private Map<String, Object> data = new HashMap<String, Object>();
    
    private TempStorage() {}
    private static TempStorage instance;
    public static TempStorage getInstance() {
        if (instance == null) {
            instance = new TempStorage();
        }
        return instance;
    }
    
    public Object get(String key) {
        return data.get(key);
    }
    
    public void put(String key, Object value) {
        data.put(key, value);
    }
}
