package com.niannian.daybook;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import org.json.JSONObject;

public final class Widgets {
    private Widgets(){}
    static void refreshAll(Context c){
        AppWidgetManager m=AppWidgetManager.getInstance(c);
        for(int id:m.getAppWidgetIds(new ComponentName(c,Compact.class)))refresh(c,m,id,true);
        for(int id:m.getAppWidgetIds(new ComponentName(c,Wide.class)))refresh(c,m,id,false);
    }
    static void refresh(Context c,AppWidgetManager m,int id,boolean compact){
        String key=Store.prefs(c).getString("widget_"+id,null);
        JSONObject event=Store.find(c,key);
        RemoteViews v=new RemoteViews(c.getPackageName(),compact?R.layout.widget_compact:R.layout.widget_wide);
        v.setTextViewText(R.id.widget_emoji,event==null?"♡":event.optString("emoji","💗"));
        v.setTextViewText(R.id.widget_title,event==null?"念念日":event.optString("title","纪念日"));
        v.setTextViewText(R.id.widget_days,event==null?"—":Store.days(event)+" 天");
        v.setTextViewText(R.id.widget_caption,Store.caption(event));
        Intent open=new Intent(c,MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pi=PendingIntent.getActivity(c,id,open,PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);
        v.setOnClickPendingIntent(R.id.widget_root,pi);
        m.updateAppWidget(id,v);
    }
    public static final class Compact extends AppWidgetProvider {
       @Override public void onUpdate(Context c,AppWidgetManager m,int[] ids){for(int id:ids)refresh(c,m,id,true);}
       @Override public void onDeleted(Context c,int[] ids){for(int id:ids)Store.prefs(c).edit().remove("widget_"+id).apply();}
    }
    public static final class Wide extends AppWidgetProvider {
       @Override public void onUpdate(Context c,AppWidgetManager m,int[] ids){for(int id:ids)refresh(c,m,id,false);}
       @Override public void onDeleted(Context c,int[] ids){for(int id:ids)Store.prefs(c).edit().remove("widget_"+id).apply();}
    }
    public static final class DayChanged extends BroadcastReceiver {
       @Override public void onReceive(Context c,Intent i){refreshAll(c);}
    }
}
