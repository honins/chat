package com.hy.example.socket.chat.component;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.HashBiMap;
import com.hy.example.socket.chat.component.constant.MessageType;
import com.hy.example.socket.chat.pojo.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.hy.example.socket.chat.component.MessageHandler.createEnterMessage;
import static com.hy.example.socket.chat.component.MessageHandler.createQuitMessage;

/**
 * 服务端
 *
 * @author Created by hy
 * @date on 2020/4/21 10:53
 */
@Slf4j
@Component
@ServerEndpoint("/chat/{fromUser}")
public class WebSocketChatService {

    /**
     * 在线人数
     */
    private static int onlineCount = 0;

    /**
     * 全部在线会话  PS: 基于场景考虑 这里使用线程安全的Map存储会话对象。
     */
    private static Map<String, Session> onlineSessions = new ConcurrentHashMap<String, Session>();

    /**
     * 用户绑定的demo数据
     * key、value 用户身份
     */
    private static HashBiMap<String, String> bindUsersMap = HashBiMap.create(16);

    /**
     * 初始化
     */
    @PostConstruct
    public void init() {
        bindUsersMap.put("p1", "d1");
        bindUsersMap.put("p2", "d2");
        bindUsersMap.put("p3", "d3");

        bindUsersMap.put("d1", "p1");
        bindUsersMap.put("d2", "p2");
        bindUsersMap.put("d3", "p3");
    }

    /**
     * 当客户端打开连接：1.添加会话对象 2.更新在线人数
     */
    @OnOpen
    public void onOpen(@PathParam("fromUser") String fromUser, Session session) {
        onlineSessions.put(fromUser, session);
        String toUser = bindUsersMap.get(fromUser);
        sendMessageToUser(createEnterMessage(fromUser, toUser));
    }

    /**
     * 当客户端发送消息：1.获取它的用户名和消息 2.发送消息给所有人
     * <p>
     * PS: 这里约定传递的消息为JSON字符串 方便传递更多参数！
     */
    @OnMessage
    public void onMessage(String jsonStr) {
        Message message = JSON.parseObject(jsonStr, Message.class);
        message.setType(MessageType.SPEAK);
        message.setToUser(bindUsersMap.get(message.getFromUser()));
        sendMessageToUser(message);
    }

    /**
     * 当关闭连接：1.移除会话对象 2.更新在线人数
     */
    @OnClose
    public void onClose(@PathParam("fromUser") String fromUser,Session session) {
        onlineSessions.remove(fromUser);
        String toUser = bindUsersMap.get(fromUser);
        sendMessageToUser(createQuitMessage(fromUser, toUser));
    }

    /**
     * 当通信发生异常：打印错误日志
     */
    @OnError
    public void onError(Session session, Throwable error) {
        error.printStackTrace();
    }

    /**
     * 发送信息给指定人
     */
    private static void sendMessageToUser(String toUser, String messageStr) {
        if (onlineSessions.containsKey(toUser)){
            onlineSessions.get(toUser).getAsyncRemote().sendText(messageStr);
        }
    }

    /**
     * 发送信息给指定人
     */
    private static void sendMessageToUser(Message message) {
        String fromUser = message.getFromUser();
        String toUser = message.getToUser();
        String messageStr = JSON.toJSONString(message);
        if (!StringUtils.isEmpty(message.getFromUser())){
            sendMessageToUser(fromUser, messageStr);
        }
        if (!StringUtils.isEmpty(message.getToUser())){
            sendMessageToUser(toUser, messageStr);
        }
    }

    /**
     * 发送信息给群组，和发送指定人的区别就是遍历要发送的用户
     */
    private static void sendMessageToGroup(Message message) {
        String fromUser = message.getFromUser();
        //得到要发送用户的集合
        List<String> toUserIds = new ArrayList<>();
        String messageStr = JSON.toJSONString(message);
        if (!StringUtils.isEmpty(message.getFromUser())){
            sendMessageToUser(fromUser, messageStr);
        }
        toUserIds.forEach(id -> {
            if (!StringUtils.isEmpty(id)){
                sendMessageToUser(id, messageStr);
            }
        });
    }

}
