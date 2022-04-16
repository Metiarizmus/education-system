package com.nikolai.education;

import com.nikolai.education.dto.OrgDTO;
import com.nikolai.education.enums.StatusOrgEnum;
import com.nikolai.education.model.Organization;
import com.nikolai.education.util.ConvertDto;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ConvertDtoTest {

    private ModelMapper modelMapper = new ModelMapper();
    private ConvertDto convertDto = new ConvertDto(modelMapper);

    @Test
    public void testOrgToDto() {
        OrgDTO orgDTO = convertDto.convertOrg(new Organization("English school", "the best", StatusOrgEnum.PUBLIC));
        assertEquals("English school", orgDTO.getName());
        assertEquals("the best", orgDTO.getDescription());
        assertEquals(StatusOrgEnum.PUBLIC, orgDTO.getStatus());
    }

}
