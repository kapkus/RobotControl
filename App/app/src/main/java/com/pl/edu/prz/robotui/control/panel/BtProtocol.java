package com.pl.edu.prz.robotui.control.panel;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class BtProtocol {
    OutputStream outputStream;
    InputStream inputStream;
    boolean confirmationReceived;
    ArrayList<String> sequence;

    BtProtocol(OutputStream outputStream, InputStream inputStream) {
        this.outputStream = outputStream;
        this.inputStream = inputStream;
        this.sequence = new ArrayList<String>();
        this.confirmationReceived = true;
    }

    public void passXY(String x, String y) {
        try {
            // Mh - Move horizontally
            outputStream.write(("<Mh" + x + "," + y + ">").getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void passZ(String z) {
        try {
            // Mp - Move perpendicularly
            outputStream.write(("<Mp" + z + ">").getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void passSequence(int pos) {
        System.out.println(sequence.get(pos));
        try {
            outputStream.write((sequence.get(pos)).getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String receiveData() {
        String readMessage = "";
        byte[] buffer = new byte[1];
        int bytes;
        try {
            bytes = inputStream.read(buffer);   //read bytes from input buffer
            readMessage = new String(buffer, 0, bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return readMessage;
    }

    public void setSequence(ArrayList<ArrayList<String>> moves) {
        String move;
        for (int i = 0; i < moves.size(); i++) {
            move = "<S" + moves.get(i).get(0) + "," + moves.get(i).get(1) + "," + moves.get(i).get(2) + ">";
            this.sequence.add(move);
        }
    }

    public void clearSequence() {
        this.sequence.clear();
    }

    public int getSequenceSize() {
        return sequence.size();
    }
}
