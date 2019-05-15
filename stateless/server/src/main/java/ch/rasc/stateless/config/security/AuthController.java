package ch.rasc.stateless.config.security;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

  @GetMapping("/authenticate")
  @PreAuthorize("isFullyAuthenticated()")
  public String authenticate(@AuthenticationPrincipal UserDetails user) {
    return user.getAuthorities().iterator().next().getAuthority();
  }

}
