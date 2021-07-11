package com.saokanneh.auth.service.impl;

import com.saokanneh.auth.exception.UserServiceException;
import com.saokanneh.auth.io.entity.UserEntity;
import com.saokanneh.auth.io.repositories.UserRepository;
import com.saokanneh.auth.service.UserService;
import com.saokanneh.auth.shared.AmazonSES;
import com.saokanneh.auth.shared.Utils;
import com.saokanneh.auth.shared.dto.AddressDto;
import com.saokanneh.auth.shared.dto.UserDto;
import com.saokanneh.auth.ui.model.response.ErrorMessages;
import org.modelmapper.ModelMapper;
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

    @Autowired
    AmazonSES ses;

    @Override
    public UserDto createUser(UserDto user) {


        if(userRepo.findUserByEmail(user.getEmail()) != null) throw new RuntimeException("Record already exists");

        for(int i = 0; i < user.getAddresses().size(); i++) {
            AddressDto addressDto = user.getAddresses().get(i);
            addressDto.setUserDetails(user);
            addressDto.setAddressId(utils.generateAddressId(30));
            user.getAddresses().set(i, addressDto);
        }

        ModelMapper modelMapper = new ModelMapper();

        UserEntity userEntity = modelMapper.map(user, UserEntity.class);

        String publicUserId = utils.generateUserId(30);
        userEntity.setUserId(publicUserId);
        userEntity.setEncryptedPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        userEntity.setEmailVerificationToken(Utils.generateEmailVerificationToken(publicUserId));
        userEntity.setEmailVerificationStatus(Boolean.FALSE);

        UserEntity storedUser = userRepo.save(userEntity);

        ses.verifyEmail(modelMapper.map(storedUser, UserDto.class));

        UserDto returnVal = modelMapper.map(storedUser, UserDto.class);


        return returnVal;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity userEntity = userRepo.findUserByEmail(email);

        if(userEntity == null) throw new UsernameNotFoundException(email);

//        return new User(userEntity.getEmail(), userEntity.getEncryptedPassword(), new ArrayList<>());
        boolean enabled = userEntity.getEmailVerificationStatus();
        return new User(userEntity.getEmail(), userEntity.getEncryptedPassword(),
                userEntity.getEmailVerificationStatus(), true,
                true,true, new ArrayList<>());
    }

    @Override
    public UserDto getUser(String email) {
        UserEntity userEntity = userRepo.findUserByEmail(email);

        if(userEntity == null) throw new UsernameNotFoundException(email);
        ModelMapper mapper = new ModelMapper();
        UserDto userDto = mapper.map(userEntity, UserDto.class);
        return userDto;
    }

    @Override
    public UserDto getUserByUserId(String userId) {
        UserEntity entity = userRepo.findByUserId(userId);

        if(entity == null) throw new UsernameNotFoundException("User with id '" + userId + "' not found");

        UserDto dto = new ModelMapper().map(entity, UserDto.class);

        return dto;
    }

    @Override
    public UserDto updateUser(String userId, UserDto details) {

        UserEntity entity = userRepo.findByUserId(userId);

        if(entity == null) throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());
        entity.setFirstName(details.getFirstName());
        entity.setLastName(details.getLastName());
        userRepo.save(entity);

        UserDto returnVal = new ModelMapper().map(entity, UserDto.class);

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
            UserDto dto = new ModelMapper().map(entity, UserDto.class);
            returnVal.add(dto);
        }


        return returnVal;
    }

    @Override
    public boolean verifyEmailToken(String token) {
        boolean returnVal = false;
        UserEntity entity = userRepo.findUserByEmailVerificationToken(token);
        if(entity != null) {
            boolean tokenExpired = Utils.hasTokenExpired(token);
            if(!tokenExpired) {
                entity.setEmailVerificationToken(null);
                entity.setEmailVerificationStatus(Boolean.TRUE);
                userRepo.save(entity);
                returnVal = true;
            }
        }
        return returnVal;
    }
}
