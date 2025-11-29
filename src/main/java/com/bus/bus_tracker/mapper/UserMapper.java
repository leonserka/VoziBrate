package com.bus.bus_tracker.mapper;

import com.bus.bus_tracker.dto.UserRegisterDto;
import com.bus.bus_tracker.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", constant = "user")
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "tickets", ignore = true)
    @Mapping(target = "favorites", ignore = true)
    // ⬇⬇⬇ OVO DODANO — eksplicitno mapiranje emaila
    @Mapping(target = "email", source = "email")
    public UserEntity toEntity(UserRegisterDto dto);
}
