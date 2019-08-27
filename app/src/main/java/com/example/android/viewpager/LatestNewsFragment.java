package com.example.android.viewpager;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

public class LatestNewsFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView= inflater.inflate(R.layout.fragment_latest_news, container, false);
        ViewPager viewPager2 = (ViewPager) rootView.findViewById(R.id.viewpager2);

        ConnectivityManager connMgr = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        // If there is a network connection, fetch data
        if (networkInfo != null && networkInfo.isConnected()) {
            // Create an adapter that knows which fragment should be shown on each page
            SecTabFragmentPagerAdapter adapter = new SecTabFragmentPagerAdapter(getContext(), getChildFragmentManager());

            // Set the adapter onto the view pager
            viewPager2.setAdapter(adapter);

            TabLayout tabLayout = (TabLayout) rootView.findViewById(R.id.tabs2);

            tabLayout.setupWithViewPager(viewPager2);
        }else {
            TextView textView = (TextView)rootView.findViewById(R.id.error);
            textView.setVisibility(View.VISIBLE);
            textView.setText("No internet connection");
        }
        return rootView;
    }
}
