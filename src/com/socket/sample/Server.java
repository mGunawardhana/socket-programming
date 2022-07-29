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

public class Server implements Runnable {

    private ArrayList<ConnectionHandler> connections;

    @Override
    public void run() {
        try {

            ServerSocket server = new ServerSocket(8000);
            Socket client = server.accept();
            connections.add(new ConnectionHandler(client));

        } catch (IOException e) {
            //TODO: handle
        }
    }

    public void broadCast(String message) {
        for (ConnectionHandler ch : connections) {
            if (ch != null) {
                ch.sendMessage(message);
            }
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
                        }

                    } else if (message.startsWith("/quit")) {
                        //TODO: quit

                    } else {
                        broadCast(nickname + ": " + message);
                    }
                }

            } catch (IOException e) {
                //TODO: handle
            }


        }

        public void sendMessage(String message) {
            out.println(message);
        }
    }
}
