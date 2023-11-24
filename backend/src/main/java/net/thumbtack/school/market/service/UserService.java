package net.thumbtack.school.market.service;

import lombok.RequiredArgsConstructor;
import net.thumbtack.school.market.dto.response.UserDtoResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    public UserDtoResponse getInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        OAuth2User user = ((OAuth2User) authentication.getPrincipal());
        return new UserDtoResponse()
                .setEmail(user.getAttribute("email"))
                .setFirstName(user.getAttribute("given_name"))
                .setLastName(user.getAttribute("family_name"))
                .setRoles(authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList());
    }

}
