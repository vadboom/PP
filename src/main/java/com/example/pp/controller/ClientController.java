package com.example.pp.controller;

import com.example.pp.servise.ClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/clients")
public class ClientController {
    private final ClientService service;

    @GetMapping()
    public void SendingMessageToFilteredClients() {
        service.sendingMessageToFilteredClients();
    }

    @GetMapping("/{clientId}")
    public void SendingMessageToClientById(@PathVariable String clientId) {
        service.sendingMessageToClientById(clientId);
    }

    @GetMapping("/delete-all")
    public void deleteAllClientsInDB() {
        service.deleteAllClientsInDB();
    }
}
