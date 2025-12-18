package technologyaa.Devit.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import technologyaa.Devit.domain.auth.jwt.entity.Member;
import technologyaa.Devit.domain.auth.jwt.entity.Role;
import technologyaa.Devit.domain.auth.jwt.repository.MemberRepository;
import technologyaa.Devit.domain.project.dto.ProjectCreateRequest;
import technologyaa.Devit.domain.project.entity.Project;
import technologyaa.Devit.domain.project.entity.Task;
import technologyaa.Devit.domain.project.repository.ProjectRepository;
import technologyaa.Devit.domain.project.repository.TaskRepository;
import technologyaa.Devit.domain.project.service.ProjectService;
import technologyaa.Devit.domain.websocketchat.dto.ChatMessage;
import technologyaa.Devit.domain.websocketchat.entity.ChatRoom;
import technologyaa.Devit.domain.websocketchat.repository.ChatMessageRepository;
import technologyaa.Devit.domain.websocketchat.repository.ChatRoomRepository;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.datasource.username=sa",
    "spring.datasource.password=",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect",
    "spring.jpa.show-sql=false",
    "spring.jwt.secret=test-secret-key-for-testing-purposes-only-minimum-length-required",
    "spring.jwt.access-token-expiration=3600000",
    "spring.jwt.refresh-token-expiration=86400000",
    "spring.mail.username=test@example.com",
    "spring.mail.password=test-password",
    "spring.data.redis.host=localhost",
    "spring.data.redis.port=6379",
    "spring.security.oauth2.client.registration.google.client-id=test-client-id",
    "spring.security.oauth2.client.registration.google.client-secret=test-client-secret",
    "server.port=0"
})
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
    private ChatRoomRepository chatRoomRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProjectService projectService;

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
        ProjectCreateRequest projectCreateRequest = new ProjectCreateRequest();
        projectCreateRequest.setTitle("테스트 프로젝트");
        projectCreateRequest.setContent("테스트 프로젝트 내용");
        Long projectId = projectService.createProject(projectCreateRequest, testMember.getId());
        testProject = projectRepository.findById(projectId).orElseThrow();

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

    @Test
    @WithMockUser(username = "testuser")
    void testChatRoomMessageSaveAndRetrieve() throws Exception {
        // 1. 채팅방 생성 (멤버 설정)
        Set<Member> members = new HashSet<>();
        members.add(testMember);
        ChatRoom testRoom = ChatRoom.builder()
                .name("테스트 채팅방")
                .description("테스트용 채팅방")
                .type(ChatRoom.RoomType.GROUP)
                .createdAt(LocalDateTime.now())
                .members(members)
                .build();
        testRoom = chatRoomRepository.save(testRoom);
        Long roomId = testRoom.getId();
        
        // 저장 후 다시 조회하여 members가 제대로 설정되었는지 확인
        ChatRoom savedRoom = chatRoomRepository.findById(roomId).orElseThrow();
        assertNotNull(savedRoom.getMembers());
        assertFalse(savedRoom.getMembers().isEmpty());

        // 2. 채팅방에 메시지 저장 (room 객체를 설정하여 room_id가 저장되도록)
        ChatMessage chatMessage1 = new ChatMessage();
        chatMessage1.setSender("testuser");
        chatMessage1.setContent("첫 번째 메시지");
        chatMessage1.setType(ChatMessage.MessageType.TALK);
        chatMessage1.setTimestamp(LocalDateTime.now());
        chatMessage1.setRoom(testRoom); // room 객체 설정
        chatMessage1.setReceiver(null); // 채팅방 메시지이므로 receiver는 null
        ChatMessage savedMessage1 = chatMessageRepository.save(chatMessage1);

        ChatMessage chatMessage2 = new ChatMessage();
        chatMessage2.setSender("testuser");
        chatMessage2.setContent("두 번째 메시지");
        chatMessage2.setType(ChatMessage.MessageType.TALK);
        chatMessage2.setTimestamp(LocalDateTime.now());
        chatMessage2.setRoom(testRoom); // room 객체 설정
        chatMessage2.setReceiver(null);
        ChatMessage savedMessage2 = chatMessageRepository.save(chatMessage2);

        // 3. 저장된 메시지가 room과 연결되어 있는지 확인
        assertNotNull(savedMessage1.getId());
        assertNotNull(savedMessage1.getRoom());
        assertEquals(roomId, savedMessage1.getRoom().getId());
        assertEquals(roomId, savedMessage1.getRoomId());

        assertNotNull(savedMessage2.getId());
        assertNotNull(savedMessage2.getRoom());
        assertEquals(roomId, savedMessage2.getRoom().getId());

        // 4. roomId로 메시지 조회
        var messagesByRoom = chatMessageRepository.findByRoom_IdOrderByTimestampAsc(roomId);
        assertNotNull(messagesByRoom);
        assertTrue(messagesByRoom.size() >= 2);
        
        // roomId로 조회한 메시지들이 모두 해당 채팅방의 메시지인지 확인
        boolean foundMessage1 = messagesByRoom.stream()
                .anyMatch(m -> m.getId().equals(savedMessage1.getId()) && m.getContent().equals("첫 번째 메시지"));
        boolean foundMessage2 = messagesByRoom.stream()
                .anyMatch(m -> m.getId().equals(savedMessage2.getId()) && m.getContent().equals("두 번째 메시지"));
        
        assertTrue(foundMessage1, "첫 번째 메시지가 roomId로 조회되어야 함");
        assertTrue(foundMessage2, "두 번째 메시지가 roomId로 조회되어야 함");

        // 5. roomId로 조회한 메시지들이 모두 roomId를 가지고 있는지 확인
        for (ChatMessage msg : messagesByRoom) {
            assertNotNull(msg.getRoomId(), "메시지의 roomId는 null이 아니어야 함");
            assertEquals(roomId, msg.getRoomId(), "메시지의 roomId가 올바르게 저장되어야 함");
            assertNotNull(msg.getRoom(), "메시지의 room 객체는 null이 아니어야 함");
            assertEquals(roomId, msg.getRoom().getId(), "메시지의 room.id가 올바르게 저장되어야 함");
        }
        
        // 6. 테스트 결과 요약: roomId로 메시지가 저장되고 조회되는지 확인 완료
        System.out.println("✅ 채팅방 메시지 저장 및 조회 테스트 성공:");
        System.out.println("   - 메시지가 room 객체와 함께 저장됨");
        System.out.println("   - roomId로 메시지 조회 가능");
        System.out.println("   - 저장된 메시지 개수: " + messagesByRoom.size());
    }
}

