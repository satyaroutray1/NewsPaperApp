package com.example.android.viewpager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Map;

public class BlockedAdapter extends ArrayAdapter<BlockedSites> {
    public BlockedAdapter(Context context, ArrayList<BlockedSites> arrayList) {
        super(context, 0, arrayList);
    }

    private String m ;
    private StringBuilder stringBuilder;
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View itemView = convertView;
        if(itemView==null){
            itemView = LayoutInflater.from(getContext()).inflate(R.layout.blocked_site_list, parent,false);
        }
        BlockedSites currentWord = getItem(position);
        final TextView newspaperName_text_view = (TextView) itemView.findViewById(R.id.blockSiteList);
        newspaperName_text_view.setText(currentWord.getBlockedName());

        ImageView imgView = (ImageView)itemView.findViewById(R.id.delBL);

        imgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newspaperName = newspaperName_text_view.getText().toString();
                NewsAdapter mNewsAdapter = new NewsAdapter(getContext(), null);
                ArrayList<String> list = mNewsAdapter.getList();

                SharedPreferences sharedPreferences = getContext().getSharedPreferences("s p", Context.MODE_PRIVATE);

                Log.e("sharedPreferences:",""+sharedPreferences.getAll().toString());

//sharedPreferences o/p example:      {task list=Livemint,Tom's Guide,Dhaka Tribune,NDTV News,News18,The Hindu,}
                String[] y = sharedPreferences.getAll().toString().split(",");

                Log.e("", ""+y.length);
                stringBuilder = new StringBuilder();
                if(y.length!=2) {
                    for (int i = 0; i < y.length - 1; i++) {
                        if (y[i].contains(newspaperName)) {
                            continue;
                        } else {
                            m = y[i] + ",";
                            stringBuilder.append(y[i]);
                            stringBuilder.append(",");
                        }
                    }

                    Log.e("stringBuilder:", "" + stringBuilder);

                    String[] sb1 = stringBuilder.toString().split("=");

                    Log.e("updated stringBuilder:", "" + sb1[1]);

                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("task list", sb1[1].toString());
                    editor.apply();

                    Intent intent = new Intent(getContext(),BlockedActivity.class);
                    getContext().startActivity(intent);
                    Toast.makeText(getContext(), "unblocked" + newspaperName,Toast.LENGTH_SHORT).show();

                }else {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.clear();
                    editor.apply();
                    Intent intent = new Intent(getContext(),settings.class);
                    getContext().startActivity(intent);
                    Toast.makeText(getContext(), "Removed " + newspaperName + " from blocked sites",Toast.LENGTH_SHORT).show();
                }



            }
        });

        return itemView;
    }
}