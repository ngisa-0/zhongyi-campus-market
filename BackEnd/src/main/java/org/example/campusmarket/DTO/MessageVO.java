package org.example.campusmarket.DTO;

import lombok.Data;

import java.time.LocalDateTime;

// 后端返回给前端的消息详情
@Data
public class MessageVO {
    private Integer id;
    private Integer senderId;
    private String senderName;  // 发送者用户名（前端展示用）
    private Integer receiverId;
    private String content;
    private LocalDateTime sendTime;
    private Integer isRead;  // 0-未读 1-已读
    private Integer type;  // 0-文本 1-图片
}