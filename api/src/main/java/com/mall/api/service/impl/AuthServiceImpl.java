package com.mall.api.service.impl;

import com.mall.api.dto.LoginDTO;
import com.mall.api.dto.RegisterDTO;
import com.mall.api.service.AuthService;
import com.mall.api.vo.LoginVO;
import com.mall.common.exception.BusinessException;
import com.mall.common.exception.ErrorCode;
import com.mall.common.security.LoginUser;
import com.mall.common.utils.JwtUtil;
import com.mall.user.entity.UserInfo;
import com.mall.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public void register(RegisterDTO dto) {
        // 检查用户名是否存在
        UserInfo existUser = userService.lambdaQuery()
                .eq(UserInfo::getUsername, dto.getUsername())
                .one();
        if (existUser != null) {
            throw new BusinessException(ErrorCode.USER_ALREADY_EXISTS);
        }

        // 检查手机号是否被占用
        if (StringUtils.hasText(dto.getPhone())) {
            UserInfo phoneExist = userService.lambdaQuery()
                    .eq(UserInfo::getPhone, dto.getPhone())
                    .one();
            if (phoneExist != null) {
                throw new BusinessException(ErrorCode.PHONE_ALREADY_EXISTS);
            }
        }

        // 创建用户
        UserInfo user = new UserInfo();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setPhone(dto.getPhone());
        user.setEmail(dto.getEmail());
        user.setStatus(1);
        userService.save(user);

        log.info("用户注册成功: {}", dto.getUsername());
    }

    @Override
    public LoginVO login(LoginDTO dto) {
        // 查找用户
        UserInfo user = userService.lambdaQuery()
                .eq(UserInfo::getUsername, dto.getUsername())
                .one();
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        // 验证密码
        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new BusinessException(ErrorCode.PASSWORD_ERROR);
        }

        // 检查状态
        if (user.getStatus() != 1) {
            throw new BusinessException("用户已被禁用");
        }

        // 生成令牌
        String accessToken = jwtUtil.generateToken(user.getId(), user.getUsername());
        String refreshToken = jwtUtil.generateRefreshToken(user.getId(), user.getUsername());

        // 缓存登录用户
        LoginUser loginUser = new LoginUser(user.getId(), user.getUsername(), user.getPassword(), user.getStatus());
        String cacheKey = "user:login:" + user.getUsername();
        redisTemplate.opsForValue().set(cacheKey, loginUser, 30, TimeUnit.MINUTES);

        // 返回登录凭证
        LoginVO vo = new LoginVO();
        vo.setAccessToken(accessToken);
        vo.setRefreshToken(refreshToken);
        vo.setUsername(user.getUsername());
        vo.setUserId(user.getId());

        log.info("用户登录成功: {}", dto.getUsername());
        return vo;
    }

    @Override
    public LoginVO refresh(String refreshToken) {
        if (!StringUtils.hasText(refreshToken)) {
            throw new BusinessException("刷新令牌不能为空");
        }

        // 去掉 Bearer 前缀
        if (refreshToken.startsWith("Bearer ")) {
            refreshToken = refreshToken.substring(7);
        }

        // 验证刷新令牌
        if (!jwtUtil.validateToken(refreshToken) || !jwtUtil.isRefreshToken(refreshToken)) {
            throw new BusinessException("刷新令牌无效或已过期");
        }

        Long userId = jwtUtil.getUserIdFromToken(refreshToken);
        String username = jwtUtil.getUsernameFromToken(refreshToken);

        // 生成新的访问令牌
        String newAccessToken = jwtUtil.generateToken(userId, username);
        String newRefreshToken = jwtUtil.generateRefreshToken(userId, username);

        // 刷新缓存
        String cacheKey = "user:login:" + username;
        LoginUser loginUser = (LoginUser) redisTemplate.opsForValue().get(cacheKey);
        if (loginUser != null) {
            redisTemplate.opsForValue().set(cacheKey, loginUser, 30, TimeUnit.MINUTES);
        }

        LoginVO vo = new LoginVO();
        vo.setAccessToken(newAccessToken);
        vo.setRefreshToken(newRefreshToken);
        vo.setUsername(username);
        vo.setUserId(userId);

        log.info("刷新令牌成功: {}", username);
        return vo;
    }

    @Override
    public void logout(String token) {
        if (StringUtils.hasText(token) && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        if (StringUtils.hasText(token)) {
            // 将 token 加入黑名单
            String blacklistKey = "blacklist:token:" + token;
            Long userId = jwtUtil.getUserIdFromToken(token);
            redisTemplate.opsForValue().set(blacklistKey, userId, 7, TimeUnit.DAYS);

            // 清除用户缓存
            String username = jwtUtil.getUsernameFromToken(token);
            if (username != null) {
                String cacheKey = "user:login:" + username;
                redisTemplate.delete(cacheKey);
            }

            log.info("用户退出登录: {}", username);
        }
    }
}