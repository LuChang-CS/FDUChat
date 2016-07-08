package com.fudan.im.network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.StreamCorruptedException;
import java.net.Socket;

import com.fudan.im.activity.ChangeUserInfoActivity;
import com.fudan.im.activity.LoginActivity;
import com.fudan.im.activity.SearchFriendActivity;
import com.fudan.im.activity.SendMomentActivity;
import com.fudan.im.activity.register.StepAccount;
import com.fudan.im.activity.register.StepPhoto;
import com.fudan.im.bean.ApplicationData;
import com.fudan.im.bean.TranObject;
import com.fudan.im.bean.TranObjectType;
import com.fudan.im.fragment.NearByFragment;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

public class ClientListenThread extends Thread {
	private Socket mSocket = null;
	private Context mContext = null;
	private ObjectInputStream mOis;

	private boolean isStart = true;

	public ClientListenThread(Context context, Socket socket) {
		this.mContext = context;
		this.mSocket = socket;
		try {
			mOis = new ObjectInputStream(mSocket.getInputStream());
		} catch (StreamCorruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void setSocket(Socket socket) {
		this.mSocket = socket;
	}

	@Override
	public void run() {
		try {
			isStart = true;
			while (isStart) {
				TranObject mReceived = null;
                System.out.println(NetService.sl);
				//System.out.println("开始接受服务器");
				mReceived = (TranObject) mOis.readObject();
				System.out.println("接受成功");
				System.out.println(mReceived.getTranType());
				TranObjectType tro = mReceived.getTranType();
				switch (tro) {
				case REGISTER_ACCOUNT:
					StepAccount.setRegisterInfo(mReceived, true);
					 System.out.println("注册账号成功");
					break;
				case REGISTER:
					StepPhoto.setRegisterInfo(mReceived, true);
					break;
				case LOGIN:
					ApplicationData.getInstance().loginMessageArrived(mReceived);
					break;
				case SEARCH_FRIEND:
					System.out.println("收到朋友查找结果");
					SearchFriendActivity.messageArrived(mReceived);
					break;
				case FRIEND_REQUEST:
					ApplicationData.getInstance().friendRequestArrived(mReceived);
					break;
				case MESSAGE:
					ApplicationData.getInstance().messageArrived(mReceived);
					break;
                case CHANGE_USERINFO:
                    ChangeUserInfoActivity.setChangeInfo(mReceived, true);
                    break;
                case PULL_MOMENT:
                    NearByFragment.setPullMoment(mReceived, true);
                    break;
                case SEND_MOMENT:
                    SendMomentActivity.setSubmitMoment(mReceived, true);
                    break;
				default:
					break;
				}
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO: handle exception
			e.printStackTrace();
		}

    }

	public void close() {
		isStart = false;
	}
}
