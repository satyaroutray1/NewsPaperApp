package com.example.android.viewpager;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.viewpager.Data.Contract;


public class FavoriteNewsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int LOADER = 0;
    private FavAdapter adapter;
    private TextView textView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_favorite_news, container, false);
        getContext().getContentResolver().query(Contract.Entry.CONTENT_URI, null, null, null,null);

        textView = (TextView)rootView.findViewById(R.id.empty_viewFav);
        ListView listViewFav=(ListView)rootView.findViewById(R.id.listViewFav);
        adapter = new FavAdapter(getContext(), null);
        listViewFav.setAdapter(adapter);
//open url
        listViewFav.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView tv = (TextView) view.findViewById(R.id.urlLinkFav);
                String urlLink = tv.getText().toString();
               // Toast.makeText(getContext(), "" + urlLink, Toast.LENGTH_SHORT).show();

                Uri uri = Uri.parse(urlLink); // missing 'http://' will cause crashed
                Intent intent = new Intent(getContext(), WebViewActivity.class);
                intent.putExtra("url", uri.toString());
                startActivity(intent);
            }
        });

        listViewFav.setEmptyView(textView);
        textView.setText("No saved articles");

        getLoaderManager().initLoader(LOADER, null, this).forceLoad();
        return rootView;
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getContext(), Contract.Entry.CONTENT_URI, null, null, null, null);

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }
}
