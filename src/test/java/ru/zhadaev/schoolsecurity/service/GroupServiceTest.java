package ru.zhadaev.schoolsecurity.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.zhadaev.schoolsecurity.api.dto.GroupDto;
import ru.zhadaev.schoolsecurity.api.mappers.GroupMapper;
import ru.zhadaev.schoolsecurity.dao.entities.Group;
import ru.zhadaev.schoolsecurity.dao.repositories.GroupRepository;
import ru.zhadaev.schoolsecurity.exception.NotFoundException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GroupServiceTest {

    private final String ID_1 = "46fa82ce-4e6d-45ae-a4e4-914971f1eb4f";
    private final String NAME_1 = "YT-80";
    private final String ID_2 = "408a9358-c6b1-4b36-8912-7bbd4803f1b1";
    private final String NAME_2 = "BA-51";

    @Mock
    private GroupRepository groupRepository;

    @Mock
    private GroupMapper mapper;

    @InjectMocks
    private GroupService groupService;

    @Test
    void save_shouldReturnValidGroupDto() {
        GroupDto groupDto = groupDtoCreate(ID_1, NAME_1);
        Group group = groupCreate(ID_1, NAME_1);

        doReturn(group).when(groupRepository).save(group);
        doReturn(group).when(mapper).toEntity(groupDto);
        doReturn(groupDto).when(mapper).toDto(group);

        GroupDto actual = groupService.save(groupDto);
        verify(groupRepository, times(1)).save(group);
        verify(mapper, times(1)).toEntity(groupDto);
        verify(mapper, times(1)).toDto(group);
        assertEquals(actual, groupDto);
    }

    @Nested
    @DisplayName("Test for updating an group")
    class UpdateTest {

        @Test
        void updatePut_shouldReturnValidGroupDto_whenEntityFoundById() {
            GroupDto groupDto = groupDtoCreate(ID_1, NAME_1);
            Group group = groupCreate(ID_1, NAME_1);

            doReturn(true).when(groupRepository).existsById(UUID.fromString(ID_1));
            doReturn(group).when(groupRepository).save(group);
            doReturn(group).when(mapper).toEntity(groupDto);
            doReturn(groupDto).when(mapper).toDto(group);

            GroupDto actual = groupService.updatePut(groupDto, UUID.fromString(ID_1));

            verify(groupRepository, times(1)).existsById(UUID.fromString(ID_1));
            verify(groupRepository, times(1)).save(group);
            verify(mapper, times(1)).toEntity(groupDto);
            verify(mapper, times(1)).toDto(group);
            assertEquals(actual, groupDto);
        }

        @Test
        void updatePut_shouldThrowNotFoundException_whenEntityNotFoundById() {
            GroupDto groupDto = groupDtoCreate(ID_1, NAME_1);

            doReturn(false).when(groupRepository).existsById(UUID.fromString(ID_1));

            assertThrows(NotFoundException.class, () -> groupService.updatePut(groupDto, UUID.fromString(ID_1)));
            verify(groupRepository, times(1)).existsById(UUID.fromString(ID_1));
        }

        @Test
        void updatePatch_shouldReturnValidGroupDto_whenEntityFoundById() {
            GroupDto groupDto = groupDtoCreate(ID_1, NAME_1);
            Group group = groupCreate(ID_1, NAME_1);

            doReturn(Optional.of(group)).when(groupRepository).findById(UUID.fromString(ID_1));
            doReturn(group).when(groupRepository).save(group);
            doNothing().when(mapper).update(groupDto, group);
            doReturn(group).when(mapper).toEntity(groupDto);
            doReturn(groupDto).when(mapper).toDto(group);

            GroupDto actual = groupService.updatePatch(groupDto, UUID.fromString(ID_1));

            verify(groupRepository, times(1)).findById(UUID.fromString(ID_1));
            verify(groupRepository, times(1)).save(group);
            verify(mapper, times(1)).toEntity(groupDto);
            verify(mapper, times(1)).update(groupDto, group);
            verify(mapper, times(2)).toDto(group);
            assertEquals(actual, groupDto);
        }

        @Test
        void updatePatch_shouldThrowNotFoundException_whenEntityNotFoundById() {
            GroupDto groupDto = groupDtoCreate(ID_1, NAME_1);

            doReturn(Optional.empty()).when(groupRepository).findById(UUID.fromString(ID_1));

            assertThrows(NotFoundException.class, () -> groupService.updatePatch(groupDto, UUID.fromString(ID_1)));
            verify(groupRepository, times(1)).findById(UUID.fromString(ID_1));
        }
    }

    @Nested
    @DisplayName("Tests for finding an group")
    class FindTest {

        @Test
        void findById_shouldReturnValidGroupDto_whenEntityFoundById() {
            GroupDto groupDto = groupDtoCreate(ID_1, NAME_1);
            Group group = groupCreate(ID_1, NAME_1);

            doReturn(Optional.of(group)).when(groupRepository).findById(UUID.fromString(ID_1));
            doReturn(groupDto).when(mapper).toDto(group);

            GroupDto actual = groupService.findById(UUID.fromString(ID_1));

            verify(groupRepository, times(1)).findById(UUID.fromString(ID_1));
            verify(mapper, times(1)).toDto(group);
            assertEquals(actual, groupDto);
        }

        @Test
        void findById_shouldThrowNotFoundException_whenEntityNotFoundById() {
            Group group = groupCreate(ID_1, NAME_1);

            doReturn(Optional.empty()).when(groupRepository).findById(group.getId());

            assertThrows(NotFoundException.class, () -> groupService.findById(UUID.fromString(ID_1)));
            verify(groupRepository, times(1)).findById(UUID.fromString(ID_1));
        }

        @Test
        void findAll_numberStudentsIsNotNull() {
            Integer pageNumber = 1;
            Integer size = 2;
            Integer numberStudent = 20;
            Pageable pageable = PageRequest.of(pageNumber, size);

            List<GroupDto> groupsDto = Arrays.asList(
                    groupDtoCreate(ID_1, NAME_1),
                    groupDtoCreate(ID_2, NAME_2));

            List<Group> groups = Arrays.asList(
                    groupCreate(ID_1, NAME_1),
                    groupCreate(ID_2, NAME_2));

            doReturn(groups).when(groupRepository).findByNumberStudents(numberStudent, pageable);
            doReturn(groupsDto).when(mapper).toDto(groups);

            List<GroupDto> actual = groupService.findAll(numberStudent, pageable);
            verify(groupRepository, times(1)).findByNumberStudents(numberStudent, pageable);
            assertEquals(actual, groupsDto);
        }

        @Test
        void findAll_numberStudentsIsNull() {
            Integer pageNumber = 1;
            Integer size = 2;
            Integer numberStudent = null;
            Pageable pageable = PageRequest.of(pageNumber, size);

            List<GroupDto> groupsDto = Arrays.asList(
                    groupDtoCreate(ID_1, NAME_1),
                    groupDtoCreate(ID_2, NAME_2));

            List<Group> groups = Arrays.asList(
                    groupCreate(ID_1, NAME_1),
                    groupCreate(ID_2, NAME_2));

            Page<Group> page = new PageImpl<>(groups);

            doReturn(page).when(groupRepository).findAll(pageable);
            doReturn(groupsDto).when(mapper).toDto(groups);

            List<GroupDto> actual = groupService.findAll(numberStudent, pageable);
            verify(groupRepository, times(1)).findAll(pageable);
            assertEquals(actual, groupsDto);
        }

        @Test
        void existsById_shouldReturnTrue_whenEntityFoundById() {
            doReturn(true).when(groupRepository).existsById(UUID.fromString(ID_1));

            Boolean result = groupService.existsById(UUID.fromString(ID_1));

            assertTrue(result);
        }

        @Test
        void existsById_shouldReturnTrue_whenEntityNotFoundById() {
            doReturn(false).when(groupRepository).existsById(UUID.fromString(ID_1));

            Boolean result = groupService.existsById(UUID.fromString(ID_1));

            assertFalse(result);
        }
    }

    @Nested
    @DisplayName("Tests for deleting an group")
    class DeleteTest {

        @Test
        void deleteById_shouldExecutedOneTime_whenEntityFoundById() {
            doReturn(true).when(groupRepository).existsById(UUID.fromString(ID_1));
            doNothing().when(groupRepository).deleteById(UUID.fromString(ID_1));

            groupService.deleteById(UUID.fromString(ID_1));

            verify(groupRepository, times(1)).deleteById(UUID.fromString(ID_1));
        }

        @Test
        void deleteById_shouldThrowNotFoundException_whenEntityNotFoundById() {
            doReturn(false).when(groupRepository).existsById(UUID.fromString(ID_1));

            assertThrows(NotFoundException.class, () -> groupService.deleteById(UUID.fromString(ID_1)));
            verify(groupRepository, times(1)).existsById(UUID.fromString(ID_1));
        }

        @Test
        void delete_shouldExecutedOneTime_whenEntityFoundById() {
            Group group = groupCreate(ID_1, NAME_1);

            doReturn(true).when(groupRepository).existsById(UUID.fromString(ID_1));
            doNothing().when(groupRepository).delete(group);

            groupService.delete(group);

            verify(groupRepository, times(1)).existsById(UUID.fromString(ID_1));
            verify(groupRepository, times(1)).delete(group);
        }

        @Test
        void delete_shouldThrowNotFoundException_whenEntityNotFoundById() {
            Group group = groupCreate(ID_1, NAME_1);

            doReturn(false).when(groupRepository).existsById(UUID.fromString(ID_1));

            assertThrows(NotFoundException.class, () -> groupService.delete(group));
            verify(groupRepository, times(1)).existsById(UUID.fromString(ID_1));
        }

        @Test
        void deleteAll() {
            doNothing().when(groupRepository).deleteAll();

            groupService.deleteAll();

            verify(groupRepository, times(1)).deleteAll();
        }
    }

    @Test
    void count() {
        long expected = 10;
        doReturn(expected).when(groupRepository).count();
        long actual = groupService.count();
        verify(groupRepository, times(1)).count();
        assertEquals(actual, expected);
    }

    private Group groupCreate(String id, String name) {
        Group group = new Group();
        group.setId(UUID.fromString(id));
        group.setName(name);
        return group;
    }

    private GroupDto groupDtoCreate(String id, String name) {
        GroupDto groupDto = new GroupDto();
        groupDto.setId(UUID.fromString(id));
        groupDto.setName(name);
        return groupDto;
    }
}