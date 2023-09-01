package com.github.filipesimoes.j3270.command;

public class CommandException extends RuntimeException {

  private static final long serialVersionUID = 9043337863637817230L;

  public CommandException(String message) {
    super(message);
  }
}
