package com.github.fzilic.validators;

import com.github.fzilic.CliOptions;
import com.github.fzilic.KeyStoreType;
import com.github.fzilic.util.KeyStoreBuilder;
import com.github.fzilic.util.SSLContextBuilder;
import java.io.IOException;
import java.net.URL;
import java.security.KeyStore;
import javax.net.ssl.HttpsURLConnection;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@Slf4j
public class SimpleValidator implements Validator {

  protected final String host;

  protected final Integer port;

  protected String keyStorePath;

  protected String keyStorePass;

  protected KeyStoreType keyStoreType;

  public SimpleValidator(final CliOptions options) {
    host = options.getHost();
    port = options.getPort() == null ? CliOptions.DEFAULT_SSL_PORT : options.getPort();

    keyStorePath = options.getKeyStore();
    keyStorePass = options.getKeyStorePass();
    keyStoreType = options.getKeyStoreType();
  }

  @Override
  public void validate() throws IOException {
    final URL url = new URL(String.format("https://%s:%d", host, port));
    final HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();

    if (StringUtils.isNotBlank(keyStorePath)) {
      final KeyStore keyStore = KeyStoreBuilder.newInstance().fromPath(keyStorePath).ofType(keyStoreType)
          .withPassword(keyStorePass).keyStore();

      if (keyStore != null) {
        SSLContextBuilder.newInstance().forConnection(connection).withKeyStore(keyStore).apply();
      }
    }

    connection.connect();

    connection.disconnect();
  }
}
