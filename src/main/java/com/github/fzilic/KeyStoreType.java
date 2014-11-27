package com.github.fzilic;

public enum KeyStoreType {
  JKS, BKS;

  private final String type;

  private KeyStoreType() {
    this.type = this.name();
  }

  public String getType() {
    return type;
  }
}
