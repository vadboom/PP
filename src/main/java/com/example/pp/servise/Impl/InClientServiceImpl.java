package com.example.pp.servise.Impl;

import com.example.pp.httpClient.ClientFeignClient;
import com.example.pp.jpa.entity.ClientEntity;
import com.example.pp.mapper.ClientDtoMapper;
import com.example.pp.model.MessageCreateModel;
import com.example.pp.producer.KafkaProducer;
import com.example.pp.jpa.repository.ClientDTORepository;
import com.example.pp.servise.ClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class InClientServiceImpl implements ClientService {
    private final ClientDTORepository repository;
    private final ClientFeignClient clientFeignClient;
    private final ClientDtoMapper mapper;
    private final KafkaProducer kafkaProducer;

    @Value("${service-discount.zoneId}")
    private String zoneId;

    @Value("${service-discount.discountBirthday}")
    private String discountBirthday;

    @Value("${service-discount.discount}")
    private String discount;

    @Value("${service-discount.lastDigitPhoneNumber}")
    private String lastDigitPhoneNumber;

    @Value("${service-discount.deadline}")
    private LocalTime deadline;

    @Value("${service-discount.topic_name}")
    private String topic_name;

    public ZonedDateTime getZonedDateTime() {
        return ZonedDateTime.now(ZoneId.of(zoneId));
    }

    @Override
    @Transactional
    public void sendingMessageToFilteredClients() {

        clientFeignClient.getAllClients().stream().filter(Objects::nonNull).
                filter(client -> client.getPhone().endsWith(lastDigitPhoneNumber)).
                filter(client -> client.getBirthday().getMonth().equals(getZonedDateTime().toLocalDate().getMonth())).
                filter(client -> !repository.existsById(client.getPhone())).
                map(mapper::clientToClientDTO).
                forEach(repository::save);
        log.info("Клиенты были отфильтрованы и сохранены в базе данных.");

        repository.getClientsDTOByMessageSendFalse().forEach(clientDTO -> {
            MessageCreateModel messageCreateModel = new MessageCreateModel();
            messageCreateModel.setPhone(clientDTO.getPhone());
            messageCreateModel.getMessage(clientDTO, discountBirthday);
            kafkaProducer.sendMessage(topic_name, messageCreateModel, getZonedDateTime().toLocalTime(), deadline);
            repository.updateMessageSendTrue(clientDTO.getPhone());
        });

    }

    @Override
    @Transactional
    public void sendingMessageToClientById(String clientId) {
        ClientEntity clientEntity = mapper.clientToClientDTO(clientFeignClient.getClient(clientId));
        if (clientEntity != null
               /* && clientDTO.getPhone().endsWith(lastDigitPhoneNumber)
                && clientDTO.getBirthday().getMonth().equals(getZonedDateTime().toLocalDate().getMonth())
                && !repository.existsById(clientDTO.getPhone())*/) {
            repository.save(clientEntity);
            log.info("Клиент был cохранен в базу данных.");
            MessageCreateModel messageCreateModel = new MessageCreateModel();
            messageCreateModel.setPhone(clientEntity.getPhone());
            messageCreateModel.getMessage(clientEntity, discount);
            kafkaProducer.sendMessage(topic_name, messageCreateModel, getZonedDateTime().toLocalTime(), deadline);
            repository.updateMessageSendTrue(clientEntity.getPhone());
        }
    }

    @Override
    @Transactional
    public void updateStatusOfSendMessage() {
        repository.getClientsDTOByMessageSendTrue().stream().
                filter(clientDTO -> !clientDTO.getBirthday().getMonth().equals(getZonedDateTime().getMonth())).
                forEach(clientDTO -> repository.updateMessageSendFalse(clientDTO.getPhone()));
        log.info("База данных обновлена");
    }

    @Override
    public void deleteAllClientsInDB() {
        repository.deleteAll();
        log.info("Данные из базы данных были удалены.");
    }
}
