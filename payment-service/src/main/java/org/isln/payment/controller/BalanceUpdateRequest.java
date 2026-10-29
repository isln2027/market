package org.isln.payment.controller;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class BalanceUpdateRequest {
    private Long balance;
}
