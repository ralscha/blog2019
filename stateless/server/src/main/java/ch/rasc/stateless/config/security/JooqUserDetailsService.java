package ch.rasc.stateless.config.security;

import static ch.rasc.stateless.db.tables.AppUser.APP_USER;

import org.jooq.DSLContext;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import ch.rasc.stateless.db.tables.records.AppUserRecord;

@Service
public class JooqUserDetailsService implements UserDetailsService {

  private final DSLContext dsl;

  public JooqUserDetailsService(DSLContext dsl) {
    this.dsl = dsl;
  }

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    AppUserRecord appUserRecord = this.dsl.selectFrom(APP_USER)
        .where(APP_USER.EMAIL.eq(email)).limit(1).fetchOne();

    if (appUserRecord != null) {
      return new JooqUserDetails(appUserRecord);
    }
    throw new UsernameNotFoundException(email);
  }

}
