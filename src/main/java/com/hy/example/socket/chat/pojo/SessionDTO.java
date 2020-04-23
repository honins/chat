package com.hy.example.socket.chat.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.websocket.Session;

/**
 * @author Created by hy
 * @date on 2020/4/21 16:31
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SessionDTO {

    private String user;

    private String sessionId;

    private Session session;

}
