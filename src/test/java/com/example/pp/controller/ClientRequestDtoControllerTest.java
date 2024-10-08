package com.example.pp.controller;

import com.example.pp.servise.ClientService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/*Класс ClientControllerTest расширяет MockitoExtension.
 Аннотацию @Mock использую для создания мока для зависимостей контроллера (ClientService),
  а аннотацию @InjectMocks для создания экземпляра контроллера с внедренным моком*/
@ExtendWith(MockitoExtension.class)
public class ClientRequestDtoControllerTest {

    @Mock
    private ClientService clientService;

    @InjectMocks
    private ClientController clientController;

    @Test
    public void testSendingMessageToFilteredClients() {
        clientController.SendingMessageToFilteredClients();
        verify(clientService, times(1)).sendingMessageToFilteredClients();
    }

    @Test
    public void testSendingMessageToClientById() {
        String clientId = "1";
        clientController.SendingMessageToClientById(clientId);
        verify(clientService, times(1)).sendingMessageToClientById(clientId);
    }

    @Test
    public void testDeleteAllClientsInDB() {
        clientController.deleteAllClientsInDB();
        verify(clientService, times(1)).deleteAllClientsInDB();
    }
}
