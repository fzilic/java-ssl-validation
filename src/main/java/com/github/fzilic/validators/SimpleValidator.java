package com.github.fzilic.validators;

import com.github.fzilic.CliOptions;
import com.github.fzilic.KeyStoreType;
import org.apache.commons.lang3.StringUtils;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import javax.transaction.xa.XAResource;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

public class SimpleValidator implements Validator {

  private final String host;

  private final Integer port;

  private String keyStorePath;

  private String keyStorePass;

  private KeyStoreType keyStoreType;

  public SimpleValidator(final CliOptions options) {
    host = options.getHost();
    port = options.getPort() == null ? CliOptions.DEFAULT_SSL_PORT : options.getPort();

    keyStorePath = options.getKeyStore();
    keyStorePass = options.getKeyStorePass();
    keyStoreType = options.getKeyStoreType();
  }

  public SimpleValidator(final String host, final Integer port) {
    this.host = host;
    this.port = port;
  }

  @Override
  public void validate() throws IOException {
    final URL url = new URL(String.format("https://%s:%d", host, port));
    final HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();

    if (StringUtils.isNotBlank(keyStorePath)) {
      final File file = new File(keyStorePath);

      if (file.exists()) {
        InputStream inputStream = null;

        try {
          final KeyStore keyStore = KeyStore.getInstance(keyStoreType.getType());
          inputStream = new FileInputStream(file);
          keyStore.load(inputStream, keyStorePass.toCharArray());

          final TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
          trustManagerFactory.init(keyStore);
          final SSLContext sslContext = SSLContext.getInstance("TLS");
          sslContext.init(null, trustManagerFactory.getTrustManagers(), null);

          connection.setSSLSocketFactory(sslContext.getSocketFactory());
        } catch (final KeyStoreException e) {
          System.out.println("Failed to read keystore");
          e.printStackTrace();
        } catch (final CertificateException e) {
          System.out.print("Failed to read certificate");
          e.printStackTrace();
        } catch (final NoSuchAlgorithmException e) {
          System.out.println("Unknown algorithm " + e.getMessage());
          e.printStackTrace();
        } catch (final IOException e) {
          System.out.println("Failed to open keystore file");
          e.printStackTrace();
        } catch (final KeyManagementException e) {
          System.out.println("Failed to create ssl context with keystore");
          e.printStackTrace();
        } finally {
          // close input stream
          if (inputStream != null) {
            try {
              inputStream.close();
            } catch (IOException e) {
              // fall trough
            }
          }
        }
      }
    }

    connection.connect();
  }
}
