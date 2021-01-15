package com.avella.sample.useraccount;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class UserAccountConfiguration {

    @Bean
    UserAccountFacade userAccountFacade() {
        return new UserAccountFacade();
    }
}
