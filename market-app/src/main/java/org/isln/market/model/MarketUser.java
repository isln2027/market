package org.isln.market.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@Accessors(chain = true)
@Table(name = "users")
@NoArgsConstructor
public class MarketUser {
    @Id
    private Long id;
    private String login;
    private String password;
}
