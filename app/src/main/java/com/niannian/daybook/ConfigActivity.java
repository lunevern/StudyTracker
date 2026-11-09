package com.niannian.daybook;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONObject;

public final class ConfigActivity extends Activity {
    int widgetId=AppWidgetManager.INVALID_APPWIDGET_ID;
    int dp(int a){return (int)(a*getResources().getDisplayMetrics().density+0.5f);}
    GradientDrawable bg(int color){GradientDrawable g=new GradientDrawable();g.setColor(color);g.setCornerRadius(dp(20));return g;}
    TextView t(String text,int sp){TextView v=new TextView(this);v.setText(text);v.setTextSize(sp);v.setTextColor(Color.rgb(102,65,79));return v;}
    @Override public void onCreate(Bundle b){super.onCreate(b);setResult(RESULT_CANCELED);if(getIntent()!=null)widgetId=getIntent().getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,widgetId);if(widgetId<0){finish();return;}}
    @Override public void onResume(){super.onResume();render();}
    void render(){
        ScrollView s=new ScrollView(this);s.setBackgroundColor(Color.rgb(255,247,250));
        LinearLayout root=new LinearLayout(this);root.setPadding(dp(25),dp(30),dp(25),dp(25));root.setOrientation(1);s.addView(root);
        TextView head=t("♡  选一个放在桌面的日子",23);head.setTypeface(null,Typeface.BOLD);root.addView(head);
        JSONArray array=Store.all(this);
        if(array.length()==0){
          TextView empty=t("还没有记录，请先打开念念日添加纪念日。",16);
          empty.setPadding(dp(8),dp(25),dp(8),dp(20));root.addView(empty);
        }
        for(int i=0;i<array.length();i++){
            JSONObject j=array.optJSONObject(i);if(j==null)continue;
            final String eventId=j.optString("id");
            TextView v=t(j.optString("emoji","💗")+"  "+j.optString("title")+"\n"+Store.caption(j)+" "+Store.days(j)+" 天",17);
            v.setBackground(bg(Color.WHITE));v.setPadding(dp(20),dp(15),dp(15),dp(15));
            LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,-2);lp.topMargin=dp(14);root.addView(v,lp);
            v.setOnClickListener(view->choose(eventId));
        }
        TextView add=t("＋  去创建纪念日",17);add.setGravity(Gravity.CENTER);add.setPadding(dp(8),dp(21),dp(8),dp(21));root.addView(add);
        add.setOnClickListener(v->startActivity(new Intent(this,MainActivity.class)));
        setContentView(s);
    }
    void choose(String key){
        Store.prefs(this).edit().putString("widget_"+widgetId,key).apply();
        AppWidgetManager mgr=AppWidgetManager.getInstance(this);
        boolean compact=mgr.getAppWidgetInfo(widgetId).provider.getClassName().contains("$Compact");
        Widgets.refresh(this,mgr,widgetId,compact);
        Intent done=new Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,widgetId);
        setResult(RESULT_OK,done);finish();
    }
}
