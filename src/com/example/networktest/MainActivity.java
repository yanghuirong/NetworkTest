package com.example.networktest;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.support.v7.app.ActionBarActivity;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.example.networktest.R;

public class MainActivity extends Activity implements OnClickListener {
	public static final int SHOW_RESPONSE = 0;
	private Button sendRequest;
	private TextView responseText;
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SHOW_RESPONSE:
				String response = (String) msg.obj; // 在这里进行UI操作,将结果显示到界面上
				responseText.setText(response);
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		sendRequest = (Button) findViewById(R.id.send_request);
		responseText = (TextView) findViewById(R.id.response);
		sendRequest.setOnClickListener(this);
	}

	@Override
    public void onClick(View v) {
        if (v.getId() == R.id.send_request) {
            sendRequestWithHttpClient();
        } }
	private void sendRequestWithHttpClient() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    HttpClient httpClient = new DefaultHttpClient();
                 // 指定访问的服务器地址是电脑本机
                    HttpGet httpGet = new HttpGet("http://10.45.48.170/~yanghuirong/get_data.xml"); 
                    HttpResponse httpResponse = httpClient.execute(httpGet); 
                    if (httpResponse.getStatusLine().getStatusCode() == 200) {
                    // 请求和响应都成功了
                    HttpEntity entity = httpResponse.getEntity(); 
                    String response = EntityUtils.toString(entity,"utf-8");
                                       parseXMLWithPull(response);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                }
            }).start();
	}
	
	private void parseXMLWithPull(String xmlData) {
	    try {
	XmlPullParserFactory factory = XmlPullParserFactory.newInstance(); 
	XmlPullParser xmlPullParser = factory.newPullParser(); 
	xmlPullParser.setInput(new StringReader(xmlData));
	int eventType = xmlPullParser.getEventType();
	        String id = "";
	        String name = "";
	        String version = "";
	        while (eventType != XmlPullParser.END_DOCUMENT) {
	String nodeName = xmlPullParser.getName(); 
	switch (eventType) {
	// 开始解析某个结点
		case XmlPullParser.START_TAG: {
	                if ("id".equals(nodeName)) {
	                   id = xmlPullParser.nextText();
	                } else if ("name".equals(nodeName)) {
	                   name = xmlPullParser.nextText();
	                } else if ("version".equals(nodeName)) {
	                   version = xmlPullParser.nextText();
	                }
	              
	                break; }
				// 完成解析某个结点
		case XmlPullParser.END_TAG: {
	          if ("app".equals(nodeName)) {
	        	  Log.d("MainActivity", "id is " + id);
	        	  Log.d("MainActivity", "name is " + name);
	        	  Log.d("MainActivity", "version is " + version);
	          }
	          break; }
	    default:
	          break;
	}
			eventType = xmlPullParser.next();
	        }
	            } catch (Exception e) {
	                e.printStackTrace();
	            }
	    }
	}


