package com.example.backend.service.impl;

import com.example.backend.dto.UserDTO;
import com.example.backend.entity.User;
import com.example.backend.dao.UserRepository;
import com.example.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${app.admin.registration.code}")
    private String adminRegistrationCode;

    @Override
    public User registerUser(UserDTO userDTO) {
        System.out.println("======================================");
        System.out.println("🎯 UserServiceImpl.registerUser() 开始执行");
        System.out.println("用户名: " + userDTO.getUsername());
        System.out.println("手机号: " + userDTO.getPhone());
        System.out.println("角色: " + userDTO.getRole());
        System.out.println("======================================");

        // 检查用户名是否已存在
        if (userRepository.findByUsername(userDTO.getUsername()).isPresent()) {
            throw new RuntimeException("用户名已存在");
        }

        // ✅ 角色验证和转换
        User.UserRole role = User.UserRole.USER; // 默认普通用户

        if ("ADMIN".equals(userDTO.getRole())) {
            // 验证管理员注册码
            if (userDTO.getAdminCode() == null || !userDTO.getAdminCode().equals(adminRegistrationCode)) {
                System.out.println("❌ 管理员注册码错误 - 输入: " + userDTO.getAdminCode() + ", 期望: " + adminRegistrationCode);
                throw new RuntimeException("管理员注册码错误");
            }
            System.out.println("✅ 管理员注册码验证通过");
            role = User.UserRole.ADMIN; // 关键修复：这里必须设置角色为ADMIN
        }

        // 创建新用户
        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setPhone(userDTO.getPhone());
        user.setEmail(userDTO.getEmail());

        String rawPassword = userDTO.getPassword();
        String encodedPassword = passwordEncoder.encode(rawPassword);
        System.out.println("原始密码: " + rawPassword);
        System.out.println("加密后密码: " + encodedPassword);

        user.setPassword(encodedPassword);
        user.setRole(role);

        // 添加角色验证日志
        System.out.println("✅ 设置用户角色为: " + role);

        User savedUser = userRepository.save(user);
        System.out.println("✅ 用户保存成功 - ID: " + savedUser.getId());
        System.out.println("✅ 最终用户角色: " + savedUser.getRole());

        return savedUser;
    }

    @Override
    public Optional<User> login(String username, String password) {
        return userRepository.findByUsername(username)
                .map(user -> {
                    System.out.println("找到用户: " + user.getUsername());
                    System.out.println("数据库密码: " + user.getPassword());
                    boolean matches = passwordEncoder.matches(password, user.getPassword());
                    System.out.println("密码匹配: " + matches);
                    return matches ? user : null;
                });
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public Optional<User> getUserById(Long userId) {
        return userRepository.findById(userId);
    }

    @Override
    public User updateUser(Long userId, UserDTO userDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        user.setPhone(userDTO.getPhone());
        user.setEmail(userDTO.getEmail());
        if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        }

        return userRepository.save(user);
    }

    @Override
    public Page<User> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Override
    public List<User> searchUsers(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return userRepository.findAll();
        }
        return userRepository.findByUsernameContainingOrEmailContaining(keyword, keyword);
    }

    @Override
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }

    @Override
    public long getUserCount() {
        return userRepository.count();
    }

    @Override
    // public UserDetails loadUserByUsername(String username) throws
    // UsernameNotFoundException {
    // return userRepository.findByUsername(username)
    // .map(user -> org.springframework.security.core.userdetails.User
    // .withUsername(user.getUsername())
    // .password(user.getPassword())
    // .roles(user.getRole().name())
    // .build())
    // .orElseThrow(() -> new UsernameNotFoundException("用户不存在: " + username));
    // }
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("用户不存在: " + username));
    }
}