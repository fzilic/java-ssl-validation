package com.github.fzilic.validators;

import com.github.fzilic.CliOptions;
import com.github.fzilic.util.KeyStoreBuilder;
import com.github.fzilic.util.KeyUsage;
import com.github.fzilic.util.SSLContextBuilder;
import java.io.IOException;
import java.net.URL;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import javax.net.ssl.HttpsURLConnection;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@Slf4j
public class DetailedValidator extends SimpleValidator implements Validator {

  public DetailedValidator(final CliOptions options) {
    super(options);
  }

  @Override
  public void validate() throws IOException {
    final URL url = new URL(String.format("https://%s:%d", host, port));
    HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
    boolean connected = false;
    IOException exception = null;

    if (StringUtils.isNotBlank(keyStorePath)) {
      final KeyStore keyStore = KeyStoreBuilder.newInstance().fromPath(keyStorePath).ofType(keyStoreType)
          .withPassword(keyStorePass).keyStore();

      if (keyStore != null) {
        SSLContextBuilder.newInstance().forConnection(connection).withKeyStore(keyStore).apply();
      }
    }

    try {
      connection.connect();
      connected = true;
    }
    catch (final IOException e) {
      log.warn("Trusted TLS connection failed");
      exception = e;
    }


    if (!connected) {
      connection = (HttpsURLConnection) url.openConnection();
      SSLContextBuilder.newInstance().forConnection(connection).trustAll();

      try {
        connection.connect();
        connected = true;
      }
      catch (final IOException e) {
        log.error("Failed to establish untrusted TLS connection");
        throw e;
      }
    }

    final Certificate[] certificates = connection.getServerCertificates();

    log.info("Server certificate chain:");

    Integer idx = 1;
    for (final Certificate certificate : certificates) {
      if (certificate instanceof X509Certificate) {
        final X509Certificate crt = (X509Certificate) certificate;

        log.info("Certificate {}", idx++);
        log.info("\tSubject DN:\t{}", crt.getSubjectDN());
        log.info("\tIssuer DN:\t{}", crt.getIssuerDN());
        log.info("\tUsages:\t\t{}", KeyUsage.formatKeyUsage(crt.getKeyUsage()));
      }
    }

    if (connected) {
      connection.disconnect();
    }

    if (exception != null) {
      throw exception;
    }

  }
}
