package com.github.filipesimoes.j3270.command;

public class PrintTextCommand extends AbstractCommand<String> {
    private StringBuilder screenBuffer = new StringBuilder();

    @Override
    protected void processData(String data) {
        screenBuffer.append(data).append("\n");
    }

    @Override
    protected String getOutput() {
        return screenBuffer.toString();
    }

    @Override
    protected String getCommand() {
        return "PrintText(string)";
    }
}
