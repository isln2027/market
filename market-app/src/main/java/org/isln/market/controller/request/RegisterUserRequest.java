package org.isln.market.controller.request;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class RegisterUserRequest {
    private String username;
    private String password;
}
