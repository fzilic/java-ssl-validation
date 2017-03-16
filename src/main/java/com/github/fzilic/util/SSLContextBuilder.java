package com.github.fzilic.util;

import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class SSLContextBuilder {

  private HttpsURLConnection connection;

  private KeyStore keyStore;

  public static SSLContextBuilder newInstance() {
    return new SSLContextBuilder();
  }

  public final void apply() {
    try {
      final TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
      trustManagerFactory.init(keyStore);

      final SSLContext sslContext = SSLContext.getInstance("TLS");
      sslContext.init(null, trustManagerFactory.getTrustManagers(), new SecureRandom());

      connection.setSSLSocketFactory(sslContext.getSocketFactory());
    }
    catch (final NoSuchAlgorithmException e) {
      log.error("Unknown algorithm", e);
    }
    catch (final KeyStoreException e) {
      log.error("Failed to read keystore", e);
    }
    catch (final KeyManagementException e) {
      log.error("Failed to create ssl context with keystore", e);
    }
  }

  public final SSLContextBuilder forConnection(final HttpsURLConnection connection) {
    this.connection = connection;
    return this;
  }

  public final void trustAll() {
    try {
      final SSLContext sslContext = SSLContext.getInstance("TLS");
      sslContext.init(null, new TrustManager[]{
          new X509TrustManager() {

            private X509Certificate[] accepted;

            @Override
            public void checkClientTrusted(final X509Certificate[] x509Certificates, final String s) throws CertificateException {
            }

            @Override
            public void checkServerTrusted(final X509Certificate[] x509Certificates, final String s) throws CertificateException {
              accepted = x509Certificates;
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
              return accepted;
            }
          }
      }, new SecureRandom());

      connection.setSSLSocketFactory(sslContext.getSocketFactory());
      connection.setHostnameVerifier(new HostnameVerifier() {
        @Override
        public boolean verify(final String s, final SSLSession sslSession) {
          return true;
        }
      });
    }
    catch (final NoSuchAlgorithmException e) {
      log.error("Unknown algorithm", e);
    }
    catch (final KeyManagementException e) {
      log.error("Failed to read keystore", e);
    }

  }

  public final SSLContextBuilder withKeyStore(final KeyStore keyStore) {
    this.keyStore = keyStore;
    return this;
  }

}
