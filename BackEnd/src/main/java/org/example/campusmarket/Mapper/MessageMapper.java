package org.example.campusmarket.Mapper;

import org.apache.ibatis.annotations.*;
import org.example.campusmarket.DTO.MessageVO;
import org.example.campusmarket.entity.Message;

import java.util.List;

@Mapper
public interface MessageMapper {
    // 发送消息（插入数据库）
    @Insert("INSERT INTO message (sender_id, receiver_id, content, type) " +
            "VALUES (#{senderId}, #{receiverId}, #{content}, #{type})")
    int sendMessage(Message message);

    // 查询两人的聊天历史（分页，按时间倒序）
    @Select("SELECT m.*, u.username AS sender_name " +
            "FROM message m " +
            "LEFT JOIN users u ON m.sender_id = u.id " +
            "WHERE (m.sender_id = #{myId} AND m.receiver_id = #{targetId}) " +
            "   OR (m.sender_id = #{targetId} AND m.receiver_id = #{myId}) " +
            "ORDER BY m.send_time DESC " +
            "LIMIT #{pageSize} OFFSET #{offset}")
    List<MessageVO> getChatHistory(
            @Param("myId") Integer myId,  // 当前登录用户ID
            @Param("targetId") Integer targetId,  // 聊天对象ID
            @Param("pageSize") Integer pageSize,
            @Param("offset") Integer offset
    );

    // 标记消息为已读（当前用户查看对方发来的未读消息）
    @Update("UPDATE message SET is_read = 1 " +
            "WHERE sender_id = #{senderId} AND receiver_id = #{receiverId} AND is_read = 0")
    int markAsRead(
            @Param("senderId") Integer senderId,  // 对方用户ID
            @Param("receiverId") Integer receiverId  // 当前用户ID
    );

    // 获取当前用户的未读消息总数
    @Select("SELECT COUNT(*) FROM message " +
            "WHERE receiver_id = #{myId} AND is_read = 0")
    int getUnreadTotal(@Param("myId") Integer myId);

    // 获取当前用户与某用户的未读消息数
    @Select("SELECT COUNT(*) FROM message " +
            "WHERE sender_id = #{targetId} AND receiver_id = #{myId} AND is_read = 0")
    int getUnreadWithTarget(
            @Param("myId") Integer myId,
            @Param("targetId") Integer targetId
    );
}