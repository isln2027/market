package org.isln.market.model;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.json.JsonMapper;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@Accessors(chain = true)
@Table(name = "users")
@NoArgsConstructor
public class MarketUser {
    @Transient
    private final JsonMapper mapper = new JsonMapper();
    @Id
    private Long id;
    private String login;
    private String password;
    @Column("roles")
    private String rawRoles;

    public Set<String> getRoles() {
        if (rawRoles == null) {
            return Collections.emptySet();
        }
        return mapper.readValue(rawRoles, new TypeReference<>() {
        });
    }

    public MarketUser setRoles(Set<String> roles) {
        if (roles == null) {
            this.rawRoles = mapper.writeValueAsString(Collections.emptySet());
        }
        this.rawRoles = mapper.writeValueAsString(roles);
        return this;
    }

    public MarketUser setRoles(String... role) {
        if (role == null) {
            this.rawRoles = mapper.writeValueAsString(Collections.emptySet());
        } else {
            this.rawRoles = mapper.writeValueAsString(List.of(role));
        }
        return this;
    }
}
