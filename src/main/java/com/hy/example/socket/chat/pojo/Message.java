package com.hy.example.socket.chat.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Created by hy
 * @date on 2020/4/21 11:14
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Message {

    /**
     * 发送人id
     */
    private Integer fromId;

    /**
     * 接收人id
     */
    private Integer toId;

    /**
     * 消息类型
     */
    private String type;

    /**
     * 发送消息
     */
    private String msg;

}
