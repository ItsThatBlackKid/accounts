package com.saokanneh.auth.ui.controller;

import com.saokanneh.auth.exception.UserServiceException;
import com.saokanneh.auth.service.AddressService;
import com.saokanneh.auth.service.UserService;
import com.saokanneh.auth.shared.dto.AddressDto;
import com.saokanneh.auth.shared.dto.UserDto;
import com.saokanneh.auth.ui.model.request.UserDetailsRequestModel;
import com.saokanneh.auth.ui.model.response.*;
import org.hibernate.service.spi.ServiceException;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("users") // http://localhost:8080/users
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    AddressService addressService;

    /**
     * Get a specific user with their id
     *
     * @return returns user with specified id
     */
    @GetMapping(path = "/{id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public UserRest getUser(@PathVariable String id) {
        UserRest returnVal = new UserRest();

        UserDto dto = userService.getUserByUserId(id);

        BeanUtils.copyProperties(dto, returnVal);

        return returnVal;
    }

    @PostMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
    public UserRest createUser(@RequestBody UserDetailsRequestModel userDetails) throws Exception {
        if (userDetails.getEmail() == null || userDetails.getEmail().isEmpty())
            throw new UserServiceException(ErrorMessages.MISSING_REQUIRED_FIELD.getErrorMessage());

        UserRest returnVal = new UserRest();
        ModelMapper modelMapper = new ModelMapper();
        UserDto userDto = modelMapper.map(userDetails, UserDto.class);

        UserDto createdUser = userService.createUser(userDto);
        returnVal = modelMapper.map(createdUser, UserRest.class);
        return returnVal;
    }

    @PutMapping(path = "/{id}")
    public UserRest updateUser(@PathVariable String id, @RequestBody UserDetailsRequestModel userDetails) {

        UserRest returnVal = new UserRest();
        UserDto dto = new UserDto();
        BeanUtils.copyProperties(userDetails, dto);

        UserDto created = userService.updateUser(id, dto);
        BeanUtils.copyProperties(created, returnVal);

        return returnVal;
    }

    @DeleteMapping(path = "/{id}")
    public OperationStatusModel deleteUser(@PathVariable String id) {
        OperationStatusModel model = new OperationStatusModel();
        model.setOperationName(RequestOperationName.DELETE.name());
        model.setOperationResult(RequestOperationStatus.SUCCESS.name());

        userService.deleteUser(id);

        return model;
    }

    @GetMapping
    public List<UserRest> getUsers(@RequestParam(value = "page", defaultValue = "0") int page,
                                   @RequestParam(value = "limit", defaultValue = "25") int limit) {
        List<UserRest> returnVal = new ArrayList<>();

        List<UserDto> userDtos = userService.getUsers(page, limit);

        ModelMapper mapper = new ModelMapper();

        for (UserDto dto : userDtos) {
            UserRest model = mapper.map(dto, UserRest.class);
            returnVal.add(model);
        }

        return returnVal;
    }

    @GetMapping(path = "/{id}/addresses",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public List<AddressRest> getUserAddresses(@PathVariable String id) {

        List<AddressRest> returnVal;

        List<AddressDto> dto = addressService.getAddresses(id);

        ModelMapper mapper = new ModelMapper();

        if (dto == null || dto.isEmpty()) throw new ServiceException(ErrorMessages.NO_RECORD_FOUND.toString());

        Type listType = new TypeToken<List<AddressRest>>() {
        }.getType();
        returnVal = mapper.map(dto, listType);


        return returnVal;
    }
    @GetMapping(path = "/{id}/addresses/{addressId}",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public AddressRest getUserAddress(@PathVariable String id, @PathVariable String addressId) {
        AddressDto dto = addressService.getAddress(addressId);

        AddressRest returnVal = new ModelMapper().map(dto, AddressRest.class);


        return returnVal;
    }

    @GetMapping(path = "/verify", produces = {MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE})
    public OperationStatusModel verifyEmailToken(@RequestParam(value = "token") String token,
                                                 HttpServletResponse response) {
        OperationStatusModel returnVal = new OperationStatusModel();
        returnVal.setOperationName(RequestOperationName.VERIFY_EMAIL.name());

        boolean isVerified = userService.verifyEmailToken(token);
        if(isVerified) {
            returnVal.setOperationResult(RequestOperationStatus.SUCCESS.name());
        } else {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            returnVal.setOperationResult(RequestOperationStatus.ERROR.name());
        }
        returnVal.setOperationResult(isVerified ?
                RequestOperationStatus.SUCCESS.name() : RequestOperationStatus.ERROR.name());


        return returnVal;
    }


}
