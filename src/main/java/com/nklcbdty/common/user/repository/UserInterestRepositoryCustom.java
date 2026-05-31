package com.nklcbdty.common.user.repository;

import java.util.List;
import com.querydsl.core.Tuple;

public interface UserInterestRepositoryCustom {
    List<Tuple> findGroupedByUserTypeAndUserId();
}
