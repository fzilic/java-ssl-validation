package com.github.fzilic;

import asg.cliche.ShellFactory;
import com.github.fzilic.validators.InteractiveValidator;
import com.github.fzilic.validators.SimpleValidator;
import com.github.fzilic.validators.Validator;
import org.apache.commons.lang3.StringUtils;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

import java.io.IOException;

public class Validation {

  private static CliOptions OPTIONS = new CliOptions();

  public static void main(String[] args) {
    final CmdLineParser parser = new CmdLineParser(OPTIONS);

    try {
      parser.parseArgument(args);
    } catch (final CmdLineException exception) {
      System.out.println(exception.getMessage());
      parser.printUsage(System.out);
      return;
    }

    if (StringUtils.isNotBlank(OPTIONS.getHost())) {
      final Validator validator = new SimpleValidator(OPTIONS);
      try {
        validator.validate();
      } catch (final IOException exception) {
        System.out.println("Validation unsuccessful");
        exception.printStackTrace();
        return;
      }
      System.out.println("Validation successful");
    } else {
      try {
        ShellFactory.createConsoleShell("ssl-val", "SSL Validation", new InteractiveValidator()).commandLoop();
      } catch (IOException exception) {
        return;
      }
    }
  }

}
