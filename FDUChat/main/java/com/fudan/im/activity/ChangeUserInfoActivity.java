package com.fudan.im.activity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.fudan.im.BaseActivity;
import com.fudan.im.R;
import com.fudan.im.action.UserAction;
import com.fudan.im.bean.ApplicationData;
import com.fudan.im.bean.TranObject;
import com.fudan.im.bean.User;
import com.fudan.im.global.Result;
import com.fudan.im.network.NetService;
import com.fudan.im.util.DateUtils;
import com.fudan.im.util.PhotoUtils;

import java.util.Calendar;
import java.util.Date;

public class ChangeUserInfoActivity extends BaseActivity implements View.OnClickListener {

    protected NetService mNetService = NetService.getInstance();

    private ImageView mChangeUserPhoto;
    private LinearLayout mChangeSelectPhoto;
    private LinearLayout mChangeTakePicture;

    private EditText mChangeNickName;
    private RadioGroup mChangeGender;
    private RadioButton mChangeGenderMale;
    private RadioButton mChangeGenderFemale;
    private RadioButton mChangeGenderOther;
    private DatePicker mChangeBirthday;

    private LinearLayout mChangeSubmit;

    User user;
    User newUser;

    private Calendar mCalendar;
    private static final int MAX_AGE = 100;
    private static final int MIN_AGE = 1;

    private static TranObject mReceivedInfo = null;
    private static boolean mIsReceived = false;

    private Bitmap userPhotoBitMap;
    private String mTakePicturePath;

    private static boolean isRunning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_user_info);
        initViews();
        initValues();
        initEvents();
    }

    @Override
    protected void onDestroy() {
        PhotoUtils.deleteImageFile();
        super.onDestroy();
    }

    @Override
    protected void initViews() {
        mChangeUserPhoto = (ImageView) findViewById(R.id.change_photo_iv_userphoto);
        mChangeSelectPhoto = (LinearLayout) findViewById(R.id.change_photo_layout_selectphoto);
        mChangeTakePicture = (LinearLayout) findViewById(R.id.change_photo_layout_takepicture);
        mChangeNickName = (EditText) findViewById(R.id.change_nickname_iv_input);
        mChangeGender = (RadioGroup) findViewById(R.id.change_gender_iv_group);
        mChangeGenderMale = (RadioButton) findViewById(R.id.change_gender_iv_male);
        mChangeGenderFemale = (RadioButton) findViewById(R.id.change_gender_iv_female);
        mChangeGenderOther = (RadioButton) findViewById(R.id.change_gender_iv_other);
        mChangeBirthday = (DatePicker) findViewById(R.id.change_birthday_dp_birthday);
        mChangeSubmit = (LinearLayout) findViewById(R.id.change_submit);
    }

    protected void initValues() {
        user = ApplicationData.getInstance().getUserInfo();
        newUser = new User(user.getAccount(), user.getUserName(), user.getPassword(), user.getBirthday(), user.getGender(), user.getPhoto());
        newUser.setId(user.getId());
        userPhotoBitMap = PhotoUtils.getBitmap(user.getPhoto());

        mChangeUserPhoto.setImageBitmap(PhotoUtils.getBitmap(user.getPhoto()));
        mChangeNickName.setText(user.getUserName());
        int gender = user.getGender();
        if (gender == 0) {
            mChangeGenderFemale.setChecked(true);
        } else if (gender == 1) {
            mChangeGenderMale.setChecked(true);
        } else {
            mChangeGenderOther.setChecked(true);
        }
        mCalendar = Calendar.getInstance();
        mCalendar.setTime(user.getBirthday());
        mChangeBirthday.init(mCalendar.get(Calendar.YEAR),
                mCalendar.get(Calendar.MONTH),
                mCalendar.get(Calendar.DAY_OF_MONTH), null);
    }

    private Date getBirthday() {
        Calendar c = Calendar.getInstance();
        c.set(mChangeBirthday.getYear(), mChangeBirthday.getMonth(), mChangeBirthday.getDayOfMonth());
        return c.getTime();
    }

    private int getGender() {
        int res;
        if (mChangeGenderMale.isChecked()) {
            res = 1;
        } else if (mChangeGenderFemale.isChecked()) {
            res = 0;
        } else {
            res = 2;
        }
        return res;
    }

    @Override
    protected void initEvents() {
        mChangeSelectPhoto.setOnClickListener(this);
        mChangeTakePicture.setOnClickListener(this);
        mChangeSubmit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.change_photo_layout_selectphoto:
                PhotoUtils.selectPhoto(this);
                break;
            case R.id.change_photo_layout_takepicture:
                mTakePicturePath = PhotoUtils.takePicture(this);
                break;
            case R.id.change_submit:
                submitUserInfoChange();
                break;
        }
    }

    private void submitUserInfoChange() {

        if (isRunning) {
            return;
        }

        newUser.setUserName(mChangeNickName.getText().toString());
        newUser.setBirthday(getBirthday());
        newUser.setGender(getGender());
        newUser.setPhoto(PhotoUtils.getBytes(userPhotoBitMap));

        putAsyncTask(new AsyncTask<Void, Void, Integer>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                isRunning = true;
                showLoadingDialog("请稍后,正在提交...");
            }

            @Override
            protected Integer doInBackground(Void... params) {
                try {
                    mIsReceived = false;
                    mNetService.setupConnection();
                    if (!mNetService.isConnected()) {
                        return 0;
                    } else {
                        UserAction.changeUserInfo(newUser);
                        long start = System.currentTimeMillis();
                        while (!mIsReceived) {
                            long end = System.currentTimeMillis();
                            if (end - start > 3000) {
                                break;
                            }
                        }// 如果没收到的话就会一直阻塞;8
                        //mNetService.closeConnection();
                        if (mReceivedInfo.getResult() == Result.UPDATE_SUCCESS) {
                            ApplicationData.getInstance().setNewUser(newUser);
                            return 1;
                        } else
                            return 2;
                    }
                } catch (Exception e) {
                    Log.d("update", "更新异常");

                }
                return 0;

            }

            @Override
            protected void onPostExecute(Integer result) {
                super.onPostExecute(result);
                isRunning = false;
                dismissLoadingDialog();
                if (result == 0) {
                    showCustomToast("服务器异常");

                } else {
                    if (result == 1) {
                        showCustomToast("更新成功");
                        finish();
                    } else if (result == 2) {
                        showCustomToast("更新失败");
                    }
                }
            }

        });
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case PhotoUtils.INTENT_REQUEST_CODE_ALBUM:
                if (data == null) {
                    return;
                }
                if (resultCode == RESULT_OK) {
                    if (data.getData() == null) {
                        return;
                    }
                    Uri uri = data.getData();
                    String[] proj = { MediaStore.MediaColumns.DATA };
                    Cursor cursor = managedQuery(uri, proj, null, null, null);
                    if (cursor != null) {
                        int column_index = cursor
                                .getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
                        if (cursor.getCount() > 0 && cursor.moveToFirst()) {
                            String path = cursor.getString(column_index);
                            Bitmap bitmap = BitmapFactory.decodeFile(path);
                            if (PhotoUtils.bitmapIsLarge(bitmap)) {
                                PhotoUtils.cropPhoto(this, this, path);
                            } else {
                                this.setUserPhoto(PhotoUtils.compressImage(bitmap));
                            }
                        }
                    }
                }
                break;

            case PhotoUtils.INTENT_REQUEST_CODE_CAMERA:
                if (resultCode == RESULT_OK) {
                    String path = this.mTakePicturePath;
                    Bitmap bitmap = BitmapFactory.decodeFile(path);
                    if (PhotoUtils.bitmapIsLarge(bitmap)) {
                        PhotoUtils.cropPhoto(this, this, path);
                    } else {
                        //mStepPhoto.setUserPhoto(bitmap);
                        this.setUserPhoto(PhotoUtils.compressImage(bitmap));
                    }
                }
                break;

            case PhotoUtils.INTENT_REQUEST_CODE_CROP:
                if (resultCode == RESULT_OK) {
                    String path = data.getStringExtra("path");
                    if (path != null) {
                        Bitmap bitmap = BitmapFactory.decodeFile(path);
                        if (bitmap != null) {
                            //mStepPhoto.setUserPhoto(bitmap);
                            this.setUserPhoto(PhotoUtils.compressImage(bitmap));
                        }
                    }
                }
                break;
        }
    }

    public void setUserPhoto(Bitmap bitmap) {
        if (bitmap != null) {
            userPhotoBitMap = bitmap;
            mChangeUserPhoto.setImageBitmap(userPhotoBitMap);
            return;
        }
        showCustomToast("未获取到图片");
        userPhotoBitMap = null;
        mChangeUserPhoto.setImageResource(R.drawable.ic_common_def_header);
    }

    public static void setChangeInfo(TranObject object, boolean isReceived) {

        mReceivedInfo = object;
        mIsReceived = true;
    }
}
