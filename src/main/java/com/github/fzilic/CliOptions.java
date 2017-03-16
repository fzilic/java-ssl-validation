package com.github.fzilic;

import lombok.Getter;
import org.kohsuke.args4j.Option;

@Getter
public class CliOptions {

  public static final Integer DEFAULT_SSL_PORT = 443;

  @Option(name = "-h", aliases = {"-H", "--host"}, usage = "A valid host name or IP address")
  private String host;

  @Option(name = "-p", aliases = {"-P", "--port"}, usage = "A valid integer port number")
  private Integer port;

  @Option(name = "-k", aliases = {"--keystore"}, usage = "Path to keystore")
  private String keyStore;

  @Option(name = "-w", aliases = {"--keystore-pass"}, usage = "Keystore pass")
  private String keyStorePass;

  @Option(name = "-t", aliases = {"--keystore-type"}, usage = "Keystore type, supported JKS, BKS")
  private KeyStoreType keyStoreType = KeyStoreType.JKS;

  @Option(name="-v", aliases = "--validator", usage = "Validator type: SIMPLE, DETAILED")
  private ValidatorType validatorType = ValidatorType.SIMPLE;

}
