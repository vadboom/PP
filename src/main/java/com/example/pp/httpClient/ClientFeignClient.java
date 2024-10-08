package com.example.pp.httpClient;

import com.example.pp.model.ClientRequestDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "Clients", url = "${Clients.url}")
public interface ClientFeignClient {
    @GetMapping("/api/v1/getClient")
    List<ClientRequestDto> getAllClients();

    @GetMapping("/api/v1/getClient/{clientId}")
    ClientRequestDto getClient(@PathVariable("clientId") String clientId);
}
