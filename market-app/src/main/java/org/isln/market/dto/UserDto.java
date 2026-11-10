package org.isln.market.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class UserDto {
    private String name;
    private boolean anonymous;

    public static UserDto anonymous() {
        return new UserDto().setName("guest").setAnonymous(true);
    }
}
