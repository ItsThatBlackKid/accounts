package com.saokanneh.auth.service.impl;

import com.saokanneh.auth.exception.UserServiceException;
import com.saokanneh.auth.io.entity.UserEntity;
import com.saokanneh.auth.io.repositories.UserRepository;
import com.saokanneh.auth.service.UserService;
import com.saokanneh.auth.shared.Utils;
import com.saokanneh.auth.shared.dto.UserDto;
import com.saokanneh.auth.ui.model.response.ErrorMessages;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepo;

    @Autowired
    Utils utils;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public UserDto createUser(UserDto user) {


        if(userRepo.findUserByEmail(user.getEmail()) != null) throw new RuntimeException("Record already exists");

        UserEntity userEntity = new UserEntity();
        BeanUtils.copyProperties(user, userEntity);

        String publicUserId = utils.generateUserId(30);
        userEntity.setUserId(publicUserId);
        userEntity.setEncryptedPassword(bCryptPasswordEncoder.encode(user.getPassword()));

        UserEntity storedUser = userRepo.save(userEntity);

        UserDto returnVal = new UserDto();
        BeanUtils.copyProperties(storedUser, returnVal);
        return returnVal;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity userEntity = userRepo.findUserByEmail(email);

        if(userEntity == null) throw new UsernameNotFoundException(email);

        return new User(userEntity.getEmail(), userEntity.getEncryptedPassword(), new ArrayList<>());
    }

    @Override
    public UserDto getUser(String email) {
        UserDto userDto = new UserDto();
        UserEntity userEntity = userRepo.findUserByEmail(email);

        if(userEntity == null) throw new UsernameNotFoundException(email);

        BeanUtils.copyProperties(userEntity, userDto);
        return userDto;
    }

    @Override
    public UserDto getUserByUserId(String userId) {
        UserEntity entity = userRepo.findByUserId(userId);

        if(entity == null) throw new UsernameNotFoundException("User with id '" + userId + "' not found");

        UserDto dto = new UserDto();
        BeanUtils.copyProperties(entity, dto);

        return dto;
    }

    @Override
    public UserDto updateUser(String userId, UserDto details) {
        UserDto returnVal = new UserDto();

        UserEntity entity = userRepo.findByUserId(userId);

        if(entity == null) throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());
        entity.setFirstName(details.getFirstName());
        entity.setLastName(details.getLastName());
        userRepo.save(entity);

        BeanUtils.copyProperties(entity, returnVal);

        return returnVal;
    }

    @Override
    public void deleteUser(String userId) {
        UserEntity entity = userRepo.findByUserId(userId);

        if(entity == null) throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());

        userRepo.delete(entity);
    }

    @Override
    public List<UserDto> getUsers(int page, int limit) {
        List<UserDto> returnVal = new ArrayList<>();

        Pageable pageable = PageRequest.of(page, limit);

        Page<UserEntity> usersPage = userRepo.findAll(pageable);
        List<UserEntity> users = usersPage.getContent();

        for(UserEntity entity: users) {
            UserDto dto = new UserDto();
            BeanUtils.copyProperties(entity,dto);
            returnVal.add(dto);
        }


        return returnVal;
    }
}
