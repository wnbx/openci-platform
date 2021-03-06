package com.xiaokaceng.openci.application.impl;

import static org.junit.Assert.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.xiaokaceng.openci.AbstractIntegrationTest;
import com.xiaokaceng.openci.EntityNullException;
import com.xiaokaceng.openci.application.ProjectApplication;
import com.xiaokaceng.openci.domain.Developer;
import com.xiaokaceng.openci.domain.Project;
import com.xiaokaceng.openci.domain.ProjectDeveloper;
import com.xiaokaceng.openci.domain.Role;
import com.xiaokaceng.openci.domain.ScmType;
import com.xiaokaceng.openci.domain.Tool;
import com.xiaokaceng.openci.dto.ProjectDto;
import com.xiaokaceng.openci.dto.ProjectQueryDto;
import com.xiaokaceng.openci.dto.ScmConfig;

public class ProjectApplicationImplTest extends AbstractIntegrationTest {
	
	@Inject
	private ProjectApplication projectApplication;

	private static final String NAME = "test";
	
	private Developer developer;
	private Role role;
	
	@Test
	public void testCreateProject() {
		ProjectDto projectDto = getProjectDtoInstance();
		projectApplication.createProject(projectDto);
		assertEquals(1, projectDto.getProjectForCis().getDevelopers().size());
		assertEquals(2, projectDto.getProjectForCis().getTools().size());
		projectDto.getProjectForCis().remove();
	}
	
	@Test(expected = NullPointerException.class)
	public void testCreateProjectIfNull() {
		projectApplication.createProject(null);
	}
	
	@Test
	public void testPagingQueryProject() {
		ProjectDto projectDto = getProjectDtoInstance();
		projectApplication.createProject(projectDto);
		
		ProjectQueryDto projectQueryDto = new ProjectQueryDto();
		projectQueryDto.setName("demo");
		List<Project> projects = projectApplication.pagingQueryProject(projectQueryDto, 1, 10).getResult();
		assertEquals(0, projects.size());
		
		projectQueryDto.setName("es");
		projects = projectApplication.pagingQueryProject(projectQueryDto, 1, 10).getResult();
		assertEquals(1, projects.size());
		
		projectDto.getProjectForCis().remove();
	}
	
	@Test
	public void testPagingQueryProject2() throws ParseException {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		
		ProjectDto projectDto = getProjectDtoInstance();
		projectApplication.createProject(projectDto);
		
		ProjectQueryDto projectQueryDto = new ProjectQueryDto();
		projectQueryDto.setName("test");
		projectQueryDto.setStartDate(dateFormat.parse("8888-1-1"));
		List<Project> projects = projectApplication.pagingQueryProject(projectQueryDto, 1, 10).getResult();
		assertEquals(0, projects.size());
		
		projectQueryDto.setStartDate(dateFormat.parse("2013-1-1"));
		projects = projectApplication.pagingQueryProject(projectQueryDto, 1, 10).getResult();
		assertEquals(1, projects.size());
		
		projectDto.getProjectForCis().remove();
	}
	
	@Test
	public void testGetDetail() {
		ProjectDto projectDto = getProjectDtoInstance();
		projectApplication.createProject(projectDto);

		Project project = projectApplication.getDetail(projectDto.getProjectForCis().getId());
		assertNotNull(project);
		assertEquals(project.getName(), projectDto.getProjectForCis().getName());
		
		projectDto.getProjectForCis().remove();
	}
	
	@Test
	public void testGetDetail2() {
		Project project = projectApplication.getDetail(0);
		assertNull(project);
	}
	
	@Test
	public void testIsExistByName() {
		ProjectDto projectDto = getProjectDtoInstance();
		projectApplication.createProject(projectDto);
		
		assertTrue(projectApplication.isExistByName(NAME));
		assertFalse(projectApplication.isExistByName("tt"));
		projectDto.getProjectForCis().remove();
	}
	
	@Test
	public void testAddIntegrationTool() {
		ProjectDto projectDto = getProjectDtoInstance();
		projectApplication.createProject(projectDto);
		projectApplication.addIntegrationTool(projectDto.getProjectForCis(), new Tool(null, projectDto.getProjectForCis()));
		assertEquals(3, projectDto.getProjectForCis().getTools().size());
	}
	
	@Test(expected = EntityNullException.class)
	public void testAddIntegrationToolIfNull() {
		Project project = getProjectInstance();
		projectApplication.addIntegrationTool(project, null);
	}

	@Test
	public void testUpdateIntegrationToolStatus() {
		ProjectDto projectDto = getProjectDtoInstance();
		projectApplication.createProject(projectDto);
		Tool tool = new Tool(null, projectDto.getProjectForCis());
		tool.save();
		
		projectApplication.updateIntegrationToolStatus(tool.getId());
		
	}
	
	@Test
	public void testRemove() {
		ProjectDto projectDto = getProjectDtoInstance();
		projectApplication.createProject(projectDto);
		assertTrue(projectApplication.isExistByName(NAME));
		assertFalse(projectApplication.isExistByName(null));
	}
	
	private ProjectDto getProjectDtoInstance() {
		ProjectDto projectDto = new ProjectDto(NAME);
		projectDto.getProjectForCis().setDevelopers(createProjectDeveloper(projectDto.getProjectForCis()));
		projectDto.getProjectForCis().setTools(createTool(projectDto.getProjectForCis()));
		projectDto.setScmConfig(createScmConfig());
		
		org.openkoala.koala.widget.Project projectForCreate = projectDto.getProjectForCreate();
		projectForCreate.setAppName("demo");
		projectForCreate.setGroupId("org.openkoala");
		projectForCreate.setArtifactId("demo");
		projectForCreate.setPackaging("pom");
		projectForCreate.initSSJProject();
		
		return projectDto;
	}
	
	private ScmConfig createScmConfig() {
		ScmConfig scmConfig = new ScmConfig();
		scmConfig.setRepositoryUrl("xxxxxxx");
		scmConfig.setScmType(ScmType.GIT);
		return scmConfig;
	}

	private Project getProjectInstance() {
		Project project = new Project(NAME);
		project.setDevelopers(createProjectDeveloper(project));
		project.setTools(createTool(project));
		return project;
	}

	private Set<Tool> createTool(Project project) {
		Set<Tool> tools = new HashSet<Tool>();
		tools.add(new Tool(null, project));
		tools.add(new Tool(null, project));
		return tools;
	}

	private Set<ProjectDeveloper> createProjectDeveloper(Project project) {
		Set<ProjectDeveloper> projectDevelopers = new HashSet<ProjectDeveloper>();
		projectDevelopers.add(new ProjectDeveloper(createRoles(), developer, project));
		return projectDevelopers;
	}

	private Set<Role> createRoles() {
		Set<Role> roles = new HashSet<Role>();
		roles.add(role);
		return roles;
	}

	@Before
	public void init() {
		developer = new Developer(NAME, NAME, NAME, NAME);
		developer.save();
		role = new Role(NAME, NAME);
		role.save();
	}
	
	@After
	public void destory() {
		List<Developer> developers = Developer.findAll(Developer.class);
		if (developers.size() > 0) {
			for (Developer each : developers) {
				each.remove();
			}
		}
	}
	
}
