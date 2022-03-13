package com.nikolai.education.util;

import com.nikolai.education.dto.OrgDTO;
import com.nikolai.education.dto.UserDTO;
import com.nikolai.education.model.Organization;
import com.nikolai.education.model.User;
import com.nikolai.education.repository.UserRepo;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ConvertDto {

    private final ModelMapper modelMapper;
    private final UserRepo userRepo;

    @Autowired
    public ConvertDto(ModelMapper modelMapper, UserRepo userRepo) {
        this.modelMapper = modelMapper;
        this.userRepo = userRepo;
    }

    public OrgDTO convertOrg(Organization organization) {
        OrgDTO orgDto = modelMapper.map(organization, OrgDTO.class);
        return orgDto;
    }

    public UserDTO convertUser(User user) {
        UserDTO userDto = modelMapper.map(user, UserDTO.class);
        return userDto;
    }
}
