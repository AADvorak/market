package net.thumbtack.school.market.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class UserDtoResponse {

    private String firstName;

    private String lastName;

    private String email;

    private List<String> roles;

}
