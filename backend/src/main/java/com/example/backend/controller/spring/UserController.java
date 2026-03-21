package com.example.backend.controller.spring;

import com.example.backend.dto.LoginDTO;
import com.example.backend.dto.UserDTO;
import com.example.backend.entity.User;
import com.example.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    // 显示注册页面
    @GetMapping("/register")
    public String showRegisterForm(Model model, HttpSession session) {
        if (session.getAttribute("user") != null) {
            return "redirect:/";
        }
        model.addAttribute("userDTO", new UserDTO());
        return "user/register";
    }

    // ✅ 处理注册请求（带完整日志）
    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("userDTO") UserDTO userDTO,
            BindingResult result,
            RedirectAttributes redirectAttributes,
            HttpSession session) {
        System.out.println("======================================");
        System.out.println("🎯 注册请求到达 - 用户名: " + userDTO.getUsername());
        System.out.println("🎯 手机号: " + userDTO.getPhone());
        System.out.println("🎯 邮箱: " + userDTO.getEmail());
        System.out.println("🎯 角色: " + userDTO.getRole());
        System.out.println("🎯 管理员码: " + userDTO.getAdminCode());
        System.out.println("======================================");

        if (session.getAttribute("user") != null) {
            return "redirect:/";
        }

        // ✅ 如果有表单验证错误，返回注册页并显示错误
        if (result.hasErrors()) {
            StringBuilder errorMsg = new StringBuilder();
            result.getAllErrors().forEach(error -> {
                errorMsg.append(error.getDefaultMessage()).append("\n");
            });
            redirectAttributes.addFlashAttribute("error", errorMsg.toString());
            return "redirect:/user/register";
        }

        try {
            // 检查用户名是否已存在
            if (userService.findByUsername(userDTO.getUsername()).isPresent()) {
                System.out.println("❌ 用户名已存在");
                redirectAttributes.addFlashAttribute("error", "用户名已存在，请使用其他用户名");
                return "redirect:/user/register";
            }

            // 注册用户
            User user = userService.registerUser(userDTO);
            System.out.println("✅ 注册成功 - 用户ID: " + user.getId() + ", 角色: " + user.getRole());

            redirectAttributes.addFlashAttribute("registered", true);
            return "redirect:/user/login";

        } catch (Exception e) {
            System.err.println("❌ 注册异常: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "注册失败: " + e.getMessage());
            return "redirect:/user/register";
        }
    }

    // 显示登录页面
    @GetMapping("/login")
    public String loginPage(Model model, HttpSession session) {
        if (session.getAttribute("user") != null) {
            return "redirect:/";
        }

        model.addAttribute("loginDTO", new LoginDTO());
        return "user/login";
    }

    // 处理登录请求（带完整日志）- ✅ 已修复：根据角色跳转
    @PostMapping("/login")
    public String login(@Valid @ModelAttribute("loginDTO") LoginDTO loginDTO,
            BindingResult result,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        System.out.println("======================================");
        System.out.println("🎯 登录请求到达 - 用户名: " + loginDTO.getUsername());
        System.out.println("======================================");

        if (session.getAttribute("user") != null) {
            System.out.println("⚠️ 用户已登录，重定向到首页");
            return "redirect:/";
        }

        if (result.hasErrors()) {
            System.out.println("❌ 表单验证错误");
            redirectAttributes.addFlashAttribute("loginError", true);
            redirectAttributes.addFlashAttribute("errorMessage", "请输入完整的登录信息");
            return "redirect:/user/login";
        }

        try {
            // 验证用户名和密码
            User user = userService.login(loginDTO.getUsername(), loginDTO.getPassword())
                    .orElseThrow(() -> new RuntimeException("用户名或密码错误"));

            System.out.println("✅ 登录成功");
            System.out.println("  用户ID: " + user.getId());
            System.out.println("  用户名: " + user.getUsername());
            System.out.println("  用户角色: " + user.getRole());
            System.out.println("  角色枚举值: " + user.getRole().name());
            System.out.println("  是否为ADMIN: " + (user.getRole() == User.UserRole.ADMIN));

            // 打印权限信息用于调试
            System.out.println("  用户权限: " + user.getAuthorities());
            System.out.println("  是否启用: " + user.isEnabled());

            // 关键修改：使用Spring Security进行认证
            try {
                // 方法1：使用Spring Security的标准认证流程
                Authentication authentication = authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                loginDTO.getUsername(), // 注意：这里传入用户名，不是user对象
                                loginDTO.getPassword() // 传入密码
                        ));

                SecurityContextHolder.getContext().setAuthentication(authentication);
                System.out.println("✅ Spring Security认证成功: " + authentication.isAuthenticated());
                System.out.println("✅ 认证信息: " + authentication.getAuthorities());

            } catch (Exception e) {
                System.err.println("❌ Spring Security认证失败: " + e.getMessage());
                // 如果认证失败，可以尝试手动设置
                Authentication authentication = new UsernamePasswordAuthenticationToken(
                        user, // 传入User对象（现在是UserDetails）
                        user.getPassword(),
                        user.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
                System.out.println("⚠️ 使用手动认证设置");
            }

            // 设置session属性
            session.setAttribute("user", user);
            session.setAttribute("userId", user.getId());
            session.setAttribute("username", user.getUsername());

            // ✅ 关键：根据角色跳转
            if (user.getRole() == User.UserRole.ADMIN) {
                System.out.println("🚀 管理员登录，跳转到 /admin/dashboard");
                return "redirect:/admin/dashboard";
            } else {
                System.out.println("👤 普通用户登录，跳转到首页 /");
                return "redirect:/";
            }

        } catch (Exception e) {
            System.err.println("❌ 登录失败: " + e.getMessage());
            redirectAttributes.addFlashAttribute("loginError", true);
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/user/login";
        }
    }

    // 退出登录
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/user/login";
    }

    // ✅ 可选：添加用户个人中心页面（如果还没有）
    @GetMapping("/dashboard")
    public String userDashboard(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/user/login";
        }

        // 如果是管理员，重定向到管理员面板
        if (user.getRole() == User.UserRole.ADMIN) {
            return "redirect:/admin/dashboard";
        }

        model.addAttribute("user", user);
        return "user/dashboard";
    }
}