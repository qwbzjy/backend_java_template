package com.mall.api.controller.user;

import com.mall.api.dto.CreateAddressDTO;
import com.mall.api.service.UserAddressService;
import com.mall.api.service.UserService;
import com.mall.api.vo.AddressVO;
import com.mall.api.vo.UserInfoVO;
import com.mall.common.common.PageResult;
import com.mall.common.common.Result;
import com.mall.common.security.LoginUser;
import com.mall.user.entity.UserAddress;
import com.mall.user.entity.UserInfo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "用户管理")
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserAddressService userAddressService;

    @Operation(summary = "获取当前用户信息")
    @GetMapping("/info")
    public Result<UserInfoVO> getUserInfo(@AuthenticationPrincipal LoginUser loginUser) {
        UserInfo user = userService.getById(loginUser.getUserId());
        UserInfoVO vo = new UserInfoVO();
        BeanUtils.copyProperties(user, vo);
        return Result.success(vo);
    }

    @Operation(summary = "更新用户信息")
    @PutMapping("/info")
    public Result<Void> updateUserInfo(@AuthenticationPrincipal LoginUser loginUser,
                                       @RequestBody UserInfoVO vo) {
        UserInfo user = new UserInfo();
        user.setId(loginUser.getUserId());
        user.setNickname(vo.getNickname());
        user.setAvatar(vo.getAvatar());
        user.setGender(vo.getGender());
        user.setBirthday(vo.getBirthday());
        userService.updateById(user);
        return Result.success();
    }

    @Operation(summary = "获取用户地址列表")
    @GetMapping("/address")
    public Result<List<AddressVO>> getAddressList(@AuthenticationPrincipal LoginUser loginUser) {
        List<UserAddress> list = userAddressService.lambdaQuery()
                .eq(UserAddress::getUserId, loginUser.getUserId())
                .list();

        List<AddressVO> voList = list.stream().map(address -> {
            AddressVO vo = new AddressVO();
            BeanUtils.copyProperties(address, vo);
            return vo;
        }).collect(Collectors.toList());

        return Result.success(voList);
    }

    @Operation(summary = "获取地址详情")
    @GetMapping("/address/{id}")
    public Result<AddressVO> getAddress(@AuthenticationPrincipal LoginUser loginUser,
                                        @PathVariable Long id) {
        UserAddress address = userAddressService.lambdaQuery()
                .eq(UserAddress::getId, id)
                .eq(UserAddress::getUserId, loginUser.getUserId())
                .one();

        if (address == null) {
            return Result.error("地址不存在");
        }

        AddressVO vo = new AddressVO();
        BeanUtils.copyProperties(address, vo);
        return Result.success(vo);
    }

    @Operation(summary = "创建地址")
    @PostMapping("/address")
    public Result<Void> createAddress(@AuthenticationPrincipal LoginUser loginUser,
                                      @Valid @RequestBody CreateAddressDTO dto) {
        // 如果设置为默认地址，先取消其他默认地址
        if (dto.getIsDefault() != null && dto.getIsDefault() == 1) {
            userAddressService.lambdaUpdate()
                    .eq(UserAddress::getUserId, loginUser.getUserId())
                    .eq(UserAddress::getIsDefault, 1)
                    .set(UserAddress::getIsDefault, 0)
                    .update();
        }

        UserAddress address = new UserAddress();
        address.setUserId(loginUser.getUserId());
        address.setReceiverName(dto.getReceiverName());
        address.setReceiverPhone(dto.getReceiverPhone());
        address.setProvince(dto.getProvince());
        address.setCity(dto.getCity());
        address.setDistrict(dto.getDistrict());
        address.setDetailAddress(dto.getDetailAddress());
        address.setPostalCode(dto.getPostalCode());
        address.setTag(dto.getTag());
        address.setIsDefault(dto.getIsDefault() != null ? dto.getIsDefault() : 0);

        userAddressService.save(address);
        return Result.success();
    }

    @Operation(summary = "更新地址")
    @PutMapping("/address/{id}")
    public Result<Void> updateAddress(@AuthenticationPrincipal LoginUser loginUser,
                                      @PathVariable Long id,
                                      @Valid @RequestBody CreateAddressDTO dto) {
        // 检查地址属于当前用户
        UserAddress existAddress = userAddressService.lambdaQuery()
                .eq(UserAddress::getId, id)
                .eq(UserAddress::getUserId, loginUser.getUserId())
                .one();

        if (existAddress == null) {
            return Result.error("地址不存在");
        }

        // 如果设置为默认地址，先取消其他默认地址
        if (dto.getIsDefault() != null && dto.getIsDefault() == 1) {
            userAddressService.lambdaUpdate()
                    .eq(UserAddress::getUserId, loginUser.getUserId())
                    .eq(UserAddress::getIsDefault, 1)
                    .ne(UserAddress::getId, id)
                    .set(UserAddress::getIsDefault, 0)
                    .update();
        }

        UserAddress address = new UserAddress();
        address.setId(id);
        address.setReceiverName(dto.getReceiverName());
        address.setReceiverPhone(dto.getReceiverPhone());
        address.setProvince(dto.getProvince());
        address.setCity(dto.getCity());
        address.setDistrict(dto.getDistrict());
        address.setDetailAddress(dto.getDetailAddress());
        address.setPostalCode(dto.getPostalCode());
        address.setTag(dto.getTag());
        address.setIsDefault(dto.getIsDefault() != null ? dto.getIsDefault() : 0);

        userAddressService.updateById(address);
        return Result.success();
    }

    @Operation(summary = "删除地址")
    @DeleteMapping("/address/{id}")
    public Result<Void> deleteAddress(@AuthenticationPrincipal LoginUser loginUser,
                                      @PathVariable Long id) {
        int rows = userAddressService.lambdaUpdate()
                .eq(UserAddress::getId, id)
                .eq(UserAddress::getUserId, loginUser.getUserId())
                .remove();

        if (rows == 0) {
            return Result.error("地址不存在");
        }

        return Result.success();
    }

    @Operation(summary = "设置默认地址")
    @PutMapping("/address/{id}/default")
    public Result<Void> setDefaultAddress(@AuthenticationPrincipal LoginUser loginUser,
                                          @PathVariable Long id) {
        // 先取消当前用户的其他默认地址
        userAddressService.lambdaUpdate()
                .eq(UserAddress::getUserId, loginUser.getUserId())
                .eq(UserAddress::getIsDefault, 1)
                .set(UserAddress::getIsDefault, 0)
                .update();

        // 设置指定地址为默认
        userAddressService.lambdaUpdate()
                .eq(UserAddress::getId, id)
                .eq(UserAddress::getUserId, loginUser.getUserId())
                .set(UserAddress::getIsDefault, 1)
                .update();

        return Result.success();
    }
}