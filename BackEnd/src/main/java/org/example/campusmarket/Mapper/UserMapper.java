package org.example.campusmarket.Mapper;

import org.apache.ibatis.annotations.Mapper;
<<<<<<< HEAD
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.example.campusmarket.entity.User;

@Mapper
public interface UserMapper {
    // 根据ID查询用户（用于获取发送者用户名）
    @Select("SELECT id, username FROM users WHERE id = #{id}")
    User findById(@Param("id") Integer id);
}
=======
import org.apache.ibatis.annotations.*;

import org.example.campusmarket.entity.Product;
import org.example.campusmarket.entity.UserInfo;

import java.util.List;

@Mapper
public interface UserMapper {
    //通过id查找用户
    @Select("SELECT * FROM userinfo WHERE user_id = #{userId}")
    UserInfo findUserinfoById(@Param("userId") Integer userId);

    //更新用户信息
    @Update({
            "<script>",
            "UPDATE userinfo",
            "<set>",
            "  <if test='avatar != null'>avatar = #{avatar},</if>",
            "  <if test='nickname != null'>nickname = #{nickname},</if>",
            "  <if test='phone != null'>phone = #{phone},</if>",
            "  <if test='address != null'>address = #{address},</if>",
            "</set>",
            "WHERE user_id = #{userId}",
            "</script>"
    })
    int updateUserInfo(@Param("userId") Integer userId,
                       @Param("avatar") String avatar,
                       @Param("nickname") String nickname,
                       @Param("phone") String phone,
                       @Param("address") String address);


    //更新密码
    @Update("UPDATE userinfo SET password = #{newPassword} WHERE user_id = #{userId}")
    int updatePassword(@Param("userId") Integer userId, @Param("newPassword") String newPassword);

    //更新用户邮箱
    @Update("UPDATE userinfo SET email = #{newEmail} WHERE user_id = #{userId}")
    int updateEmail(@Param("userId") Integer userId, @Param("newEmail") String newEmail);

    //查找用户发布商品
    @Select("""
    SELECT pro_id, pro_name, price, is_seal, discription, picture, saler_id
    FROM products
    WHERE saler_id = #{userId}
    ORDER BY pro_id DESC
    """)
    List<Product> findPublishedProducts(@Param("userId") Integer userId);

    //查找用户已购买商品
    @Select("""
    SELECT p.pro_id, p.pro_name, p.price, p.is_seal, p.discription, p.picture, p.saler_id
    FROM buy_products bp
    JOIN products p ON p.pro_id = bp.pro_id
    WHERE bp.user_id = #{userId}
    ORDER BY p.pro_id DESC
    """)
    List<Product> findPurchasedProducts(@Param("userId") Integer userId);

    //查找用户收藏商品
    @Select("""
    SELECT p.pro_id, p.pro_name, p.price, p.is_seal, p.discription, p.picture, p.saler_id
    FROM fav_products fp
    JOIN products p ON p.pro_id = fp.pro_id
    WHERE fp.user_id = #{userId}
    ORDER BY p.pro_id DESC
    """)
    List<Product> findFavoriteProducts(@Param("userId") Integer userId);
}
>>>>>>> ebe4f8a2a33c8b6f64f66e6ae44304a33717d1ed
