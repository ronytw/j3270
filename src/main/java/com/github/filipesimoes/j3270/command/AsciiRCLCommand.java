package com.github.filipesimoes.j3270.command;

public class AsciiRCLCommand extends AbstractCommand<String> {

  private int row;
  private int col;
  private int length;

  private String ascii = null;

  public AsciiRCLCommand(int row, int col, int length) {
    this.row = row - 1;
    this.col = col - 1;
    this.length = length;
  }

  @Override
  protected void processData(String data) {
    ascii = data;
  }

  @Override
  protected String getOutput() {
    return ascii;
  }

  @Override
  protected String getCommand() {
    return "Ascii(" + row + "," + col + "," + length + ")";
  }

}
