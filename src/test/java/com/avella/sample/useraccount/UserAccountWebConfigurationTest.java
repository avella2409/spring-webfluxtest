package com.avella.sample.useraccount;

import com.avella.sample.useraccount.dto.UserInfoDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@WebFluxTest
@Import(UserAccountWebConfiguration.class)
class UserAccountWebConfigurationTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private UserAccountFacade userAccountFacade;

    @Test
    void should_find_user_info() {
        String userId = "randomUserId";

        UserInfoDto expectedUserInfoDto = new UserInfoDto("Anthony", "Paris");

        when(userAccountFacade.findUserInfo(userId)).thenReturn(
                Mono.just(expectedUserInfoDto)
        );

        webTestClient.get().uri("/info/" + userId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(UserInfoDto.class)
                .value(userInfoDto -> assertEquals(expectedUserInfoDto, userInfoDto));

        verify(userAccountFacade, times(1)).findUserInfo(userId);
    }

    @Test
    void should_not_find_user_info() {
        String userId = "randomUserId";

        when(userAccountFacade.findUserInfo(userId)).thenReturn(Mono.empty());

        webTestClient.get().uri("/info/" + userId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();

        verify(userAccountFacade, times(1)).findUserInfo(userId);
    }
}
