package com.hy.example.socket.chat.component;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.HashBiMap;
import com.hy.example.socket.chat.component.constant.MessageType;
import com.hy.example.socket.chat.pojo.Message;
import com.hy.example.socket.chat.pojo.SessionDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 服务端
 *
 * @author Created by hy
 * @date on 2020/4/21 10:53
 */
@Slf4j
@Component
@ServerEndpoint("/chat/{uid}")
public class WebSocketChatService {

    /**
     * 在线人数
     */
    private static int onlineCount = 0;

    /**
     * 全部在线会话  PS: 基于场景考虑 这里使用线程安全的Map存储会话对象。
     */
    private static Map<Integer, SessionDTO> onlineSessions = new ConcurrentHashMap<Integer, SessionDTO>();

    /**
     * 用户绑定的demo数据
     * key 患者id
     * value 医生id
     */
    private static HashBiMap<Integer, Integer> bindUsersMap = HashBiMap.create(16);

    /**
     * 初始化
     */
    @PostConstruct
    public void init() {
        bindUsersMap.put(1, 101);
        bindUsersMap.put(2, 202);
        bindUsersMap.put(3, 103);
        bindUsersMap.put(101, 1);
        bindUsersMap.put(102, 2);
        bindUsersMap.put(103, 3);
    }

    /**
     * 当客户端打开连接：1.添加会话对象 2.更新在线人数
     */
    @OnOpen
    public void onOpen(@PathParam("uid") Integer uid, Session session) {
        if (onlineSessions.containsKey(uid)) {
            return;
        }
        SessionDTO sessionDTO = SessionDTO.builder()
                .uid(uid)
                .session(session)
                .build();
        onlineSessions.put(uid, sessionDTO);
        onlineCount++;
        Integer toId = bindUsersMap.get(uid);
        if (toId == null) {
            return;
        }
        sendMessageToUser(MessageHandler.generatorEnterMessage(uid, toId));
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
        Integer toId = bindUsersMap.get(message.getFromId());
        message.setToId(toId);
        sendMessageToUser(message);
    }

    /**
     * 当关闭连接：1.移除会话对象 2.更新在线人数
     */
    @OnClose
    public void onClose(@PathParam("uid") Integer uid) {
        onlineCount--;
        onlineSessions.remove(uid);
        Integer toId = bindUsersMap.get(uid);
        sendMessageToUser(MessageHandler.generatorQuitMessage(uid, toId));
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
    private static void sendMessageToUser(Message message) {
        Integer toId = message.getToId();
        String messageStr = JSON.toJSONString(message);
        try {
            if (onlineSessions.containsKey(message.getFromId())) {
                onlineSessions.get(message.getFromId()).getSession().getBasicRemote().sendText(messageStr);
            }
            if (onlineSessions.containsKey(toId)) {
                onlineSessions.get(toId).getSession().getBasicRemote().sendText(messageStr);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送信息给所有人，用于群聊
     */
    private static void sendMessageToAll(Message message) {
        String messageStr = JSON.toJSONString(message);
        onlineSessions.forEach((id, sessionDTO) -> {
            try {
                sessionDTO.getSession().getBasicRemote().sendText(messageStr);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

}
