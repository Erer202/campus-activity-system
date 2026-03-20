package com.er.campus.server.controller;

import com.er.campus.server.entity.User;
import com.er.campus.server.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/user")
@CrossOrigin("*")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    /**
     * 登录接口（POST）
     * @param loginUser 前端传来的 {userId, userPassword}
     * @return 成功返回完整 User 对象，失败返回错误信息
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User loginUser) {
        Optional<User> optionalUser = userRepository.findById(loginUser.getUserId());

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (user.getPassword().equals(loginUser.getPassword())) {
                return ResponseEntity.ok(user);   // 登录成功，返回完整用户信息
            }
        }
        return ResponseEntity.badRequest().body("学号或密码错误");
    }

    /**
     * 注册接口（POST）
     * @param user 前端传来的完整用户信息
     * @return 成功返回保存后的 User，失败返回错误信息
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        // 检查学号是否已存在
        if (userRepository.existsById(user.getUserId())) {
            return ResponseEntity.badRequest().body("学号已存在");
        }

        // 你的特殊逻辑：userId == "2" 时设为管理员
        if ("2".equals(user.getUserId())) {
            user.setIsAdmin(User.Role.ADMIN.getValue());
        } else {
            user.setIsAdmin(User.Role.USER.getValue());
        }

        User savedUser = userRepository.save(user);
        return ResponseEntity.ok(savedUser);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<?> getUserById(@PathVariable String userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isPresent()) {
            return ResponseEntity.ok(optionalUser.get());
        }
        return ResponseEntity.notFound().build();
    }

}