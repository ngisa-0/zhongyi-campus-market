package org.example.campusmarket.Service;

import lombok.RequiredArgsConstructor;
import org.example.campusmarket.Mapper.MessageMapper;
import org.example.campusmarket.Mapper.UserMapper;
import org.example.campusmarket.DTO.SendMessageDTO;
import org.example.campusmarket.DTO.MessageVO;
import org.example.campusmarket.entity.Message;
import org.example.campusmarket.entity.Result;
import org.example.campusmarket.entity.User;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor  // 替代 @Autowired，通过构造器注入依赖，更符合Spring规范
public class ChatService {

    private final MessageMapper messageMapper;
    private final UserMapper userMapper;
    private final SimpMessagingTemplate websocketTemplate;  // WebSocket推送工具

    /**
     * 发送消息（带事务和参数校验）
     */
    @Transactional(rollbackFor = Exception.class)  // 消息发送失败时回滚数据库操作
    public Result sendMessage(Integer senderId, SendMessageDTO dto) {
        // 1. 参数校验（避免空指针和非法参数）
        Assert.notNull(senderId, "发送者ID不能为空");
        Assert.notNull(dto.getReceiverId(), "接收者ID不能为空");
        Assert.hasText(dto.getContent(), "消息内容不能为空");
        Assert.isTrue(dto.getContent().length() <= 500, "消息内容不能超过500字");

        // 2. 校验发送者和接收者是否为同一人
        if (senderId.equals(dto.getReceiverId())) {
            return Result.error("不能向自己发送消息");
        }

        // 3. 校验接收者是否存在
        User receiver = userMapper.findById(dto.getReceiverId());
        if (receiver == null) {
            return Result.error("接收者不存在或已注销");
        }

        // 4. 校验发送者是否存在（防止无效用户ID发送消息）
        User sender = userMapper.findById(senderId);
        if (sender == null) {
            return Result.error("发送者信息不存在");
        }

        // 5. 构建消息实体并插入数据库
        Message message = new Message();
        message.setSenderid(senderId);
        message.setReceiverid(dto.getReceiverId());
        message.setContent(dto.getContent());
        message.setType(dto.getType() == null ? 0 : dto.getType());  // 默认为文本消息
        int rows = messageMapper.sendMessage(message);
        if (rows <= 0) {
            throw new RuntimeException("消息插入数据库失败");  // 触发事务回滚
        }

        // 6. 构建返回给前端的消息VO（包含发送者用户名）
        MessageVO messageVO = new MessageVO();
        messageVO.setId(message.getId());
        messageVO.setSenderId(senderId);
        messageVO.setSenderName(sender.getUsername());  // 补充发送者用户名（前端展示）
        messageVO.setReceiverId(dto.getReceiverId());
        messageVO.setContent(dto.getContent());
        messageVO.setSendTime(LocalDateTime.now());
        messageVO.setIsRead(0);  // 初始为未读
        messageVO.setType(message.getType());

        // 7. 通过WebSocket实时推送给接收者（失败不影响主流程）
        try {
            // 推送地址：/user/{接收者ID}/queue/chat，前端需订阅此路径
            websocketTemplate.convertAndSendToUser(
                    dto.getReceiverId().toString(),
                    "/queue/chat",
                    messageVO
            );
        } catch (Exception e) {
            // 记录日志（不抛出异常，避免影响消息发送主流程）
            // log.error("WebSocket推送消息失败：{}", e.getMessage());
        }

        return Result.success(messageVO);
    }

    /**
     * 获取与目标用户的聊天历史（分页）
     */
    public Result getChatHistory(Integer myId, Integer targetId, Integer page, Integer pageSize) {
        // 1. 参数校验
        Assert.notNull(myId, "当前用户ID不能为空");
        Assert.notNull(targetId, "目标用户ID不能为空");
        Assert.isTrue(page >= 1, "页码必须大于等于1");
        Assert.isTrue(pageSize >= 1 && pageSize <= 100, "每页条数必须在1-100之间");

        // 2. 校验目标用户是否存在
        User targetUser = userMapper.findById(targetId);
        if (targetUser == null) {
            return Result.error("目标用户不存在或已注销");
        }

        // 3. 计算分页偏移量
        int offset = (page - 1) * pageSize;

        // 4. 查询聊天历史（按时间倒序，最新的在前）
        List<MessageVO> history = messageMapper.getChatHistory(myId, targetId, pageSize, offset);

        // 5. 标记对方发来的未读消息为已读（当前用户查看历史时）
        messageMapper.markAsRead(targetId, myId);

        return Result.success(history);
    }

    /**
     * 获取当前用户的未读消息总数
     */
    public Result getUnreadTotal(Integer myId) {
        Assert.notNull(myId, "用户ID不能为空");
        int total = messageMapper.getUnreadTotal(myId);
        return Result.success(total);
    }

    /**
     * 获取与指定用户的未读消息数（用于聊天列表显示小红点）
     */
    public Result getUnreadWithTarget(Integer myId, Integer targetId) {
        Assert.notNull(myId, "用户ID不能为空");
        Assert.notNull(targetId, "目标用户ID不能为空");

        int count = messageMapper.getUnreadWithTarget(myId, targetId);
        return Result.success(count);
    }
}