package com.example.android.viewpager;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class BlockedActivity extends AppCompatActivity {

    private static final String TAG = "BlockedActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blocked);

        Intent intent = getIntent();

        ListView listView=(ListView)findViewById(R.id.listViewBlock);

        NewsAdapter mNewsAdapter = new NewsAdapter(getApplicationContext(),null);
        ArrayList<String> list1 = mNewsAdapter.getList();

        ArrayList<BlockedSites> arrayList = new ArrayList<>();

        if(list1!=null) {
            String[] names = new String[list1.size()];
            for (int i = 0; i < list1.size(); i++) {
                names[i] = list1.get(i);

                arrayList.add(new BlockedSites(names[i]));
                Log.d(TAG, "blocked newspapers: " + i + ": " + names[i]);
            }
            Log.d(TAG, "blocked newspapers list size: " +arrayList.size());

            BlockedAdapter blockedAdapter= new BlockedAdapter(this, arrayList);

            listView.setAdapter(blockedAdapter);

        }else {
            Toast.makeText(getApplicationContext(), "nothing", Toast.LENGTH_SHORT).show();
        }

    }
}
