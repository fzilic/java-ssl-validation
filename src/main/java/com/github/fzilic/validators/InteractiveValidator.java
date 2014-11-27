package com.github.fzilic.validators;

import asg.cliche.Command;
import com.github.fzilic.CliOptions;

import java.io.IOException;

public class InteractiveValidator implements Validator {

  private String m_host = null;

  private Integer m_port = CliOptions.DEFAULT_SSL_PORT;

  @Command(abbrev = "h", description = "Set target host")
  public void host(final String p_host) {
    m_host = p_host;
  }

  @Command(abbrev = "p", description = "Set target port")
  public void port(final Integer p_port) {
    m_port = p_port;
  }

  @Override
  @Command(abbrev = "v", description = "Validate host")
  public void validate() throws IOException {
    try {
      new SimpleValidator(m_host, m_port).validate();
    } catch (final IOException exception) {
      System.out.println("Validation unsuccessful");
      exception.printStackTrace();
    }
    System.out.println("Validation successful");

  }
}
