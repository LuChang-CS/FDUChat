package com.fudan.im.fragment;

import com.fudan.im.R;
import com.fudan.im.action.UserAction;
import com.fudan.im.activity.SendMomentActivity;
import com.fudan.im.adapter.MomentListAdapter;
import com.fudan.im.bean.ApplicationData;
import com.fudan.im.bean.Moment;
import com.fudan.im.bean.TranObject;
import com.fudan.im.global.Result;
import com.fudan.im.network.NetService;
import com.fudan.im.view.TitleBarView;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class NearByFragment extends Fragment implements View.OnClickListener {
	private Context mContext;
	private View mBaseView;
	private TitleBarView mTitleBarView;
	private ListView mMomentListView;
    private List<Moment> mMomentList;
    private MomentListAdapter adapter;

    protected NetService mNetService = NetService.getInstance();
    private static TranObject mReceivedInfo = null;
    private static boolean mIsReceived = false;
    private static boolean isRunning = false;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mContext = getActivity();
		mBaseView = inflater.inflate(R.layout.fragment_nearby, null);
		findView();
		init();
		return mBaseView;
	}
	private void findView() {
        mTitleBarView = (TitleBarView) mBaseView.findViewById(R.id.title_bar);
        mMomentListView = (ListView) mBaseView.findViewById(R.id.moment_listview);
	}
	
	private void init() {
        mTitleBarView.setCommonTitle(View.GONE, View.VISIBLE, View.VISIBLE);
        mTitleBarView.setTitleText("动态");
        mTitleBarView.setBtnRight(R.drawable.ic_reg_camera);

        mTitleBarView.setBtnRightOnclickListener(this);
        pullMoment();
	}

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_btn_right:
                Intent intent = new Intent(mContext, SendMomentActivity.class);
                startActivity(intent);
                break;
        }
    }

    private void pullMoment() {
        if (isRunning) {
            return;
        }
        new AsyncTask<Void, Void, Integer>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                isRunning = true;
            }

            @Override
            protected Integer doInBackground(Void... params) {
                try {
                    mIsReceived = false;
                    mNetService.setupConnection();
                    if (!mNetService.isConnected()) {
                        return 0;
                    } else {
                        UserAction.pullMoment(ApplicationData.getInstance().getUserInfo().getId());
                        while (!mIsReceived) {
                        }// 如果没收到的话就会一直阻塞;
                        //mNetService.closeConnection();
                        if (mReceivedInfo.getResult() == Result.PULL_MOMENT_SUCCESS) {
                            mMomentList = (List<Moment>) mReceivedInfo.getObject();
                            return 1;
                        } else {
                            mMomentList = null;
                            return 1;
                        }
                    }
                } catch (Exception e) {
                    Log.d("pull moment", "获取朋友圈异常");
                }
                return 0;

            }

            @Override
            protected void onPostExecute(Integer result) {
                super.onPostExecute(result);
                isRunning = false;
                if (result == 0) {
                    Log.d("pull moment", "服务器异常");
                } else {
                    if (result == 2) {
                        Log.d("pull moment", "获取朋友圈异常");
                    } else if (result == 1) {
                        if (mMomentList == null) {
                            return;
                        }
                        adapter = new MomentListAdapter(mContext, mMomentList);
                        mMomentListView.setAdapter(adapter);
                    }
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public static void setPullMoment(TranObject object, boolean isReceived) {

        mReceivedInfo = object;
        mIsReceived = true;
    }
}

