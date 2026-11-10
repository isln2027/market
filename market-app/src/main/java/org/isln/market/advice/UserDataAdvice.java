package org.isln.market.advice;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import org.isln.market.dto.UserDto;
import org.isln.market.service.UserService;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
@RequiredArgsConstructor
public class UserDataAdvice {
    private final UserService userService;

    @ModelAttribute("currentUser")
    public Mono<UserDto> currentUser() {
        return userService.getCurrentUser();
    }
}