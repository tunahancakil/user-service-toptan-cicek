package com.ttsoftware.userservice.application.mapper;

import com.ttsoftware.userservice.domain.dto.UserDto;
import com.ttsoftware.userservice.domain.entity.User;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-08-05T00:36:26+0300",
    comments = "version: 1.5.5.Final, compiler: IncrementalProcessingEnvironment from gradle-language-java-8.5.jar, environment: Java 17.0.12 (Amazon.com Inc.)"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public List<UserDto> toBankInformationDtoList(List<User> bankInformationList) {
        if ( bankInformationList == null ) {
            return null;
        }

        List<UserDto> list = new ArrayList<UserDto>( bankInformationList.size() );
        for ( User user : bankInformationList ) {
            list.add( userToUserDto( user ) );
        }

        return list;
    }

    protected UserDto userToUserDto(User user) {
        if ( user == null ) {
            return null;
        }

        UserDto userDto = new UserDto();

        userDto.setId( user.getId() );
        userDto.setName( user.getName() );
        userDto.setSurname( user.getSurname() );
        userDto.setEmail( user.getEmail() );
        userDto.setUsername( user.getUsername() );
        userDto.setCreatedDate( user.getCreatedDate() );

        return userDto;
    }
}
