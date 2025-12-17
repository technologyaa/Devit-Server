package technologyaa.Devit.domain.project;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import technologyaa.Devit.domain.project.dto.ProjectCreateRequest;
import technologyaa.Devit.domain.project.dto.TaskRequest;
import technologyaa.Devit.domain.project.entity.Project;
import technologyaa.Devit.domain.project.entity.Task;
import technologyaa.Devit.domain.project.entity.TaskFile;
import technologyaa.Devit.domain.project.entity.Task.TaskStatus;
import technologyaa.Devit.domain.project.repository.ProjectRepository;
import technologyaa.Devit.domain.project.repository.TaskFileRepository;
import technologyaa.Devit.domain.project.repository.TaskRepository;
import technologyaa.Devit.domain.auth.jwt.entity.Member;
import technologyaa.Devit.domain.auth.jwt.entity.Role;
import technologyaa.Devit.domain.auth.jwt.repository.MemberRepository;
import technologyaa.Devit.domain.project.service.ProjectService;
import technologyaa.Devit.domain.project.service.TaskFileService;
import technologyaa.Devit.domain.project.service.TaskService;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {
    "file.upload.dir=./test-uploads"
})
@Transactional
public class TaskFileIntegrationTest {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private TaskFileService taskFileService;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskFileRepository taskFileRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private MemberRepository memberRepository;

    private Member testMember;

    @BeforeEach
    public void setUp() throws Exception {
        // 테스트 업로드 디렉토리 생성
        Path testUploadDir = Paths.get("./test-uploads");
        if (!Files.exists(testUploadDir)) {
            Files.createDirectories(testUploadDir);
        }

        // 테스트용 유저 생성
        testMember = memberRepository.findByUsername("testuser")
                .orElse(null);
        if (testMember == null) {
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
        }
    }

    @Test
    @WithMockUser(username = "testuser")
    public void testCreateTaskAndUploadFile() throws Exception {
        // 1. 프로젝트 생성
        ProjectCreateRequest projectRequest = new ProjectCreateRequest();
        projectRequest.setTitle("테스트 프로젝트");
        projectRequest.setContent("테스트 내용");
        Long projectId = projectService.createProject(projectRequest, testMember.getId());
        Project project = projectRepository.findById(projectId).orElseThrow();
        assertNotNull(project);
        assertNotNull(project.getProjectId());

        // 2. 업무 생성
        TaskRequest taskRequest = new TaskRequest();
        taskRequest.setTitle("테스트 업무");
        taskRequest.setDescription("테스트 업무 설명");
        taskRequest.setStatus(TaskStatus.TODO);
        Task task = taskService.create(project.getProjectId(), taskRequest);
        assertNotNull(task);
        assertNotNull(task.getTaskId());
        assertEquals("테스트 업무", task.getTitle());
        assertEquals(TaskStatus.TODO, task.getStatus());

        // 3. 파일 업로드
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test-file.txt",
                "text/plain",
                "테스트 파일 내용".getBytes()
        );

        TaskFile taskFile = taskFileService.uploadFile(task.getTaskId(), file);

        // 검증
        assertNotNull(taskFile);
        assertNotNull(taskFile.getFileId());
        assertEquals("test-file.txt", taskFile.getOriginalFileName());
        assertNotNull(taskFile.getFileName());
        assertTrue(taskFile.getFileName().contains(".txt"));
        assertEquals(task.getTaskId(), taskFile.getTask().getTaskId());

        // 파일이 실제로 저장되었는지 확인
        Path uploadDir = Paths.get("./test-uploads");
        Path filePath = uploadDir.resolve(taskFile.getFileName());
        assertTrue(Files.exists(filePath));
    }

    @Test
    @WithMockUser(username = "testuser")
    public void testFileDownload() throws Exception {
        // 프로젝트 생성
        ProjectCreateRequest projectRequest = new ProjectCreateRequest();
        projectRequest.setTitle("다운로드 테스트 프로젝트");
        projectRequest.setContent("테스트 내용");
        Long projectId = projectService.createProject(projectRequest, testMember.getId());
        Project project = projectRepository.findById(projectId).orElseThrow();

        // 업무 생성
        TaskRequest taskRequest = new TaskRequest();
        taskRequest.setTitle("다운로드 테스트 업무");
        taskRequest.setDescription("테스트 업무 설명");
        Task task = taskService.create(project.getProjectId(), taskRequest);

        // 파일 업로드
        String fileContent = "다운로드 테스트 파일 내용";
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "download-test.txt",
                "text/plain",
                fileContent.getBytes()
        );

        TaskFile taskFile = taskFileService.uploadFile(task.getTaskId(), file);

        // 파일 다운로드
        var resource = taskFileService.downloadFile(task.getTaskId(), taskFile.getFileId());

        // 검증
        assertNotNull(resource);
        assertTrue(resource.exists());
        assertTrue(resource.isReadable());
    }

    @Test
    @WithMockUser(username = "testuser")
    public void testGetFilesByTaskId() throws Exception {
        // 프로젝트 생성
        ProjectCreateRequest projectRequest = new ProjectCreateRequest();
        projectRequest.setTitle("목록 테스트 프로젝트");
        projectRequest.setContent("테스트 내용");
        Long projectId = projectService.createProject(projectRequest, testMember.getId());
        Project project = projectRepository.findById(projectId).orElseThrow();

        // 업무 생성
        TaskRequest taskRequest = new TaskRequest();
        taskRequest.setTitle("목록 테스트 업무");
        taskRequest.setDescription("테스트 업무 설명");
        Task task = taskService.create(project.getProjectId(), taskRequest);

        // 파일 2개 업로드
        MockMultipartFile file1 = new MockMultipartFile(
                "file",
                "file1.txt",
                "text/plain",
                "파일1 내용".getBytes()
        );

        MockMultipartFile file2 = new MockMultipartFile(
                "file",
                "file2.txt",
                "text/plain",
                "파일2 내용".getBytes()
        );

        taskFileService.uploadFile(task.getTaskId(), file1);
        taskFileService.uploadFile(task.getTaskId(), file2);

        // 파일 목록 조회
        List<TaskFile> files = taskFileService.getFilesByTaskId(task.getTaskId());

        // 검증
        assertEquals(2, files.size());
    }

    @Test
    @WithMockUser(username = "testuser")
    public void testDeleteFile() throws Exception {
        // 프로젝트 생성
        ProjectCreateRequest projectRequest = new ProjectCreateRequest();
        projectRequest.setTitle("삭제 테스트 프로젝트");
        projectRequest.setContent("테스트 내용");
        Long projectId = projectService.createProject(projectRequest, testMember.getId());
        Project project = projectRepository.findById(projectId).orElseThrow();

        // 업무 생성
        TaskRequest taskRequest = new TaskRequest();
        taskRequest.setTitle("삭제 테스트 업무");
        taskRequest.setDescription("테스트 업무 설명");
        Task task = taskService.create(project.getProjectId(), taskRequest);

        // 파일 업로드
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "delete-test.txt",
                "text/plain",
                "삭제 테스트 파일".getBytes()
        );

        TaskFile taskFile = taskFileService.uploadFile(task.getTaskId(), file);
        String fileName = taskFile.getFileName();

        // 파일 삭제
        taskFileService.deleteFile(task.getTaskId(), taskFile.getFileId());

        // 검증 - DB에서 삭제되었는지 확인
        assertFalse(taskFileRepository.findById(taskFile.getFileId()).isPresent());

        // 검증 - 파일 시스템에서 삭제되었는지 확인
        Path uploadDir = Paths.get("./test-uploads");
        Path filePath = uploadDir.resolve(fileName);
        assertFalse(Files.exists(filePath));
    }

    @Test
    @WithMockUser(username = "testuser")
    public void testTaskCRUD() throws Exception {
        // 프로젝트 생성
        ProjectCreateRequest projectRequest = new ProjectCreateRequest();
        projectRequest.setTitle("CRUD 테스트 프로젝트");
        projectRequest.setContent("테스트 내용");
        Long projectId = projectService.createProject(projectRequest, testMember.getId());
        Project project = projectRepository.findById(projectId).orElseThrow();

        // 업무 생성
        TaskRequest taskRequest = new TaskRequest();
        taskRequest.setTitle("CRUD 테스트 업무");
        taskRequest.setDescription("테스트 업무 설명");
        taskRequest.setStatus(TaskStatus.TODO);
        Task task = taskService.create(project.getProjectId(), taskRequest);

        // 업무 조회
        Task foundTask = taskService.findOne(task.getTaskId());
        assertEquals("CRUD 테스트 업무", foundTask.getTitle());
        assertEquals(TaskStatus.TODO, foundTask.getStatus());

        // 프로젝트의 모든 업무 조회
        List<Task> tasks = taskService.findAllByProjectId(project.getProjectId());
        assertEquals(1, tasks.size());

        // 업무 수정
        TaskRequest updateRequest = new TaskRequest();
        updateRequest.setTitle("수정된 업무");
        updateRequest.setDescription("수정된 설명");
        updateRequest.setStatus(TaskStatus.IN_PROGRESS);
        Task updatedTask = taskService.update(task.getTaskId(), updateRequest);
        assertEquals("수정된 업무", updatedTask.getTitle());
        assertEquals(TaskStatus.IN_PROGRESS, updatedTask.getStatus());

        // 업무 삭제
        taskService.delete(task.getTaskId());
        assertThrows(technologyaa.Devit.domain.project.exception.ProjectException.class, () -> {
            taskService.findOne(task.getTaskId());
        });
    }
}


