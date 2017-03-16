package com.github.fzilic;

import com.github.fzilic.validators.DetailedValidator;
import com.github.fzilic.validators.SimpleValidator;
import com.github.fzilic.validators.Validator;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

@Slf4j
public class Validation {

  private static CliOptions OPTIONS = new CliOptions();

  public static void main(String[] args) {
    final CmdLineParser parser = new CmdLineParser(OPTIONS);

    try {
      parser.parseArgument(args);
    }
    catch (final CmdLineException exception) {
      log.error("Error parsing arguments", exception);
      final ByteArrayOutputStream out = new ByteArrayOutputStream();
      parser.printUsage(out);
      log.info("\n" + new String(out.toByteArray()));
      return;
    }


    if (StringUtils.isBlank(OPTIONS.getHost())) {
      log.warn("Empty host, please provide host to connect to");
      final ByteArrayOutputStream out = new ByteArrayOutputStream();
      parser.printUsage(out);
      log.info("\n" + new String(out.toByteArray()));
      return;
    }

    Validator validator;
    switch (OPTIONS.getValidatorType()) {
      case SIMPLE:
        validator = new SimpleValidator(OPTIONS);
        break;
      case DETAILED:
        validator= new DetailedValidator(OPTIONS);
        break;
      default:
        log.error("Unknown validator type");
        return;
    }

    try {
      validator.validate();
    }
    catch (final IOException exception) {
      log.warn("Validation unsuccessful", exception);
      return;
    }
    log.info("Validation successful");

  }

}
