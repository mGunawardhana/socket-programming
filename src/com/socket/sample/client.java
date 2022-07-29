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
import java.net.Socket;

public class client implements Runnable {
    private Socket client;
    private BufferedReader in;
    private PrintWriter out;

    @Override
    public void run() {
        try {
            Socket client = new Socket("127.0.0.1", 8000);
            out = new PrintWriter(client.getOutputStream(),true);
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));

        } catch (IOException e) {
            //TODO: handle
        }

        class InputHandler implements Runnable{

            @Override
            public void run() {
                try{

                }catch (IOException e){

                }
            }
        }
    }
}
