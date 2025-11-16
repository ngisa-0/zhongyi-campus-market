package org.example.campusmarket.DTO;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

// 发送消息时前端传入的参数
@Data
public class SendMessageDTO {
    @NotNull(message = "接收者id不能为空")
    private Integer receiverId;  //接收者id
    @NotNull(message = "消息内容不能为空")
    @Size(max = 500, message = "消息内容不能超过500字")
    private String content;  //消息内容
    private Integer type = 0;  //默认为文本消息
}
