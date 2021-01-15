package com.avella.sample.useraccount;

import com.avella.sample.useraccount.dto.UserInfoDto;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Configuration
class UserAccountWebConfiguration {

    private Mono<ServerResponse> findUserInfo(ServerRequest serverRequest, UserAccountFacade userAccountFacade) {

        return userAccountFacade.findUserInfo(serverRequest.pathVariable("userId"))
                .flatMap(userInfoDto -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(Mono.just(userInfoDto), UserInfoDto.class))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    @Bean
    RouterFunction<ServerResponse> route(UserAccountFacade userAccountFacade) {
        return RouterFunctions.route()
                .GET("/info/{userId}", serverRequest -> findUserInfo(serverRequest, userAccountFacade))
                .build();
    }
}
