package com.oxinet;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.apache.tomcat.util.json.JSONParser;
import org.json.JSONException;
import org.json.JSONObject;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;
import org.json.JSONArray;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class Blockchain {

    private List<Block> chain;
    private int index;
    private int blockSize;
    private List<Transaction> pendingTransactions;

    public Blockchain(){
        this.index = findMaxIndex();
        // chain = new ArrayList<>();
        pendingTransactions = new ArrayList<>();
        this.blockSize = Config.blockSize;
        // Block genesisBlock = new Block(0, System.currentTimeMillis() / 1000, new ArrayList<>(), "0");
        // chain.add(genesisBlock);
    }

    private static int findMaxIndex() {
        File blocksDirectory = new File("./blocks");
        File[] blockFiles = blocksDirectory.listFiles();
        int maxIndex = -1;
        if (blockFiles != null) {
            for (File blockFile : blockFiles) {
                if (blockFile.isFile()) {
                    String fileName = blockFile.getName();
                    try {
                        int index = Integer.parseInt(fileName.replaceAll("\\D", ""));       
                        if (index > maxIndex) {
                            maxIndex = index;
                        }
                    } catch (NumberFormatException e) {
                    }
                }
            }
        }

        return maxIndex;
    }

    // public static int findMaxIndexJson() {
    //     String blocksDirectoryPath = Config.directoryPath;
    //     AtomicInteger maxIndex = new AtomicInteger(-1);

    //     try (Stream<Path> paths = Files.walk(Paths.get(blocksDirectoryPath))) {
    //         paths.filter(Files::isRegularFile)
    //              .filter(path -> path.getFileName().toString().matches("block\\d+\\.json"))
    //              .forEach(path -> {
    //                  try {
    //                      String jsonString = Files.readString(path);
    //                      JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();

    //                      if (jsonObject.has("index")) {
    //                          int index = jsonObject.get("index").getAsInt();
    //                          maxIndex.updateAndGet(currentMax -> Math.max(currentMax, index));
    //                      }
    //                  } catch (IOException e) {
    //                      e.printStackTrace();
    //                  }
    //              });

    //     } catch (IOException e) {    
    //         e.printStackTrace();
    //     }

    //     return maxIndex.get();
    // }
    
    public void addTransaction(String sender, String recipient, double amount){
        Transaction transaction = new Transaction(sender, recipient, amount);
        if (transaction.getAmount() <= 0){
            System.out.println("Ошибка. Количество должно быть больше 0");
            return;
        } 
        pendingTransactions.add(transaction);
        createBlock();
    }

    private void createBlock(){
        if (pendingTransactions.size() >= blockSize){
            int index = chain.size();
            long timestamp = System.currentTimeMillis() / 1000;
            String previousHash = chain.get(chain.size() - 1).getHash();
            List<Transaction> transactionsInBlock = new ArrayList<>(pendingTransactions);
            Block newBlockAdd = new Block(index, timestamp, transactionsInBlock, previousHash);
            chain.add(newBlockAdd);
            pendingTransactions.clear();
            System.out.println("Создан новый блок. Длинна сети: " + chain.size());
        }
    }

    public Block getLastBlock(){
        return chain.get(chain.size() - 1);
    }

    public void printBlockByIndex(int index){
        if (index >= 0 && index < chain.size()){
            Block block = chain.get(index);
            System.out.println("Block index: " + block.getIndex());
            System.out.println("Timestamp: " + block.getTimestamp());
            System.out.println("Previous Hash: " + block.getPreviousHash());
            System.out.println("Hash: " + block.getHash());

            List<Transaction> transactions = block.getTransactions();
            if (!transactions.isEmpty()){
                System.out.println("Transactions:");
                for (Transaction transaction : transactions){
                    System.out.println("   Sender:   " + transaction.getSender());
                    System.out.println("   Recipient:   " + transaction.getRecipient());
                    System.out.println("   Amount:   " + transaction.getAmount());
                }
            } else {
                System.out.println("No transaction to this block.");
            }
        } else {
            System.out.println("Block with index " + index + " not found.");
        }
    }

    public void printCurrentBlock() {
        Block currentBlock = getLastBlock();
        System.out.println("Block index: " + currentBlock.getIndex());
        System.out.println("Timestamp: " + currentBlock.getTimestamp());
        System.out.println("Previous Hash: " + currentBlock.getPreviousHash());
        System.out.println("Hash: " + currentBlock.getHash());

        List<Transaction> transactions = currentBlock.getTransactions();

        if (!transactions.isEmpty()){
            System.out.println("Transactions:");
            for (Transaction transaction : transactions){
                System.out.println("   Sender:   " + transaction.getSender());
                System.out.println("   Recipient:   " + transaction.getRecipient());
                System.out.println("   Amount:   " + transaction.getAmount());
            }
        } else {
            System.out.println("No transaction to this block.");
        }
    }

    public String readBlockFromFile(int blockNumber) {
        String blocksDirectoryPath = "." + "/blocks";
        String fileName = blocksDirectoryPath + "/block" + blockNumber + ".json";
        StringBuilder blockData = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                blockData.append(line);
            }

        } catch (IOException e){
            e.printStackTrace();
        }
        return blockData.toString();
    }
}   

