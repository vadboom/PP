package com.example.pp.mapper;

import com.example.pp.jpa.entity.ClientEntity;
import com.example.pp.model.ClientRequestDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface ClientDtoMapper {


    @Mapping(expression = "java(clientRequestDto.getName() + ' ' + clientRequestDto.getMiddleName() + ' ' + clientRequestDto.getSurname())", target = "fullName")
    @Mapping(source = "clientRequestDto.phone", target = "phone")
    @Mapping(source = "clientRequestDto.birthday", target = "birthday")
    @Mapping(ignore = true, target = "messageSend")
    ClientEntity clientToClientDTO(ClientRequestDto clientRequestDto);
}
