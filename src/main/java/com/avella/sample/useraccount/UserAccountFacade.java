package com.avella.sample.useraccount;

import com.avella.sample.useraccount.dto.UserInfoDto;
import reactor.core.publisher.Mono;

public class UserAccountFacade {

    public Mono<UserInfoDto> findUserInfo(String userId) {
        return Mono.just(new UserInfoDto("hardCodedName", "hardCodedCity"));
    }
}
