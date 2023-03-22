package ru.zhadaev.schoolsecurity.integration;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import ru.zhadaev.schoolsecurity.api.dto.PermissionDto;
import ru.zhadaev.schoolsecurity.api.dto.UserDto;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
@WithMockUser(username = "super_admin", password = "adminPass",
        authorities = {"USER_CREATE", "USER_READ", "USER_UPDATE", "USER_DELETE"})
public class UserIntegrationTest {

    private final String ID = "f3f6ab13-61b4-48c0-a65b-b03363116190";
    private final String USERNAME = "user";
    private final String RAW_PASSWORD = "userPass";
    private final String PERMISSION_NAME_1 = "COURSE_READ";
    private final String PERMISSION_DESCRIPTION_1 = "Endpoint: courses; operation: read";
    private final String PERMISSION_NAME_2 = "GROUP_READ";
    private final String PERMISSION_DESCRIPTION_2 = "Endpoint: groups; operation: read";
    private final String PERMISSION_NAME_3 = "STUDENT_READ";
    private final String PERMISSION_DESCRIPTION_3 = "Endpoint: students; operation: read";

    private final MockMvc mockMvc;

    @Autowired
    private final PasswordEncoder passwordEncoder;

    @Nested
    @DisplayName("Tests for finding an user")
    class FindTest {
        @Test
        void findAll_shouldReturnZeroPageOfListOfTwoValidUserDto() throws Exception {
            String id1 = "f3f6ab13-61b4-48c0-a65b-b03363116190";
            String username1 = "user";
            String password1 = "$2a$12$qh9xaN766tGARzNL0xOrAuBRF0ZkPMMkvdtKS.NMVCwxOItQv9MLm";
            Set<PermissionDto> permissions1 = new LinkedHashSet<>(Arrays.asList(
                    permissionDtoCreate("COURSE_READ", "Endpoint: courses; operation: read"),
                    permissionDtoCreate("GROUP_READ", "Endpoint: groups; operation: read"),
                    permissionDtoCreate("STUDENT_READ", "Endpoint: students; operation: read")));
            UserDto user1 = userDtoCreate(id1, username1, password1, permissions1);

            String id2 = "17b5254a-e96e-4e85-a6d3-6f2a4b68f16c";
            String username2 = "teacher";
            String password2 = "$2a$12$XWOgJwQlqcjShcivTEGztOoRrE.9WsQBKBUVECj/mW2yBGjv0VgUi";
            Set<PermissionDto> permissions2 = new LinkedHashSet<>(Arrays.asList(
                    permissionDtoCreate("COURSE_READ", "Endpoint: courses; operation: read"),
                    permissionDtoCreate("COURSE_DELETE", "Endpoint: courses; operation: delete"),
                    permissionDtoCreate("GROUP_DELETE", "Endpoint: groups; operation: delete"),
                    permissionDtoCreate("GROUP_UPDATE", "Endpoint: groups; operation: update"),
                    permissionDtoCreate("GROUP_CREATE", "Endpoint: groups; operation: create"),
                    permissionDtoCreate("GROUP_READ", "Endpoint: groups; operation: read"),
                    permissionDtoCreate("COURSE_CREATE", "Endpoint: courses; operation: create"),
                    permissionDtoCreate("COURSE_UPDATE", "Endpoint: courses; operation: update"),
                    permissionDtoCreate("STUDENT_READ", "Endpoint: students; operation: read")));
            UserDto user2 = userDtoCreate(id2, username2, password2, permissions2);

            List<UserDto> expected = new LinkedList<>();
            expected.add(user1);
            expected.add(user2);

            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("page", "0");
            params.add("size", "2");
            params.add("sort", "username,desc");

            MvcResult result = mockMvc.perform(get("/api/users")
                    .params(params))
                    .andExpect(status().isOk())
                    .andReturn();

            ObjectMapper objectMapper = new ObjectMapper();
            String responseBody = result.getResponse().getContentAsString();
            List<UserDto> actual = objectMapper.readValue(responseBody, new TypeReference<>() {
            });
            assertEquals(expected, actual);
        }

        @Test
        void findById_shouldReturnValidUserDto_whenEntityFoundById() throws Exception {
            Set<PermissionDto> permissions = new LinkedHashSet<>(Arrays.asList(
                    permissionDtoCreate(PERMISSION_NAME_1, PERMISSION_DESCRIPTION_1),
                    permissionDtoCreate(PERMISSION_NAME_2, PERMISSION_DESCRIPTION_2),
                    permissionDtoCreate(PERMISSION_NAME_3, PERMISSION_DESCRIPTION_3)));
            String encodePassword = "$2a$12$qh9xaN766tGARzNL0xOrAuBRF0ZkPMMkvdtKS.NMVCwxOItQv9MLm";
            UserDto expected = userDtoCreate(ID, USERNAME, encodePassword, permissions);

            MvcResult result = mockMvc.perform(get("/api/users/{id}", ID))
                    .andExpect(status().isOk())
                    .andReturn();

            ObjectMapper objectMapper = new ObjectMapper();
            String responseBody = result.getResponse().getContentAsString();
            UserDto actual = objectMapper.readValue(responseBody, UserDto.class);
            assertEquals(expected, actual);
        }

        @Test
        void findById_shouldReturnNotFoundError_whenEntityNotFoundById() throws Exception {
            String badId = "8e2e1511-8105-441f-97e8-5bce88c0267b";
            String expectedMsg = "User not found by id = " + badId;

            mockMvc.perform(get("/api/users/{id}", badId))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$..message").value(expectedMsg));
        }
    }

    @Nested
    @DisplayName("Tests for creation an user")
    class CreateTest {

        @Test
        void save_shouldReturnValidUserDto_whenUsernameIsValid() throws Exception {
            String savedUsername = "user2";
            UserDto savedUser = new UserDto();
            savedUser.setUsername(savedUsername);
            savedUser.setPassword(RAW_PASSWORD);
            Set<PermissionDto> permissions = new LinkedHashSet<>(Arrays.asList(
                    permissionDtoCreate(PERMISSION_NAME_1, PERMISSION_DESCRIPTION_1),
                    permissionDtoCreate(PERMISSION_NAME_2, PERMISSION_DESCRIPTION_2),
                    permissionDtoCreate(PERMISSION_NAME_3, PERMISSION_DESCRIPTION_3)));
            savedUser.setPermissions(permissions);
            ObjectMapper objectMapper = new ObjectMapper();
            String content = objectMapper.writeValueAsString(savedUser);

            MvcResult result = mockMvc.perform(post("/api/users")
                    .contentType("application/json")
                    .content(content))
                    .andExpect(status().isCreated())
                    .andReturn();

            String responseBody = result.getResponse().getContentAsString();
            UserDto actual = objectMapper.readValue(responseBody, UserDto.class);
            savedUser.setId(actual.getId());
            assertTrue(passwordEncoder.matches(savedUser.getPassword(), actual.getPassword()));
            savedUser.setPassword(actual.getPassword());
            assertEquals(savedUser, actual);
        }

        @Test
        void save_shouldReturnValidError_whenUsernameIsNotValidAndWhitespace() throws Exception {
            String expectedMsg = "The user's name must not be null and must contain at least one non-whitespace character";
            String savedUsername = "  ";
            UserDto savedUser = new UserDto();
            savedUser.setUsername(savedUsername);
            savedUser.setPassword(RAW_PASSWORD);
            Set<PermissionDto> permissions = new LinkedHashSet<>(Arrays.asList(
                    permissionDtoCreate(PERMISSION_NAME_1, PERMISSION_DESCRIPTION_1),
                    permissionDtoCreate(PERMISSION_NAME_2, PERMISSION_DESCRIPTION_2),
                    permissionDtoCreate(PERMISSION_NAME_3, PERMISSION_DESCRIPTION_3)));
            savedUser.setPermissions(permissions);
            ObjectMapper objectMapper = new ObjectMapper();
            String content = objectMapper.writeValueAsString(savedUser);

            mockMvc.perform(post("/api/users")
                    .contentType("application/json")
                    .content(content))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$..message").value(expectedMsg))
                    .andReturn();
        }

        @Test
        void save_shouldReturnValidError_whenPasswordIsNotValidAndWhitespace() throws Exception {
            String expectedMsg = "The user's password must not be null and must contain at least one non-whitespace character";
            String savedPassword = "  ";
            UserDto savedUser = new UserDto();
            savedUser.setUsername(USERNAME);
            savedUser.setPassword(savedPassword);
            Set<PermissionDto> permissions = new LinkedHashSet<>(Arrays.asList(
                    permissionDtoCreate(PERMISSION_NAME_1, PERMISSION_DESCRIPTION_1),
                    permissionDtoCreate(PERMISSION_NAME_2, PERMISSION_DESCRIPTION_2),
                    permissionDtoCreate(PERMISSION_NAME_3, PERMISSION_DESCRIPTION_3)));
            savedUser.setPermissions(permissions);
            ObjectMapper objectMapper = new ObjectMapper();
            String content = objectMapper.writeValueAsString(savedUser);

            mockMvc.perform(post("/api/users")
                    .contentType("application/json")
                    .content(content))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$..message").value(expectedMsg));
        }

        @Test
        void save_shouldReturnValidError_whenPermissionsIsNotValidAndNull() throws Exception {
            String expectedMsg = "The user's permissions must be not null";
            String savedUsername = "user2";
            UserDto savedUser = new UserDto();
            savedUser.setUsername(savedUsername);
            savedUser.setPassword(RAW_PASSWORD);
            savedUser.setPermissions(null);
            ObjectMapper objectMapper = new ObjectMapper();
            String content = objectMapper.writeValueAsString(savedUser);

            mockMvc.perform(post("/api/users")
                    .contentType("application/json")
                    .content(content))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$..message").value(expectedMsg));
        }
    }

    @Nested
    @DisplayName("Tests for update an user")
    class UpdateTest {

        @Test
        void updatePut_shouldReturnValidUserDto_whenEntityFoundById() throws Exception {
            String updatedUsername = "user3";
            String updatedPassword = "userPass";
            UserDto updatedUser = new UserDto();
            updatedUser.setUsername(updatedUsername);
            updatedUser.setPassword(updatedPassword);
            Set<PermissionDto> permissions = new LinkedHashSet<>(Arrays.asList(
                    permissionDtoCreate(PERMISSION_NAME_1, PERMISSION_DESCRIPTION_1),
                    permissionDtoCreate(PERMISSION_NAME_2, PERMISSION_DESCRIPTION_2),
                    permissionDtoCreate(PERMISSION_NAME_3, PERMISSION_DESCRIPTION_3)));
            updatedUser.setPermissions(permissions);
            ObjectMapper objectMapper = new ObjectMapper();
            String content = objectMapper.writeValueAsString(updatedUser);

            MvcResult result = mockMvc.perform(put("/api/users/{id}", ID)
                    .contentType("application/json")
                    .content(content))
                    .andExpect(status().isAccepted())
                    .andReturn();

            String responseBody = result.getResponse().getContentAsString();
            UserDto actual = objectMapper.readValue(responseBody, UserDto.class);
            updatedUser.setId(actual.getId());
            assertTrue(passwordEncoder.matches(updatedUser.getPassword(), actual.getPassword()));
            updatedUser.setPassword(actual.getPassword());
            assertEquals(updatedUser, actual);
        }

        @Test
        void updatePut_shouldReturnNotFoundError_whenEntityNotFoundById() throws Exception {
            String badId = "8e2e1511-8105-441f-97e8-5bce88c0267b";
            String expectedMsg = "User replace error. User not found by id = " + badId;
            String updatedUsername = "user3";
            String updatedPassword = "userPass";
            UserDto updatedUser = new UserDto();
            updatedUser.setUsername(updatedUsername);
            updatedUser.setPassword(updatedPassword);
            Set<PermissionDto> permissions = new LinkedHashSet<>(Arrays.asList(
                    permissionDtoCreate(PERMISSION_NAME_1, PERMISSION_DESCRIPTION_1),
                    permissionDtoCreate(PERMISSION_NAME_2, PERMISSION_DESCRIPTION_2),
                    permissionDtoCreate(PERMISSION_NAME_3, PERMISSION_DESCRIPTION_3)));
            updatedUser.setPermissions(permissions);
            ObjectMapper objectMapper = new ObjectMapper();
            String content = objectMapper.writeValueAsString(updatedUser);

            mockMvc.perform(put("/api/users/{id}", badId)
                    .contentType("application/json")
                    .content(content))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$..message").value(expectedMsg));
        }

        @Test
        void updatePut_shouldReturnValidError_whenUsernameIsNotValidAndWhitespace() throws Exception {
            String expectedMsg = "The user's name must not be null and must contain at least one non-whitespace character";
            String updatedUsername = "  ";
            String updatedPassword = "userPass";
            UserDto updatedUser = new UserDto();
            updatedUser.setUsername(updatedUsername);
            updatedUser.setPassword(updatedPassword);
            Set<PermissionDto> permissions = new LinkedHashSet<>(Arrays.asList(
                    permissionDtoCreate(PERMISSION_NAME_1, PERMISSION_DESCRIPTION_1),
                    permissionDtoCreate(PERMISSION_NAME_2, PERMISSION_DESCRIPTION_2),
                    permissionDtoCreate(PERMISSION_NAME_3, PERMISSION_DESCRIPTION_3)));
            updatedUser.setPermissions(permissions);
            ObjectMapper objectMapper = new ObjectMapper();
            String content = objectMapper.writeValueAsString(updatedUser);

            mockMvc.perform(put("/api/users/{id}", ID)
                    .contentType("application/json")
                    .content(content))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$..message").value(expectedMsg));
        }

        @Test
        void updatePut_shouldReturnValidError_whenPasswordIsNotValidAndWhitespace() throws Exception {
            String expectedMsg = "The user's password must not be null and must contain at least one non-whitespace character";
            String updatedUsername = "user2";
            String updatedPassword = "  ";
            UserDto updatedUser = new UserDto();
            updatedUser.setUsername(updatedUsername);
            updatedUser.setPassword(updatedPassword);
            Set<PermissionDto> permissions = new LinkedHashSet<>(Arrays.asList(
                    permissionDtoCreate(PERMISSION_NAME_1, PERMISSION_DESCRIPTION_1),
                    permissionDtoCreate(PERMISSION_NAME_2, PERMISSION_DESCRIPTION_2),
                    permissionDtoCreate(PERMISSION_NAME_3, PERMISSION_DESCRIPTION_3)));
            updatedUser.setPermissions(permissions);
            ObjectMapper objectMapper = new ObjectMapper();
            String content = objectMapper.writeValueAsString(updatedUser);

            mockMvc.perform(put("/api/users/{id}", ID)
                    .contentType("application/json")
                    .content(content))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$..message").value(expectedMsg));
        }

        @Test
        void updatePut_shouldReturnValidError_whenPermissionsIsNotValidAndNull() throws Exception {
            String expectedMsg = "The user's permissions must be not null";
            String updatedUsername = "user2";
            UserDto updatedUser = new UserDto();
            updatedUser.setUsername(updatedUsername);
            updatedUser.setPassword(RAW_PASSWORD);
            updatedUser.setPermissions(null);
            ObjectMapper objectMapper = new ObjectMapper();
            String content = objectMapper.writeValueAsString(updatedUser);

            mockMvc.perform(put("/api/users/{id}", ID)
                    .contentType("application/json")
                    .content(content))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$..message").value(expectedMsg));
        }

        @Test
        void updatePatch_shouldReturnValidUserDto_whenEntityFoundByIdAndUpdatePassword() throws Exception {
            String updatedPassword = "userPass2";
            UserDto updatedUser = new UserDto();
            updatedUser.setPassword(updatedPassword);
            ObjectMapper objectMapper = new ObjectMapper();
            String content = objectMapper.writeValueAsString(updatedUser);
            Set<PermissionDto> permissions = new LinkedHashSet<>(Arrays.asList(
                    permissionDtoCreate(PERMISSION_NAME_1, PERMISSION_DESCRIPTION_1),
                    permissionDtoCreate(PERMISSION_NAME_2, PERMISSION_DESCRIPTION_2),
                    permissionDtoCreate(PERMISSION_NAME_3, PERMISSION_DESCRIPTION_3)));
            UserDto expected = userDtoCreate(ID, USERNAME, updatedPassword, permissions);

            MvcResult result = mockMvc.perform(patch("/api/users/{id}", ID)
                    .contentType("application/json")
                    .content(content))
                    .andExpect(status().isAccepted())
                    .andReturn();

            String responseBody = result.getResponse().getContentAsString();
            UserDto actual = objectMapper.readValue(responseBody, UserDto.class);
            assertTrue(passwordEncoder.matches(updatedUser.getPassword(), actual.getPassword()));
            expected.setPassword(actual.getPassword());
            assertEquals(expected, actual);
        }

        @Test
        void updatePatch_shouldReturnValidUserDto_whenEntityFoundByIdAndUpdatePermissions() throws Exception {
            String updatedPermissionName1 = "STUDENT_CREATE";
            String updatedPermissionDescription1 = "Endpoint: students; operation: create";
            String updatedPermissionName2 = "STUDENT_UPDATE";
            String updatedPermissionDescription2 = "Endpoint: students; operation: update";
            String updatedPermissionName3 = "STUDENT_DELETE";
            String updatedPermissionDescription3 = "Endpoint: students; operation: delete";
            Set<PermissionDto> updatedPermissions = new LinkedHashSet<>(Arrays.asList(
                    permissionDtoCreate(updatedPermissionName1, updatedPermissionDescription1),
                    permissionDtoCreate(updatedPermissionName2, updatedPermissionDescription2),
                    permissionDtoCreate(updatedPermissionName3, updatedPermissionDescription3)));
            UserDto updatedUserDto = new UserDto();
            updatedUserDto.setPermissions(updatedPermissions);
            ObjectMapper objectMapper = new ObjectMapper();
            String content = objectMapper.writeValueAsString(updatedUserDto);
            UserDto expected = userDtoCreate(ID, USERNAME, RAW_PASSWORD, updatedPermissions);

            MvcResult result = mockMvc.perform(patch("/api/users/{id}", ID)
                    .contentType("application/json")
                    .content(content))
                    .andExpect(status().isAccepted())
                    .andReturn();

            String responseBody = result.getResponse().getContentAsString();
            UserDto actual = objectMapper.readValue(responseBody, UserDto.class);
            assertTrue(passwordEncoder.matches(expected.getPassword(), actual.getPassword()));
            expected.setPassword(actual.getPassword());
            assertEquals(expected, actual);
        }

        @Test
        void updatePatch_shouldReturnNotFoundError_whenEntityNotFoundById() throws Exception {
            String badId = "8e2e1511-8105-441f-97e8-5bce88c0267b";
            String expectedMsg = "User not found by id = " + badId;
            UserDto updatedUserDto = new UserDto();
            ObjectMapper objectMapper = new ObjectMapper();
            String content = objectMapper.writeValueAsString(updatedUserDto);

            mockMvc.perform(patch("/api/users/{id}", badId)
                    .contentType("application/json")
                    .content(content))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$..message").value(expectedMsg));
        }

        @Test
        void updatePatch_shouldReturnValidError_whenUsernameIsNotValidAndWhitespace() throws Exception {
            String expectedMsg = "The user's name must contain at least one non-whitespace character. Can be null";
            String updatedUsername = "  ";
            UserDto updatedUserDto = new UserDto();
            updatedUserDto.setUsername(updatedUsername);
            ObjectMapper objectMapper = new ObjectMapper();
            String content = objectMapper.writeValueAsString(updatedUserDto);

            mockMvc.perform(patch("/api/users/{id}", ID)
                    .contentType("application/json")
                    .content(content))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$..message").value(expectedMsg));
        }

        @Test
        void updatePatch_shouldReturnValidError_whenPasswordIsNotValidAndWhitespace() throws Exception {
            String expectedMsg = "The user's password must contain at least one non-whitespace character. Can be null";
            String updatedPassword = "  ";
            UserDto updatedUserDto = new UserDto();
            updatedUserDto.setPassword(updatedPassword);
            ObjectMapper objectMapper = new ObjectMapper();
            String content = objectMapper.writeValueAsString(updatedUserDto);

            mockMvc.perform(patch("/api/users/{id}", ID)
                    .contentType("application/json")
                    .content(content))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$..message").value(expectedMsg));
        }
    }

    @Nested
    @DisplayName("Tests for delete an user")
    class DeleteTest {

        @Test
        void deleteById_shouldReturnOk_whenEntityFoundById() throws Exception {
            mockMvc.perform(delete("/api/users/{id}", ID))
                    .andExpect(status().isOk());
        }

        @Test
        void deleteById_shouldReturnNotFoundError_whenEntityNotFoundById() throws Exception {
            String badId = "8e2e1511-8105-441f-97e8-5bce88c0267b";
            String expectedMsg = "User delete error. User not found by id = " + badId;
            mockMvc.perform(delete("/api/users/{id}", badId))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$..message").value(expectedMsg));
        }

        @Test
        void deleteAll_shouldReturnOk() throws Exception {
            mockMvc.perform(delete("/api/users/"))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("Tests for Method Security")
    @WithMockUser(username = "userName", password = "userPassword")
    class MethodSecurityTest {

        @Test
        void findAll_shouldReturnForbiddenError_WhenIsNoPermission() throws Exception {
            mockMvc.perform(get("/api/users"))
                    .andExpect(status().isForbidden());
        }

        @Test
        void findById_shouldReturnForbiddenError_WhenIsNoPermission() throws Exception {
            mockMvc.perform(get("/api/users/{id}", ID))
                    .andExpect(status().isForbidden());
        }

        @Test
        void save_shouldReturnForbiddenError_WhenIsNoPermission() throws Exception {
            String savedUsername = "user2";
            UserDto savedUser = new UserDto();
            savedUser.setUsername(savedUsername);
            savedUser.setPassword(RAW_PASSWORD);
            Set<PermissionDto> permissions = new LinkedHashSet<>(Arrays.asList(
                    permissionDtoCreate(PERMISSION_NAME_1, PERMISSION_DESCRIPTION_1),
                    permissionDtoCreate(PERMISSION_NAME_2, PERMISSION_DESCRIPTION_2),
                    permissionDtoCreate(PERMISSION_NAME_3, PERMISSION_DESCRIPTION_3)));
            savedUser.setPermissions(permissions);
            ObjectMapper objectMapper = new ObjectMapper();
            String content = objectMapper.writeValueAsString(savedUser);

            mockMvc.perform(post("/api/users")
                    .contentType("application/json")
                    .content(content))
                    .andExpect(status().isForbidden());
        }

        @Test
        void updatePut_shouldReturnForbiddenError_WhenIsNoPermission() throws Exception {
            String updatedUsername = "user3";
            String updatedPassword = "userPass";
            UserDto updatedUser = new UserDto();
            updatedUser.setUsername(updatedUsername);
            updatedUser.setPassword(updatedPassword);
            Set<PermissionDto> permissions = new LinkedHashSet<>(Arrays.asList(
                    permissionDtoCreate(PERMISSION_NAME_1, PERMISSION_DESCRIPTION_1),
                    permissionDtoCreate(PERMISSION_NAME_2, PERMISSION_DESCRIPTION_2),
                    permissionDtoCreate(PERMISSION_NAME_3, PERMISSION_DESCRIPTION_3)));
            updatedUser.setPermissions(permissions);
            ObjectMapper objectMapper = new ObjectMapper();
            String content = objectMapper.writeValueAsString(updatedUser);

            mockMvc.perform(put("/api/users/{id}", ID)
                    .contentType("application/json")
                    .content(content))
                    .andExpect(status().isForbidden());
        }

        @Test
        void updatePatch_shouldReturnForbiddenError_WhenIsNoPermission() throws Exception {
            String updatedPassword = "userPass2";
            UserDto updatedUser = new UserDto();
            updatedUser.setPassword(updatedPassword);
            ObjectMapper objectMapper = new ObjectMapper();
            String content = objectMapper.writeValueAsString(updatedUser);

            mockMvc.perform(patch("/api/users/{id}", ID)
                    .contentType("application/json")
                    .content(content))
                    .andExpect(status().isForbidden());
        }

        @Test
        void deleteById_shouldReturnForbiddenError_WhenIsNoPermission() throws Exception {
            mockMvc.perform(delete("/api/users/{id}", ID))
                    .andExpect(status().isForbidden());
        }

        @Test
        void deleteAll_shouldReturnForbiddenError_WhenIsNoPermission() throws Exception {
            mockMvc.perform(delete("/api/users/"))
                    .andExpect(status().isForbidden());
        }
    }

    private PermissionDto permissionDtoCreate(String name, String description) {
        PermissionDto permissionDto = new PermissionDto();
        permissionDto.setName(name);
        permissionDto.setDescription(description);
        return permissionDto;
    }

    private UserDto userDtoCreate(String id, String username, String password, Set<PermissionDto> permissions) {
        UserDto userDto = new UserDto();
        userDto.setId(UUID.fromString(id));
        userDto.setUsername(username);
        userDto.setPassword(password);
        userDto.setPermissions(permissions);
        return userDto;
    }
}
