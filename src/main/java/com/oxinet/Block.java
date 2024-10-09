package com.oxinet;

import java.util.List;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

class Block {
    private int index;
    private long timestamp;
    private List<Transaction> transactions;
    private String previousHash;
    private String hash;

    public Block(int index, long timestamp, List<Transaction> transactions, String previousHash){
        this.index = index;
        this.timestamp = timestamp;
        this.transactions = transactions;
        this.previousHash = previousHash;
        this.hash = calculateHash();
        getTransactions();
        saveToJsonFile();
    }

    private String calculateHash(){
        try {
            StringBuilder dataToHash = new StringBuilder();
            dataToHash.append(index);
            dataToHash.append(timestamp);
            dataToHash.append(previousHash);
            
            for (Transaction transaction : transactions){
                dataToHash.append(transaction.getSender());
                dataToHash.append(transaction.getRecipient());
                dataToHash.append(transaction.getAmount());
            }

            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(dataToHash.toString().getBytes());

            StringBuilder hexString = new StringBuilder();
            for (byte hashByte : hashBytes) {
                String hex = Integer.toHexString(0xff & hashByte);
                if (hex.length() == 1){
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void saveToJsonFile() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String blockJson = gson.toJson(this);
        String blocksDirectoryPath = "." + "/blocks";

        File blocksDirectory = new File(blocksDirectoryPath);
        if (!blocksDirectory.exists()) {
            blocksDirectory.mkdirs();
        }
        String filePath = blocksDirectoryPath + "/block" + this.index + ".json";

        try (FileWriter fileWriter = new FileWriter(filePath)) {
            fileWriter.write(blockJson);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
   

    public String getHash() {
        return hash;
    }
    public int getIndex(){
        return index;
    }
    public double getTimestamp(){
        return timestamp;
    }
    public String getPreviousHash(){
        return previousHash;
    }
    public List<Transaction> getTransactions(){
        return transactions;
    } 
}