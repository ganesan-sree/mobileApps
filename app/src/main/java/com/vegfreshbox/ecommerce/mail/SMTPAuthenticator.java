package com.vegfreshbox.ecommerce.mail;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

public class SMTPAuthenticator extends Authenticator {
public SMTPAuthenticator() {

super();
}

@Override
public PasswordAuthentication getPasswordAuthentication() {
String username = "freshvegbox@gmail";
String password = "yojitha@2015";
if ((username != null) && (username.length() > 0) && (password != null)
&& (password.length() > 0)) {

return new PasswordAuthentication(username, password);
}

return null;
}
}
