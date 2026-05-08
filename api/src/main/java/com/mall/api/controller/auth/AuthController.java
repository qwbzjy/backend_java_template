package com.mall.api.controller.auth;

import com.mall.api.dto.LoginDTO;
import com.mall.api.dto.RegisterDTO;
import com.mall.api.service.AuthService;
import com.mall.api.vo.LoginVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "认证管理")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "用户注册")
    @PostMapping("/register")
    public ResponseEntity<Void> register(@Valid @RequestBody RegisterDTO dto) {
        authService.register(dto);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public ResponseEntity<LoginVO> login(@Valid @RequestBody LoginDTO dto) {
        LoginVO vo = authService.login(dto);
        return ResponseEntity.ok(vo);
    }

    @Operation(summary = "刷新令牌")
    @PostMapping("/refresh")
    public ResponseEntity<LoginVO> refresh(@RequestHeader("Authorization") String refreshToken) {
        LoginVO vo = authService.refresh(refreshToken);
        return ResponseEntity.ok(vo);
    }

    @Operation(summary = "退出登录")
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader("Authorization") String token) {
        authService.logout(token);
        return ResponseEntity.ok().build();
    }
}