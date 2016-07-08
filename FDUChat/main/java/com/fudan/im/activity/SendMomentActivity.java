package com.fudan.im.activity;

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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.fudan.im.BaseActivity;
import com.fudan.im.R;
import com.fudan.im.action.UserAction;
import com.fudan.im.bean.ApplicationData;
import com.fudan.im.bean.Moment;
import com.fudan.im.bean.TranObject;
import com.fudan.im.global.Result;
import com.fudan.im.network.NetService;
import com.fudan.im.util.PhotoUtils;

public class SendMomentActivity extends BaseActivity implements View.OnClickListener {

    protected NetService mNetService = NetService.getInstance();

    private static TranObject mReceivedInfo = null;
    private static boolean mIsReceived = false;

    EditText mMomentText;

    private ImageView mMomentPhoto;
    private LinearLayout mMomentSelectPhoto;
    private LinearLayout mMomentTakePicture;

    private LinearLayout mMomentSubmit;

    private Bitmap momentPhotoBitMap = null;
    private String mTakePicturePath = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_moment);
        initViews();
        initEvents();
    }

    @Override
    protected void initViews() {
        mMomentText = (EditText) findViewById(R.id.moment_text);
        mMomentPhoto = (ImageView) findViewById(R.id.moment_photo);
        mMomentSelectPhoto = (LinearLayout) findViewById(R.id.moment_layout_selectphoto);
        mMomentTakePicture = (LinearLayout) findViewById(R.id.moment_layout_takepicture);
        mMomentSubmit = (LinearLayout) findViewById(R.id.moment_submit);
    }

    @Override
    protected void initEvents() {
        mMomentSubmit.setOnClickListener(this);
        mMomentSelectPhoto.setOnClickListener(this);
        mMomentTakePicture.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.moment_layout_selectphoto:
                PhotoUtils.selectPhoto(this);
                break;
            case R.id.moment_layout_takepicture:
                mTakePicturePath = PhotoUtils.takePicture(this);
                break;
            case R.id.moment_submit:
                momentSubmitHandler();
                break;
        }
    }

    private void momentSubmitHandler() {
        String text = mMomentText.getText().toString();
        byte[] photo = null;
        if (momentPhotoBitMap != null) {
            photo = PhotoUtils.getBytes(momentPhotoBitMap);
        }
        momentSubmit(text, photo);
    }

    private void momentSubmit(final String text, final byte[] photo) {
        putAsyncTask(new AsyncTask<Void, Void, Integer>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
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
                        int userId = ApplicationData.getInstance().getUserInfo().getId();
                        Moment moment = new Moment(-1, userId, text, photo);
                        UserAction.sendMoment(moment);
                        while (!mIsReceived) {
                        }// 如果没收到的话就会一直阻塞;
                        //mNetService.closeConnection();
                        if (mReceivedInfo.getResult() == Result.SEND_MOMENT_SUCCESS) {
                            return 1;
                        } else
                            return 2;
                    }
                } catch (Exception e) {
                    Log.d("submit_moment", "发朋友圈异常");

                }
                return 0;

            }

            @Override
            protected void onPostExecute(Integer result) {
                super.onPostExecute(result);
                dismissLoadingDialog();
                if (result == 0) {
                    showCustomToast("服务器异常");

                } else {
                    if (result == 1) {
                        showCustomToast("发布成功");
                        finish();
                    } else if (result == 2) {
                        showCustomToast("发布失败");
                    }
                }
            }

        });
    }

    public void setMomentPhoto(Bitmap bitmap) {
        if (bitmap != null) {
            momentPhotoBitMap = bitmap;
            mMomentPhoto.setImageBitmap(momentPhotoBitMap);
            return;
        }
        showCustomToast("未获取到图片");
        momentPhotoBitMap = null;
        mMomentPhoto.setImageResource(R.drawable.ic_common_def_header);
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
                            this.setMomentPhoto(bitmap);
                        }
                    }
                }
                break;

            case PhotoUtils.INTENT_REQUEST_CODE_CAMERA:
                if (resultCode == RESULT_OK) {
                    String path = this.mTakePicturePath;
                    Bitmap bitmap = BitmapFactory.decodeFile(path);
                    this.setMomentPhoto(bitmap);
                }
                break;

            case PhotoUtils.INTENT_REQUEST_CODE_CROP:
                if (resultCode == RESULT_OK) {
                    String path = data.getStringExtra("path");
                    if (path != null) {
                        Bitmap bitmap = BitmapFactory.decodeFile(path);
                        if (bitmap != null) {
                            //mStepPhoto.setUserPhoto(bitmap);
                            this.setMomentPhoto(bitmap);
                        }
                    }
                }
                break;
        }
    }

    public static void setSubmitMoment(TranObject object, boolean isReceived) {

        mReceivedInfo = object;
        mIsReceived = true;
    }
}
