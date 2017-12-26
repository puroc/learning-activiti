package com.example.activiti.testcase.usertask;


import com.example.activiti.testcase.TestcaseParent;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:usertask/activiti.cfg.xml")
public class UserTaskTestcase1 extends TestcaseParent {
    @Before
    @Override
    public void setUp() throws Exception {
        super.setUp();
        Deployment deployment = repositoryService
                .createDeployment()
                .addClasspathResource(
                        "usertask/usertask1.bpmn")
                .deploy();
        deploymentId = deployment.getId();
    }

    @After
    @Override
    public void tearDown() throws Exception {
        super.tearDown();
        repositoryService.deleteDeployment(deploymentId, true);
    }


    @Test
    public void test() {
        ProcessInstance pi = runtimeService.startProcessInstanceByKey("usertask1");
        Assert.assertNotNull(pi);
        TaskService taskService = processEngine.getTaskService();
        List<Task> list = taskService.createTaskQuery().active().list();
        for (Task task : list) {
            System.out.println(task.getId());
        }

    }


}
