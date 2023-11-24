package net.thumbtack.school.market.endpoint;

import lombok.RequiredArgsConstructor;
import net.thumbtack.school.market.dto.response.UserDtoResponse;
import net.thumbtack.school.market.service.UserService;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class UserEndpoint {

    private final UserService userService;

    @QueryMapping
    public UserDtoResponse getUserInfo() {
        return userService.getInfo();
    }

}
