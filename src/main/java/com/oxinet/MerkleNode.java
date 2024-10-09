package com.oxinet;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Array;
import java.util.ArrayList;
import java.util.List;

public class MerkleNode {
    private String data;
    private MerkleNode left;
    private MerkleNode right;
    private String hash;

    public MerkleNode(String data) {
        this.data = data;
        this.hash = calculateHash(data);

    }

    public String getData() {
        return data;
    }

    public String getHash() {
        return hash;
    }
    public static String calculateHash(String data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data.getBytes());
            StringBuilder hexString = new StringBuilder();

            for (byte b: hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static MerkleNode buildTree(List<String> data) {
        List<MerkleNode> leaves = new ArrayList<>();
        for (String item : data) {
            leaves.add(new MerkleNode(item));
        }
        while (leaves.size() > 1) {
            List<MerkleNode> level = new ArrayList<>();

            for (int i = 0; i < leaves.size(); i += 2) {
                MerkleNode left = leaves.get(i);
                MerkleNode right = (i + 1 < leaves.size()) ? leaves.get(i + 1) : null;
                String combinedHash = left.getHash() + ((right != null) ? right.getHash() : "");
                MerkleNode parent = new MerkleNode(combinedHash);
                parent.left = left;
                parent.right = right;
                level.add(parent);
            }

            leaves = level;
        }

        return leaves.get(0);
    }
}
