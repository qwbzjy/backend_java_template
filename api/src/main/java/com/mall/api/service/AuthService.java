package com.mall.api.service;

import com.mall.api.dto.LoginDTO;
import com.mall.api.dto.RegisterDTO;
import com.mall.api.vo.LoginVO;

public interface AuthService {

    void register(RegisterDTO dto);

    LoginVO login(LoginDTO dto);

    LoginVO refresh(String refreshToken);

    void logout(String token);
}