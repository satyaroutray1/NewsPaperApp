package com.example.android.viewpager;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class QueryUtils {
    private static final String LOG_TAG = "";
    public static final SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    private QueryUtils(){
    }
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }

    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(30000 /* milliseconds */);
            urlConnection.setConnectTimeout(45000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the earthquake JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }
    //getting all urls of of the new article
    public static ArrayList<String> urlList = new ArrayList<>();
    public static ArrayList<News> extractNews(String SAMPLE_JSON){
        if (TextUtils.isEmpty(SAMPLE_JSON)) {
            return null;
        }

        ArrayList<News> news = new ArrayList<News>();
        try {

            JSONObject jsonObject1 = new JSONObject(SAMPLE_JSON);
            JSONArray baseJSONArray = jsonObject1.getJSONArray("articles");

            for (int i = 0; i < baseJSONArray.length(); i++) {
                JSONObject jsonObject = baseJSONArray.getJSONObject(i);
                JSONObject source = jsonObject.getJSONObject("source");

                String url1 = jsonObject.getString("url");

                News news1 = new News();
                String articleTitle = jsonObject.optString("title");
                String[] a = articleTitle.split("-");
                StringBuilder builder = new StringBuilder();
                for (int k = 0; k < a.length - 1; k++) {
                    builder.append(a[k]);
                }
                String joined = builder.toString();
                news1.setArticle(joined);

                news1.setNewspaperName(a[a.length-1]);

                news1.setImg(jsonObject.optString("urlToImage"));
                news1.setUrlLink(jsonObject.optString("url"));

                String dateStr = jsonObject.optString("publishedAt");

                Log.e("actual date: ", dateStr );

//Converting time to time ago format
                String convTime = null;
                String prefix = "";
                String suffix = "Ago";
                String actualDate = null;

                try {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                    Date pasTime = dateFormat.parse(dateStr);

                    actualDate = "#"+pasTime.toString().split(" ")[1] + " " +
                            pasTime.toString().split(" ")[2] + ", " + pasTime.toString().split(" ")[5];
                    //Log.e("actual date: ", ""+ actualDate);

                    Date nowTime = new Date();

                    long dateDiff = nowTime.getTime() - pasTime.getTime();

                    long second = TimeUnit.MILLISECONDS.toSeconds(dateDiff);
                    long minute = TimeUnit.MILLISECONDS.toMinutes(dateDiff);
                    long hour   = TimeUnit.MILLISECONDS.toHours(dateDiff);
                    long day  = TimeUnit.MILLISECONDS.toDays(dateDiff);

                    if (second < 60) {
                        convTime = second+" Seconds "+suffix;
                    } else if (minute < 60) {
                        convTime = minute+" Minutes "+suffix;
                    } else if (hour < 24) {
                        convTime = hour+" Hours "+suffix;
                    } else if (day >= 7) {
                        if (day > 30) {
                            convTime = (day / 30)+" Months "+suffix;
                        } else if (day > 360) {
                            convTime = (day / 360)+" Years "+suffix;
                        } else {
                            convTime = (day / 7) + " Week "+suffix;
                        }
                    } else if (day < 7) {
                        convTime = day+" Days "+suffix;
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                    Log.e("ConvTimeE", e.getMessage());
                }

                news1.setTime(convTime+actualDate);

                news.add(news1);
                urlList.add(url1);
            }
        } catch (JSONException j) {

        }
        return news;
    }
    public static ArrayList<News> fetchData(String requestUrl) {
        URL url = createUrl(requestUrl);

        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        ArrayList<News> news = extractNews(jsonResponse);

        return news;
    }
}
