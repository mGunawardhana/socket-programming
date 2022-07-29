/*
 * Developed by - mGunawardhana
 * Contact email - mrgunawardhana27368@gmail.com
 * what's app - 071 - 9043372
 */

package com.socket.sample;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server implements Runnable {

    private final ArrayList<ConnectionHandler> connections;
    private ServerSocket server;
    private boolean done;
    private ExecutorService pool;

    public Server() {
        connections = new ArrayList<>();
        done = false;
    }

    @Override
    public void run() {
        try {

            server = new ServerSocket(8000);
            pool = Executors.newCachedThreadPool();
            while (!done) {
                Socket client = server.accept();
                ConnectionHandler handler = new ConnectionHandler(client);
                connections.add(handler);
                pool.execute(handler);
            }


        } catch (IOException e) {
            shutdown();
        }
    }

    public void broadCast(String message) {
        for (ConnectionHandler ch : connections) {
            if (ch != null) {
                ch.sendMessage(message);
            }
        }
    }

    public void shutdown() {
        try {
            done = true;
            if (!server.isClosed()) {
                server.close();
            }
            for (ConnectionHandler ch : connections) {
                ch.shutdown();
            }
        } catch (IOException ignored) {
        }

    }


    class ConnectionHandler implements Runnable {

        private final Socket client;
        public String nickname;
        private BufferedReader in;
        private PrintWriter out;

        public ConnectionHandler(Socket client) {
            this.client = client;
        }


        @Override
        public void run() {
            try {

                out = new PrintWriter(client.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                out.println("Please enter a nickname :");
                nickname = in.readLine();
                System.out.println(nickname + "connected!");
                broadCast(nickname + "joined the chat! ");

                String message;
                while ((message = in.readLine()) != null) {

                    if (message.startsWith("/nick")) {
                        //TODO: handle nickname
                        String[] messageSplit = message.split(" ", 2);

                        if (messageSplit.length == 2) {
                            broadCast(nickname + " renamed themselves to " + messageSplit[1]);
                            System.out.println(nickname + " renamed themselves to " + messageSplit[1]);
                            nickname = messageSplit[1];
                            out.println("Successfully changed nickname to " + nickname);
                        } else {
                            out.println("No nickname provided! ");
                        }
                    } else if (message.startsWith("/quit")) {
                        broadCast(nickname+" left the chat!");
                        shutdown();
                    } else {
                        broadCast(nickname + ": " + message);
                    }
                }
            } catch (IOException e) {
                shutdown();
            }
        }

        public void sendMessage(String message) {
            out.println(message);
        }

        public void shutdown() {
            try {
                in.close();
                out.close();
                if (!client.isClosed()) {
                    client.close();
                }
            } catch (IOException ignored) {}
        }
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.run();
    }
}
