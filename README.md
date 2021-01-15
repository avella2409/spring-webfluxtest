# Spring WebFluxTest

## Goal

Test web layer of a super simple sample application with WebFluxTest.

## Steps

### Step 1 : Create the web endpoint

In this sample application there is only one endpoint returning some information about a user.
We handle two case : 
- We find the user : `ServerResponse.ok()`
- We didn't find the user : `ServerReponse.notFound()`

```java
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
```

### Step 2 : Create a facade

The project goal is to show how to use WebFluxTest, so we just create a fake facade that return hard coded data. (On a real application we could have a database and use a spring `Repository`)

```java
public class UserAccountFacade {

    public Mono<UserInfoDto> findUserInfo(String userId) {
        return Mono.just(new UserInfoDto("hardCodedName", "hardCodedCity"));
    }
}
```

### Step 3 : Create a basic DTO

```java
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoDto {
    private String name;
    private String city;
}
```

### Step 4 : Create test cases

- Annotate the test class with `@WebFluxTest`
- `Import` our web configuration class
- `Autowire` the `WebTestClient`, needed to make request
- `MockBean` our `Facade`, needed to test our web layer as a `black box`, we want to choose what the facade return to test all case

```java
@WebFluxTest
@Import(UserAccountWebConfiguration.class)
class UserAccountWebConfigurationTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private UserAccountFacade userAccountFacade;
}
```

We create a first test case to check the case where we correctly find the user. So we use `Mockito` to set what the method `findUserInfo`return, on this test it return some fake user info.

We then make the request and specify what we expect :
1) There is a GET endpoint at `/info/{userId}`
2) The server respond with status code `isOk()`
3) The server respond with header `APPLICATION_JSON`
4) The body can be mapped to a UserInfoDto object
5) The UserInfoDto returned is what we expect

At the end on this test we also check that the method `findUserInfo` is called once.

```java
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
```

In our next test case we will check for the case where we didn't find the user, so as before we set what `findUserInfo` return and make the request but this time we expect the server will respond with status code `isNotFound()`. 

```java
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
```