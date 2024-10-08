package com.example.pp.scheduling;

import com.example.pp.servise.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ScheduledTasks {

    private final ClientService clientService;

    @Scheduled(cron = "${service-discount.cron}", zone = "${service-discount.zoneId}")
    public void scheduledMessageSendingTask() {
        clientService.sendingMessageToFilteredClients();
    }

    @Scheduled(cron = "0 0 0 1 1,6 *", zone = "${service-discount.zoneId}")
    public void scheduledTaskToUpdateTheStatusOfMessages() {
        clientService.updateStatusOfSendMessage();
    }
}
