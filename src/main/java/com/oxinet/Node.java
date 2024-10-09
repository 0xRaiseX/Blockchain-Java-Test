package com.oxinet;

public class Node {
    private String publicKey;
    private String privateKey;
    private double stake;

    public Node(String publicKey, double stake) {
        this.publicKey = publicKey;
        this.stake = stake;
    }

    
}
