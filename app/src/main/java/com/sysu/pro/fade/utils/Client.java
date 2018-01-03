package com.sysu.pro.fade.utils;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

/**
 * Created by road on 2017/11/18.
 * websocket客户端
 */

public class Client extends WebSocketClient {

    public Client(URI serverURI, Draft draft) {
        super(serverURI);
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        System.out.println("连接成功");
    }

    @Override
    public void onMessage(String message) {
        System.out.println("接收到服务器返回的消息");
        //EventBus发给事件接受者
        try{

        }catch (Exception e){
            e.printStackTrace();
            System.out.println("服务器返回消息异常");
        }

    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("连接关闭");
    }

    @Override
    public void onError(Exception ex) {
        System.out.println("连接失败"+ex.getMessage());
    }
}
