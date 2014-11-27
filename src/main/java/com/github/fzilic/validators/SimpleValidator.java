package com.github.fzilic.validators;

import com.github.fzilic.CliOptions;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.net.URL;

public class SimpleValidator implements Validator {

  private final String m_host;

  private final Integer m_port;

  public SimpleValidator(final CliOptions p_options) {
    m_host = p_options.getHost();
    m_port = p_options.getPort() == null ? CliOptions.DEFAULT_SSL_PORT : p_options.getPort();
  }

  public SimpleValidator(final String p_host, final Integer p_port) {
    m_host = p_host;
    m_port = p_port;
  }

  @Override
  public void validate() throws IOException {
    final URL url = new URL(String.format("https://%s:%d", m_host, m_port));
    final HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
    connection.connect();
  }
}
