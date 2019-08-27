package com.example.android.viewpager;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.android.viewpager.Data.Contract;
import com.example.android.viewpager.Data.Contract.Entry;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class NewsAdapter extends ArrayAdapter<News> {

    private static final String TAG = "NewsAdapter";
    private ArrayList<String> mStringList;
    private String block;
    private StringBuilder stringBuilder;

    public ArrayList<String> getList(){

        SharedPreferences sharedPreferences = getContext().getSharedPreferences("s p", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson =new Gson();

        String a = sharedPreferences.getString("task list", "");
        String[] a1 = a.split(",");
        ArrayList<String> k = new ArrayList<>();
        for(int i=0; i< a1.length; i++){
            k.add(a1[i]);
        }
        return k;
    }

    public void save(){
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("s p", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("task list", stringBuilder.toString());
        Log.e("sB to string:",""+sharedPreferences.getAll());

        editor.commit();
    }
    public NewsAdapter(Context context, List<News> news) {
        super(context, 0, news);
    }

    private ImageView imageView;
    private TextView article1, time, newspaperName, urlLink;

    private class ViewHolder {
        private ImageView item_img_news;
        private TextView item_txt_article;
    }
    public int a;
    public boolean pv;
    public void getPrefSavedTextSize(){
        SharedPreferences sharedPreferences1 = PreferenceManager.getDefaultSharedPreferences(getContext());
        SharedPreferences.Editor editor = sharedPreferences1.edit();

        a = sharedPreferences1.getInt("textSize", 16);
    }

    public void getPrefSavedPic(){
        SharedPreferences sharedPreferences1 = PreferenceManager.getDefaultSharedPreferences(getContext());
        SharedPreferences.Editor editor = sharedPreferences1.edit();

        pv = sharedPreferences1.getBoolean("imageSide", true);
    }

    public void updateUI(int k){

        if(k == 12){
            article1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        }else if(k == 16){
            article1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        }else if(k == 20){
            article1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        } else {
            Log.d("NewsAdapter", "error updating text size");
        }

    }
    public boolean test;
    public void imageSide(){
        test = pv;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        final ViewHolder holder;

        Log.d(TAG, "pv is 1: " + pv );
        getPrefSavedPic();
        imageSide();
        Log.d(TAG, "pv is 2: " + pv );


        if (listItemView == null) {

            Log.d(TAG, "blocked_site_list is 1: " + test );

            if(test==true) {
                listItemView = LayoutInflater.from(getContext()).inflate(R.layout.news_list, parent, false);
            }else if(test==false){
                listItemView = LayoutInflater.from(getContext()).inflate(R.layout.news_list_1, parent, false);
            }
            holder = new ViewHolder();

            holder.item_img_news = listItemView.findViewById(R.id.img);
            holder.item_txt_article = listItemView.findViewById(R.id.article);

            listItemView.setTag(holder);
        }else {
            holder = (ViewHolder) listItemView.getTag();
        }

        final News latestNews = getItem(position);

        imageView = listItemView.findViewById(R.id.img);
        Glide.with(getContext()).load(latestNews.getImg()).into(holder.item_img_news);
        //imageView.setImageBitmap(latestNews.getImg());

        article1 = listItemView.findViewById(R.id.article);
        final TextView article = listItemView.findViewById(R.id.article);
        article.setText(latestNews.getArticle());
        getPrefSavedTextSize();
        updateUI(a);

        String[] a1= latestNews.getTime().toString().split("#");
        String a = a1[0];
        final TextView time = listItemView.findViewById(R.id.time);
        time.setText(latestNews.getTime().toString().split("#")[0]);

        final TextView timeActual = listItemView.findViewById(R.id.timeStamp);
        timeActual.setText(latestNews.getTime().toString().split("#")[1]);

        final TextView newspaperName = listItemView.findViewById(R.id.newspaperName);
        newspaperName.setText(latestNews.getNewspaperName());

        final TextView urlLink = listItemView.findViewById(R.id.urlLink);
        urlLink.setText(latestNews.getUrlLink());

        final ImageView opt = listItemView.findViewById(R.id.opt);
        final View finalListItemView = listItemView;
        opt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                //Toast.makeText(getContext(),"works", Toast.LENGTH_SHORT).show();
                PopupMenu popup = new PopupMenu(getContext(), opt);
                //Inflating the Popup using xml file
                popup.getMenuInflater()
                        .inflate(R.menu.dropdown_menu_main, popup.getMenu());

                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.save:
                                String article1 = article.getText().toString();
                                String time1 = timeActual.getText().toString();
                                String newspaperName1 = newspaperName.getText().toString().trim();
                                String urlLink1 = urlLink.getText().toString();

//----------------------- Checks DB if the article is already saved -----------------------------
                                String[] p = new String[]{Entry.COLUMN_ARTICLE};
                                String selection = Entry.COLUMN_ARTICLE + "=?";
                                String[] sArg = new String[]{article1};
                                Cursor cursor = getContext().getContentResolver().query(Entry.CONTENT_URI, p, selection, sArg,null);
//                                Toast.makeText(getContext(), ""+cursor.getCount(), Toast.LENGTH_SHORT).show();

                                if(cursor.getCount() == 0) {
                                    ContentValues contentValues = new ContentValues();
                                    contentValues.put(Entry.COLUMN_ARTICLE, article1);
                                    contentValues.put(Entry.COLUMN_TIME, time1);
                                    contentValues.put(Entry.COLUMN_PAPER, newspaperName1);
                                    contentValues.put(Entry.COLUMN_LINK, urlLink1);

                                    Uri mUri = getContext().getContentResolver().insert(Entry.CONTENT_URI, contentValues);
                                    long rowId = Long.valueOf(mUri.getLastPathSegment());

                                    Toast.makeText(getContext(), "saved for later read", Toast.LENGTH_SHORT).show();
                                }else {
                                    Toast.makeText(getContext(), "already saved", Toast.LENGTH_SHORT).show();

                                }
                                return true;
                            case R.id.share:

                                Intent sendIntent = new Intent();

                                sendIntent.setAction(Intent.ACTION_SEND);
                                sendIntent.putExtra(Intent.EXTRA_TEXT, "Check this from my favourite News Source. " + latestNews.getUrlLink() + " Download the app at urlzs.com/hBnvY");
                                sendIntent.setType("text/plain");
                                //sendIntent.setPackage("com.whatsapp"); //to share only on whatspp
                                getContext().startActivity(Intent.createChooser(sendIntent, "Share with friends via"));

                                return true;
                            case R.id.blockSite:
                                mStringList = new ArrayList<>();
                                stringBuilder = new StringBuilder();
                                TextView newspaperName = finalListItemView.findViewById(R.id.newspaperName);
                                block = newspaperName.getText().toString().trim();
                                mStringList.add(block);
                                Log.e("newspaper list: ",""+getList());
                                ArrayList<String> s1 = getList();

                                for (String s : s1){
                                    if(!mStringList.contains(s)) { //checks for duplicate
                                        if (s != null && !s.isEmpty()) {
                                            stringBuilder.append(s);
                                            stringBuilder.append(",");
                                        } else {
                                            stringBuilder.append(s);
                                        }
                                    }else {
                                        //Toast.makeText(getContext(), "EXITS",Toast.LENGTH_SHORT).show();
                                    }
                                }
                                stringBuilder.append(block);
                                stringBuilder.append(",");
                                Log.e("stringBuilder: ",""+stringBuilder);
                                save();
                                Toast.makeText(getContext(),"you won't see news from "+newspaperName.getText().toString()+" in future",Toast.LENGTH_SHORT).show();
                                return true;
                        }
                        return true;
                    }
                });
                popup.show();
            }
        });

        return listItemView;
    }
}
