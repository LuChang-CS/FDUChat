package com.fudan.im.bean;

/**
 * 传输对象类型
 * 
 * 
 * 
 */
public enum TranObjectType {
	REGISTER, // 注册
	REGISTER_ACCOUNT,//注册的账号验证
	LOGIN, // 用户登录
	LOGOUT, // 用户退出登录
	FRIENDLOGIN, // 好友上线
	FRIENDLOGOUT, // 好友下线
	MESSAGE, // 用户发送消息
	UNCONNECTED, // 无法连接
	FILE, // 传输文件
	REFRESH, // 刷新
	SEARCH_FRIEND,//找朋友
	FRIEND_REQUEST,//好友申请
	CHANGE_USERINFO, //更新个人信息
	PULL_MOMENT, // 拉取朋友圈
	SEND_MOMENT; // 发朋友圈
}
