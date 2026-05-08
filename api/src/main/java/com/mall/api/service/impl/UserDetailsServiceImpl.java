package com.mall.api.service.impl;

import com.mall.common.security.LoginUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        String cacheKey = "user:login:" + username;
        LoginUser loginUser = (LoginUser) redisTemplate.opsForValue().get(cacheKey);

        if (loginUser == null) {
            log.warn("用户不存在或已过期: {}", username);
            throw new UsernameNotFoundException("用户不存在: " + username);
        }

        return loginUser;
    }

    public void cacheUser(LoginUser loginUser) {
        String cacheKey = "user:login:" + loginUser.getUsername();
        redisTemplate.opsForValue().set(cacheKey, loginUser, 30, TimeUnit.MINUTES);
    }

    public void evictUser(String username) {
        String cacheKey = "user:login:" + username;
        redisTemplate.delete(cacheKey);
    }
}