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
import ru.zhadaev.schoolsecurity.api.dto.GroupDto;
import ru.zhadaev.schoolsecurity.api.dto.StudentDto;

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
@WithMockUser(username = "admin", password = "adminPass",
        authorities = {"STUDENT_CREATE", "STUDENT_READ", "STUDENT_UPDATE", "STUDENT_DELETE"})
public class StudentIntegrationTest {

    private final String ID = "a46e9a8e-f535-4437-8bbf-c3a7216e74e5";
    private final String FIRST_NAME = "Mia";
    private final String LAST_NAME = "Phillips";
    private final String GROUP_ID = "46fa82ce-4e6d-45ae-a4e4-914971f1eb4f";
    private final String GROUP_NAME = "YT-80";
    private final String COURSE_ID_1 = "d6d50d2f-d3e7-4b74-aff6-4429fcdb51f5";
    private final String COURSE_NAME_1 = "Biology";
    private final String COURSE_DESCRIPTION_1 = "Subject Biology";
    private final String COURSE_ID_2 = "cea9af46-4727-4822-8f06-807efe886f42";
    private final String COURSE_NAME_2 = "Economics";
    private final String COURSE_DESCRIPTION_2 = "Subject Economics";
    private final String COURSE_ID_3 = "1a94740f-cab8-4522-91fa-ad996c72b92d";
    private final String COURSE_NAME_3 = "Computer science";
    private final String COURSE_DESCRIPTION_3 = "Subject Computer science";

    private final MockMvc mockMvc;

    @Nested
    @DisplayName("Tests for finding an student")
    class FindTest {
        @Test
        void findAll_shouldReturnFirstPageOfListOfTwoValidStudentDto_whenNumberCoursesIsNull() throws Exception {
            String id1 = "b0d48471-179e-4ac8-b0f2-741e3eededbd";
            String firstName1 = "George";
            String lastName1 = "Morgan";
            String groupId1 = "46fa82ce-4e6d-45ae-a4e4-914971f1eb4f";
            String groupName1 = "YT-80";
            String courseId1_1 = "51d5f402-7150-4bab-af7c-816bdba735f5";
            String courseName1_1 = "Astronomy";
            String courseDescription1_1 = "Subject Astronomy";
            String courseId1_2 = "acffffde-c76d-4618-9a32-2e79d5cd087e";
            String courseName1_2 = "Music";
            String courseDescription1_2 = "Subject Music";
            String courseId1_3 = "99c44d20-1056-49a7-a3a4-2e86cf4e0688";
            String courseName1_3 = "Botany";
            String courseDescription1_3 = "Subject Botany";

            String id2 = "a242b083-8b95-4d37-a932-5badc4a7053b";
            String firstName2 = "Emily";
            String lastName2 = "Lewis";
            String groupId2 = "46fa82ce-4e6d-45ae-a4e4-914971f1eb4f";
            String groupName2 = "YT-80";
            String courseId2_1 = "1a94740f-cab8-4522-91fa-ad996c72b92d";
            String courseName2_1 = "Computer science";
            String courseDescription2_1 = "Subject Computer science";
            String courseId2_2 = "51d5f402-7150-4bab-af7c-816bdba735f5";
            String courseName2_2 = "Astronomy";
            String courseDescription2_2 = "Subject Astronomy";
            String courseId2_3 = "1565f8d4-35bb-4c48-9045-c91acdb753ec";
            String courseName2_3 = "Chemistry";
            String courseDescription2_3 = "Subject Chemistry";

            GroupDto group1 = groupDtoCreate(groupId1, groupName1);
            Set<CourseDto> courses1 = new LinkedHashSet<>();
            courses1.add(courseDtoCreate(courseId1_1, courseName1_1, courseDescription1_1));
            courses1.add(courseDtoCreate(courseId1_2, courseName1_2, courseDescription1_2));
            courses1.add(courseDtoCreate(courseId1_3, courseName1_3, courseDescription1_3));
            StudentDto student1 = studentDtoCreate(id1, firstName1, lastName1, group1, courses1);

            GroupDto group2 = groupDtoCreate(groupId2, groupName2);
            Set<CourseDto> courses2 = new LinkedHashSet<>();
            courses2.add(courseDtoCreate(courseId2_1, courseName2_1, courseDescription2_1));
            courses2.add(courseDtoCreate(courseId2_2, courseName2_2, courseDescription2_2));
            courses2.add(courseDtoCreate(courseId2_3, courseName2_3, courseDescription2_3));
            StudentDto student2 = studentDtoCreate(id2, firstName2, lastName2, group2, courses2);

            List<StudentDto> expected = new LinkedList<>();
            expected.add(student1);
            expected.add(student2);

            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("page", "1");
            params.add("size", "2");

            MvcResult result = mockMvc.perform(get("/api/students")
                    .params(params))
                    .andExpect(status().isOk())
                    .andReturn();

            ObjectMapper objectMapper = new ObjectMapper();
            String responseBody = result.getResponse().getContentAsString();
            List<StudentDto> actual = objectMapper.readValue(responseBody, new TypeReference<>() {
            });
            assertEquals(expected, actual);
        }

        @Test
        void findAll_shouldReturnFirstPageOfListOfTwoSortedValidStudentDto_whenNumberCoursesIsNull() throws Exception {
            String id1 = "fda95366-c072-40c0-b49c-65073f300bd7";
            String firstName1 = "Harry";
            String lastName1 = "Anderson";
            String courseId1_1 = "acffffde-c76d-4618-9a32-2e79d5cd087e";
            String courseName1_1 = "Music";
            String courseDescription1_1 = "Subject Music";
            String courseId1_2 = "d6d50d2f-d3e7-4b74-aff6-4429fcdb51f5";
            String courseName1_2 = "Biology";
            String courseDescription1_2 = "Subject Biology";

            String id2 = "fd80cfbb-ad6e-41c4-a932-909a2a83e9bc";
            String firstName2 = "Mia";
            String lastName2 = "Baker";
            String groupId2 = "6fa657c1-e8e6-405c-94d5-7314ad758039";
            String groupName2 = "TR-49";
            String courseId2_1 = "1565f8d4-35bb-4c48-9045-c91acdb753ec";
            String courseName2_1 = "Chemistry";
            String courseDescription2_1 = "Subject Chemistry";

            Set<CourseDto> courses1 = new LinkedHashSet<>();
            courses1.add(courseDtoCreate(courseId1_1, courseName1_1, courseDescription1_1));
            courses1.add(courseDtoCreate(courseId1_2, courseName1_2, courseDescription1_2));
            StudentDto student1 = studentDtoCreate(id1, firstName1, lastName1, null, courses1);

            GroupDto group2 = groupDtoCreate(groupId2, groupName2);
            Set<CourseDto> courses2 = new LinkedHashSet<>();
            courses2.add(courseDtoCreate(courseId2_1, courseName2_1, courseDescription2_1));
            StudentDto student2 = studentDtoCreate(id2, firstName2, lastName2, group2, courses2);

            List<StudentDto> expected = new LinkedList<>();
            expected.add(student1);
            expected.add(student2);

            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("page", "1");
            params.add("size", "2");
            params.add("sort", "id,desc");

            MvcResult result = mockMvc.perform(get("/api/students")
                    .params(params))
                    .andExpect(status().isOk())
                    .andReturn();

            ObjectMapper objectMapper = new ObjectMapper();
            String responseBody = result.getResponse().getContentAsString();
            List<StudentDto> actual = objectMapper.readValue(responseBody, new TypeReference<>() {
            });
            assertEquals(expected, actual);
        }

        @Test
        void findAll_shouldReturnListOfValidStudentDto_whenNumberCoursesIsNotNull() throws Exception {
            String id1 = "e479b1a9-4ab0-4fef-839c-3e1c8c254ea5";
            String firstName1 = "Jack";
            String lastName1 = "King";
            String groupId1 = "d73d111e-7159-48e5-9247-e050db9e0437";
            String groupName1 = "OU-60";
            String courseId1_1 = "acffffde-c76d-4618-9a32-2e79d5cd087e";
            String courseName1_1 = "Music";
            String courseDescription1_1 = "Subject Music";
            String courseId1_2 = "cea9af46-4727-4822-8f06-807efe886f42";
            String courseName1_2 = "Economics";
            String courseDescription1_2 = "Subject Economics";

            String id2 = "db9715e3-55f9-4cc2-92c1-6398a5dcddd0";
            String firstName2 = "Oliver";
            String lastName2 = "Wood";
            String groupId2 = "10562d54-0acc-4fbf-baba-98c0aa77a900";
            String groupName2 = "SV-51";
            String courseId2_1 = "c4b891c8-d3bb-4ac2-8a9b-aa4644230160";
            String courseName2_1 = "History";
            String courseDescription2_1 = "Subject History";
            String courseId2_2 = "cea9af46-4727-4822-8f06-807efe886f42";
            String courseName2_2 = "Economics";
            String courseDescription2_2 = "Subject Economics";
            String courseId2_3 = "1565f8d4-35bb-4c48-9045-c91acdb753ec";
            String courseName2_3 = "Chemistry";
            String courseDescription2_3 = "Subject Chemistry";

            GroupDto group1 = groupDtoCreate(groupId1, groupName1);
            Set<CourseDto> courses1 = new LinkedHashSet<>();
            courses1.add(courseDtoCreate(courseId1_1, courseName1_1, courseDescription1_1));
            courses1.add(courseDtoCreate(courseId1_2, courseName1_2, courseDescription1_2));
            StudentDto student1 = studentDtoCreate(id1, firstName1, lastName1, group1, courses1);

            GroupDto group2 = groupDtoCreate(groupId2, groupName2);
            Set<CourseDto> courses2 = new LinkedHashSet<>();
            courses2.add(courseDtoCreate(courseId2_1, courseName2_1, courseDescription2_1));
            courses2.add(courseDtoCreate(courseId2_2, courseName2_2, courseDescription2_2));
            courses2.add(courseDtoCreate(courseId2_3, courseName2_3, courseDescription2_3));
            StudentDto student2 = studentDtoCreate(id2, firstName2, lastName2, group2, courses2);

            List<StudentDto> expected = new LinkedList<>();
            expected.add(student1);
            expected.add(student2);

            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("courseId", "cea9af46-4727-4822-8f06-807efe886f42");
            params.add("page", "1");
            params.add("size", "2");
            params.add("sort", "id,desc");

            MvcResult result = mockMvc.perform(get("/api/students")
                    .params(params))
                    .andExpect(status().isOk())
                    .andReturn();

            ObjectMapper objectMapper = new ObjectMapper();
            String responseBody = result.getResponse().getContentAsString();
            List<StudentDto> actual = objectMapper.readValue(responseBody, new TypeReference<>() {
            });
            assertEquals(expected, actual);
        }

        @Test
        void findById_shouldReturnValidStudentDto_whenEntityFoundById() throws Exception {
            StudentDto expected = studentDtoCreate(ID, FIRST_NAME, LAST_NAME,
                    groupDtoCreate(GROUP_ID, GROUP_NAME),
                    new HashSet<>(Arrays.asList(
                            courseDtoCreate(COURSE_ID_1, COURSE_NAME_1, COURSE_DESCRIPTION_1),
                            courseDtoCreate(COURSE_ID_2, COURSE_NAME_2, COURSE_DESCRIPTION_2),
                            courseDtoCreate(COURSE_ID_3, COURSE_NAME_3, COURSE_DESCRIPTION_3)
                    ))
            );

            MvcResult result = mockMvc.perform(get("/api/students/{id}", expected.getId()))
                    .andExpect(status().isOk())
                    .andReturn();

            ObjectMapper objectMapper = new ObjectMapper();
            String responseBody = result.getResponse().getContentAsString();
            StudentDto actual = objectMapper.readValue(responseBody, StudentDto.class);
            assertEquals(expected, actual);
        }

        @Test
        void findById_shouldReturnNotFoundError_whenEntityNotFoundById() throws Exception {
            String badId = "8e2e1511-8105-441f-97e8-5bce88c0267b";
            String expectedMsg = "Student not found by id = " + badId;
            StudentDto expected = studentDtoCreate(badId, FIRST_NAME, LAST_NAME,
                    groupDtoCreate(GROUP_ID, GROUP_NAME),
                    new HashSet<>(Arrays.asList(
                            courseDtoCreate(COURSE_ID_1, COURSE_NAME_1, COURSE_DESCRIPTION_1),
                            courseDtoCreate(COURSE_ID_2, COURSE_NAME_2, COURSE_DESCRIPTION_2),
                            courseDtoCreate(COURSE_ID_3, COURSE_NAME_3, COURSE_DESCRIPTION_3)
                    ))
            );

            mockMvc.perform(get("/api/students/{id}", expected.getId()))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$..message").value(expectedMsg));
        }
    }

    @Nested
    @DisplayName("Tests for creation an student")
    class CreateTest {

        @Test
        void save_shouldReturnValidStudentDto_whenNameIsValid() throws Exception {
            StudentDto expected = new StudentDto();
            expected.setFirstName(FIRST_NAME);
            expected.setLastName(LAST_NAME);
            expected.setGroup(groupDtoCreate(GROUP_ID, GROUP_NAME));
            expected.setCourses(
                    new HashSet<>(Arrays.asList(
                            courseDtoCreate(COURSE_ID_1, COURSE_NAME_1, COURSE_DESCRIPTION_1),
                            courseDtoCreate(COURSE_ID_2, COURSE_NAME_2, COURSE_DESCRIPTION_2),
                            courseDtoCreate(COURSE_ID_3, COURSE_NAME_3, COURSE_DESCRIPTION_3)
                    ))
            );

            ObjectMapper objectMapper = new ObjectMapper();
            String content = objectMapper.writeValueAsString(expected);

            MvcResult result = mockMvc.perform(post("/api/students")
                    .contentType("application/json")
                    .content(content))
                    .andExpect(status().isCreated())
                    .andReturn();

            String responseBody = result.getResponse().getContentAsString();
            StudentDto actual = objectMapper.readValue(responseBody, StudentDto.class);
            expected.setId(actual.getId());
            assertEquals(expected, actual);
        }

        @Test
        void save_shouldReturnValidError_whenNameIsNotValidAndOneCharacter() throws Exception {
            String expectedMsg = "The student's first name must consist of at least two characters";
            String savedStudentFirstName = "A";
            StudentDto expected = new StudentDto();
            expected.setFirstName(savedStudentFirstName);
            expected.setLastName(LAST_NAME);
            expected.setGroup(groupDtoCreate(GROUP_ID, GROUP_NAME));
            expected.setCourses(
                    new HashSet<>(Arrays.asList(
                            courseDtoCreate(COURSE_ID_1, COURSE_NAME_1, COURSE_DESCRIPTION_1),
                            courseDtoCreate(COURSE_ID_2, COURSE_NAME_2, COURSE_DESCRIPTION_2),
                            courseDtoCreate(COURSE_ID_3, COURSE_NAME_3, COURSE_DESCRIPTION_3)
                    ))
            );
            ObjectMapper objectMapper = new ObjectMapper();
            String content = objectMapper.writeValueAsString(expected);

            mockMvc.perform(post("/api/students")
                    .contentType("application/json")
                    .content(content))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$..message").value(expectedMsg))
                    .andReturn();
        }

        @Test
        void save_shouldReturnValidError_whenNameIsNotValidAndWhitespace() throws Exception {
            String expectedMsg = "The student's first name must not be null and must contain at least one non-whitespace character";
            String savedStudentFirstName = "  ";
            StudentDto expected = new StudentDto();
            expected.setFirstName(savedStudentFirstName);
            expected.setLastName(LAST_NAME);
            expected.setGroup(groupDtoCreate(GROUP_ID, GROUP_NAME));
            expected.setCourses(
                    new HashSet<>(Arrays.asList(
                            courseDtoCreate(COURSE_ID_1, COURSE_NAME_1, COURSE_DESCRIPTION_1),
                            courseDtoCreate(COURSE_ID_2, COURSE_NAME_2, COURSE_DESCRIPTION_2),
                            courseDtoCreate(COURSE_ID_3, COURSE_NAME_3, COURSE_DESCRIPTION_3)
                    ))
            );
            ObjectMapper objectMapper = new ObjectMapper();
            String content = objectMapper.writeValueAsString(expected);

            mockMvc.perform(post("/api/students")
                    .contentType("application/json")
                    .content(content))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$..message").value(expectedMsg))
                    .andReturn();
        }
    }

    @Nested
    @DisplayName("Tests for update an student")
    class UpdateTest {

        @Test
        void updatePut_shouldReturnValidStudentDto_whenEntityFoundById() throws Exception {
            String expectedFirstName = "Ivan";
            String expectedLastName = "Ivanov";
            String expectedGroupId = "408a9358-c6b1-4b36-8912-7bbd4803f1b1";
            String expectedGroupName = "BA-51";
            StudentDto expected = new StudentDto();
            expected.setFirstName(expectedFirstName);
            expected.setLastName(expectedLastName);
            expected.setGroup(groupDtoCreate(expectedGroupId, expectedGroupName));
            ObjectMapper objectMapper = new ObjectMapper();
            String content = objectMapper.writeValueAsString(expected);

            MvcResult result = mockMvc.perform(put("/api/students/{id}", ID)
                    .contentType("application/json")
                    .content(content))
                    .andExpect(status().isAccepted())
                    .andReturn();

            String responseBody = result.getResponse().getContentAsString();
            StudentDto actual = objectMapper.readValue(responseBody, StudentDto.class);
            expected.setId(actual.getId());
            assertEquals(expected, actual);
        }

        @Test
        void updatePut_shouldReturnNotFoundError_whenEntityNotFoundById() throws Exception {
            String badId = "8e2e1511-8105-441f-97e8-5bce88c0267b";
            String expectedMsg = "Student replace error. Student not found by id = " + badId;
            String expectedFirstName = "Ivan";
            String expectedLastName = "Ivanov";
            String expectedGroupId = "408a9358-c6b1-4b36-8912-7bbd4803f1b1";
            String expectedGroupName = "BA-51";
            StudentDto expected = new StudentDto();
            expected.setFirstName(expectedFirstName);
            expected.setLastName(expectedLastName);
            expected.setGroup(groupDtoCreate(expectedGroupId, expectedGroupName));
            ObjectMapper objectMapper = new ObjectMapper();
            String content = objectMapper.writeValueAsString(expected);

            mockMvc.perform(put("/api/students/{id}", badId)
                    .contentType("application/json")
                    .content(content))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$..message").value(expectedMsg));
        }

        @Test
        void updatePut_shouldReturnValidError_whenNameIsNotValid() throws Exception {
            String expectedMsg = "The student's first name must not be null and must contain at least one non-whitespace character";
            String expectedFirstName = "  ";
            String expectedLastName = "Ivanov";
            String expectedGroupId = "408a9358-c6b1-4b36-8912-7bbd4803f1b1";
            String expectedGroupName = "BA-51";
            StudentDto expected = new StudentDto();
            expected.setFirstName(expectedFirstName);
            expected.setLastName(expectedLastName);
            expected.setGroup(groupDtoCreate(expectedGroupId, expectedGroupName));
            ObjectMapper objectMapper = new ObjectMapper();
            String content = objectMapper.writeValueAsString(expected);

            mockMvc.perform(put("/api/students/{id}", ID)
                    .contentType("application/json")
                    .content(content))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$..message").value(expectedMsg));
        }

        @Test
        void updatePatch_shouldReturnValidStudentDto_whenEntityFoundByIdAndUpdateGroup() throws Exception {
            String updatedGroupId = "408a9358-c6b1-4b36-8912-7bbd4803f1b1";
            String updatedGroupName = "BA-51";
            StudentDto updatedStudentDto = new StudentDto();
            GroupDto updatedGroup = new GroupDto();
            updatedGroup.setId(UUID.fromString(updatedGroupId));
            updatedStudentDto.setGroup(updatedGroup);
            ObjectMapper objectMapper = new ObjectMapper();
            String content = objectMapper.writeValueAsString(updatedStudentDto);

            StudentDto expected = studentDtoCreate(ID, FIRST_NAME, LAST_NAME,
                    groupDtoCreate(updatedGroupId, updatedGroupName),
                    new HashSet<>(Arrays.asList(
                            courseDtoCreate(COURSE_ID_1, COURSE_NAME_1, COURSE_DESCRIPTION_1),
                            courseDtoCreate(COURSE_ID_2, COURSE_NAME_2, COURSE_DESCRIPTION_2),
                            courseDtoCreate(COURSE_ID_3, COURSE_NAME_3, COURSE_DESCRIPTION_3)
                    ))
            );

            MvcResult result = mockMvc.perform(patch("/api/students/{id}", ID)
                    .contentType("application/json")
                    .content(content))
                    .andExpect(status().isAccepted())
                    .andReturn();

            String responseBody = result.getResponse().getContentAsString();
            StudentDto actual = objectMapper.readValue(responseBody, StudentDto.class);
            updatedStudentDto.setId(actual.getId());
            assertEquals(expected, actual);
        }

        @Test
        void updatePatch_shouldReturnValidStudentDto_whenEntityFoundByIdAndUpdateCourses() throws Exception {
            String updatedCourseId1 = "9edbc2f6-15a6-4529-a298-379ea64fef12";
            String updatedCourseName1 = "Math";
            String updatedCourseDescription1 = "Subject Math";
            String updatedCourseId2 = "df5b330d-5f6a-4223-8d0b-b71f45636b9f";
            String updatedCourseName2 = "Literature";
            String updatedCourseDescription2 = "Subject Literature";
            CourseDto updatedCourse1 = new CourseDto();
            updatedCourse1.setId(UUID.fromString(updatedCourseId1));
            CourseDto updatedCourse2 = new CourseDto();
            updatedCourse2.setId(UUID.fromString(updatedCourseId2));
            Set<CourseDto> updatedCourses = new HashSet<>();
            updatedCourses.add(updatedCourse1);
            updatedCourses.add(updatedCourse2);
            StudentDto updatedStudentDto = new StudentDto();
            updatedStudentDto.setCourses(updatedCourses);
            ObjectMapper objectMapper = new ObjectMapper();
            String content = objectMapper.writeValueAsString(updatedStudentDto);

            StudentDto expected = studentDtoCreate(ID, FIRST_NAME, LAST_NAME,
                    groupDtoCreate(GROUP_ID, GROUP_NAME),
                    new HashSet<>(Arrays.asList(
                            courseDtoCreate(updatedCourseId1, updatedCourseName1, updatedCourseDescription1),
                            courseDtoCreate(updatedCourseId2, updatedCourseName2, updatedCourseDescription2)
                    ))
            );

            MvcResult result = mockMvc.perform(patch("/api/students/{id}", ID)
                    .contentType("application/json")
                    .content(content))
                    .andExpect(status().isAccepted())
                    .andReturn();

            String responseBody = result.getResponse().getContentAsString();
            StudentDto actual = objectMapper.readValue(responseBody, StudentDto.class);
            updatedStudentDto.setId(actual.getId());
            assertEquals(expected, actual);
        }

        @Test
        void updatePatch_shouldReturnNotFoundError_whenEntityNotFoundById() throws Exception {
            String badId = "8e2e1511-8105-441f-97e8-5bce88c0267b";
            String expectedMsg = "Student not found by id = " + badId;
            StudentDto updatedStudentDto = new StudentDto();
            ObjectMapper objectMapper = new ObjectMapper();
            String content = objectMapper.writeValueAsString(updatedStudentDto);

            mockMvc.perform(patch("/api/students/{id}", badId)
                    .contentType("application/json")
                    .content(content))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$..message").value(expectedMsg));
        }

        @Test
        void updatePatch_shouldReturnValidError_whenNameIsNotValidAndWhitespace() throws Exception {
            String expectedMsg = "The student's first name must contain at least one non-whitespace character. Can be null";
            String updatedStudentFirstName = "  ";
            StudentDto updatedStudentDto = new StudentDto();
            updatedStudentDto.setFirstName(updatedStudentFirstName);
            ObjectMapper objectMapper = new ObjectMapper();
            String content = objectMapper.writeValueAsString(updatedStudentDto);

            mockMvc.perform(patch("/api/students/{id}", ID)
                    .contentType("application/json")
                    .content(content))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$..message").value(expectedMsg));
        }
    }

    @Nested
    @DisplayName("Tests for delete an student")
    class DeleteTest {

        @Test
        void deleteById_shouldReturnOk_whenEntityFoundById() throws Exception {
            mockMvc.perform(delete("/api/students/{id}", ID))
                    .andExpect(status().isOk());
        }

        @Test
        void deleteById_shouldReturnNotFoundError_whenEntityNotFoundById() throws Exception {
            String badId = "8e2e1511-8105-441f-97e8-5bce88c0267b";
            String expectedMsg = "Student delete error. Student not found by id = " + badId;
            mockMvc.perform(delete("/api/students/{id}", badId))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$..message").value(expectedMsg));
        }

        @Test
        void deleteAll_shouldReturnOk() throws Exception {
            mockMvc.perform(delete("/api/students/"))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("Tests for Method Security")
    @WithMockUser(username = "userName", password = "userPassword")
    class MethodSecurityTest {

        @Test
        void findAll_shouldReturnForbiddenError_WhenIsNoPermission() throws Exception {
            mockMvc.perform(get("/api/students"))
                    .andExpect(status().isForbidden());
        }

        @Test
        void findById_shouldReturnForbiddenError_WhenIsNoPermission() throws Exception {
            mockMvc.perform(get("/api/students/{id}", ID))
                    .andExpect(status().isForbidden());
        }

        @Test
        void save_shouldReturnForbiddenError_WhenIsNoPermission() throws Exception {
            String savedStudentFirstName = "Ivan";
            String savedStudentLastName = "Ivanov";
            StudentDto savedStudentDto = new StudentDto();
            savedStudentDto.setFirstName(savedStudentFirstName);
            savedStudentDto.setLastName(savedStudentLastName);
            ObjectMapper objectMapper = new ObjectMapper();
            String content = objectMapper.writeValueAsString(savedStudentDto);

            mockMvc.perform(post("/api/students")
                    .contentType("application/json")
                    .content(content))
                    .andExpect(status().isForbidden());
        }

        @Test
        void updatePut_shouldReturnForbiddenError_WhenIsNoPermission() throws Exception {
            String updatedStudentFirstName = "Ivan";
            String updatedStudentLastName = "Ivanov";
            StudentDto updatedStudentDto = new StudentDto();
            updatedStudentDto.setFirstName(updatedStudentFirstName);
            updatedStudentDto.setLastName(updatedStudentLastName);
            ObjectMapper objectMapper = new ObjectMapper();
            String content = objectMapper.writeValueAsString(updatedStudentDto);

            mockMvc.perform(put("/api/students/{id}", ID)
                    .contentType("application/json")
                    .content(content))
                    .andExpect(status().isForbidden());
        }

        @Test
        void updatePatch_shouldReturnForbiddenError_WhenIsNoPermission() throws Exception {
            String updatedStudentName = "Ivan";
            StudentDto updatedStudentDto = new StudentDto();
            updatedStudentDto.setFirstName(updatedStudentName);
            ObjectMapper objectMapper = new ObjectMapper();
            String content = objectMapper.writeValueAsString(updatedStudentDto);

            mockMvc.perform(patch("/api/students/{id}", ID)
                    .contentType("application/json")
                    .content(content))
                    .andExpect(status().isForbidden());
        }

        @Test
        void deleteById_shouldReturnForbiddenError_WhenIsNoPermission() throws Exception {
            mockMvc.perform(delete("/api/students/{id}", ID))
                    .andExpect(status().isForbidden());
        }

        @Test
        void deleteAll_shouldReturnForbiddenError_WhenIsNoPermission() throws Exception {
            mockMvc.perform(delete("/api/students/"))
                    .andExpect(status().isForbidden());
        }
    }

    private GroupDto groupDtoCreate(String id, String name) {
        GroupDto groupDto = new GroupDto();
        groupDto.setId(UUID.fromString(id));
        groupDto.setName(name);
        return groupDto;
    }

    private CourseDto courseDtoCreate(String id, String name, String description) {
        CourseDto courseDto = new CourseDto();
        courseDto.setId(UUID.fromString(id));
        courseDto.setName(name);
        courseDto.setDescription(description);
        return courseDto;
    }

    private StudentDto studentDtoCreate(String id, String firstName, String lastName, GroupDto group, Set<CourseDto> courses) {
        StudentDto studentDto = new StudentDto();
        studentDto.setId(UUID.fromString(id));
        studentDto.setFirstName(firstName);
        studentDto.setLastName(lastName);
        studentDto.setGroup(group);
        studentDto.setCourses(courses);
        return studentDto;
    }
}
