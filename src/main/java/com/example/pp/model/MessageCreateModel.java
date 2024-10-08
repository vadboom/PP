package com.example.pp.model;

import com.example.pp.jpa.entity.ClientEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class MessageCreateModel {

    private String phone;
    private String message;

    public String getMessage(ClientEntity clientEntity, String discount) {
        String[] name = clientEntity.getFullName().split(" ");
        message = name[0] + " " + name[1] + ", в этом месяце для вас действует скидка " + discount;
        return message;
    }
}
