package com.onlineshop.security;

import com.onlineshop.model.User;
import com.onlineshop.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Collection;
import java.util.Collections;

/**
 * 自定義UserDetailsService
 * 用於Spring Security用戶認證
 */
@Service
@Transactional
public class CustomUserDetailsService implements UserDetailsService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("用戶名或密碼錯誤"));
        
        //檢查用戶是否啟用
        if (!user.getEnabled()) {
            throw new UsernameNotFoundException("該帳號已被注銷，無法登錄。如需重新啟用，請聯繫管理員。");
        }
        
        return new org.springframework.security.core.userdetails.User(
            user.getUsername(),
            user.getPassword(),
            user.getEnabled(),  // 啟用狀態
            true,   // 帳號未過期
            true,   // 憑證未過期
            true,   // 帳號未鎖定
            getAuthorities(user)
        );
    }
    
    /**
     * 獲取用戶權限
     */
    private Collection<? extends GrantedAuthority> getAuthorities(User user) {
        // 添加ROLE_前綴
        String role = "ROLE_" + user.getRole().name();
        return Collections.singletonList(new SimpleGrantedAuthority(role));
    }
}
