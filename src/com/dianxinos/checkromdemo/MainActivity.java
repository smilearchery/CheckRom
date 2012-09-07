
package com.dianxinos.checkromdemo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.PackageParser;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends Activity {

    private ExpandableListView lv_packages;
    private ListAdapter adapter;
    private ExpandableListAdapter ex_adapter;
    private Handler h;
    private ProgressDialog pd;
    private PackageCompare pCompare;
    private CheckPackage c;
    private Thread t;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expandablelist);
        lv_packages = (ExpandableListView) findViewById(R.id.lv_packages);
        c = new CheckPackage();
        pCompare = new PackageCompare(MainActivity.this);
        ex_adapter = new ExpandableAdapter(MainActivity.this, getResources()
                .getStringArray(R.array.exlv_groupname), pCompare.itemsNotShow,
                pCompare.items);
        lv_packages.setAdapter(ex_adapter);
        ToDetailChildListener toDetailChildListener = new ToDetailChildListener();
        lv_packages.setOnChildClickListener(toDetailChildListener);

        h = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                // TODO Auto-generated method stub
                Bundle bundle = msg.getData();
                switch (msg.what) {
                    case 2:
                        pd.dismiss();
                        break;
                    case 3:
                        pd = new ProgressDialog(MainActivity.this);
                        pd.setTitle(bundle.getString(CheckRomUtils.TITLE_DIALOG_STRING));
                        pd.setMessage(bundle.getString(CheckRomUtils.MSG_DIALOG_STRING));
                        pd.setMax(pCompare.getFileCount());
                        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                        pd.setCancelable(false);
                        pd.show();
                        break;
                    case 4:
                        pd.incrementProgressBy(1);
                        break;
                    case 5:
                        lv_packages.expandGroup(0);
                    default:
                        break;
                }
                // adapter = new PackageAdapter(MainActivity.this,
                // pCompare.getNotShowList());

                super.handleMessage(msg);
            }
        };
        refreshList();
        t = new RefleshListThread();
        t.start();
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
    }

    @SuppressLint("NewApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // TODO Auto-generated method stub
        menu.add(Menu.NONE, 0, 0, getString(R.string.menu_refresh))
                // .setIcon(R.drawable.ic_menu_scan_network)
                .setEnabled(true)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        switch (item.getItemId()) {
            case 0:
                t = new RefleshListThread();
                refreshList();
                t.start();
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void compareApp() {
        Message msg;
        for (File file : pCompare.fileList) {
            pCompare.comparePackage(file);
            msg = new Message();
            msg.what = 4;
            h.sendMessage(msg);
        }
    }

    public void refreshList() {
        pCompare.items.clear();
        pCompare.itemsNotShow.clear();
        pCompare.fileList.clear();
    }

    private Message readPermissionsDialog() {
        Message msg = new Message();
        Bundle bundle = new Bundle();
        msg.what = 1;

        bundle.putString(CheckRomUtils.TITLE_DIALOG_STRING,
                MainActivity.this.getString(R.string.title_waiting_dialog));
        bundle.putString(CheckRomUtils.MSG_DIALOG_STRING,
                MainActivity.this.getString(R.string.msg_reading_permissions_dialog));
        msg.setData(bundle);

        return msg;
    }

    private Message doTheCompare() {
        Message msg = new Message();
        Bundle bundle = new Bundle();
        msg.what = 3;

        bundle.putString(CheckRomUtils.TITLE_DIALOG_STRING,
                MainActivity.this.getString(R.string.title_waiting_dialog));
        bundle.putString(CheckRomUtils.MSG_DIALOG_STRING,
                MainActivity.this.getString(R.string.msg_doing_compare_dialog));
        msg.setData(bundle);

        return msg;
    }

    public class RefleshListThread extends Thread {
        @Override
        public void run() {
            // TODO Auto-generated method stub
            if (pCompare.itemsNotShow.size() == 0) {
                Message msg;
                msg = new Message();
                msg = doTheCompare();
                pCompare.doWork();
                h.sendMessage(msg);
                c.readPermissions();
                compareApp();
                msg = new Message();
                msg.what = 2;
                h.sendMessage(msg);
                msg = new Message();
                msg.what = 5;
                h.sendMessage(msg);
                super.run();
            }
        }
    }

    public class ToDetailChildListener implements ExpandableListView.OnChildClickListener {

        @Override
        public boolean onChildClick(ExpandableListView parent, View v, int groupPosition,
                int childPosition, long id) {
            // TODO Auto-generated method stub
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, PackageDetailActivity.class);
            if (groupPosition == 0) {
                intent.putExtra(CheckRomUtils.EXTRA_TODETAIL_TYPE,
                        CheckRomUtils.EXTRA_TODETAIL_TYPE_NOSHOW);
            }
            else {
                intent.putExtra(CheckRomUtils.EXTRA_TODETAIL_TYPE,
                        CheckRomUtils.EXTRA_TODETAIL_TYPE_SHOW);
            }
            intent.putExtra(CheckRomUtils.EXTRA_TODETAIL_ID, childPosition);
            startActivity(intent);
            return false;
        }

    }
}
