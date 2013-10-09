
package com.example.apkversion;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends Activity {

    EditText txt_url;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        txt_url = (EditText) findViewById( R.id.textview1 );
        txt_url.setText("https://play.google.com/store/apps/details?id=com.eztable.shareshopping&hl=zh_TW");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    class fetchUrl extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... arg0) {
            // TODO Auto-generated method stub
            String url = arg0[0];
            
            HttpPost post = new HttpPost(url);

            HttpClient client = AndroidHttpClient.newInstance("android");
            
            BufferedReader reader = null; 
            try {
                HttpResponse reponse = client.execute(post);
                
                reader = new BufferedReader(new InputStreamReader(reponse.getEntity().getContent()));
                String line;
                String content = "";
                Pattern p = Pattern.compile("\"softwareVersion\"\\W*([\\d\\.]+)");
                while( ( line = reader.readLine() ) != null ){
                    Matcher matcher = p.matcher(line);
                    if( matcher.find() ){
                        Log.v("ids", "ver.: " + matcher.group(1) );
                        return matcher.group(1);
                    }
                    content += line;
                }
                
                Log.v("ids", content);

                
            } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }finally{
                Log.v("ids", "close reader");
                try{
                    if( reader != null ){
                        reader.close();
                    }
                }catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            try {
                PackageInfo info = getPackageManager().getPackageInfo("com.example.apkversion", 0);
                TextView text = (TextView)findViewById(R.id.textView1);
                text.setText( result.compareTo(info.versionName) == 0 ? "Same version" : "Need Updated" );
            } catch (NameNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            
        }
        
    }

    public void query(View v){
        TextView text = (TextView)findViewById(R.id.textView1);
        text.setText("--- progressing ---");
        new fetchUrl().execute(txt_url.getEditableText().toString());
    }
}
