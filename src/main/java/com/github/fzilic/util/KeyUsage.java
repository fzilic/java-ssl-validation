package com.github.fzilic.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public enum KeyUsage {
  DIGITAL_SIGNATURE(0, "digitalSignature"),
  NON_REPUDIATION(1, "nonRepudiation"),
  KEY_ENCIPHERMENT(2, "keyEncipherment"),
  DATA_ENCIPHERMENT(3, "dataEncipherment"),
  KEY_AGREEMENT(4, "keyAgreement"),
  KEY_CERT_SIGN(5, "keyCertSign"),
  CRL_SIGN(6, "cRLSign"),
  ENCIPHER_ONLY(7, "encipherOnly"),
  DECIPHER_ONLY(8, "decipherOnly");

  private final int index;

  private final String description;

  public static String formatKeyUsage(final boolean[] keyUsages) {
    if (keyUsages == null || keyUsages.length != KeyUsage.values().length) {
      return "#UNKNOWN#";
    }

    final StringBuilder builder = new StringBuilder();

    for (final KeyUsage usage : KeyUsage.values()) {
      if (keyUsages[usage.index]) {
        builder.append(" ").append(usage.description);
      }
    }

    return builder.toString().trim();
  }

  public static List<KeyUsage> parseKeyUsage(final boolean[] keyUsages) {
    if (keyUsages == null || keyUsages.length != KeyUsage.values().length) {
      return Collections.emptyList();
    }

    final List<KeyUsage> usages = new ArrayList<>(9);

    for (final KeyUsage usage : KeyUsage.values()) {
      if(keyUsages[usage.index]) {
        usages.add(usage);
      }
    }

    return usages;
  }

  KeyUsage(final int index, final String description) {
    this.index = index;
    this.description = description;
  }
}
