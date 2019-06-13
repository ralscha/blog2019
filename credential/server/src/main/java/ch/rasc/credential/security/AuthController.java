package ch.rasc.credential.security;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

  @GetMapping("/authenticate")
  @ResponseStatus(code = HttpStatus.NO_CONTENT)
  public void authenticate() {
    // nothing here
  }

}
