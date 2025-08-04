package com.ttsoftware.userservice.application.mapper;

import com.ttsoftware.userservice.domain.dto.UserDto;
import com.ttsoftware.userservice.domain.entity.User;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
    List<UserDto> toBankInformationDtoList(List<User> bankInformationList);
}