package com.salesoptimizer.oauth;

public class CommonUtils {
    
    /** String blank/empty checks */
    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }
    public static boolean isEmpty(String str) {
        return str == null;
    }
    public static boolean isNotBlank(String str) {
        return !isBlank(str);
    }
    public static boolean isBlank(String str) {
        return isEmpty(str) || str.isEmpty() || str.trim().isEmpty();
    }
    
}
