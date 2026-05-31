package com.nklcbdty.common.user.service;

import java.util.ArrayList;
import java.util.List;

import com.nklcbdty.common.user.dto.UserIdAndEmailDto;
import com.nklcbdty.common.user.dto.UserResponseDto;
import com.nklcbdty.common.user.repository.UserRepository;
import com.nklcbdty.common.vo.UserVo;

// 양 프로젝트가 공유하는 단순 조회 메서드 모음. Spring @Service 는 subclass 에서 붙임.
// 메인은 추가로 UserDetailService(OAuth) 까지 implement 하기 때문에 base 만 분리.
public class BaseUserService {

    protected final UserRepository userRepository;

    public BaseUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserResponseDto findByUserId(String userId) {
        UserVo user = userRepository.findByUserId(userId);
        return UserResponseDto.builder()
            .username(user.getUsername())
            .email(user.getEmail())
            .build();
    }

    public List<UserIdAndEmailDto> findByUserIdIn(List<String> userIds) {
        List<UserVo> items = userRepository.findByUserIdIn(userIds);
        List<UserIdAndEmailDto> results = new ArrayList<>();
        for (UserVo item : items) {
            results.add(new UserIdAndEmailDto(item.getUserId(), item.getEmail()));
        }
        return results;
    }
}
