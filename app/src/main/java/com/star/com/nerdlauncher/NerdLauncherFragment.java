package com.star.com.nerdlauncher;


import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class NerdLauncherFragment extends ListFragment {

    private static final String TAG = "NerdLauncherFragment";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent startupIntent = new Intent(Intent.ACTION_MAIN);
        startupIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        PackageManager packageManager = getActivity().getPackageManager();
        List<ResolveInfo> activities = packageManager.queryIntentActivities(startupIntent, 0);

        Log.i(TAG, "I've found " + activities.size() + " activities.");

        Collections.sort(activities, new Comparator<ResolveInfo>() {
            @Override
            public int compare(ResolveInfo lhs, ResolveInfo rhs) {

                PackageManager packageManager = getActivity().getPackageManager();

                return String.CASE_INSENSITIVE_ORDER.compare(
                        lhs.loadLabel(packageManager).toString(),
                        rhs.loadLabel(packageManager).toString());
            }
        });

        ResolveInfoAdapter adapter = new ResolveInfoAdapter(activities);

//        ArrayAdapter<ResolveInfo> adapter = new ArrayAdapter<ResolveInfo>(
//                getActivity(), android.R.layout.simple_list_item_1, activities
//        ) {
//            @Override
//            public View getView(int position, View convertView, ViewGroup parent) {
//                PackageManager packageManager = getActivity().getPackageManager();
//                View view = super.getView(position, convertView, parent);
//
//                TextView textView = (TextView) view;
//                ResolveInfo resolveInfo = getItem(position);
//                textView.setText(resolveInfo.loadLabel(packageManager));
//                return view;
//            }
//        };

        setListAdapter(adapter);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        ResolveInfo resolveInfo = (ResolveInfo) l.getAdapter().getItem(position);
        ActivityInfo activityInfo = resolveInfo.activityInfo;

        if (activityInfo != null) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.setClassName(activityInfo.applicationInfo.packageName, activityInfo.name);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    private class ResolveInfoAdapter extends ArrayAdapter<ResolveInfo> {

        public ResolveInfoAdapter(List<ResolveInfo> resolveInfos) {
            super(getActivity(), 0, resolveInfos);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            PackageManager packageManager = getActivity().getPackageManager();

            if (convertView == null) {
                convertView = getActivity().getLayoutInflater().inflate(R.layout.list_item_resolveinfo, null);
            }

            ResolveInfo resolveInfo = getItem(position);

            ImageView imageView = (ImageView) convertView.findViewById(R.id.resolveInfo_imageView);

            TextView textView = (TextView) convertView.findViewById(R.id.resolveInfo_label);

            imageView.setImageDrawable(resolveInfo.loadIcon(packageManager));
            textView.setText(resolveInfo.loadLabel(packageManager));

            return convertView;
        }
    }
}
