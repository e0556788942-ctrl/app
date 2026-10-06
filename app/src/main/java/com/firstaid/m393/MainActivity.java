package com.firstaid.m393;
import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
public class MainActivity extends Activity {
  @Override public void onCreate(Bundle b){
    super.onCreate(b);
    WebView w=new WebView(this);
    WebSettings s=w.getSettings();
    s.setJavaScriptEnabled(true);
    s.setDomStorageEnabled(true);
    s.setAllowFileAccess(true);
    w.setVerticalScrollBarEnabled(false);
    w.loadUrl("file:///android_asset/index.html");
    setContentView(w);
  }
}