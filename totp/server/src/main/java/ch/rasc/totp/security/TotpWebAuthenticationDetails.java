package ch.rasc.totp.security;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.util.StringUtils;

public class TotpWebAuthenticationDetails extends WebAuthenticationDetails {

  private static final long serialVersionUID = 1L;

  private Long totpKey;

  public TotpWebAuthenticationDetails(HttpServletRequest request) {
    super(request);

    String totpKeyString = request.getParameter("totpkey");
    if (StringUtils.hasText(totpKeyString)) {
      try {
        totpKeyString = totpKeyString.replaceAll("\\s+", "");
        this.totpKey = Long.valueOf(totpKeyString);
      }
      catch (NumberFormatException e) {
        this.totpKey = null;
      }
    }
  }

  public Long getTotpKey() {
    return this.totpKey;
  }

}