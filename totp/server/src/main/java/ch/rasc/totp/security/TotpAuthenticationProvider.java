package ch.rasc.totp.security;

import org.jboss.aerogear.security.otp.Totp;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class TotpAuthenticationProvider extends DaoAuthenticationProvider {

  TotpAuthenticationProvider(UserDetailsService userDetailService) {
    setUserDetailsService(userDetailService);
  }

  @Override
  public Authentication authenticate(Authentication authentication)
      throws AuthenticationException {

    Authentication auth = super.authenticate(authentication);

    if (auth.getDetails() instanceof TotpWebAuthenticationDetails
        && auth.getPrincipal() instanceof JooqUserDetails) {
      String secret = ((JooqUserDetails) auth.getPrincipal()).getSecret();

      if (StringUtils.hasText(secret)) {
        Long totpKey = ((TotpWebAuthenticationDetails) auth.getDetails()).getTotpKey();
        if (totpKey != null) {
          Totp totp = new Totp(secret);
          if (!totp.verify(totpKey.toString())) {
            throw new BadCredentialsException("TOTP Code is not valid");
          }
        }
        else {
          throw new BadCredentialsException("TOTP Code is mandatory");
        }

      }
    }

    return auth;
  }

}
