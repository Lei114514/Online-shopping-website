package com.onlineshop.service;

import com.onlineshop.model.User;
import com.onlineshop.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

/**
 * 用戶服務類
 */
@Service
@Transactional
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    /**
     * 註冊新用戶
     */
    public User registerUser(User user) {
        // 檢查用戶名是否已存在
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new IllegalArgumentException("用戶名已存在");
        }
        
        // 檢查電子郵件是否已存在
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("電子郵件已存在");
        }
        
        // 加密密碼
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        // 如果沒有設置角色,默認為顧客
        if (user.getRole() == null) {
            user.setRole(User.UserRole.CUSTOMER);
        }
        // 如果沒有設置角色,默認為顧客
        if (user.getRole() == null) {
            user.setRole(User.UserRole.CUSTOMER);
        }
        user.setEnabled(true);
        
        return userRepository.save(user);
    }
    
    /**
     * 更新用戶信息
     */
    public User updateUser(Long userId, User updatedUser) {
        User existingUser = getUserById(userId);
        
        // 更新允許修改的字段
        if (updatedUser.getFirstName() != null) {
            existingUser.setFirstName(updatedUser.getFirstName());
        }
        if (updatedUser.getLastName() != null) {
            existingUser.setLastName(updatedUser.getLastName());
        }
        if (updatedUser.getPhone() != null) {
            existingUser.setPhone(updatedUser.getPhone());
        }
        if (updatedUser.getAddress() != null) {
            existingUser.setAddress(updatedUser.getAddress());
        }
        if (updatedUser.getEmail() != null && !existingUser.getEmail().equals(updatedUser.getEmail())) {
            // 檢查新郵件是否已存在
            if (userRepository.existsByEmail(updatedUser.getEmail())) {
                throw new IllegalArgumentException("電子郵件已存在");
            }
            existingUser.setEmail(updatedUser.getEmail());
        }
        
        return userRepository.save(existingUser);
    }
    
    /**
     * 根據ID獲取用戶
     */
    public User getUserById(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("用戶不存在"));
    }
    
    /**
     * 根據用戶名獲取用戶
     */
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
            .orElseThrow(() -> new IllegalArgumentException("用戶不存在"));
    }
    
    /**
     * 獲取所有用戶
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    /**
     * 搜索用戶
     */
    public List<User> searchUsers(String keyword) {
        return userRepository.searchUsers(keyword);
    }
    
    /**
     * 刪除用戶
     */
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new IllegalArgumentException("用戶不存在");
        }
        userRepository.deleteById(id);
    }
    
    /**
     * 啟用/禁用用戶
     */
    public User toggleUserStatus(Long id) {
        User user = getUserById(id);
        user.setEnabled(!user.getEnabled());
        return userRepository.save(user);
    }
    
    /**
     * 驗證用戶憑證
     */
    public boolean validateCredentials(String username, String password) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            return passwordEncoder.matches(password, user.getPassword()) && user.getEnabled();
        }
        return false;
    }
    
    /**
     * 更改用戶密碼
     */
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        User user = getUserById(userId);
        
        // 驗證舊密碼
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new IllegalArgumentException("舊密碼不正確");
        }
        
        // 更新密碼
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
}
