package com.saokanneh.auth.service;

import com.saokanneh.auth.shared.dto.AddressDto;

import java.util.List;

public interface AddressService {
    List<AddressDto> getAddresses(String id);
    AddressDto getAddress(String id);
}
