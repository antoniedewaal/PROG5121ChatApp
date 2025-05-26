/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.chatapp;

/**
 *
 * @author RC_Student_lab
 */
import javax.swing.*;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class Message {
    private static int messageCounter = 0;
    private static int totalMessagesSent = 0;
    private static final JSONArray storedMessages = new JSONArray();
    private static final List<String> sentMessagesList = new ArrayList<>();

    private String messageID;
    private int messageNumber;
    private String recipient;
    private String content;
    private String messageHash;
    private String userActionResult;

    public void sendMessages() {
        boolean continueSending = true;

        while (continueSending) {
            messageCounter++;
            this.messageNumber = messageCounter;

            this.messageID = generateMessageID();

            if (!checkMessageID(messageID)) {
                JOptionPane.showMessageDialog(null, "Generated Message ID is invalid.");
                continue;
            }

            while (true) {
                this.recipient = JOptionPane.showInputDialog("Enter recipient cell number (must start with +27 and have 9 additional characters):");
                String cellValidationMessage = validateRecipientCell(this.recipient);
                JOptionPane.showMessageDialog(null, cellValidationMessage);
                if (checkRecipientCell(this.recipient) == 1) {
                    break;
                }
            }

            while (true) {
                this.content = JOptionPane.showInputDialog("Enter your message (must be less than 250 characters):");
                String lengthValidationMessage = validateMessageLength(this.content);
                if (this.content.length() <= 250) {
                    JOptionPane.showMessageDialog(null, "Message ready to send.");
                    break;
                } else {
                    JOptionPane.showMessageDialog(null, lengthValidationMessage);
                }
            }

            this.messageHash = createMessageHash();

            String formattedMessage = wrapText(content, 60);

            String fullMessage = "Message Preview:\n" +
                    "Message ID: " + messageID + "\n" +
                    "Message Hash: " + messageHash + "\n" +
                    "Recipient: " + recipient + "\n" +
                    "Message: \n" + formattedMessage + "\n\n" +
                    "What would you like to do with this message?";

            String[] options = {"Send Message", "Disregard Message", "Store Message"};
            int choice = JOptionPane.showOptionDialog(null, fullMessage, "Message Options",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);

            if (choice == 0) {
                totalMessagesSent++;
                printMessages();
                sentMessagesList.add("Message ID: " + messageID + "\n" +
                        "Message Hash: " + messageHash + "\n" +
                        "Recipient: " + recipient + "\n" +
                        "Message: \n" + formattedMessage);
                userActionResult = "Message successfully sent.";
                JOptionPane.showMessageDialog(null, userActionResult);
            } else if (choice == 1) {
                userActionResult = "Press 0 to delete message.";
                JOptionPane.showOptionDialog(null, userActionResult, "Delete Message", JOptionPane.DEFAULT_OPTION,
                        JOptionPane.INFORMATION_MESSAGE, null, new Object[]{"0 (Delete)"}, "0 (Delete)");
            } else if (choice == 2) {
                storeMessage();
                userActionResult = "Message successfully stored.";
                JOptionPane.showMessageDialog(null, userActionResult);
            }

            int again = JOptionPane.showConfirmDialog(null, "Do you want to send another message?", "Continue?", JOptionPane.YES_NO_OPTION);
            if (again != JOptionPane.YES_OPTION) {
                continueSending = false;
            }
        }

        JOptionPane.showMessageDialog(null, "Total messages sent: " + returnTotalMessages());
    }

    public String getUserActionResult() {
        return userActionResult;
    }

    public boolean checkMessageID(String id) {
        return id.length() == 10;
    }

    public int checkRecipientCell(String cell) {
        return (cell.length() == 12 && cell.startsWith("+27")) ? 1 : 0;
    }

    public String validateRecipientCell(String cell) {
        if (checkRecipientCell(cell) == 1) {
            return "Cell phone number successfully captured.";
        } else {
            return "Cell phone number is incorrectly formatted or does not contain an international code. Please correct the number and try again.";
        }
    }

    public String validateMessageLength(String message) {
        if (message.length() <= 250) {
            return "Message ready to send.";
        } else {
            int diff = message.length() - 250;
            return "Message exceeds 250 characters by " + diff + ", please reduce size.";
        }
    }

    public String createMessageHash() {
        String[] words = content.trim().split("\\s+");
        String first = words.length > 0 ? words[0] : "";
        String last = words.length > 1 ? words[words.length - 1] : first;
        return (messageID.substring(0, 2) + ":" + messageNumber + ":" + first + last).toUpperCase();
    }

    public void printMessages() {
        String formattedMessage = wrapText(content, 60);
        JOptionPane.showMessageDialog(null, "Message Details:\n" +
                "Message ID: " + messageID + "\n" +
                "Message Hash: " + messageHash + "\n" +
                "Recipient: " + recipient + "\n" +
                "Message: \n" + formattedMessage);
    }

    public int returnTotalMessages() {
        return totalMessagesSent;
    }

    public void storeMessage() {
        JSONObject msg = new JSONObject();
        msg.put("MessageID", messageID);
        msg.put("MessageHash", messageHash);
        msg.put("Recipient", recipient);
        msg.put("Message", content);

        storedMessages.add(msg);

        try (FileWriter file = new FileWriter("messages.json")) {
            file.write(storedMessages.toJSONString());
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String generateMessageID() {
        Random rand = new Random();
        StringBuilder id = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            id.append(rand.nextInt(10));
        }
        return id.toString();
    }

    public void showRecentMessages() {
        if (sentMessagesList.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Coming Soon.");
        } else {
            StringBuilder sb = new StringBuilder();
            for (String msg : sentMessagesList) {
                sb.append(msg).append("\n\n");
            }
            JOptionPane.showMessageDialog(null, sb.toString());
        }
    }

    private String wrapText(String text, int lineLength) {
        StringBuilder wrapped = new StringBuilder();
        int i = 0;
        while (i < text.length()) {
            int end = Math.min(i + lineLength, text.length());
            wrapped.append(text, i, end).append("\n");
            i = end;
        }
        return wrapped.toString();
    }
}
