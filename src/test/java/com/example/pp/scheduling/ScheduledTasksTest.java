package com.example.pp.scheduling;

import com.example.pp.servise.ClientService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ScheduledTasksTest {

    @Mock
    private ClientService clientService;

    @InjectMocks
    private ScheduledTasks scheduledTasks;

    @Test
    public void testScheduledMessageSendingTask() {
        scheduledTasks.scheduledMessageSendingTask();
        verify(clientService, times(1)).sendingMessageToFilteredClients();
    }

    @Test
    public void testScheduledTaskToUpdateTheStatusOfMessages() {
        scheduledTasks.scheduledTaskToUpdateTheStatusOfMessages();
        verify(clientService, times(1)).updateStatusOfSendMessage();
    }
}