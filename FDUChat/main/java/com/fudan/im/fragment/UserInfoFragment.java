package com.fudan.im.fragment;

import com.fudan.im.R;
import com.fudan.im.action.UserAction;
import com.fudan.im.activity.ChangeUserInfoActivity;
import com.fudan.im.activity.MainActivity;
import com.fudan.im.bean.ApplicationData;
import com.fudan.im.bean.TranObject;
import com.fudan.im.bean.User;
import com.fudan.im.dialog.FlippingLoadingDialog;
import com.fudan.im.global.Result;
import com.fudan.im.network.NetService;
import com.fudan.im.util.PhotoUtils;
import com.fudan.im.view.HandyTextView;
import com.fudan.im.view.TitleBarView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class UserInfoFragment extends Fragment implements View.OnClickListener {
	private Context mContext;
	private View mBaseView;
	private TitleBarView mTitleBarView;

	private ImageView mUserInfoAvatar;
	private LinearLayout mLayoutChangeUserinfo;

	private HandyTextView mUserInfoUserId;
	private HandyTextView mUserinfoNickname;
	private HandyTextView mUserInfoGender;
	private HandyTextView mUserInfoBirthday;

	private User user;

	public final static int INTENT_REQUEST_CODE_UPDATE = 0;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mContext = getActivity();
		mBaseView = inflater.inflate(R.layout.fragment_userinfo, null);
		findView();
		init();
		return mBaseView;
	}
	private void findView(){
		mTitleBarView = (TitleBarView) mBaseView.findViewById(R.id.title_bar);
	}

	private void init(){

		mTitleBarView.setCommonTitle(View.GONE, View.VISIBLE, View.GONE);
		mTitleBarView.setTitleText("个人信息");

		initView();
		setUserInfo();
		initEvents();
	}

	private void initView() {
		mUserInfoAvatar   = (ImageView) mBaseView.findViewById(R.id.iv_userinfo_userphoto);
		mUserInfoUserId   = (HandyTextView) mBaseView.findViewById(R.id.iv_userinfo_userid);
		mUserinfoNickname = (HandyTextView) mBaseView.findViewById(R.id.iv_userinfo_nickname);
		mUserInfoGender   = (HandyTextView) mBaseView.findViewById(R.id.iv_userinfo_gender);
		mUserInfoBirthday = (HandyTextView) mBaseView.findViewById(R.id.iv_userinfo_birthday);
		mLayoutChangeUserinfo = (LinearLayout) mBaseView.findViewById(R.id.iv_userinfo_change);
	}

	private void initEvents() {
		mLayoutChangeUserinfo.setOnClickListener(this);
	}

	private void setUserInfo() {
        user = ApplicationData.getInstance().getUserInfo();
		Bitmap userPhoto = PhotoUtils.getBitmap(user.getPhoto());
		mUserInfoAvatar.setImageBitmap(userPhoto);
		mUserInfoUserId.setText(user.getAccount());
		mUserinfoNickname.setText(user.getUserName());
		int gender = user.getGender();
        String[] genders = { "女", "男", "其他" };
        mUserInfoGender.setText(genders[gender]);

		Date d = user.getBirthday();
		SimpleDateFormat sp = new SimpleDateFormat("yyyy-MM-dd");
		mUserInfoBirthday.setText(sp.format(d));
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.iv_userinfo_change:
				Intent intent = new Intent(mContext, ChangeUserInfoActivity.class);
				startActivityForResult(intent, INTENT_REQUEST_CODE_UPDATE);
				break;
		}
	}

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case INTENT_REQUEST_CODE_UPDATE:
                setUserInfo();
                break;
            default:
                break;
        }
    }
}

