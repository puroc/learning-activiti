package com.example.activiti.testcase.usertask;


import com.example.activiti.testcase.TestcaseParent;
import org.activiti.engine.HistoryService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricProcessInstance;
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

import java.util.HashMap;
import java.util.Map;

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

        //设置第一个任务节点的变量，包括候选用户和候选用户组
        Map<String, Object> variables =new HashMap<String, Object>();
        variables.put("user","zhangsan");
        variables.put("group","leader");

        //启动流程
        ProcessInstance pi = runtimeService.startProcessInstanceByKey("usertask1",variables);

        TaskService taskService = processEngine.getTaskService();

        Assert.assertFalse(taskService.createTaskQuery().taskCandidateUser("zhangsan").list().isEmpty());
        Assert.assertFalse(taskService.createTaskQuery().taskCandidateGroup("leader").list().isEmpty());

        //查询候选用户是张三的任务列表，并取出第一个任务
        Task task1 = taskService.createTaskQuery().taskCandidateUser("zhangsan").list().get(0);
        //张三签收该任务
        taskService.claim(task1.getId(),"zhangsan");
        //签收之后，可以根据执行人是张三查找到该任务
        Assert.assertFalse(taskService.createTaskQuery().taskAssignee("zhangsan").list().isEmpty());
        //即将结束第一任务，为下一个任务设置变量，包括下一个任务的候选用户和候选用户组
        Map<String, Object> variables2 =new HashMap<String, Object>();
        variables2.put("user","lisi");
        variables2.put("group","boss");
        //结束第一个任务
        taskService.complete(task1.getId(),variables2);

        Assert.assertFalse(taskService.createTaskQuery().taskCandidateUser("lisi").list().isEmpty());
        Assert.assertFalse(taskService.createTaskQuery().taskCandidateGroup("boss").list().isEmpty());
        //根据候选用户是李四，查询出他负责的任务
        Task task2 = taskService.createTaskQuery().taskCandidateUser("lisi").list().get(0);
        //结束第二个任务
        taskService.complete(task2.getId());

        //在历史任务中查询到该任务，打印该任务的结束时间
        HistoryService historyService = processEngine.getHistoryService();
        HistoricProcessInstance historicProcessInstance =
                historyService.createHistoricProcessInstanceQuery().processInstanceId(pi.getId()).singleResult();
        System.out.println("Process instance end time: " + historicProcessInstance.getEndTime());
    }


}
