package com.example.backend.controller.api;

import com.example.backend.dto.UserDTO;
import com.example.backend.dto.ApiResponse;
import com.example.backend.dto.LoginDTO;
import com.example.backend.entity.User;
import com.example.backend.service.UserService;

import jakarta.servlet.http.HttpSession;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class ApiUserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ApiResponse<Map<String, Object>> register(@RequestBody UserDTO userDTO) {
        try {
            User user = userService.registerUser(userDTO);
            Map<String, Object> data = new HashMap<>();
            data.put("id", user.getId());
            data.put("username", user.getUsername());
            data.put("email", user.getEmail());
            // 关键：处理 phone，若为 Collection 则取第一个元素
            Object phoneObj = user.getPhone();
            if (phoneObj instanceof Collection) {
                Collection<?> coll = (Collection<?>) phoneObj;
                data.put("phone", coll.isEmpty() ? null : coll.iterator().next());
            } else {
                data.put("phone", phoneObj);
            }
            data.put("role", user.getRole());
            return ApiResponse.success("注册成功", data);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    // 如果需要，可以添加登录接口，但登录已由原 UserController 的 POST /user/login 处理
    // 注意：原登录接口返回的是重定向，不适合前后端分离，建议也添加 /api/user/login

    @PostMapping("/login")
    public ApiResponse<Map<String, Object>> login(@RequestBody LoginDTO loginDTO, HttpSession session) {
        try {
            User user = userService.login(loginDTO.getUsername(), loginDTO.getPassword())
                    .orElseThrow(() -> new RuntimeException("用户名或密码错误"));

            // 设置 session
            session.setAttribute("user", user);
            session.setAttribute("userId", user.getId());
            session.setAttribute("username", user.getUsername());

            // 返回用户信息（不含密码）
            Map<String, Object> data = new HashMap<>();
            data.put("id", user.getId());
            data.put("username", user.getUsername());
            data.put("email", user.getEmail());
            data.put("phone", user.getPhone());
            data.put("role", user.getRole());
            data.put("avatar", user.getAvatar());

            return ApiResponse.success("登录成功", data);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }
}