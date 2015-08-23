
package com.tiangongkaiwu.utility;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class ZufangConfig {

    /** 在本地服务器测试时的机器IP, 模拟器不支持localhost和127.0.0.1 */
    //public static final String SERVER_URL = "http://10.80.3.172/index.php";
    public static final String SERVER_URL = "http://t0311.com/zufangPHPServer/index.php";

    public static final String PREFERENCE_NAME = "zufang_preference";

    private SharedPreferences mPreference;

    private static ZufangConfig zufangConfig;

    private Context context;
    
    public static final String OLD_MIN_PAST_TIME = "old_min_past_time";
    
    public static final String SAVE_TIME = "save_time";
    
    public static final String CITY = "city";
    
    public static final String KEYWORD = "keyword";
    
    public static final String UPDATE_INTERNAL = "update_internal";
    
    public static final String CATEGORY = "category";
    
    /** 记录某个时刻系统时候处于自动更新租房信息状态。*/
    public static boolean AUTO_UPDATE = false;
    
    private ZufangConfig(Context context) {
        mPreference = context.getSharedPreferences(PREFERENCE_NAME, 0);
        this.context = context;
    }

    public static ZufangConfig getZufangConfig(Context context) {
        if (zufangConfig == null) {
            zufangConfig = new ZufangConfig(context);
        }
        return zufangConfig;
    }

    public class TimeConfig {
        public String old_min_past_time;
        public long savedTime;
    }

    public void saveKeyword(String keyword) {
        Editor shareData = mPreference.edit();
        shareData.putString(KEYWORD, keyword);
        shareData.commit();
    }

    public String getKeyword() {
        String keyword = context.getSharedPreferences(PREFERENCE_NAME, 0)
                .getString(KEYWORD, "");
        return keyword;
    }
    
    /**
     * Save category;
     * @param keyword
     */
    public void saveCategory(String category) {
        Editor shareData = mPreference.edit();
        shareData.putString(CATEGORY, category);
        shareData.commit();
    }

    /**
     * Get category.
     * @return
     */
    public String getCategory() {
        String category = context.getSharedPreferences(PREFERENCE_NAME, 0).getString(CATEGORY, "");
        return category;
    }
    
    public void saveCity(int city){
    	Editor shareData = mPreference.edit();
        shareData.putInt(CITY, city);
        shareData.commit();
    }
    
    public int getCity() {
        int city = context.getSharedPreferences(PREFERENCE_NAME, 0)
                .getInt(CITY, -1);
        return city;
    }
    
    

    public void saveLatestUpdateTime(String millis, long saveTime) {
        Editor shareData = mPreference.edit();
        shareData.putString(OLD_MIN_PAST_TIME, millis);
        shareData.putLong(SAVE_TIME, saveTime);
        shareData.commit();
    }

    public TimeConfig getLatestUpdateTime() {
        TimeConfig tc = new TimeConfig();
        tc.savedTime = mPreference.getLong(SAVE_TIME, 0);
        tc.old_min_past_time = mPreference.getString(OLD_MIN_PAST_TIME, null);
        return tc;
    }
    
    /** 自动更新的频率设置 */
    public void setUpdateInternal(String string) {
        Editor shareData = mPreference.edit();
        shareData.putString(UPDATE_INTERNAL, string);
        shareData.commit();
    }
    
    public String getUpdateInternal() {
        String value = mPreference.getString(UPDATE_INTERNAL, "");
        /** 如果没有值，默认是5分钟 */
        if(value.equals("")){
            value = "5分钟";
        }
        return value;
    }
}
