package ch.rasc.totp.security;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import ch.rasc.totp.db.tables.records.AppUserRecord;

public class JooqUserDetails implements UserDetails {

  private static final long serialVersionUID = 1L;

  private final String password;

  private final String username;

  private final String secret;

  private final Long userDbId;

  private final boolean enabled;

  public JooqUserDetails(AppUserRecord user) {
    this.userDbId = user.getId();
    this.password = user.getPasswordHash();
    this.username = user.getUsername();
    this.secret = user.getSecret();
    this.enabled = user.getEnabled();
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return List.of();
  }

  public Long getUserDbId() {
    return this.userDbId;
  }

  public String getSecret() {
    return this.secret;
  }

  @Override
  public String getPassword() {
    return this.password;
  }

  @Override
  public String getUsername() {
    return this.username;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return this.enabled;
  }

}
