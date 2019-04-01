package pojos;

import lombok.Data;
import lombok.EqualsAndHashCode;
import enums.RoleName;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import pojos.User;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@EqualsAndHashCode
public class CustomUserDetails implements UserDetails {

    User user;

    public CustomUserDetails(final User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<String> roles = Arrays.asList(RoleName.ROLE_USER.getType(), RoleName.ROLE_ADMIN.getType());
        return roles.stream().map(r -> new SimpleGrantedAuthority(r)).collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }


    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return user.getActive();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return user.getIsEmailVerified();
    }

    public Long getId() {
        return user.getId();
    }

    public String getEmail(){
        return user.getEmail();
    }

    public Set<Role> getRoles(){
        return user.getRoles();
    }

}
