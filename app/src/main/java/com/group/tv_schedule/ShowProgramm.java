package com.group.tv_schedule;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.io.InputStream;


public class ShowProgramm extends Activity {

    WebView webView;
    TextView textView;
    ImageView imageView;
    String message;

    String urlImage;
    String urlForImageText;

    private String  LOG = "myLog";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.show_programm);

//        webView = (WebView) findViewById(R.id.webView1);
        textView = (TextView) findViewById(R.id.textView);
        imageView = (ImageView) findViewById(R.id.imageView);
        String url = getResources().getString(R.string.urlRoot);
        Intent intent = getIntent();
        message = url+intent.getStringExtra(PageFragment.EXTRA_MESSAGE);
//        message = intent.getStringExtra(PageFragment.EXTRA_MESSAGE);
//        Log.d(LOG, "url = "+message);
        WebTask webTask = new WebTask(this);
        webTask.execute(message);
//        webView.loadUrl(message);

}

    class WebTask extends AsyncTask<String, Void, Bitmap> {

        ProgressDialog progressDialog;
        ShowProgramm context;

        public WebTask(ShowProgramm context){
            this.context = context;
            lockScreenOrientation();
            progressDialog = new ProgressDialog(context, R.style.MyTheme);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Bitmap doInBackground (String... params) {

            Document document;
            try {
                //��������� ��������� ��������
//                Log.d(LOG,"\nurl = "+params[0]);
                document = Jsoup.connect(params[0]).get();

                if (document != null) {

                    for (Element element : document.body().getElementsByAttributeValueContaining("id", "ncnt")) {
//                        for (Element urlTag : element.getElementsByTag("img")) {
//                        urlImage = urlTag.attr("abs:src");
//                        urlForImageText = element.text();
//                        }
//                        Element urlTag = element.getElementsByTag("img");
                        try {
                            urlImage = element.getElementsByTag("img").attr("abs:src");
                            urlForImageText = element.getElementsByTag("p").text();
                        } catch (Exception e){
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.not_activated),Toast.LENGTH_LONG).show();
                        }

                    }
//                    Log.d(LOG,"\nurlImage = "+urlImage);
//                    Log.d(LOG,"\nurlForImageText = "+urlForImageText);
                }
            } catch (IOException e) {
                //���� �� ���������� �������
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.not_activated),Toast.LENGTH_LONG).show();
            }


            final DefaultHttpClient client = new DefaultHttpClient();
            final HttpGet getRequest = new HttpGet(urlImage);
            try {

                HttpResponse response = client.execute(getRequest);
            //check 200 OK for success
            final int statusCode = response.getStatusLine().getStatusCode();

            if (statusCode != HttpStatus.SC_OK) {
                Log.w("ImageDownloader", "Error " + statusCode +
                        " while retrieving bitmap from " + urlImage);
                return null;

            }

            final HttpEntity entity = response.getEntity();
            if (entity != null) {
                InputStream inputStream = null;
                try {
                    // getting contents from the stream
                    inputStream = entity.getContent();
                    // decoding stream data back into image Bitmap that android understands
                    final Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                    return bitmap;
                } finally {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                    entity.consumeContent();
                }
            }
        } catch (Exception e) {
            // You Could provide a more explicit error message for IOException
            getRequest.abort();
            Log.e("ImageDownloader", "Something went wrong while" +
                    " retrieving bitmap from " + urlImage + e.toString());
        }
//            StringBuffer stringBuffer = new StringBuffer();
//            stringBuffer.append(urlImage);
//            stringBuffer.append(urlImage+"\n");
//            stringBuffer.append(urlForImageText);

//            String stringMessage = stringBuffer.toString();
//            Log.d(LOG, "\nstringMessage = " + stringMessage);
            return null;
//            return stringBuffer.toString();
        }


        @Override
        protected void onPostExecute(Bitmap aVoid) {
//            super.onPostExecute(aVoid);
//            Log.d(LOG, "\nwebview.load = " +
            progressDialog.dismiss();
            unlockScreenOrientation();
            imageView.setImageBitmap(aVoid);
            textView.setText(urlForImageText);

//            webView.loadUrl(urlImage);
//            webview.loadData(urlForImageText, "text/html; charset=utf-8", null);
//            webView.loadUrl(aVoid);
//            webview.loadData(aVoid, "text/html; charset=utf-8", null);
//              webview.loadUrl("http://tivix.net/42-22.html");
//            textView.setText(urlForImageText);
        }
    }

    private void lockScreenOrientation() {
        int currentOrientation = getResources().getConfiguration().orientation;
        if (currentOrientation == Configuration.ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    }

    private void unlockScreenOrientation() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
    }
}
