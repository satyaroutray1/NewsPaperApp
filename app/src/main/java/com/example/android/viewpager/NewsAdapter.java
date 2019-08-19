package com.example.android.viewpager;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
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
import com.example.android.viewpager.Data.Contract.Entry;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class NewsAdapter extends ArrayAdapter<News> {

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
        editor.commit();
    }
    public NewsAdapter(Context context, List<News> news) {
        super(context, 0, news);
    }

    private ImageView imageView;
    private TextView article, time, newspaperName, urlLink;

    private class ViewHolder {
        private ImageView item_img_news;
        private TextView item_txt_article;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        final ViewHolder holder;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.news_list, parent, false);

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

        article = listItemView.findViewById(R.id.article);
        article.setText(latestNews.getArticle());

        time = listItemView.findViewById(R.id.time);
        time.setText(latestNews.getTime());

        newspaperName = listItemView.findViewById(R.id.newspaperName);
        newspaperName.setText(latestNews.getNewspaperName());

        urlLink = listItemView.findViewById(R.id.urlLink);
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
                                String time1 = time.getText().toString();
                                String newspaperName1 = newspaperName.getText().toString().trim();
                                String urlLink1 = urlLink.getText().toString();

                                ContentValues contentValues = new ContentValues();
                                contentValues.put(Entry.COLUMN_ARTICLE, article1);
                                contentValues.put(Entry.COLUMN_TIME, time1);
                                contentValues.put(Entry.COLUMN_PAPER, newspaperName1);
                                contentValues.put(Entry.COLUMN_LINK, urlLink1);


                                Uri mUri = getContext().getContentResolver().insert(Entry.CONTENT_URI, contentValues);
                                long rowId =Long.valueOf(mUri.getLastPathSegment());

                                Toast.makeText(getContext(), "inserted at " + rowId, Toast.LENGTH_SHORT).show();

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
                                Log.e("ooo",""+getList());
                                ArrayList<String> s1 = getList();

                                for (String s : s1){
                                    if(s!=null&&!s.isEmpty()) {
                                        stringBuilder.append(s);
                                        stringBuilder.append(",");
                                    }else {
                                        stringBuilder.append(s);
                                    }
                                }
                                stringBuilder.append(block);
                                stringBuilder.append(",");
                                Log.e("ppp",""+stringBuilder);
                                save();
                                Toast.makeText(getContext(),"your preference is saved for "+newspaperName.getText().toString(),Toast.LENGTH_SHORT).show();
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
