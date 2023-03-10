package org.example;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class EvalServer {

  private static final int EXPECTED_ARG = 1;

  public static void main(String[] args) {

    // The server starts with one argument, the receiving port number
    if (args.length != EXPECTED_ARG) {
      System.err.println("Usage: provide port number");
      System.exit(1);
    }

    int portNumber = Integer.parseInt(args[0]);

    // Server keeps running and start a thread whenever a new client connection is received.
    try (ServerSocket serverSocket = new ServerSocket(portNumber)) {
      System.out.println("Server start waiting...");
      serverSocket.setReuseAddress(true);
      while (true) {
        Socket client = serverSocket.accept();
        System.out.println("New client connected " + client.getInetAddress().getHostAddress());
        EvalServerThread evalHandler = new EvalServerThread(client);
        new Thread(evalHandler).start();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
