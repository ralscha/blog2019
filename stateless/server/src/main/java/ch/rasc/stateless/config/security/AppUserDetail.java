package ch.rasc.stateless.config.security;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import ch.rasc.stateless.db.tables.records.AppUserRecord;

public class JooqUserDetails implements UserDetails {

  private static final long serialVersionUID = 1L;

  private final Collection<GrantedAuthority> authorities;

  private final String username;

  private final String password;

  private final boolean enabled;

  private final Long userDbId;

  private final boolean locked;

  private final boolean expired;

  public JooqUserDetails(AppUserRecord user) {
    this.userDbId = user.getId();

    this.username = user.getEmail();
    this.password = user.getPasswordHash();
    this.enabled = user.getEnabled() != null ? user.getEnabled().booleanValue() : false;

    this.expired = false;
    this.locked = false;

    this.authorities = Collections
        .singleton(new SimpleGrantedAuthority(user.getAuthority()));
  }

  @Override
  public Collection<GrantedAuthority> getAuthorities() {
    return this.authorities;
  }

  @Override
  public String getPassword() {
    return this.password;
  }

  @Override
  public String getUsername() {
    return this.username;
  }

  public Long getUserDbId() {
    return this.userDbId;
  }

  @Override
  public boolean isAccountNonExpired() {
    return !this.expired;
  }

  @Override
  public boolean isAccountNonLocked() {
    return !this.locked;
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
