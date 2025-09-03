package Core.Banking.System.CBS.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static Core.Banking.System.CBS.entity.Permission.*;


@RequiredArgsConstructor
public enum Role {

    ADMIN(Set.of()),

//    USER(Collections.emptySet()),

    USER(Set.of());

    @Getter
    public final Set<Permission> permissions;

    public List<SimpleGrantedAuthority> getAuthorities(){
        var authorities=getPermissions()
                .stream()
                .map(permission -> new SimpleGrantedAuthority(permission.getPermission()))
                .collect(Collectors.toList());
        authorities.add(new SimpleGrantedAuthority("ROLE_"+this.name()));
        return authorities;
    }
}
