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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
@Sql(
        scripts = {"classpath:schemaDropIntegrationTest.sql"},
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
)
@WithMockUser(username = "adminName", password = "adminPass",
        authorities = {"GROUP_CREATE", "GROUP_READ", "GROUP_UPDATE", "GROUP_DELETE"})
public class GroupIntegrationTest {

    private final String ID = "46fa82ce-4e6d-45ae-a4e4-914971f1eb4f";
    private final String NAME = "YT-80";

    private final MockMvc mockMvc;

    @Nested
    @DisplayName("Tests for finding an group")
    class FindTest {
        @Test
        void findAll_shouldReturnFirstPageOfListOfTwoValidGroupDto_whenNumberStudentsIsNull() throws Exception {
            String id1 = "d73d111e-7159-48e5-9247-e050db9e0437";
            String name1 = "OU-60";
            String id2 = "10562d54-0acc-4fbf-baba-98c0aa77a900";
            String name2 = "SV-51";
            List<GroupDto> expected = new LinkedList<>();
            expected.add(groupDtoCreate(id1, name1));
            expected.add(groupDtoCreate(id2, name2));

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
            String id1 = "6fa657c1-e8e6-405c-94d5-7314ad758039";
            String name1 = "TR-49";
            String id2 = "8e2e1511-8105-441f-97e8-5bce88c0267a";
            String name2 = "UZ-48";
            List<GroupDto> expected = new LinkedList<>();
            expected.add(groupDtoCreate(id2, name2));
            expected.add(groupDtoCreate(id1, name1));

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
            String id = "0cbb0226-1ae7-4c93-8bd9-e49766ead6a4";
            String name = "BG-24";
            List<GroupDto> expected = Collections.singletonList(
                    groupDtoCreate(id, name));

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
            GroupDto expected = groupDtoCreate(ID, NAME);

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
            GroupDto expected = groupDtoCreate(badId, NAME);

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

            MvcResult result = mockMvc.perform(put("/api/groups/{id}", ID)
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

            mockMvc.perform(put("/api/groups/{id}", ID)
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

            MvcResult result = mockMvc.perform(patch("/api/groups/{id}", ID)
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

            mockMvc.perform(patch("/api/groups/{id}", ID)
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
            mockMvc.perform(delete("/api/groups/{id}", ID))
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
    @WithMockUser(username = "userName", password = "userPassword")
    class MethodSecurityTest {

        @Test
        void findAll_shouldReturnForbiddenError_WhenIsNoPermission() throws Exception {
            mockMvc.perform(get("/api/groups"))
                    .andExpect(status().isForbidden());
        }

        @Test
        void findById_shouldReturnForbiddenError_WhenIsNoPermission() throws Exception {
            mockMvc.perform(get("/api/groups/{id}", ID))
                    .andExpect(status().isForbidden());
        }

        @Test
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
        void updatePut_shouldReturnForbiddenError_WhenIsNoPermission() throws Exception {
            String updatedGroupName = "R2-95";
            GroupDto updatedGroupDto = new GroupDto();
            updatedGroupDto.setName(updatedGroupName);
            ObjectMapper objectMapper = new ObjectMapper();
            String content = objectMapper.writeValueAsString(updatedGroupDto);

            mockMvc.perform(put("/api/groups/{id}", ID)
                    .contentType("application/json")
                    .content(content))
                    .andExpect(status().isForbidden());
        }

        @Test
        void updatePatch_shouldReturnForbiddenError_WhenIsNoPermission() throws Exception {
            String updatedGroupName = "R2-95";
            GroupDto updatedGroupDto = new GroupDto();
            updatedGroupDto.setName(updatedGroupName);
            ObjectMapper objectMapper = new ObjectMapper();
            String content = objectMapper.writeValueAsString(updatedGroupDto);

            mockMvc.perform(patch("/api/groups/{id}", ID)
                    .contentType("application/json")
                    .content(content))
                    .andExpect(status().isForbidden());
        }

        @Test
        void deleteById_shouldReturnForbiddenError_WhenIsNoPermission() throws Exception {
            mockMvc.perform(delete("/api/groups/{id}", ID))
                    .andExpect(status().isForbidden());
        }

        @Test
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
