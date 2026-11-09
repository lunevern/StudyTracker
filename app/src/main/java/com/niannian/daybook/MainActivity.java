package com.niannian.daybook;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import org.json.JSONArray;
import org.json.JSONObject;
import java.time.LocalDate;
import java.util.UUID;

public final class MainActivity extends Activity {
    final int PINK=Color.rgb(192,79,115),INK=Color.rgb(87,56,71),MUTED=Color.rgb(162,119,137);
    int dp(int x){return (int)(getResources().getDisplayMetrics().density*x+0.5f);}
    GradientDrawable card(int fill,int radius){GradientDrawable d=new GradientDrawable();d.setColor(fill);d.setCornerRadius(dp(radius));return d;}
    TextView text(String s,int size,int color,boolean bold){
      TextView t=new TextView(this);t.setText(s);t.setTextSize(size);t.setTextColor(color);
      if(bold)t.setTypeface(Typeface.create("sans-serif-medium",Typeface.NORMAL));return t;
    }
    @Override public void onCreate(Bundle b){super.onCreate(b);getWindow().setStatusBarColor(Color.rgb(255,247,250));getWindow().setNavigationBarColor(Color.rgb(255,247,250));getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR|View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);render();}
    @Override public void onResume(){super.onResume();render();}
    void render(){
        ScrollView scroll=new ScrollView(this);scroll.setFillViewport(true);scroll.setBackgroundColor(Color.rgb(255,247,250));
        LinearLayout body=new LinearLayout(this);body.setOrientation(1);body.setPadding(dp(23),dp(27),dp(23),dp(40));scroll.addView(body);
        TextView brand=text("念念日  ♡",19,PINK,true);body.addView(brand);
        TextView headline=text("把每一个心动的日子\n都珍藏起来。",27,INK,true);
        LinearLayout.LayoutParams hp=new LinearLayout.LayoutParams(-1,-2);hp.topMargin=dp(23);body.addView(headline,hp);
        TextView sub=text("在一起的每一天，都值得纪念 💗",14,MUTED,false);
        LinearLayout.LayoutParams sp=new LinearLayout.LayoutParams(-1,-2);sp.topMargin=dp(10);sp.bottomMargin=dp(23);body.addView(sub,sp);
        JSONArray a=Store.all(this);
        if(a.length()==0){
            TextView empty=text("还没有重要日子\n点击下面的按钮，添加第一次心动 ♡",16,MUTED,false);
            empty.setGravity(Gravity.CENTER);empty.setPadding(dp(12),dp(50),dp(12),dp(50));empty.setBackground(card(Color.WHITE,24));body.addView(empty);
        }
        for(int i=0;i<a.length();i++){
            JSONObject j=a.optJSONObject(i);if(j==null)continue;
            LinearLayout box=new LinearLayout(this);box.setOrientation(1);box.setPadding(dp(21),dp(18),dp(20),dp(19));box.setBackground(card(Color.WHITE,24));
            LinearLayout.LayoutParams bp=new LinearLayout.LayoutParams(-1,-2);bp.bottomMargin=dp(14);body.addView(box,bp);
            TextView title=text(j.optString("emoji","💗")+"  "+j.optString("title","纪念日"),17,INK,true);box.addView(title);
            TextView number=text(Store.days(j)+" 天",43,PINK,true);
            LinearLayout.LayoutParams np=new LinearLayout.LayoutParams(-1,-2);np.topMargin=dp(8);box.addView(number,np);
            TextView detail=text(Store.caption(j)+"     ·     "+j.optString("date",""),13,MUTED,false);
            box.addView(detail);
            box.setOnClickListener(v->edit(j));
            box.setOnLongClickListener(v->{new AlertDialog.Builder(this).setTitle("删除这个纪念日？").setMessage(j.optString("title")).setNegativeButton("取消",null).setPositiveButton("删除",(d,w)->remove(j.optString("id"))).show();return true;});
        }
        TextView button=text("＋  添加重要的日子",17,Color.WHITE,true);
        button.setGravity(Gravity.CENTER);button.setPadding(dp(14),dp(19),dp(14),dp(19));button.setBackground(card(PINK,19));
        LinearLayout.LayoutParams addlp=new LinearLayout.LayoutParams(-1,-2);addlp.topMargin=dp(14);body.addView(button,addlp);
        button.setOnClickListener(v->edit(null));
        TextView hint=text("点击卡片可编辑 · 长按卡片可删除\n桌面长按空白处 → 小组件 → 念念日",12,MUTED,false);hint.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams hintlp=new LinearLayout.LayoutParams(-1,-2);hintlp.topMargin=dp(20);body.addView(hint,hintlp);
        setContentView(scroll);
    }
    void remove(String id){
        JSONArray old=Store.all(this),a=new JSONArray();
        for(int i=0;i<old.length();i++){JSONObject j=old.optJSONObject(i);if(j!=null&&!id.equals(j.optString("id")))a.put(j);}
        Store.save(this,a);render();
    }
    void edit(JSONObject old){
        LinearLayout form=new LinearLayout(this);form.setOrientation(1);form.setPadding(dp(22),dp(8),dp(22),dp(3));
        EditText title=new EditText(this);title.setSingleLine(true);title.setTextSize(16);title.setHint("例：和老婆认识的那一天");if(old!=null)title.setText(old.optString("title"));form.addView(title);
        TextView dateLabel=text("选择日期",14,MUTED,false);form.addView(dateLabel);
        LocalDate initial=LocalDate.now();
        try{if(old!=null)initial=LocalDate.parse(old.optString("date"));}catch(Exception ignored){}
        final LocalDate[] selected={initial};
        TextView date=text(selected[0].toString()+"  ▾",18,PINK,true);date.setPadding(dp(0),dp(17),dp(0),dp(17));form.addView(date);
        date.setOnClickListener(v->{LocalDate d=selected[0];new DatePickerDialog(this,(picker,y,m,day)->{selected[0]=LocalDate.of(y,m+1,day);date.setText(selected[0].toString()+"  ▾");},d.getYear(),d.getMonthValue()-1,d.getDayOfMonth()).show();});
        TextView modeTitle=text("记录方式",14,MUTED,false);form.addView(modeTitle);
        RadioGroup group=new RadioGroup(this);group.setOrientation(0);
        RadioButton up=new RadioButton(this);up.setText("已经过去");up.setId(View.generateViewId());
        RadioButton down=new RadioButton(this);down.setText("倒计时");down.setId(View.generateViewId());
        group.addView(up);group.addView(down);group.check(old!=null&&"down".equals(old.optString("mode"))?down.getId():up.getId());form.addView(group);
        TextView emojiLabel=text("选择小图标",14,MUTED,false);form.addView(emojiLabel);
        String[] em={"💗","❤️","💍","🎂","🌸","⭐","🎁","✈️"};
        Spinner spinner=new Spinner(this);ArrayAdapter<String> adapter=new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item,em);spinner.setAdapter(adapter);
        if(old!=null){String icon=old.optString("emoji");for(int i=0;i<em.length;i++)if(em[i].equals(icon))spinner.setSelection(i);}
        form.addView(spinner);
        AlertDialog dlg=new AlertDialog.Builder(this).setTitle(old==null?"添加纪念日 ♡":"编辑纪念日 ♡").setView(form).setNegativeButton("取消",null).setPositiveButton("保存",null).create();
        dlg.setOnShowListener(v->dlg.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(b->{
            String name=title.getText().toString().trim();if(name.isEmpty()){title.setError("请输入纪念日名称");return;}
            if(name.length()>80){title.setError("最多80个字符");return;}
            try {
                JSONObject entry=new JSONObject();entry.put("id",old==null?UUID.randomUUID().toString():old.optString("id"));
                entry.put("title",name);entry.put("date",selected[0].toString());
                entry.put("mode",group.getCheckedRadioButtonId()==down.getId()?"down":"up");
                entry.put("emoji",spinner.getSelectedItem().toString());
                JSONArray records=Store.all(this),next=new JSONArray();
                for(int i=0;i<records.length();i++){JSONObject j=records.optJSONObject(i);if(j!=null&&(old==null||!j.optString("id").equals(old.optString("id"))))next.put(j);}
                next.put(entry);Store.save(this,next);dlg.dismiss();render();
            }catch(Exception e){Toast.makeText(this,"保存失败",Toast.LENGTH_SHORT).show();}
        }));
        dlg.show();
    }
}
