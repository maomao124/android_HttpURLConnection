package mao.android_httpurlconnection;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

import java.net.URL;
import java.nio.charset.StandardCharsets;


public class MainActivity extends AppCompatActivity
{

    /**
     * 标签
     */
    private static final String TAG = "MainActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView textView = findViewById(R.id.TextView);

        findViewById(R.id.Button).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                try
                {
                    getHTML("https://www.bilibili.com/", new HTTPHandlerListener()
                    {
                        @Override
                        public void OKHandler(String responseString)
                        {
                            //注意，此回调不在ui线程中
                            runOnUiThread(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    textView.setText(responseString);
                                }
                            });
                        }

                        @Override
                        public void ExceptionHandler(IOException e)
                        {
                            Log.e(TAG, "ExceptionHandler: ", e);
                        }
                    });
                }
                catch (IOException e)
                {
                    toastShow("异常：" + e.getMessage());
                    e.printStackTrace();
                }
            }
        });
    }


    public interface HTTPHandlerListener
    {
        void OKHandler(String responseString);

        void ExceptionHandler(IOException e);

    }


    private void getHTML(String urlString, HTTPHandlerListener listener) throws IOException
    {
        //使用使用线程池
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                BufferedReader bufferedReader = null;
                InputStreamReader inputStreamReader = null;
                InputStream inputStream = null;
                HttpURLConnection httpURLConnection = null;
                try
                {
                    URL url = new URL(urlString);
                    httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("GET");
                    httpURLConnection.setConnectTimeout(5000);
                    httpURLConnection.setReadTimeout(5000);
                    //请求头
                    httpURLConnection.addRequestProperty("key", "value");
                    inputStream = httpURLConnection.getInputStream();
                    inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
                    bufferedReader = new BufferedReader(inputStreamReader);
                    String str;
                    StringBuilder stringBuilder = new StringBuilder();
                    while ((str = bufferedReader.readLine()) != null)
                    {
                        stringBuilder.append(str).append("\n");
                    }
                    listener.OKHandler(stringBuilder.toString());
                }
                catch (IOException e)
                {
                    listener.ExceptionHandler(e);
                }
                finally
                {
                    try
                    {
                        if (bufferedReader != null)
                        {
                            bufferedReader.close();
                        }
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                    try
                    {
                        if (inputStreamReader != null)
                        {
                            inputStreamReader.close();
                        }
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                    try
                    {
                        if (inputStream != null)
                        {
                            inputStream.close();
                        }
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                    if (httpURLConnection != null)
                    {
                        httpURLConnection.disconnect();
                    }
                }
            }
        }).start();
    }

    /**
     * 显示消息
     *
     * @param message 消息
     */
    private void toastShow(String message)
    {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}