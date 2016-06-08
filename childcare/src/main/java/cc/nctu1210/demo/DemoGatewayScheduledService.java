package cc.nctu1210.demo;

import android.app.Service;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cc.nctu1210.childcare.GatewayLoginActivity;
import cc.nctu1210.childcare.MasterLoginMainFragment;
import cc.nctu1210.entity.ChildProfile;
import cc.nctu1210.tool.ApplicationContext;
import cc.nctu1210.tool.CallBack;
import cc.nctu1210.tool.CallBackContent;
import cc.nctu1210.view.ChildItem;
import cc.nctu1210.view.ChildrenListAdapter;
import cc.nctu1210.view.ChildrenListAdapterForGateway;
/**
 * Created by User on 2016/6/2.
 */
public class DemoGatewayScheduledService extends Service {
    private static final String TAG = DemoGatewayScheduledService.class.getSimpleName();
    private Timer timer = new Timer();
    private Handler handler = new Handler();
    private int count;
    private List<ChildItem> mChildItems = new ArrayList<ChildItem>();
    private List<ChildProfile> mListChildren  = new ArrayList<ChildProfile>();
    private DemoChildrenListAdapter mChildListAdapter;
    private List<ChildProfile> mListScan;

    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        Log.i(TAG, "mIsServiceOn:" + ApplicationContext.mIsServiceOn);
        mChildItems = DemoGatewayLoginActivity.mChildItems;
        mListScan = DemoGatewayLoginActivity.mListScan;
        mChildListAdapter = DemoGatewayLoginActivity.mChildListAdapter;
        count = 0;
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                count++;
                if (count > 1) {
                    handler.post(new Runnable() {
                        public void run() {
                            synchronized(mChildItems) {
                                StringBuilder cids = new StringBuilder("");
                                StringBuilder status = new StringBuilder("");
                                int unixTime = (int) (System.currentTimeMillis() / 1000L);
                                for (int i=0; i<mChildItems.size(); i++) {
                                    ChildItem child = mChildItems.get(i);
                                    /*
                                    double average_rssi = 0;
                                    for(int j=0; j<child.mScanedRssiList.size();j++)
                                    {
                                        average_rssi += Double.parseDouble(child.mScanedRssiList.get(j));
                                    }
                                    average_rssi /= child.mScanedRssiList.size();
*/
                                    cids.append(child.cid).append(",");
                                    // status.append(String.valueOf(Math.round(average_rssi))).append(",");
                                    status.append(String.valueOf(child.control_rssi)).append(",");
                                    //child.mScanedRssiList.clear();
                                }
                                //mListScan.clear();
                                ApplicationContext.gateway_upload(ApplicationContext.mGid, cids.toString(), status.toString(), String.valueOf(unixTime));
                            }
                            /*
                            ApplicationContext.show_all_children(ApplicationContext.login_mid, false, -1, new CallBack() {
                                @Override
                                public void done(CallBackContent content) {
                                    if (content != null) {
                                        mListChildren = content.getShow_children();
                                        populateList();
                                    } else {
                                        Log.e(TAG, "show_child_by_id fail" + "\n");
                                    }
                                }
                            });*/
                        }
                    });
                }
            }
        }, 0, 1 * 10 * 1000);//10 sec
    }


    private void populateList() {
        mChildListAdapter.getData().clear();
        Log.i("TAG", "Initializing ListView....." + mChildListAdapter.getData().size());
        Collections.sort(mListChildren);
        for (int i = 0, size = mListChildren.size(); i < size; i++) {
            ChildItem object = new ChildItem(mListChildren.get(i).getName(),mListChildren.get(i).getStatus());
            object.photoName = mListChildren.get(i).getPhotoName();
            object.place = mListChildren.get(i).getPlace();
            object.flag = mListChildren.get(i).getFlag();
            object.cid = mListChildren.get(i).getCid();
            object.rssi = mListChildren.get(i).getRssi();
            mChildItems.add(object);
        }
        Log.i("TAG", "Initialized ListView....." + mChildListAdapter.getData().size());
        mChildListAdapter.notifyDataSetChanged();

    }

    @Override
    public void onDestroy() {
        timer.cancel();
        stopSelf();
        super.onDestroy();
    }
}
