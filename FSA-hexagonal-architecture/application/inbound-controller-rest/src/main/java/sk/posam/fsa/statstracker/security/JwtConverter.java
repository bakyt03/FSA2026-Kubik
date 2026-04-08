package sk.posam.fsa.statstracker.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;

class JwtConverter extends AbstractAuthenticationToken {

    private final Jwt source;

    public JwtConverter(Jwt source) {
        super(toAuthorities(source));
        this.source = Objects.requireNonNull(source);
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return List.of();
    }

    /** Returns the JWT subject (user identifier) as the principal. */
    @Override
    public Object getPrincipal() {
        return source.getSubject();
    }

    private static Collection<? extends GrantedAuthority> toAuthorities(Jwt source) {
        Map<String, Object> realmAccess = source.getClaimAsMap("realm_access");
        if (realmAccess == null || realmAccess.get("roles") == null) {
            return List.of();
        }

        @SuppressWarnings("unchecked")
        List<String> roles = (List<String>) realmAccess.get("roles");

        return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .toList();
    }
}
