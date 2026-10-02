package com.e0556788942ctrl.smartfilemanager;

import android.app.*;
import android.os.*;
import android.content.*;
import android.graphics.Color;
import android.net.Uri;
import android.provider.Settings;
import android.view.*;
import android.widget.*;
import java.io.File;
import java.text.DateFormat;
import java.util.*;

public class MainActivity extends Activity {
    LinearLayout root, list; TextView pathTitle; EditText search; File current; boolean apps, video; final int PAD=18;
    int blue=Color.rgb(49,89,215), navy=Color.rgb(20,33,61), bg=Color.rgb(247,249,252), gray=Color.rgb(102,112,133);
    @Override public void onCreate(Bundle b){super.onCreate(b); if(!getPreferences(0).getBoolean("configured",false)) firstRun(); else {loadPrefs(); home();}}
    void firstRun(){
        ScrollView s=new ScrollView(this); LinearLayout box=col(); box.setPadding(30,50,30,35); s.addView(box);
        TextView logo=t("קבצים חכמים",30,navy); box.addView(logo); box.addView(t("מנהל הקבצים שלך — פשוט, מהיר ומסודר",18,gray));
        Space sp=new Space(this); box.addView(sp,new LinearLayout.LayoutParams(1,38));
        box.addView(t("בחר את החוויה שלך",24,navy)); box.addView(t("הבחירות האלה נשמרות ולא יוצגו שוב בהפעלה הבאה.",15,gray));
        Switch a=new Switch(this); a.setText("ניהול והתקנת אפליקציות"); a.setTextSize(17); a.setChecked(true); box.addView(a,lp(1,62));
        Switch v=new Switch(this); v.setText("נגן וידאו מובנה"); v.setTextSize(17); v.setChecked(true); box.addView(v,lp(1,62));
        TextView go=t("המשך",18,Color.WHITE); go.setGravity(Gravity.CENTER); go.setBackground(round(blue,18)); box.addView(go,lp(1,58));
        go.setOnClickListener(x->{getPreferences(0).edit().putBoolean("configured",true).putBoolean("apps",a.isChecked()).putBoolean("video",v.isChecked()).apply(); apps=a.isChecked(); video=v.isChecked(); home();});
        setContentView(s);
    }
    void loadPrefs(){apps=getPreferences(0).getBoolean("apps",true); video=getPreferences(0).getBoolean("video",true);}
    void home(){current=EnvironmentRoot(); root=col(); root.setBackgroundColor(bg); setContentView(root); header(); categories(); refresh();}
    File EnvironmentRoot(){File d=getExternalFilesDir(null); File p=new File("/storage/emulated/0"); return p.exists()?p:d;}
    void header(){LinearLayout h=col(); h.setPadding(22,24,22,18); h.setBackground(round(navy,0)); TextView title=t("קבצים חכמים",28,Color.WHITE); h.addView(title); pathTitle=t(current.getAbsolutePath(),13,0xffcbd5ee); h.addView(pathTitle); search=new EditText(this); search.setHint("חיפוש בקבצים…"); search.setSingleLine(); search.setTextColor(Color.WHITE); search.setHintTextColor(0xffb9c2d7); search.setBackground(round(0x223a5c9e,50)); search.setPadding(18,0,18,0); h.addView(search,lp(1,52)); search.setOnEditorActionListener((v,a,e)->{refresh();return true;}); root.addView(h);}
    void categories(){LinearLayout c=row(); c.setPadding(16,14,16,10); String[] n={"הכל","תמונות","מסמכים","הורדות"}; for(String x:n){TextView q=t(x,14,blue); q.setGravity(17); q.setBackground(round(0xffedf3ff,40)); c.addView(q,lpw(1,44)); q.setOnClickListener(v->{search.setText(""); refresh();});} root.addView(c);}
    void refresh(){if(list!=null)root.removeView(list); list=col(); list.setPadding(16,8,16,80); root.addView(list); File[] fs=current.listFiles(); if(fs==null){list.addView(t("אין הרשאת גישה לתיקייה זו",16,gray));return;} Arrays.sort(fs,(x,y)->Boolean.compare(!x.isDirectory(),!y.isDirectory())); String q=search==null?"":search.getText().toString().toLowerCase(); int count=0; for(File f:fs){if(!q.isEmpty()&&!f.getName().toLowerCase().contains(q))continue; addFile(f);if(++count>300)break;} if(count==0)list.addView(t("לא נמצאו קבצים",16,gray));}
    void addFile(File f){LinearLayout card=row(); card.setPadding(14,10,10,10); card.setBackground(round(Color.WHITE,20)); TextView icon=t(f.isDirectory()?"▰":iconFor(f),25,f.isDirectory()?blue:navy); icon.setGravity(17); card.addView(icon,lpw(0,54)); LinearLayout info=col(); TextView n=t(f.getName(),16,navy); TextView m=t(f.isDirectory()?"תיקייה":""+human(f.length())+"  •  "+DateFormat.getDateInstance().format(new Date(f.lastModified())),12,gray); info.addView(n);info.addView(m);card.addView(info,lp(1,58)); card.setOnClickListener(v->{if(f.isDirectory()){current=f;pathTitle.setText(f.getAbsolutePath());refresh();}else openFile(f);}); card.setOnLongClickListener(v->{menu(f);return true;}); list.addView(card,new LinearLayout.LayoutParams(-1,70){ {setMargins(0,0,0,9);} });}
    void openFile(File f){String type="*/*";String n=f.getName().toLowerCase(); if(n.endsWith(".jpg")||n.endsWith(".png")||n.endsWith(".webp"))type="image/*"; else if(n.endsWith(".mp4")||n.endsWith(".mkv")||n.endsWith(".webm"))type="video/*"; if(type.equals("video/*")&&!video){Toast.makeText(this,"נגן הווידאו כבוי בהגדרה הראשונית",Toast.LENGTH_SHORT).show();return;} Intent i=new Intent(Intent.ACTION_VIEW);i.setDataAndType(Uri.fromFile(f),type);i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);try{startActivity(i);}catch(Exception e){Toast.makeText(this,"אין אפליקציה מתאימה לפתיחת הקובץ",Toast.LENGTH_SHORT).show();}}
    void menu(File f){new AlertDialog.Builder(this).setTitle(f.getName()).setItems(new String[]{"שתף","מחק","פרטים"},(d,w)->{if(w==0){Intent s=new Intent(Intent.ACTION_SEND);s.setType("*/*");s.putExtra(Intent.EXTRA_STREAM,Uri.fromFile(f));startActivity(Intent.createChooser(s,"שתף קובץ"));}else if(w==1){if(f.delete())refresh();else Toast.makeText(this,"לא ניתן למחוק",0).show();}else new AlertDialog.Builder(this).setTitle("פרטים").setMessage(f.getAbsolutePath()+"\n\nגודל: "+human(f.length())).setPositiveButton("סגור",null).show();}).show();}
    String iconFor(File f){String n=f.getName().toLowerCase(); if(n.endsWith(".jpg")||n.endsWith(".png")||n.endsWith(".webp"))return "▧"; if(n.endsWith(".mp4")||n.endsWith(".mkv"))return "▶"; if(n.endsWith(".pdf"))return "P"; if(n.endsWith(".zip")||n.endsWith(".rar"))return "Z"; return "•";}
    String human(long n){if(n<1024)return n+" B"; if(n<1048576)return n/1024+" KB"; if(n<1073741824)return String.format(Locale.US,"%.1f MB",n/1048576.0);return String.format(Locale.US,"%.1f GB",n/1073741824.0);}
    LinearLayout col(){LinearLayout x=new LinearLayout(this);x.setOrientation(LinearLayout.VERTICAL);return x;} LinearLayout row(){LinearLayout x=new LinearLayout(this);x.setOrientation(LinearLayout.HORIZONTAL);return x;}
    TextView t(String s,float z,int c){TextView x=new TextView(this);x.setText(s);x.setTextSize(z);x.setTextColor(c);x.setGravity(Gravity.CENTER_VERTICAL);x.setTypeface(null,1);x.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);return x;}
    LinearLayout.LayoutParams lp(int w,int h){return new LinearLayout.LayoutParams(w,h){ {setMargins(0,8,0,8);} };} LinearLayout.LayoutParams lpw(int w,int h){return new LinearLayout.LayoutParams(w,h,1);}
    android.graphics.drawable.GradientDrawable round(int c,int r){android.graphics.drawable.GradientDrawable g=new android.graphics.drawable.GradientDrawable();g.setColor(c);g.setCornerRadius(r);return g;}
}
