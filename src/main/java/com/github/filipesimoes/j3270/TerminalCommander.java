package com.github.filipesimoes.j3270;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

@Slf4j
public class TerminalCommander implements Closeable, AutoCloseable {

  private int scriptPort;

  private Socket socket = null;
  private Writer writer = null;
  private BufferedReader reader = null;

  public TerminalCommander(int scriptPort) {
    super();
    this.scriptPort = scriptPort;
  }

  public <V> V execute(Command<V> command) {
    return command.execute(writer, reader);
  }

  public void connect() throws IOException, TimeoutException {
    try {
      waitForSocket();
    } catch (InterruptedException e) {
      throw new TimeoutException("Script socket connection timed out.");
    }
    writer = new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.US_ASCII);
    reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.US_ASCII));
  }

  private void waitForSocket() throws InterruptedException, TimeoutException {
    int attempts = 0;
    while (attempts < 10) {
      try {
        socket = new Socket("localhost", scriptPort);
        log.debug("Connected to 3270");
        return;
      } catch (IOException e) {
        log.trace("Cannot connect to 3270");
        Thread.sleep(50);
        attempts++;
      }

    }
    throw new TimeoutException("Emulator socket connection timed out.");
  }

  public void disconnect() {
    if (writer != null) {
      try {
        writer.close();
      } catch (IOException e) {
        // Nothing to do.
      }
    }
    if (reader != null) {
      try {
        reader.close();
      } catch (IOException e) {
        // Nothing to do.
      }
    }
    if (socket != null) {
      try {
        socket.close();
      } catch (IOException e) {
        // Nothing to do.
      }
    }

  }

  @Override
  public void close() throws IOException {
    disconnect();
  }

}
