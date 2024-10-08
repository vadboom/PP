package com.example.pp.jpa.repository;

import com.example.pp.jpa.entity.ClientEntity;
import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ClientDTORepository extends JpaRepository<ClientEntity, String> {
    @Query("SELECT c FROM ClientEntity c WHERE c.messageSend = false")
    List<ClientEntity> getClientsDTOByMessageSendFalse();

    @Query("SELECT c FROM ClientEntity c WHERE c.messageSend = true ")
    List<ClientEntity> getClientsDTOByMessageSendTrue();

    @Modifying
    @Query("UPDATE ClientEntity c SET c.messageSend = true WHERE c.phone = :phone")
    void updateMessageSendTrue(@Param("phone") String phone);

    @Modifying
    @Query("UPDATE ClientEntity c SET c.messageSend = false WHERE c.phone = :phone")
    void updateMessageSendFalse(@Param("phone") String phone);
}
