package com.fudan.im.action;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.fudan.im.bean.ApplicationData;
import com.fudan.im.bean.ChatEntity;
import com.fudan.im.bean.Moment;
import com.fudan.im.bean.TranObject;
import com.fudan.im.bean.TranObjectType;
import com.fudan.im.bean.User;
import com.fudan.im.databse.ImDB;
import com.fudan.im.global.Result;
import com.fudan.im.network.NetService;

public class UserAction {
	private static NetService mNetService = NetService.getInstance();

	public static void accountVerify(String account) throws IOException {

		TranObject t = new TranObject(account, TranObjectType.REGISTER_ACCOUNT);
		mNetService.send(t);

	}

	public static void register(User user) throws IOException {

		TranObject t = new TranObject(user, TranObjectType.REGISTER);
		mNetService.send(t);

	}

	public static void changeUserInfo(User user) throws IOException {

		TranObject t= new TranObject(user, TranObjectType.CHANGE_USERINFO);
		mNetService.send(t);

	}

	public static void loginVerify(User user) throws IOException {
		TranObject t = new TranObject(user, TranObjectType.LOGIN);
		mNetService.send(t);
	}

	public static void searchFriend(String message) throws IOException {
		TranObject t = new TranObject(message, TranObjectType.SEARCH_FRIEND);
		mNetService.send(t);

	}

	public static void sendFriendRequest(Result result, Integer id) {
		TranObject t = new TranObject();
		t.setReceiveId(id);
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("MM-dd hh:mm:ss");
		String sendTime = sdf.format(date);
		t.setSendTime(sendTime);
		User user = ApplicationData.getInstance().getUserInfo();
		t.setResult(result);
		t.setSendId(user.getId());
		t.setTranType(TranObjectType.FRIEND_REQUEST);
		t.setSendName(user.getUserName());
		try {
			mNetService.send(t);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void sendMessage(ChatEntity message) {

		TranObject t = new TranObject();
		t.setTranType(TranObjectType.MESSAGE);
		t.setReceiveId(message.getReceiverId());
		t.setSendName(ApplicationData.getInstance().getUserInfo().getUserName());
		t.setObject(message);
		try {
			mNetService.send(t);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void sendMoment(Moment moment) throws IOException {
        TranObject t = new TranObject(moment, TranObjectType.SEND_MOMENT);
        mNetService.send(t);
    }

    public static void pullMoment(int userId) throws IOException {
        TranObject t = new TranObject(userId, TranObjectType.PULL_MOMENT);
        mNetService.send(t);
    }

}
