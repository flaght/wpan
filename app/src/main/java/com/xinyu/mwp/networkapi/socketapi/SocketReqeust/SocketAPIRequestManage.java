package com.xinyu.mwp.networkapi.socketapi.SocketReqeust;


import android.os.Handler;

import com.xinyu.mwp.listener.OnAPIListener;
import com.xinyu.mwp.networkapi.NetworkAPIException;
import com.xinyu.mwp.util.LogUtil;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by yaowang on 2017/2/20.
 */

public class SocketAPIRequestManage {

    private static SocketAPIRequestManage socketAPIRequestManage = null;
    private HashMap<Long, SocketAPIRequest> socketAPIRequestHashMap = new HashMap<Long, SocketAPIRequest>();
    private long sessionId = 10000;
    private Handler handler = new Handler();
    private  Runnable runnable = new Runnable(){
        @Override
        public void run() {
            checkReqeustTimeout();
            handler.postDelayed(this, 1000);
        }
    };

    public static synchronized SocketAPIRequestManage getInstance() {
        if (socketAPIRequestManage == null) {
            socketAPIRequestManage = new SocketAPIRequestManage();
        }
        return socketAPIRequestManage;
    }




    public void start() {
        stop();
        SocketAPINettyBootstrap.getInstance().connect(true);
        handler.postDelayed(runnable, 1000);// 打开定时器，执行操作
    }


    public void  stop() {
        handler.removeCallbacks(runnable);// 关闭定时器处理
        SocketAPINettyBootstrap.getInstance().closeChannel();
    }

    private synchronized long getSessionId() {
        if (sessionId > 2000000000) {
            sessionId = 10000;
        }
        sessionId += 1;
        return sessionId;
    }

    public synchronized void  notifyResponsePacket(SocketDataPacket socketDataPacket) {
        if (socketDataPacket != null) {
            SocketAPIRequest socketAPIRequest = socketAPIRequestHashMap.get(socketDataPacket.getSessionId());
            if( socketAPIRequest != null && socketAPIRequest.getListener() != null ) {
                socketAPIRequestHashMap.remove(socketDataPacket.getSessionId());
                SocketAPIResponse socketAPIResponse = new SocketAPIResponse(socketDataPacket);
                int statusCode = socketAPIResponse.statusCode();
                if( statusCode == 0 ) {
                    socketAPIRequest.onSuccess(socketAPIResponse);
                    LogUtil.d("jsonResponse:"+socketAPIResponse.jsonObject());
                }
                else {
                    socketAPIRequest.onErrorCode(statusCode);
                }
            }
        }
    }

    public void startJsonRequest(SocketDataPacket socketDataPacket, OnAPIListener<SocketAPIResponse> listener) {
        if (socketDataPacket != null && listener != null) {
            SocketAPIRequest socketAPIRequest = new SocketAPIRequest();
            socketAPIRequest.setListener(listener);
            long sessionId = getSessionId();
            socketDataPacket.setSessionId(sessionId);
            socketDataPacket.setTimestamp((int)(socketAPIRequest.getTimestamp()/1000));
            socketAPIRequestHashMap.put(sessionId, socketAPIRequest);
            SocketAPINettyBootstrap.getInstance().writeAndFlush(socketDataPacket);
        }
    }

    private synchronized void checkReqeustTimeout() {
        for (HashMap.Entry<Long, SocketAPIRequest> entry : socketAPIRequestHashMap.entrySet()) {

            if( entry.getValue().isReqeustTimeout() ) {
                socketAPIRequestHashMap.remove(entry.getKey());
                entry.getValue().onErrorCode(NetworkAPIException.SOCKET_REQEUST_TIMEOUT);
                break;
            }

        }
    }
}
