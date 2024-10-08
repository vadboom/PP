package com.example.pp.servise.Impl;


import com.example.pp.httpClient.ClientFeignClient;
import com.example.pp.jpa.entity.ClientEntity;
import com.example.pp.mapper.ClientDtoMapper;
import com.example.pp.model.ClientRequestDto;
import com.example.pp.model.MessageCreateModel;
import com.example.pp.producer.KafkaProducer;
import com.example.pp.jpa.repository.ClientDTORepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class InClientServiceImplTestRequestDto {

    @Mock
    private ClientDTORepository repository;

    @Mock
    private ClientFeignClient clientFeignClient;

    @Mock
    private ClientDtoMapper mapper;

    @Mock
    private KafkaProducer kafkaProducer;

    @InjectMocks
    private InClientServiceImpl service;

    private List<ClientRequestDto> clientRequestDtos;
    private List<ClientEntity> clientEntities;

    @BeforeEach
    public void setUp() {
        ReflectionTestUtils.setField(service, "zoneId", "Europe/Moscow");
        ReflectionTestUtils.setField(service, "lastDigitPhoneNumber", "7");
        ReflectionTestUtils.setField(service, "discountBirthday", "15%");
        ReflectionTestUtils.setField(service, "topic_name", "massageSMS");
        ReflectionTestUtils.setField(service, "deadline", LocalTime.of(23, 30));

        clientRequestDtos = new ArrayList<>();
        clientEntities = new ArrayList<>();

        Collections.addAll(clientRequestDtos,
                new ClientRequestDto("1", "Akim", "Aleksandrovich", "Parish", 28L, LocalDate.of(2004, 8, 1), "+79586719627"),
                new ClientRequestDto("2", "Vadim", "Alekseevich", "Zharkov", 24L, LocalDate.of(2000, 8, 31), "+79085557957"),
                new ClientRequestDto("3", "Roman", "Romanich", "Dovzhenko", 36L, LocalDate.of(1987, 9, 12), "+79968017272"));

        Collections.addAll(clientEntities,
                new ClientEntity("Akim Aleksandrovich Parish", "+79586719627", LocalDate.of(1990, 8, 1), false),
                new ClientEntity("Vadim Vadimich Zharkov", "+79085557957", LocalDate.of(2000, 8, 2), false),
                new ClientEntity("Roman Romanich Dovzhenko", "+79968017272", LocalDate.of(1987, 9, 12), true));

        when(clientFeignClient.getAllClients()).thenReturn(clientRequestDtos);
        when(mapper.clientToClientDTO(any(ClientRequestDto.class))).thenReturn(clientEntities.get(0), clientEntities.get(1));
    }

    @Test
    public void testSendingMessageToFilteredClients() {
        when(repository.existsById(anyString())).thenReturn(false);
        List<ClientEntity> clientsDTOByMessageSendFalse = clientEntities.stream().filter(ClientDTO -> !ClientDTO.isMessageSend()).toList();
        when(repository.getClientsDTOByMessageSendFalse()).thenReturn(clientsDTOByMessageSendFalse);

        service.sendingMessageToFilteredClients();

        verify(repository, times(2)).save(any(ClientEntity.class));
        verify(kafkaProducer, times(2)).sendMessage(anyString(), any(MessageCreateModel.class), any(LocalTime.class), any(LocalTime.class));
        verify(repository, times(2)).updateMessageSendTrue(anyString());
    }

    @Test
    public void testSendingMessageToClientById() {
        when(clientFeignClient.getClient("1")).thenReturn(clientRequestDtos.get(0));
        when(repository.existsById("+79586719627")).thenReturn(false);

        service.sendingMessageToClientById("1");

        verify(repository, times(1)).save(any(ClientEntity.class));
        verify(kafkaProducer, times(1)).sendMessage(anyString(), any(MessageCreateModel.class), any(LocalTime.class), any(LocalTime.class));
        verify(repository, times(1)).updateMessageSendTrue(anyString());
    }

    @Test
    public void testUpdateStatusOfSendMessage() {

        List<ClientEntity> clientDTOsWithMessageSendTrue = clientEntities.stream()
                .filter(com.example.pp.jpa.entity.ClientEntity::isMessageSend)
                .toList();

        when(repository.getClientsDTOByMessageSendTrue()).thenReturn(clientDTOsWithMessageSendTrue);

        service.updateStatusOfSendMessage();

        verify(repository, times(1)).updateMessageSendFalse(anyString());
    }

    @Test
    public void testDeleteAllClientsInDB() {
        service.deleteAllClientsInDB();
        verify(repository, times(1)).deleteAll();
    }
}
