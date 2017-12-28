package com.example.activiti.testcase;

import org.activiti.engine.*;
import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.User;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;


public class TestcaseParent {

    public static final String LEADER_1_ID = "zhangsan";
    public static final String GROUP_1_ID = "leader";
    public static final String LEADER_2_ID = "lisi";
    public static final String GROUP_2_ID = "boss";

    @Autowired
    protected ProcessEngine processEngine;

    @Autowired
    protected RepositoryService repositoryService;

    @Autowired
    protected RuntimeService runtimeService;

    @Autowired
    protected TaskService taskService;

    @Autowired
    protected HistoryService historyService;

    @Autowired
    protected ManagementService managementService;

    @Autowired
    protected IdentityService identityService;

    private HashMap<String, Group> groupMap = new HashMap<String, Group>();

    private HashMap<String, User> userMap = new HashMap<String, User>();

    protected String deploymentId;

    public String getDeploymentId() {
        return deploymentId;
    }

    public void setDeploymentId(String deploymentId) {
        this.deploymentId = deploymentId;
    }

    public void setUp() throws Exception {
        createMemberShip(GROUP_1_ID, LEADER_1_ID);
        createMemberShip(GROUP_2_ID, LEADER_2_ID);
    }

    private void createMemberShip(String groupId, String userId) {
        Group group = null;
        User user = null;
        if (identityService.createGroupQuery().groupId(groupId).list().isEmpty()) {
            group = identityService.newGroup(groupId);
            group.setType("assignment");
            identityService.saveGroup(group);
        }
        if (identityService.createUserQuery().userId(userId).list().isEmpty()) {
            user = identityService.newUser(userId);
            user.setPassword("123456");
            identityService.saveUser(user);
        }
        if (identityService.createUserQuery().memberOfGroup(groupId).list().isEmpty()) {
            identityService.createMembership(userId, groupId);
        }
        User userInGroup = identityService.createUserQuery().memberOfGroup(groupId).singleResult();
        Assert.assertNotNull(userInGroup);
        Assert.assertEquals(userId, userInGroup.getId());
        userMap.putIfAbsent(user.getId(), user);
        groupMap.putIfAbsent(group.getId(), group);
    }

    public void tearDown() throws Exception {
        for (Map.Entry<String, User> entry : userMap.entrySet()) {
            if (!identityService.createUserQuery().userId(entry.getValue().getId()).list().isEmpty()) {
                identityService.deleteUser(entry.getValue().getId());
            }
        }
        for (Map.Entry<String, Group> entry : groupMap.entrySet()) {
            if (!identityService.createGroupQuery().groupId(entry.getValue().getId()).list().isEmpty()) {
                identityService.deleteGroup(entry.getValue().getId());
            }
        }
    }
}
