package com.example.memefun;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.BitSet;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {
    String url;

    ImageView image;
    public void DownloadImage() throws ExecutionException, InterruptedException {
        DownloadTask downloadImage = new DownloadTask();
        ImageUrl imageUrl = new ImageUrl();
        String URL = imageUrl.execute("https://meme-api.herokuapp.com/gimme").get().toString();
        imageUrl.onPostExecute(URL);
        Bitmap myImage;
        try{
            myImage = downloadImage.execute(url).get();
            image.setImageBitmap(myImage);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static class DownloadTask extends AsyncTask<String, Void, Bitmap>{



        @Override
        protected Bitmap doInBackground(String... urls) {
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.connect();

                InputStream inputStream = httpURLConnection.getInputStream();

                Bitmap image = BitmapFactory.decodeStream(inputStream);
                return  image;
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return null;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }

        }
    }

    public class ImageUrl extends AsyncTask<String,Void,String>{

        @Override
        protected String doInBackground(String... urls2) {
            String result = "";
            URL url;
            try{
                url = new URL(urls2[0]);

                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream in = httpURLConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);

                int data = reader.read();
                while(data != -1){
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }
                return result;


            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject jsonObject = new JSONObject(s);
                url = jsonObject.getString("url");

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        image = findViewById(R.id.imageView);;
        try {
            DownloadImage();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ;
    }
    public void next(View view) throws ExecutionException, InterruptedException {
        DownloadImage();
    }

    public void share(View view){
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.setType("image/*");
        Intent.createChooser(sendIntent,"Share via");
        startActivity(sendIntent);
    }
}