package com.niannian.daybook;

import android.content.Context;
import org.json.JSONArray;
import org.json.JSONObject;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public final class Store {
    private Store(){}
    static android.content.SharedPreferences prefs(Context c){return c.getApplicationContext().getSharedPreferences("niannian_records", Context.MODE_PRIVATE);}
    static JSONArray all(Context c){try{return new JSONArray(prefs(c).getString("events","[]"));}catch(Exception e){return new JSONArray();}}
    static void save(Context c,JSONArray array){prefs(c).edit().putString("events",array.toString()).apply();Widgets.refreshAll(c);}
    static JSONObject find(Context c,String id){if(id==null)return null;JSONArray a=all(c);for(int i=0;i<a.length();i++){JSONObject j=a.optJSONObject(i);if(j!=null&&id.equals(j.optString("id")))return j;}return null;}
    static long days(JSONObject j){
      try{ LocalDate d=LocalDate.parse(j.getString("date")); long delta=ChronoUnit.DAYS.between(LocalDate.now(),d);
        return "down".equals(j.optString("mode"))?Math.max(0,delta):Math.max(1,1-delta);
      }catch(Exception e){return 0;}
    }
    static String caption(JSONObject j){
       if(j==null)return "添加一个重要的日子";
       try{long diff=ChronoUnit.DAYS.between(LocalDate.now(),LocalDate.parse(j.getString("date")));
          if("down".equals(j.optString("mode")))return diff>0?"距离这一天还有":(diff==0?"就是今天 ✨":"这一天已经到来");
          return diff>0?"距离这一天开始还有":"已经珍藏的日子";
       }catch(Exception e){return "日期格式有误";}
    }
}
