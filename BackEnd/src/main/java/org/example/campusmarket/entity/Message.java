package org.example.campusmarket.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Message {
    private Integer id;
    private Integer Senderid;  //发送者id
    private Integer receiverid;  //接收者id
    private String content;  //消息内容
    private LocalDateTime sendTime;  //发送时间
    private Integer isRead;  //0-未读 1-已读
    private Integer type;  // 0-文本 1-图片
}
