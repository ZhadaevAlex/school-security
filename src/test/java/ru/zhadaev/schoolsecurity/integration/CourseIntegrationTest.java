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
import ru.zhadaev.schoolsecurity.api.dto.CourseDto;
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
        authorities = {"COURSE_CREATE", "COURSE_READ", "COURSE_UPDATE", "COURSE_DELETE"})
public class CourseIntegrationTest {

    private final String ID = "1a94740f-cab8-4522-91fa-ad996c72b92d";
    private final String NAME = "Computer science";
    private final String DESCRIPTION = "Subject Computer science";

    private final MockMvc mockMvc;

    @Nested
    @DisplayName("Tests for finding an course")
    class FindTest {
        @Test
        void findAll_shouldReturnFirstPageOfListOfTwoValidCourseDto() throws Exception {
            String id1 = "1565f8d4-35bb-4c48-9045-c91acdb753ec";
            String name1 = "Chemistry";
            String description1 = "Subject Chemistry";
            String id2 = "df5b330d-5f6a-4223-8d0b-b71f45636b9f";
            String name2 = "Literature";
            String description2 = "Subject Literature";
            List<CourseDto> expected = Arrays.asList(
                    courseDtoCreate(id1, name1, description1),
                    courseDtoCreate(id2, name2, description2));

            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("page", "1");
            params.add("size", "2");

            MvcResult result = mockMvc.perform(get("/api/courses")
                    .params(params))
                    .andExpect(status().isOk())
                    .andReturn();

            ObjectMapper objectMapper = new ObjectMapper();
            String responseBody = result.getResponse().getContentAsString();
            List<CourseDto> actual = objectMapper.readValue(responseBody, new TypeReference<>() {
            });
            assertEquals(expected, actual);
        }

        @Test
        void findAll_shouldReturnFirstPageOfListOfTwoSortedValidCourseDto() throws Exception {
            String id1 = "df5b330d-5f6a-4223-8d0b-b71f45636b9f";
            String name1 = "Literature";
            String description1 = "Subject Literature";
            String id2 = "c4b891c8-d3bb-4ac2-8a9b-aa4644230160";
            String name2 = "History";
            String description2 = "Subject History";
            List<CourseDto> expected = new LinkedList<>();
            expected.add(courseDtoCreate(id1, name1, description1));
            expected.add(courseDtoCreate(id2, name2, description2));

            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("page", "1");
            params.add("size", "2");
            params.add("sort", "name,desc");

            MvcResult result = mockMvc.perform(get("/api/courses")
                    .params(params))
                    .andExpect(status().isOk())
                    .andReturn();

            ObjectMapper objectMapper = new ObjectMapper();
            String responseBody = result.getResponse().getContentAsString();
            List<CourseDto> actual = objectMapper.readValue(responseBody, new TypeReference<>() {
            });
            assertEquals(expected, actual);
        }

        @Test
        void findById_shouldReturnValidCourseDto_whenEntityFoundById() throws Exception {
            CourseDto expected = courseDtoCreate(ID, NAME, DESCRIPTION);

            MvcResult result = mockMvc.perform(get("/api/courses/{id}", expected.getId()))
                    .andExpect(status().isOk())
                    .andReturn();

            ObjectMapper objectMapper = new ObjectMapper();
            String responseBody = result.getResponse().getContentAsString();
            CourseDto actual = objectMapper.readValue(responseBody, CourseDto.class);
            assertEquals(expected, actual);
        }

        @Test
        void findById_shouldReturnNotFoundError_whenEntityNotFoundById() throws Exception {
            String badId = "8e2e1511-8105-441f-97e8-5bce88c0267b";
            String expectedMsg = "Course not found by id = " + badId;
            CourseDto expected = courseDtoCreate(badId, NAME, DESCRIPTION);

            mockMvc.perform(get("/api/courses/{id}", expected.getId()))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$..message").value(expectedMsg));
        }
    }

    @Nested
    @DisplayName("Tests for creation an course")
    class CreateTest {

        @Test
        void save_shouldReturnValidCourseDto_whenNameIsValid() throws Exception {
            String savedCourseName = "Physics";
            CourseDto savedCourseDto = new CourseDto();
            savedCourseDto.setName(savedCourseName);
            ObjectMapper objectMapper = new ObjectMapper();
            String content = objectMapper.writeValueAsString(savedCourseDto);

            MvcResult result = mockMvc.perform(post("/api/courses")
                    .contentType("application/json")
                    .content(content))
                    .andExpect(status().isCreated())
                    .andReturn();

            String responseBody = result.getResponse().getContentAsString();
            CourseDto actual = objectMapper.readValue(responseBody, CourseDto.class);
            savedCourseDto.setId(actual.getId());
            assertEquals(savedCourseDto, actual);
        }

        @Test
        void save_shouldReturnValidError_whenNameIsNotValid() throws Exception {
            String expectedMsg = "The course name must not be null and must contain at least one non-whitespace character";
            String savedCourseName = " ";
            CourseDto savedCourseDto = new CourseDto();
            savedCourseDto.setName(savedCourseName);
            ObjectMapper objectMapper = new ObjectMapper();
            String content = objectMapper.writeValueAsString(savedCourseDto);

            mockMvc.perform(post("/api/courses")
                    .contentType("application/json")
                    .content(content))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$..message").value(expectedMsg))
                    .andReturn();
        }
    }

    @Nested
    @DisplayName("Tests for update an course")
    class UpdateTest {

        @Test
        void updatePut_shouldReturnValidCourseDto_whenEntityFoundById() throws Exception {
            String updatedCourseName = "Physics";
            CourseDto updatedCourseDto = new CourseDto();
            updatedCourseDto.setName(updatedCourseName);
            ObjectMapper objectMapper = new ObjectMapper();
            String content = objectMapper.writeValueAsString(updatedCourseDto);

            MvcResult result = mockMvc.perform(put("/api/courses/{id}", ID)
                    .contentType("application/json")
                    .content(content))
                    .andExpect(status().isAccepted())
                    .andReturn();

            String responseBody = result.getResponse().getContentAsString();
            CourseDto actual = objectMapper.readValue(responseBody, CourseDto.class);
            updatedCourseDto.setId(actual.getId());
            assertEquals(updatedCourseDto, actual);
        }

        @Test
        void updatePut_shouldReturnNotFoundError_whenEntityNotFoundById() throws Exception {
            String badId = "8e2e1511-8105-441f-97e8-5bce88c0267b";
            String expectedMsg = "Course replace error. Course not found by id = " + badId;
            String updatedCourseName = "Physics";
            CourseDto updatedCourseDto = new CourseDto();
            updatedCourseDto.setName(updatedCourseName);
            ObjectMapper objectMapper = new ObjectMapper();
            String content = objectMapper.writeValueAsString(updatedCourseDto);

            mockMvc.perform(put("/api/courses/{id}", badId)
                    .contentType("application/json")
                    .content(content))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$..message").value(expectedMsg));
        }

        @Test
        void updatePut_shouldReturnValidError_whenNameIsNotValid() throws Exception {
            String expectedMsg = "The course name must not be null and must contain at least one non-whitespace character";
            String updatedCourseName = " ";
            CourseDto updatedCourseDto = new CourseDto();
            updatedCourseDto.setName(updatedCourseName);
            ObjectMapper objectMapper = new ObjectMapper();
            String content = objectMapper.writeValueAsString(updatedCourseDto);

            mockMvc.perform(put("/api/courses/{id}", ID)
                    .contentType("application/json")
                    .content(content))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$..message").value(expectedMsg));
        }

        @Test
        void updatePatch_shouldReturnValidCourseDto_whenEntityFoundById() throws Exception {
            String updatedCourseName = "Physics";
            String updatedCourseDesc = "Subject Physics";
            CourseDto updatedCourseDto = new CourseDto();
            updatedCourseDto.setName(updatedCourseName);
            updatedCourseDto.setDescription(updatedCourseDesc);
            ObjectMapper objectMapper = new ObjectMapper();
            String content = objectMapper.writeValueAsString(updatedCourseDto);

            MvcResult result = mockMvc.perform(patch("/api/courses/{id}", ID)
                    .contentType("application/json")
                    .content(content))
                    .andExpect(status().isAccepted())
                    .andReturn();

            String responseBody = result.getResponse().getContentAsString();
            CourseDto actual = objectMapper.readValue(responseBody, CourseDto.class);
            updatedCourseDto.setId(actual.getId());
            assertEquals(updatedCourseDto, actual);
        }

        @Test
        void updatePatch_shouldReturnNotFoundError_whenEntityNotFoundById() throws Exception {
            String badId = "8e2e1511-8105-441f-97e8-5bce88c0267b";
            String expectedMsg = "Course not found by id = " + badId;
            String updatedCourseName = "Physics";
            String updatedCourseDesc = "Subject Physics";
            CourseDto updatedCourseDto = new CourseDto();
            updatedCourseDto.setName(updatedCourseName);
            updatedCourseDto.setDescription(updatedCourseDesc);
            ObjectMapper objectMapper = new ObjectMapper();
            String content = objectMapper.writeValueAsString(updatedCourseDto);

            mockMvc.perform(patch("/api/courses/{id}", badId)
                    .contentType("application/json")
                    .content(content))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$..message").value(expectedMsg));
        }

        @Test
        void updatePatch_shouldReturnValidError_whenNameIsNotValid() throws Exception {
            String expectedMsg = "The course name must contain at least one non-whitespace character. Can be null";
            String updatedCourseName = " ";
            CourseDto updatedCourseDto = new CourseDto();
            updatedCourseDto.setName(updatedCourseName);
            ObjectMapper objectMapper = new ObjectMapper();
            String content = objectMapper.writeValueAsString(updatedCourseDto);

            mockMvc.perform(patch("/api/courses/{id}", ID)
                    .contentType("application/json")
                    .content(content))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$..message").value(expectedMsg));
        }
    }

    @Nested
    @DisplayName("Tests for delete an course")
    class DeleteTest {

        @Test
        void deleteById_shouldReturnOk_whenEntityFoundById() throws Exception {
            mockMvc.perform(delete("/api/courses/{id}", ID))
            .andExpect(status().isOk());
        }

        @Test
        void deleteById_shouldReturnNotFoundError_whenEntityNotFoundById() throws Exception {
            String badId = "8e2e1511-8105-441f-97e8-5bce88c0267b";
            String expectedMsg = "Course delete error. Course not found by id = " + badId;
            mockMvc.perform(delete("/api/courses/{id}", badId))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$..message").value(expectedMsg));
        }

        @Test
        void deleteAll_shouldReturnOk() throws Exception {
            mockMvc.perform(delete("/api/courses/"))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("Tests for Method Security")
    @WithMockUser(username = "userName", password = "userPassword")
    class MethodSecurityTest {

        @Test
        void findAll_shouldReturnForbiddenError_WhenIsNoPermission() throws Exception {
           mockMvc.perform(get("/api/courses"))
                    .andExpect(status().isForbidden());
        }

        @Test
        void findById_shouldReturnForbiddenError_WhenIsNoPermission() throws Exception {
            mockMvc.perform(get("/api/courses/{id}", ID))
                    .andExpect(status().isForbidden());
        }

        @Test
        void save_shouldReturnForbiddenError_WhenIsNoPermission() throws Exception {
            String savedCourseName = "Physics";
            CourseDto savedCourseDto = new CourseDto();
            savedCourseDto.setName(savedCourseName);
            ObjectMapper objectMapper = new ObjectMapper();
            String content = objectMapper.writeValueAsString(savedCourseDto);

            mockMvc.perform(post("/api/courses")
                    .contentType("application/json")
                    .content(content))
                    .andExpect(status().isForbidden());
        }

        @Test
        void updatePut_shouldReturnForbiddenError_WhenIsNoPermission() throws Exception {
            String updatedCourseName = "Physics";
            CourseDto updatedCourseDto = new CourseDto();
            updatedCourseDto.setName(updatedCourseName);
            ObjectMapper objectMapper = new ObjectMapper();
            String content = objectMapper.writeValueAsString(updatedCourseDto);

            mockMvc.perform(put("/api/courses/{id}", ID)
                    .contentType("application/json")
                    .content(content))
                    .andExpect(status().isForbidden());
        }

        @Test
        void updatePatch_shouldReturnForbiddenError_WhenIsNoPermission() throws Exception {
            String updatedCourseName = "Physics";
            CourseDto updatedCourseDto = new CourseDto();
            updatedCourseDto.setName(updatedCourseName);
            ObjectMapper objectMapper = new ObjectMapper();
            String content = objectMapper.writeValueAsString(updatedCourseDto);

            mockMvc.perform(patch("/api/courses/{id}", ID)
                    .contentType("application/json")
                    .content(content))
                    .andExpect(status().isForbidden());
        }

        @Test
        void deleteById_shouldReturnForbiddenError_WhenIsNoPermission() throws Exception {
            mockMvc.perform(delete("/api/courses/{id}", ID))
                    .andExpect(status().isForbidden());
        }

        @Test
        void deleteAll_shouldReturnForbiddenError_WhenIsNoPermission() throws Exception {
            mockMvc.perform(delete("/api/courses/"))
                    .andExpect(status().isForbidden());
        }
    }

    private CourseDto courseDtoCreate(String id, String name, String description) {
        CourseDto courseDto = new CourseDto();
        courseDto.setId(UUID.fromString(id));
        courseDto.setName(name);
        courseDto.setDescription(description);
        return courseDto;
    }
}
