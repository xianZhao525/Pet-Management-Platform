package com.example.backend.controller.api;

import com.example.backend.dto.UserDTO;
import com.example.backend.dto.ApiResponse;
import com.example.backend.entity.User;
import com.example.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class ApiUserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ApiResponse<User> register(@RequestBody UserDTO userDTO) {
        try {
            User user = userService.registerUser(userDTO);
            return ApiResponse.success("注册成功", user);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    // 如果需要，可以添加登录接口，但登录已由原 UserController 的 POST /user/login 处理
    // 注意：原登录接口返回的是重定向，不适合前后端分离，建议也添加 /api/user/login
}