package com.example.pp.servise;


public interface ClientService {
    void sendingMessageToFilteredClients();
    void sendingMessageToClientById(String clientId);
    void deleteAllClientsInDB();
    void updateStatusOfSendMessage();
}
