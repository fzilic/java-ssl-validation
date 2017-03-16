package com.github.fzilic.util;

import com.github.fzilic.KeyStoreType;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class KeyStoreBuilder {

  private InputStream inputStream;

  private KeyStore keyStore = null;

  private KeyStoreType keyStoreType = KeyStoreType.JKS;

  private String password;

  private boolean isInError = false;

  public static KeyStoreBuilder newInstance() {
    return new KeyStoreBuilder();
  }

  public final KeyStoreBuilder fromPath(final String path) {
    return fromFile(new File(path));
  }

  public final KeyStoreBuilder fromFile(final File file) {
    if (file.exists() && file.isFile() && file.canRead()) {
      try {
        return fromInputStream(new FileInputStream(file));
      }
      catch (FileNotFoundException e) {
        // fall trough - we are checking for it
      }
    }
    else {
      isInError = true;
      log.error("File not found or not readable, keystore will not initialize.");
    }
    return this;
  }

  public final KeyStoreBuilder fromInputStream(final InputStream inputStream) {
    this.inputStream = inputStream;
    return this;
  }

  public final KeyStoreBuilder withPassword(final String password) {
    this.password = password;
    return this;
  }

  public final KeyStore keyStore() {
    if (keyStore == null && !isInError) {

      try {
        final KeyStore store = KeyStore.getInstance(keyStoreType.getType());
        store.load(inputStream, password.toCharArray());

        keyStore = store;
      }
      catch (final KeyStoreException e) {
        log.error("Failed to read keystore", e);
      }
      catch (final CertificateException e) {
        log.error("Failed to read certificate", e);
      }
      catch (final NoSuchAlgorithmException e) {
        log.error("Unknown algorithm", e);
      }
      catch (final IOException e) {
        log.error("Failed to open keystore file", e);
      }
      finally {
        if (inputStream != null) {
          try {
            inputStream.close();
          }
          catch (IOException e) {
            // fall trough
          }
        }

      }
    }
    return keyStore;
  }

  public KeyStoreBuilder ofType(KeyStoreType keyStoreType) {
    this.keyStoreType = keyStoreType;
    return this;
  }
}
