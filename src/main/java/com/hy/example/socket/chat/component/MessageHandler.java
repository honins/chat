package com.hy.example.socket.chat.component;

import com.hy.example.socket.chat.component.constant.MessageType;
import com.hy.example.socket.chat.pojo.Message;

/**
 * @author Created by hy
 * @date on 2020/4/22 9:34
 */
public class MessageHandler {

    public static Message generatorEnterMessage(Integer fromId, Integer toId){
        return Message.builder()
                .fromId(fromId)
                .toId(toId)
                .type(MessageType.ENTER)
                .msg("上线了")
                .build();
    }

    public static Message generatorQuitMessage(Integer fromId,Integer toId){
        return Message.builder()
                .fromId(fromId)
                .toId(toId)
                .type(MessageType.QUIT)
                .msg("下线了")
                .build();
    }

}
