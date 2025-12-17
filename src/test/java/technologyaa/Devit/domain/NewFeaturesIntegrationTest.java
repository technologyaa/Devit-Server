package technologyaa.Devit.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import technologyaa.Devit.domain.auth.jwt.entity.Member;
import technologyaa.Devit.domain.auth.jwt.entity.Role;
import technologyaa.Devit.domain.auth.jwt.repository.MemberRepository;
import technologyaa.Devit.domain.project.entity.Project;
import technologyaa.Devit.domain.project.entity.Major;
import technologyaa.Devit.domain.project.entity.Task;
import technologyaa.Devit.domain.project.repository.ProjectRepository;
import technologyaa.Devit.domain.project.repository.TaskRepository;
import technologyaa.Devit.domain.websocketchat.dto.ChatMessage;
import technologyaa.Devit.domain.websocketchat.repository.ChatMessageRepository;

import java.time.LocalDateTime;
import java.util.HashSet;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class NewFeaturesIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Member testMember;
    private Project testProject;
    private Task testTask;

    @BeforeEach
    void setUp() {
        // 테스트용 유저 생성
        testMember = Member.builder()
                .username("testuser")
                .password("encodedPassword")
                .email("test@example.com")
                .createdAt("2024-01-01 00:00:00")
                .credit(0L)
                .role(Role.ROLE_USER)
                .isDeveloper(false)
                .build();
        testMember = memberRepository.save(testMember);

        // 테스트용 프로젝트 생성
        testProject = Project.builder()
                .title("테스트 프로젝트")
                .content("테스트 프로젝트 내용")
                .major(Major.BACKEND)
                .members(new HashSet<>())
                .build();
        testProject.getMembers().add(testMember);
        testProject = projectRepository.save(testProject);

        // 테스트용 업무 생성
        testTask = Task.builder()
                .project(testProject)
                .title("테스트 업무")
                .description("테스트 업무 설명")
                .status(Task.TaskStatus.TODO)
                .build();
        testTask = taskRepository.save(testTask);

        // 테스트용 채팅 메시지 생성
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setSender("testuser");
        chatMessage.setContent("테스트 메시지");
        chatMessage.setType(ChatMessage.MessageType.TALK);
        chatMessage.setTimestamp(LocalDateTime.now());
        chatMessageRepository.save(chatMessage);
    }

    @Test
    @WithMockUser(username = "testuser")
    void testGetCurrentMember() throws Exception {
        mockMvc.perform(get("/auth/me")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.username").value("testuser"))
                .andExpect(jsonPath("$.data.email").value("test@example.com"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void testGetAllMembers() throws Exception {
        mockMvc.perform(get("/auth/members")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].username").exists());
    }

    @Test
    @WithMockUser(username = "testuser")
    void testGetMyProjects() throws Exception {
        mockMvc.perform(get("/projects/my-projects")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].projectId").exists())
                .andExpect(jsonPath("$.data[0].title").value("테스트 프로젝트"))
                .andExpect(jsonPath("$.data[0].tasks").isArray())
                .andExpect(jsonPath("$.data[0].tasks[0].title").value("테스트 업무"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void testGetChattingUsers() throws Exception {
        // 다른 유저 생성 및 채팅 메시지 추가
        Member otherMember = Member.builder()
                .username("otheruser")
                .password("encodedPassword")
                .email("other@example.com")
                .createdAt("2024-01-01 00:00:00")
                .credit(0L)
                .role(Role.ROLE_USER)
                .isDeveloper(false)
                .build();
        otherMember = memberRepository.save(otherMember);

        ChatMessage otherChatMessage = new ChatMessage();
        otherChatMessage.setSender("otheruser");
        otherChatMessage.setContent("다른 유저 메시지");
        otherChatMessage.setType(ChatMessage.MessageType.TALK);
        otherChatMessage.setTimestamp(LocalDateTime.now());
        chatMessageRepository.save(otherChatMessage);

        mockMvc.perform(get("/chat/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].username").value("otheruser"))
                .andExpect(jsonPath("$.data[0].email").value("other@example.com"));
    }
}

