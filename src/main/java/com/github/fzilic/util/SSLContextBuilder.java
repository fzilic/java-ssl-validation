package com.github.fzilic.util;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

public final class SSLContextBuilder {

  private HttpsURLConnection connection;

  private KeyStore keyStore;

  public static SSLContextBuilder newInstance() {
    return new SSLContextBuilder();
  }

  public final SSLContextBuilder withKeyStore(final KeyStore keyStore) {
    this.keyStore = keyStore;
    return this;
  }

  public final SSLContextBuilder forConnection(final HttpsURLConnection connection) {
    this.connection = connection;
    return this;
  }

  public final void apply() {
    try {
      final TrustManagerFactory trustManagerFactory =
          TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
      trustManagerFactory.init(keyStore);

      final SSLContext sslContext = SSLContext.getInstance("TLS");
      sslContext.init(null, trustManagerFactory.getTrustManagers(), null);

      connection.setSSLSocketFactory(sslContext.getSocketFactory());
    } catch (final NoSuchAlgorithmException e) {
      System.out.println("Unknown algorithm " + e.getMessage());
      e.printStackTrace();
    } catch (final KeyStoreException e) {
      System.out.println("Failed to read keystore");
      e.printStackTrace();
    } catch (final KeyManagementException e) {
      System.out.println("Failed to create ssl context with keystore");
      e.printStackTrace();
    }
  }

}
