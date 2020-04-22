package com.hy.example.socket.chat.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.websocket.Session;

/**
 * @author Created by hy
 * @date on 2020/4/21 16:31
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SessionDTO {

    @NotNull
    private Integer uid;

    @NotNull
    private Session session;

}
