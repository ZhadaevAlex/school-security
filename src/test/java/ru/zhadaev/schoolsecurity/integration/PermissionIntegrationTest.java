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
import ru.zhadaev.schoolsecurity.api.dto.PermissionDto;
import java.util.LinkedList;
import java.util.List;

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
@WithMockUser(username = "super_admin", password = "superAdminPass",
        authorities = {"PERMISSION_CREATE", "PERMISSION_READ", "PERMISSION_UPDATE", "PERMISSION_DELETE"})
public class PermissionIntegrationTest {

    private final String NAME = "GROUP_CREATE";

    private final MockMvc mockMvc;

    @Nested
    @DisplayName("Tests for finding an permission")
    class FindTest {
        @Test
        void findAll_shouldReturnFirstPageOfListOfTwoValidPermissionDto() throws Exception {
            String name1 = "GROUP_UPDATE";
            String description1 = "Endpoint: groups; operation: update";
            String name2 = "GROUP_DELETE";
            String description2 = "Endpoint: groups; operation: delete";
            List<PermissionDto> expected = new LinkedList<>();
            expected.add(permissionDtoCreate(name1, description1));
            expected.add(permissionDtoCreate(name2, description2));

            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("page", "1");
            params.add("size", "2");

            MvcResult result = mockMvc.perform(get("/api/permissions")
                    .params(params))
                    .andExpect(status().isOk())
                    .andReturn();

            ObjectMapper objectMapper = new ObjectMapper();
            String responseBody = result.getResponse().getContentAsString();
            List<PermissionDto> actual = objectMapper.readValue(responseBody, new TypeReference<>() {
            });
            assertEquals(expected, actual);
        }

        @Test
        void findAll_shouldReturnFirstPageOfListOfTwoSortedValidPermissionDto() throws Exception {
            String name1 = "USER_DELETE";
            String description1 = "Endpoint: users; operation: delete";
            String name2 = "USER_CREATE";
            String description2 = "Endpoint: users; operation: create";
            List<PermissionDto> expected = new LinkedList<>();
            expected.add(permissionDtoCreate(name1, description1));
            expected.add(permissionDtoCreate(name2, description2));

            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("page", "1");
            params.add("size", "2");
            params.add("sort", "name,desc");

            MvcResult result = mockMvc.perform(get("/api/permissions")
                    .params(params))
                    .andExpect(status().isOk())
                    .andReturn();

            ObjectMapper objectMapper = new ObjectMapper();
            String responseBody = result.getResponse().getContentAsString();
            List<PermissionDto> actual = objectMapper.readValue(responseBody, new TypeReference<>() {
            });
            assertEquals(expected, actual);
        }

        @Test
        void findById_shouldReturnValidPermissionDto_whenEntityFoundById() throws Exception {
            String descriprion = "Endpoint: groups; operation: create";
            PermissionDto expected = permissionDtoCreate(NAME, descriprion);

            MvcResult result = mockMvc.perform(get("/api/permissions/{id}", NAME))
                    .andExpect(status().isOk())
                    .andReturn();

            ObjectMapper objectMapper = new ObjectMapper();
            String responseBody = result.getResponse().getContentAsString();
            PermissionDto actual = objectMapper.readValue(responseBody, PermissionDto.class);
            assertEquals(expected, actual);
        }

        @Test
        void findById_shouldReturnNotFoundError_whenEntityNotFoundById() throws Exception {
            String badId = "SCHOOL_CREATE";
            String expectedMsg = "Permission not found by id = " + badId;

            mockMvc.perform(get("/api/permissions/{id}", badId))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$..message").value(expectedMsg));
        }
    }

    @Nested
    @DisplayName("Tests for creation an permission")
    class CreateTest {

        @Test
        void save_shouldReturnValidPermissionDto_whenNameIsValid() throws Exception {
            String savedPermissionName = "SCHOOL_CREATE";
            PermissionDto savedPermission = new PermissionDto();
            savedPermission.setName(savedPermissionName);
            ObjectMapper objectMapper = new ObjectMapper();
            String content = objectMapper.writeValueAsString(savedPermission);

            MvcResult result = mockMvc.perform(post("/api/permissions")
                    .contentType("application/json")
                    .content(content))
                    .andExpect(status().isCreated())
                    .andReturn();

            String responseBody = result.getResponse().getContentAsString();
            PermissionDto actual = objectMapper.readValue(responseBody, PermissionDto.class);
            savedPermission.setName(actual.getName());
            assertEquals(savedPermission, actual);
        }

        @Test
        void save_shouldReturnValidError_whenNameIsNotValid() throws Exception {
            String expectedMsg = "The permission name must not be null and must contain at least one non-whitespace character";
            String savedPermissionName = " ";
            PermissionDto savedPermission = new PermissionDto();
            savedPermission.setName(savedPermissionName);
            ObjectMapper objectMapper = new ObjectMapper();
            String content = objectMapper.writeValueAsString(savedPermission);

            mockMvc.perform(post("/api/permissions")
                    .contentType("application/json")
                    .content(content))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$..message").value(expectedMsg));
        }
    }

    @Nested
    @DisplayName("Tests for update an permission")
    class UpdateTest {

        @Test
        void updatePatch_shouldReturnValidPermissionDto_whenEntityFoundById() throws Exception {
            String updatedPermissionDesc = "Endpoint: groups; operation: save";
            PermissionDto updatedPermission = new PermissionDto();
            updatedPermission.setDescription(updatedPermissionDesc);
            ObjectMapper objectMapper = new ObjectMapper();
            String content = objectMapper.writeValueAsString(updatedPermission);

            MvcResult result = mockMvc.perform(patch("/api/permissions/{id}", NAME)
                    .contentType("application/json")
                    .content(content))
                    .andExpect(status().isAccepted())
                    .andReturn();

            String responseBody = result.getResponse().getContentAsString();
            PermissionDto actual = objectMapper.readValue(responseBody, PermissionDto.class);
            updatedPermission.setName(actual.getName());
            assertEquals(updatedPermission, actual);
        }

        @Test
        void updatePatch_shouldReturnNotFoundError_whenEntityNotFoundById() throws Exception {
            String badId = "SCHOOL_CREATE";
            String expectedMsg = "Permission not found by id = " + badId;
            String updatedPermissionDesc = "Endpoint: schools; operation: create";
            PermissionDto updatedPermission = new PermissionDto();
            updatedPermission.setDescription(updatedPermissionDesc);
            ObjectMapper objectMapper = new ObjectMapper();
            String content = objectMapper.writeValueAsString(updatedPermission);

            mockMvc.perform(patch("/api/permissions/{id}", badId)
                    .contentType("application/json")
                    .content(content))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$..message").value(expectedMsg));
        }
    }

    @Nested
    @DisplayName("Tests for delete an permission")
    class DeleteTest {

        @Test
        void deleteById_shouldReturnOk_whenEntityFoundById() throws Exception {
            mockMvc.perform(delete("/api/permissions/{id}", NAME))
            .andExpect(status().isOk());
        }

        @Test
        void deleteById_shouldReturnNotFoundError_whenEntityNotFoundById() throws Exception {
            String badId = "SCHOOL_CREATE";
            String expectedMsg = "Permission delete error. Permission not found by id = " + badId;
            mockMvc.perform(delete("/api/permissions/{id}", badId))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$..message").value(expectedMsg));
        }

        @Test
        void deleteAll_shouldReturnOk() throws Exception {
            mockMvc.perform(delete("/api/permissions/"))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("Tests for Method Security")
    @WithMockUser(username = "user", password = "userPass")
    class MethodSecurityTest {

        @Test
        void findAll_shouldReturnForbiddenError_WhenIsNoPermission() throws Exception {
           mockMvc.perform(get("/api/permissions"))
                    .andExpect(status().isForbidden());
        }

        @Test
        void findById_shouldReturnForbiddenError_WhenIsNoPermission() throws Exception {
            mockMvc.perform(get("/api/permissions/{id}", NAME))
                    .andExpect(status().isForbidden());
        }

        @Test
        void save_shouldReturnForbiddenError_WhenIsNoPermission() throws Exception {
            String savedPermissionName = "SCHOOL_CREATE";
            PermissionDto savedPermission = new PermissionDto();
            savedPermission.setName(savedPermissionName);
            ObjectMapper objectMapper = new ObjectMapper();
            String content = objectMapper.writeValueAsString(savedPermission);

            mockMvc.perform(post("/api/permissions")
                    .contentType("application/json")
                    .content(content))
                    .andExpect(status().isForbidden());
        }

        @Test
        void updatePatch_shouldReturnForbiddenError_WhenIsNoPermission() throws Exception {
            String updatedPermissionDesc = "Endpoint: groups; operation: save";
            PermissionDto updatedPermission = new PermissionDto();
            updatedPermission.setDescription(updatedPermissionDesc);
            ObjectMapper objectMapper = new ObjectMapper();
            String content = objectMapper.writeValueAsString(updatedPermission);

            mockMvc.perform(patch("/api/permissions/{id}", NAME)
                    .contentType("application/json")
                    .content(content))
                    .andExpect(status().isForbidden());
        }

        @Test
        void deleteById_shouldReturnForbiddenError_WhenIsNoPermission() throws Exception {
            mockMvc.perform(delete("/api/permissions/{id}", NAME))
                    .andExpect(status().isForbidden());
        }

        @Test
        void deleteAll_shouldReturnForbiddenError_WhenIsNoPermission() throws Exception {
            mockMvc.perform(delete("/api/permissions/"))
                    .andExpect(status().isForbidden());
        }
    }

    private PermissionDto permissionDtoCreate(String name, String description) {
        PermissionDto permissionDto = new PermissionDto();
        permissionDto.setName(name);
        permissionDto.setDescription(description);
        return permissionDto;
    }
}
