package com.example.android.viewpager;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.nfc.Tag;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;

public class settings extends AppCompatActivity {

    private static final String TAG = "Settings.java";
    private TextView fontSize_textView, imageSidePref, blockedSources_textView, reset, feedback;
    private boolean picOnRight;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Intent intent = getIntent();

        fontSize_textView = (TextView)findViewById(R.id.fontSize);
        blockedSources_textView=(TextView)findViewById(R.id.blockedSources);
        imageSidePref = (TextView)findViewById(R.id.imageSidePref);
        reset = (TextView)findViewById(R.id.reset);
        feedback = (TextView)findViewById(R.id.feedback);

//        updateUI(getPrefSavedTextSize());


        fontSize_textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String[] fonts = {"default", "Small", "Large"};
                AlertDialog.Builder builder = new AlertDialog.Builder(settings.this);
                builder.setTitle("Select a text size");
                builder.setItems(fonts, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if ("default".equals(fonts[which])){
                            text_size_preferred = 16;
                            savePrefTextSize();
                            //updateUI(getPrefSavedTextSize());
                        }else if ("Small".equals(fonts[which])){
                            text_size_preferred = 12;
                            savePrefTextSize();
                            //updateUI(getPrefSavedTextSize());
                        }else {
                            text_size_preferred = 20;
                            savePrefTextSize();
                            //updateUI(getPrefSavedTextSize());
                        }
                    }
                });
                builder.show();
            }
        });

        imageSidePref.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String[] pic = {"Right (Default)", "Left"};
                AlertDialog.Builder builder = new AlertDialog.Builder(settings.this);
                builder.setTitle("See article image on");
                builder.setItems(pic, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if ("Right (Default)".equals(pic[which])){
                            picOnRight = true;
                            savePicPref();
                        }else if("Left".equals(pic[which])){
                            picOnRight =false;
                            savePicPref();
                        }
                    }
                });
                builder.show();
            }
        });

        blockedSources_textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NewsAdapter newsAdapter = new NewsAdapter(getApplicationContext(), null);

                if(newsAdapter.getList().size()>1) {

                    Intent intent1 = new Intent(settings.this, BlockedActivity.class);
                    startActivity(intent1);
                }else {
                    Toast.makeText(getApplicationContext(), "You have not blocked any sites",Toast.LENGTH_SHORT).show();
                }
            }
        });

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                text_size_preferred = 16;
                                savePrefTextSize();
                                picOnRight = true;
                                savePicPref();

                                SharedPreferences sharedPreferences = getSharedPreferences("s p", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.clear();
                                editor.apply();
                                Toast.makeText(getApplicationContext(),"Restored to default settings", Toast.LENGTH_SHORT).show();
                                break;
                            case DialogInterface.BUTTON_NEGATIVE:
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(settings.this);
                builder.setMessage("Do you want to reset app to default settings?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();
            }
        });

        feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String recepientEmail = "sroutraykec@gmail.com"; // either set to destination email or leave empty
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:" + recepientEmail));
                intent.putExtra(Intent.EXTRA_SUBJECT, "feedback for news soucre app");
                intent.putExtra(Intent.EXTRA_TEXT, "hi, ");
                startActivity(intent);
            }
        });
    }


    public void updateUI(int k){
        Toast.makeText(getApplicationContext(), "text size is " + k, Toast.LENGTH_SHORT).show();
        if(k == 12){
            fontSize_textView.setTextSize(getResources().getDimension(R.dimen.article_text_size_small));
        }else if(k == 16){
            fontSize_textView.setTextSize(getResources().getDimension(R.dimen.article_text_size_default));
        }else if(k == 20){
            //fontSize_textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
            fontSize_textView.setTextSize(getResources().getDimension(R.dimen.article_text_size_large));

        } else {
            Log.d(TAG, "error updating text size");
        }
    }
    private int text_size_preferred;
    public void savePrefTextSize(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                //getApplicationContext().getSharedPreferences("s p", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putInt("textSize", text_size_preferred);
        editor.commit();
    }
    public void savePicPref(){
        SharedPreferences sharedPreferences1 = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        //getApplicationContext().getSharedPreferences("s p", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences1.edit();

        editor.putBoolean("imageSide", picOnRight);
        editor.commit();
    }
    public int getPrefSavedTextSize(){

        SharedPreferences sharedPreferences1 = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                //getBaseContext().getSharedPreferences("s p", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences1.edit();

        a = sharedPreferences1.getInt("textSize", 0);
        Toast.makeText(getApplicationContext(), ""+a, Toast.LENGTH_SHORT).show();
        return a;
    }
    public int a;

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}

