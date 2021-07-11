package com.saokanneh.auth.service.impl;

import com.saokanneh.auth.io.entity.AddressEntity;
import com.saokanneh.auth.io.entity.UserEntity;
import com.saokanneh.auth.io.repositories.AddressRepository;
import com.saokanneh.auth.io.repositories.UserRepository;
import com.saokanneh.auth.service.AddressService;
import com.saokanneh.auth.shared.dto.AddressDto;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AddressServiceImpl implements AddressService {
    @Autowired
    AddressRepository addressRepo;

    @Autowired
    UserRepository userRepo;

    @Override
    public List<AddressDto> getAddresses(String id) {
        List<AddressDto> returnVal = new ArrayList<>();

        UserEntity entity = userRepo.findByUserId(id);

        if(entity == null) return  null;
        Iterable<AddressEntity> addresses = addressRepo.findAllByUserDetails(entity);

        for (AddressEntity address : addresses) {
            returnVal.add(new ModelMapper().map(address, AddressDto.class));
        }

        return returnVal;
    }

    @Override
    public AddressDto getAddress(String id) {
        AddressEntity entity = addressRepo.findByAddressId(id);

        if(entity == null) return null;

        return new ModelMapper().map(entity, AddressDto.class);
    }
}
