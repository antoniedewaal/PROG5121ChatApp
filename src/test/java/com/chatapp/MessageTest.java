package com.chatapp;

import org.junit.Test;
import static org.junit.Assert.*;

public class MessageTest {

    @Test
    public void testMessageLengthValid() {
        Message msg = new Message();
        String result = msg.validateMessageLength("Hello this is valid.");
        assertEquals("Message ready to send.", result);
    }

    @Test
    public void testMessageLengthTooLong() {
        Message msg = new Message();
        String longText = "A".repeat(260);
        String result = msg.validateMessageLength(longText);
        assertTrue(result.contains("Message exceeds 250 characters"));
    }

    @Test
    public void testValidPhoneNumber() {
        Message msg = new Message();
        String result = msg.validateRecipientCell("+27123456789");
        assertEquals("Cell phone number successfully captured.", result);
    }

    @Test
    public void testInvalidPhoneNumber() {
        Message msg = new Message();
        String result = msg.validateRecipientCell("0812345678");
        assertTrue(result.contains("incorrectly formatted"));
    }
}
