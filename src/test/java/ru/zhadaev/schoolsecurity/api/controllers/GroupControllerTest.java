package ru.zhadaev.schoolsecurity.api.controllers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.zhadaev.schoolsecurity.api.dto.GroupDto;
import ru.zhadaev.schoolsecurity.service.GroupService;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class GroupControllerTest {

    private final String ID_1 = "46fa82ce-4e6d-45ae-a4e4-914971f1eb4f";
    private final String NAME_1 = "YT-80";
    private final String ID_2 = "408a9358-c6b1-4b36-8912-7bbd4803f1b1";
    private final String NAME_2 = "BA-51";
    private final UUID ID1 = UUID.fromString(ID_1);

    @Mock
    private GroupService groupService;

    @InjectMocks
    private GroupController groupController;

    @Test
    void findAll_shouldReturnValidListOfGroupDto() {
        Integer pageNumber = 1;
        Integer size = 2;
        Integer numberStudent = 20;
        Pageable pageable = PageRequest.of(pageNumber, size);
        List<GroupDto> groupsDto = Arrays.asList(
                groupDtoCreate(ID_1, NAME_1),
                groupDtoCreate(ID_2, NAME_2));
        doReturn(groupsDto).when(groupService).findAll(numberStudent, pageable);

        List<GroupDto> actual = groupService.findAll(numberStudent, pageable);
        verify(groupService, times(1)).findAll(numberStudent, pageable);
        assertEquals(groupsDto, actual);
    }

    @Test
    void save_shouldReturnValidGroupDto() {
        GroupDto groupDto = groupDtoCreate(ID_1, NAME_1);
        doReturn(groupDto).when(groupService).save(groupDto);

        GroupDto actual = groupController.save(groupDto);

        verify(groupService, times(1)).save(groupDto);
        assertEquals(groupDto, actual);
    }

    @Test
    void findById_shouldReturnValidGroupDto() {
        GroupDto groupDto = groupDtoCreate(ID_1, NAME_1);
        Mockito.doReturn(groupDto).when(groupService).findById(ID1);

        GroupDto actual = groupController.findById(ID1);

        assertEquals(groupDto, actual);
        verify(groupService, times(1)).findById(ID1);
    }

    @Test
    void deleteById_shouldExecutedOneTime() {
        doNothing().when(groupService).deleteById(ID1);

        groupController.deleteById(ID1);

        verify(groupService, times(1)).deleteById(ID1);
    }

    @Test
    void deleteAll_shouldExecutedOneTime() {
        doNothing().when(groupService).deleteAll();

        groupController.deleteAll();

        verify(groupService, times(1)).deleteAll();
    }

    @Test
    void updatePut_shouldReturnValidGroupDto() {
        GroupDto groupDto = groupDtoCreate(ID_1, NAME_1);
        doReturn(groupDto).when(groupService).updatePut(groupDto, ID1);

        GroupDto actual = groupController.updatePut(groupDto, ID1);

        verify(groupService, times(1)).updatePut(groupDto, ID1);
        assertEquals(groupDto, actual);
    }

    @Test
    void updatePatch_shouldReturnValidGroupDto() {
        GroupDto groupDto = groupDtoCreate(ID_1, NAME_1);
        doReturn(groupDto).when(groupService).updatePatch(groupDto, ID1);

        GroupDto actual = groupController.updatePatch(groupDto, ID1);

        verify(groupService, times(1)).updatePatch(groupDto, ID1);
        assertEquals(groupDto, actual);
    }

    private GroupDto groupDtoCreate(String id, String name) {
        GroupDto groupDto = new GroupDto();
        groupDto.setId(UUID.fromString(id));
        groupDto.setName(name);
        return groupDto;
    }
}