package com.star.com.nerdlauncher;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

public class NerdTaskManagerFragment extends ListFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityManager activityManager = (ActivityManager)
                getActivity().getSystemService(Activity.ACTIVITY_SERVICE);

        List<RunningTaskInfo> activities =
                activityManager.getRunningTasks(5);

        RunningTaskInfoAdapter adapter = new RunningTaskInfoAdapter(activities);

        setListAdapter(adapter);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        RunningTaskInfo runningTaskInfo =
                (RunningTaskInfo) l.getAdapter().getItem(position);

        ActivityManager activityManager = (ActivityManager)
                getActivity().getSystemService(Activity.ACTIVITY_SERVICE);

        activityManager.moveTaskToFront(runningTaskInfo.id,
                ActivityManager.MOVE_TASK_WITH_HOME);
    }

    private class RunningTaskInfoAdapter extends ArrayAdapter<RunningTaskInfo> {

        public RunningTaskInfoAdapter(List<RunningTaskInfo> runningTaskInfos) {
            super(getActivity(), 0, runningTaskInfos);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            PackageManager packageManager = getActivity().getPackageManager();

            if (convertView == null) {
                convertView = getActivity().getLayoutInflater().inflate(
                        R.layout.list_item_runningtaskinfo, null);
            }

            RunningTaskInfo runningTaskInfo = getItem(position);

            ApplicationInfo applicationInfo = null;
            try {
                applicationInfo = packageManager.getApplicationInfo(
                        runningTaskInfo.baseActivity.getPackageName(),
                        PackageManager.GET_META_DATA);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }

            ImageView imageView = (ImageView) convertView.findViewById(
                    R.id.runningTaskInfo_imageView);

            TextView textView = (TextView) convertView.findViewById(
                    R.id.runningTaskInfo_label);

            imageView.setImageDrawable(applicationInfo.loadIcon(packageManager));
            textView.setText(applicationInfo.loadLabel(packageManager));

            return convertView;
        }
    }
}
