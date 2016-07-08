package com.fudan.im.adapter;


import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fudan.im.bean.ApplicationData;
import com.fudan.im.bean.Moment;
import com.fudan.im.util.PhotoUtils;

import com.fudan.im.R;

import java.util.ArrayList;
import java.util.List;

public class MomentListAdapter extends BaseAdapter {

    private List<Moment> mMomentList;
    private LayoutInflater mInflator;

    public MomentListAdapter(Context context, List<Moment> vector) {
        this.mMomentList = vector;
        this.mInflator = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return this.mMomentList.size();
    }

    @Override
    public Object getItem(int position) {
        return this.mMomentList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView avatarView;
        TextView nameView;
        TextView contentTextView;
        ImageView contentPhotoView;
        TextView dateView;

        Moment moment = this.mMomentList.get(position);
        int userId = moment.getUserid();
        Bitmap userAvatar = ApplicationData.getInstance().getFriendPhoto(userId);
        String userName = ApplicationData.getInstance().getUserById(userId).getUserName();
        byte[] bytePhoto = moment.getPhoto();
        Bitmap photo = null;
        String text = moment.getText();

        convertView = mInflator.inflate(R.layout.moments_item, null);
        avatarView = (ImageView) convertView.findViewById(R.id.moments_user_photo);
        nameView = (TextView) convertView.findViewById(R.id.moments_user_name);
        contentTextView = (TextView) convertView.findViewById(R.id.moments_send_text);
        contentPhotoView = (ImageView) convertView.findViewById(R.id.moments_send_photo);
        dateView = (TextView) convertView.findViewById(R.id.moments_send_time);

        if (userAvatar == null) {
            avatarView.setImageResource(R.drawable.ic_common_def_header);
        } else {
            avatarView.setImageBitmap(userAvatar);
        }
        nameView.setText(userName);
        if (text == null) {
            ViewGroup p = (ViewGroup) contentTextView.getParent();
            if (p != null) {
                p.removeView(contentTextView);
            }
        } else {
            contentTextView.setText(text);
        }
        if (bytePhoto == null) {
            ViewGroup p = (ViewGroup) contentPhotoView.getParent();
            if (p != null) {
                p.removeView(contentPhotoView);
            }
        } else {
            photo = PhotoUtils.getBitmap(bytePhoto);
            int width = photo.getWidth();
            int height = photo.getHeight();
//            int containerWidth = contentPhotoView.getWidth();
//            int containerHeight = contentPhotoView.getHeight();
//            if (width > containerWidth) {
//                contentPhotoView.setMaxHeight((int) (containerHeight * containerHeight / (double) height));
//            } else if (height > containerHeight) {
//                contentPhotoView.setMaxWidth((int) ());
//            }
            contentPhotoView.setMaxWidth(width);
            contentPhotoView.setMaxHeight(height);
            contentPhotoView.setImageBitmap(photo);
        }
        dateView.setText(moment.getSendDate());

        return convertView;
    }
}
