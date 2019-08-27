package com.example.android.viewpager;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class BusinessFragment extends Fragment {

    private NewsAdapter mNewsAdapter;
    private TextView mEmptyStateTextView;

    private static final String REQUEST_URL ="https://newsapi.org/v2/top-headlines?country=in&category=business&apiKey=" + MainActivity.API_KEY;

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_business_news, container, false);

        ListView listView = (ListView) rootView.findViewById(R.id.listView);
        ConnectivityManager connMgr = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        // If there is a network connection, fetch data
        if (networkInfo != null && networkInfo.isConnected()) {
            BusinessFragment.NewsAsyncTask task = new BusinessFragment.NewsAsyncTask();
            task.execute(REQUEST_URL);

            mNewsAdapter = new NewsAdapter(getContext(), new ArrayList<News>());
            listView.setAdapter(mNewsAdapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    TextView tv = (TextView) view.findViewById(R.id.urlLink);
                    String urlLink = tv.getText().toString();
                    //Toast.makeText(getContext(), "" + urlLink, Toast.LENGTH_SHORT).show();

                    Uri uri = Uri.parse(urlLink); // missing 'http://' will cause crashed
                    Intent intent = new Intent(getContext(), WebViewActivity.class);
                    intent.putExtra("url", uri.toString());
                    startActivity(intent);
                }
            });
        }else {
            mEmptyStateTextView = (TextView) rootView.findViewById(R.id.empty_view);
            listView.setEmptyView(mEmptyStateTextView);

            mEmptyStateTextView.setText("No internet connection");
        }
        return rootView;
    }

    private class NewsAsyncTask extends AsyncTask<String, Void, ArrayList<News>> {
        ProgressDialog p;

        @Override
        protected ArrayList<News> doInBackground(String... urls) {
            if (urls.length < 1 || urls[0] == null) {
                return null;
            }

            ArrayList<News> result = QueryUtils.fetchData(urls[0]);
            ArrayList<String> list = mNewsAdapter.getList();

            Log.e("BLOCK LIST", " "+ list);

            if (list != null) {
                String[] names = new String[list.size()];

                for (int i = 0; i < list.size(); i++) {
                    names[i] = list.get(i);
                }
                for (int k = 0; k < result.size(); k++) {
                    for (int j = 0; j < names.length; j++) {

                        if (result.get(k).getNewspaperName().contains(names[j])) {
                            result.remove(k);
                        }
                    }
                }
            }
            return result;
        }

        @Override
        protected void onPostExecute(ArrayList<News> data) {
            mNewsAdapter.clear();

            if (data != null && !data.isEmpty()) {
                //             p.dismiss();
                mNewsAdapter.addAll(data);
            }
        }

    }
}
