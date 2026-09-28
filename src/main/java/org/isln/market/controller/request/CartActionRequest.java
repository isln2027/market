package org.isln.market.controller.request;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import org.isln.market.dto.CartAction;

@Getter
@Setter
@Accessors(chain = true)
public class CartActionRequest extends ItemRequestParameters {
    private Long id;
    private CartAction action;
}
