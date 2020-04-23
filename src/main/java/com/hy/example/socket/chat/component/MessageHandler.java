package com.hy.example.socket.chat.component;

import com.alibaba.fastjson.JSON;
import com.hy.example.socket.chat.component.constant.MessageType;
import com.hy.example.socket.chat.pojo.Message;

/**
 * @author Created by hy
 * @date on 2020/4/22 9:34
 */
public class MessageHandler {

    public static Message createEnterMessage(String fromUser, String toUser){
        return Message.builder()
                .fromUser(fromUser)
                .toUser(toUser)
                .type(MessageType.ENTER)
                .msg("上线了")
                .build();
    }

    public static String createEnterMessageStr(String fromUser, String toUser){
        return JSON.toJSONString(createEnterMessage(fromUser,toUser));
    }

    public static Message createQuitMessage(String fromUser, String toUser){
        return Message.builder()
                .fromUser(fromUser)
                .toUser(toUser)
                .type(MessageType.QUIT)
                .msg("下线了")
                .build();
    }

    public static String createQuitMessageStr(String fromUser, String toUser){
        return JSON.toJSONString(createQuitMessageStr(fromUser,toUser));
    }
}
