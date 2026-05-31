package com.nklcbdty.common.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserIdAndEmailDto {
    String userId;
    String email;
}
