package org.example.campusmarket.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//给前端的返回结果
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Result {

    //给前端返回的状态码
    private Integer code;

    //给前端返回的消息，例如登录成功，注册成功
    private String message;

    //给前端返回的数据，例如token，user数据
    private Object data;

    public static Result error(String msg) {
        return new Result(500, msg, null);
    }

    // 成功响应（带数据）
    public static Result success(Object data) {
        Result result = new Result();
        result.setCode(200);
        result.setMessage("操作成功");
        result.setData(data);
        return result;
    }
    // 成功响应（无数据）
    public static Result success() {
        return success(null);
    }
}
