package com.example.prosecommandement;

import java.util.ArrayList;
import java.util.List;

public class MessageManager {
    private static MessageManager instance;
    private List<Message> messages;

    private MessageManager() {
        messages = new ArrayList<>();
        // Ajouter des messages par défaut
        messages.add(new Message("ID Allié", "La cible est en ligne de mire", "12:30"));
        messages.add(new Message("ID Allié", "Demande autorisation de tirer", "12:32"));
    }

    public static synchronized MessageManager getInstance() {
        if (instance == null) {
            instance = new MessageManager();
        }
        return instance;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void addMessage(Message message) {
        messages.add(message);
    }

    public void reset() {
        messages.clear();
    }
}
