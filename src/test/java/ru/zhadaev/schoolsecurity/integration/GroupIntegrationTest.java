package ru.zhadaev.schoolsecurity.integration;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import ru.zhadaev.schoolsecurity.api.dto.GroupDto;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@RequiredArgsConstructor
@ActiveProfiles("test")
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@Sql({
        "classpath:schemaIntegrationTest.sql",
        "classpath:dataIntegrationTest.sql"
})
@WithMockUser(username = "adminName", password = "adminPass",
        authorities = {"GROUP_CREATE", "GROUP_READ", "GROUP_UPDATE", "GROUP_DELETE"})
public class GroupIntegrationTest {

    private final String ID_1 = "d73d111e-7159-48e5-9247-e050db9e0437";
    private final String NAME_1 = "OU-60";

    private final MockMvc mockMvc;

    @Nested
    @DisplayName("Tests for finding an group")
    class FindTest {
        @Test
        void findAll_shouldReturnFirstPageOfListOfTwoValidGroupDto_whenNumberStudentsIsNull() throws Exception {
            String ID_2 = "10562d54-0acc-4fbf-baba-98c0aa77a900";
            String NAME_2 = "SV-51";
            List<GroupDto> expected = Arrays.asList(
                    groupDtoCreate(ID_1, NAME_1),
                    groupDtoCreate(ID_2, NAME_2));

            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("page", "1");
            params.add("size", "2");

            MvcResult result = mockMvc.perform(get("/api/groups")
                    .params(params))
                    .andExpect(status().isOk())
                    .andReturn();

            ObjectMapper objectMapper = new ObjectMapper();
            String responseBody = result.getResponse().getContentAsString();
            List<GroupDto> actual = objectMapper.readValue(responseBody, new TypeReference<>() {
            });
            assertEquals(expected, actual);
        }

        @Test
        void findAll_shouldReturnFirstPageOfListOfTwoSortedValidGroupDto_whenNumberStudentsIsNull() throws Exception {
            String ID_3 = "6fa657c1-e8e6-405c-94d5-7314ad758039";
            String NAME_3 = "TR-49";
            String ID_4 = "8e2e1511-8105-441f-97e8-5bce88c0267a";
            String NAME_4 = "UZ-48";
            List<GroupDto> expected = Stream.of(
                    groupDtoCreate(ID_3, NAME_3),
                    groupDtoCreate(ID_4, NAME_4)).sorted(Comparator.comparing(GroupDto::getName).reversed()).collect(Collectors.toList());

            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("page", "1");
            params.add("size", "2");
            params.add("sort", "name,desc");

            MvcResult result = mockMvc.perform(get("/api/groups")
                    .params(params))
                    .andExpect(status().isOk())
                    .andReturn();

            ObjectMapper objectMapper = new ObjectMapper();
            String responseBody = result.getResponse().getContentAsString();
            List<GroupDto> actual = objectMapper.readValue(responseBody, new TypeReference<>() {
            });
            assertEquals(expected, actual);
        }

        @Test
        void findAll_shouldReturnListOfValidGroupDto_whenNumberStudentsIsNotNull() throws Exception {
            String ID_5 = "0cbb0226-1ae7-4c93-8bd9-e49766ead6a4";
            String NAME_5 = "BG-24";
            List<GroupDto> expected = Collections.singletonList(
                    groupDtoCreate(ID_5, NAME_5));

            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("numberStudents", "10");

            MvcResult result = mockMvc.perform(get("/api/groups")
                    .params(params))
                    .andExpect(status().isOk())
                    .andReturn();

            ObjectMapper objectMapper = new ObjectMapper();
            String responseBody = result.getResponse().getContentAsString();
            List<GroupDto> actual = objectMapper.readValue(responseBody, new TypeReference<>() {
            });
            assertEquals(expected, actual);
        }

        @Test
        void findAll_shouldReturnValidError_whenNumberStudentsIsNotValid() throws Exception {
            String expectedMsg = "The number of students must be greater than or equal to zero";

            mockMvc.perform(get("/api/groups")
                    .param("numberStudents", "-1"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$..message").value(expectedMsg));
        }

        @Test
        void findById_shouldReturnValidGroupDto_whenEntityFoundById() throws Exception {
            GroupDto expected = groupDtoCreate(ID_1, NAME_1);

            MvcResult result = mockMvc.perform(get("/api/groups/{id}", expected.getId()))
                    .andExpect(status().isOk())
                    .andReturn();

            ObjectMapper objectMapper = new ObjectMapper();
            String responseBody = result.getResponse().getContentAsString();
            GroupDto actual = objectMapper.readValue(responseBody, GroupDto.class);
            assertEquals(expected, actual);
        }

        @Test
        void findById_shouldReturnNotFoundError_whenEntityNotFoundById() throws Exception {
            String badId = "8e2e1511-8105-441f-97e8-5bce88c0267b";
            String expectedMsg = "Group not found by id = " + badId;
            GroupDto expected = groupDtoCreate(badId, NAME_1);

            mockMvc.perform(get("/api/groups/{id}", expected.getId()))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$..message").value(expectedMsg));
        }
    }

    @Nested
    @DisplayName("Tests for creation an group")
    class CreateTest {

        @Test
        void save_shouldReturnValidGroupDto_whenNameIsValid() throws Exception {
            String savedGroupName = "R2-95";
            GroupDto savedGroupDto = new GroupDto();
            savedGroupDto.setName(savedGroupName);
            ObjectMapper objectMapper = new ObjectMapper();
            String content = objectMapper.writeValueAsString(savedGroupDto);

            MvcResult result = mockMvc.perform(post("/api/groups")
                    .contentType("application/json")
                    .content(content))
                    .andExpect(status().isCreated())
                    .andReturn();

            String responseBody = result.getResponse().getContentAsString();
            GroupDto actual = objectMapper.readValue(responseBody, GroupDto.class);
            savedGroupDto.setId(actual.getId());
            assertEquals(savedGroupDto, actual);
        }

        @Test
        void save_shouldReturnValidError_whenNameIsNotValid() throws Exception {
            String expectedMsg = "The group name must not be null and must contain at least one non-whitespace character";
            String savedGroupName = " ";
            GroupDto savedGroupDto = new GroupDto();
            savedGroupDto.setName(savedGroupName);
            ObjectMapper objectMapper = new ObjectMapper();
            String content = objectMapper.writeValueAsString(savedGroupDto);

            mockMvc.perform(post("/api/groups")
                    .contentType("application/json")
                    .content(content))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$..message").value(expectedMsg))
                    .andReturn();
        }
    }

    @Nested
    @DisplayName("Tests for update an group")
    class UpdateTest {

        @Test
        void updatePut_shouldReturnValidGroupDto_whenEntityFoundById() throws Exception {
            String updatedGroupName = "R2-95";
            GroupDto updatedGroupDto = new GroupDto();
            updatedGroupDto.setName(updatedGroupName);
            ObjectMapper objectMapper = new ObjectMapper();
            String content = objectMapper.writeValueAsString(updatedGroupDto);

            MvcResult result = mockMvc.perform(put("/api/groups/{id}", ID_1)
                    .contentType("application/json")
                    .content(content))
                    .andExpect(status().isAccepted())
                    .andReturn();

            String responseBody = result.getResponse().getContentAsString();
            GroupDto actual = objectMapper.readValue(responseBody, GroupDto.class);
            updatedGroupDto.setId(actual.getId());
            assertEquals(updatedGroupDto, actual);
        }

        @Test
        void updatePut_shouldReturnNotFoundError_whenEntityNotFoundById() throws Exception {
            String badId = "8e2e1511-8105-441f-97e8-5bce88c0267b";
            String expectedMsg = "Group replace error. Group not found by id = " + badId;
            String updatedGroupName = "R2-95";
            GroupDto updatedGroupDto = new GroupDto();
            updatedGroupDto.setName(updatedGroupName);
            ObjectMapper objectMapper = new ObjectMapper();
            String content = objectMapper.writeValueAsString(updatedGroupDto);

            mockMvc.perform(put("/api/groups/{id}", badId)
                    .contentType("application/json")
                    .content(content))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$..message").value(expectedMsg));
        }

        @Test
        void updatePut_shouldReturnValidError_whenNameIsNotValid() throws Exception {
            String expectedMsg = "The group name must not be null and must contain at least one non-whitespace character";
            String updatedGroupName = " ";
            GroupDto updatedGroupDto = new GroupDto();
            updatedGroupDto.setName(updatedGroupName);
            ObjectMapper objectMapper = new ObjectMapper();
            String content = objectMapper.writeValueAsString(updatedGroupDto);

            mockMvc.perform(put("/api/groups/{id}", ID_1)
                    .contentType("application/json")
                    .content(content))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$..message").value(expectedMsg));
        }

        @Test
        void updatePatch_shouldReturnValidGroupDto_whenEntityFoundById() throws Exception {
            String updatedGroupName = "R2-95";
            GroupDto updatedGroupDto = new GroupDto();
            updatedGroupDto.setName(updatedGroupName);
            ObjectMapper objectMapper = new ObjectMapper();
            String content = objectMapper.writeValueAsString(updatedGroupDto);

            MvcResult result = mockMvc.perform(patch("/api/groups/{id}", ID_1)
                    .contentType("application/json")
                    .content(content))
                    .andExpect(status().isAccepted())
                    .andReturn();

            String responseBody = result.getResponse().getContentAsString();
            GroupDto actual = objectMapper.readValue(responseBody, GroupDto.class);
            updatedGroupDto.setId(actual.getId());
            assertEquals(updatedGroupDto, actual);
        }

        @Test
        void updatePatch_shouldReturnNotFoundError_whenEntityNotFoundById() throws Exception {
            String badId = "8e2e1511-8105-441f-97e8-5bce88c0267b";
            String expectedMsg = "Group not found by id = " + badId;
            String updatedGroupName = "R2-95";
            GroupDto updatedGroupDto = new GroupDto();
            updatedGroupDto.setName(updatedGroupName);
            ObjectMapper objectMapper = new ObjectMapper();
            String content = objectMapper.writeValueAsString(updatedGroupDto);

            mockMvc.perform(patch("/api/groups/{id}", badId)
                    .contentType("application/json")
                    .content(content))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$..message").value(expectedMsg));
        }

        @Test
        void updatePatch_shouldReturnValidError_whenNameIsNotValid() throws Exception {
            String expectedMsg = "The group name must not be null and must contain at least one non-whitespace character";
            String updatedGroupName = " ";
            GroupDto updatedGroupDto = new GroupDto();
            updatedGroupDto.setName(updatedGroupName);
            ObjectMapper objectMapper = new ObjectMapper();
            String content = objectMapper.writeValueAsString(updatedGroupDto);

            mockMvc.perform(patch("/api/groups/{id}", ID_1)
                    .contentType("application/json")
                    .content(content))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$..message").value(expectedMsg));
        }
    }

    @Nested
    @DisplayName("Tests for delete an group")
    class DeleteTest {

        @Test
        void deleteById_shouldReturnOk_whenEntityFoundById() throws Exception {
            mockMvc.perform(delete("/api/groups/{id}", ID_1))
            .andExpect(status().isOk());
        }

        @Test
        void deleteById_shouldReturnNotFoundError_whenEntityNotFoundById() throws Exception {
            String badId = "8e2e1511-8105-441f-97e8-5bce88c0267b";
            String expectedMsg = "Group delete error. Group not found by id = " + badId;
            mockMvc.perform(delete("/api/groups/{id}", badId))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$..message").value(expectedMsg));
        }

        @Test
        void deleteAll_shouldReturnOk() throws Exception {
            mockMvc.perform(delete("/api/groups/"))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("Tests for Method Security")
    class MethodSecurityTest {

        @Test
        @WithMockUser(username = "userName", password = "userPassword")
        void findAll_shouldReturnForbiddenError_WhenIsNoPermission() throws Exception {
           mockMvc.perform(get("/api/groups"))
                    .andExpect(status().isForbidden());
        }

        @Test
        @WithMockUser(username = "userName", password = "userPassword")
        void findById_shouldReturnForbiddenError_WhenIsNoPermission() throws Exception {
            mockMvc.perform(get("/api/groups/{id}", ID_1))
                    .andExpect(status().isForbidden());
        }

        @Test
        @WithMockUser(username = "userName", password = "userPassword")
        void save_shouldReturnForbiddenError_WhenIsNoPermission() throws Exception {
            String savedGroupName = "R2-95";
            GroupDto savedGroupDto = new GroupDto();
            savedGroupDto.setName(savedGroupName);
            ObjectMapper objectMapper = new ObjectMapper();
            String content = objectMapper.writeValueAsString(savedGroupDto);

            mockMvc.perform(post("/api/groups")
                    .contentType("application/json")
                    .content(content))
                    .andExpect(status().isForbidden());
        }

        @Test
        @WithMockUser(username = "userName", password = "userPassword")
        void updatePut_shouldReturnForbiddenError_WhenIsNoPermission() throws Exception {
            String updatedGroupName = "R2-95";
            GroupDto updatedGroupDto = new GroupDto();
            updatedGroupDto.setName(updatedGroupName);
            ObjectMapper objectMapper = new ObjectMapper();
            String content = objectMapper.writeValueAsString(updatedGroupDto);

            mockMvc.perform(put("/api/groups/{id}", ID_1)
                    .contentType("application/json")
                    .content(content))
                    .andExpect(status().isForbidden());
        }

        @Test
        @WithMockUser(username = "userName", password = "userPassword")
        void updatePatch_shouldReturnForbiddenError_WhenIsNoPermission() throws Exception {
            String updatedGroupName = "R2-95";
            GroupDto updatedGroupDto = new GroupDto();
            updatedGroupDto.setName(updatedGroupName);
            ObjectMapper objectMapper = new ObjectMapper();
            String content = objectMapper.writeValueAsString(updatedGroupDto);

            mockMvc.perform(patch("/api/groups/{id}", ID_1)
                    .contentType("application/json")
                    .content(content))
                    .andExpect(status().isForbidden());
        }

        @Test
        @WithMockUser(username = "userName", password = "userPassword")
        void deleteById_shouldReturnForbiddenError_WhenIsNoPermission() throws Exception {
            mockMvc.perform(delete("/api/groups/{id}", ID_1))
                    .andExpect(status().isForbidden());
        }

        @Test
        @WithMockUser(username = "userName", password = "userPassword")
        void deleteAll_shouldReturnForbiddenError_WhenIsNoPermission() throws Exception {
            mockMvc.perform(delete("/api/groups/"))
                    .andExpect(status().isForbidden());
        }
    }

    private GroupDto groupDtoCreate(String id, String name) {
        GroupDto groupDto = new GroupDto();
        groupDto.setId(UUID.fromString(id));
        groupDto.setName(name);
        return groupDto;
    }
}
