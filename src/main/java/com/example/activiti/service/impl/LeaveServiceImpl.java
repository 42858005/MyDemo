package com.example.activiti.service.impl;

import com.example.activiti.mapper.LeaveMapper;
import com.example.activiti.mapper.UserMapper;
import com.example.activiti.model.Leave;
import com.example.activiti.service.LeaveService;
import com.example.activiti.utils.ProcessKey;
import org.activiti.engine.*;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.spring.ProcessEngineFactoryBean;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class LeaveServiceImpl implements LeaveService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private LeaveMapper leaveMapper;

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private ManagementService managementService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private ProcessEngineConfiguration processEngineConfiguration;

    @Autowired
    private ProcessEngineFactoryBean processEngine;

    @Autowired
    private HistoryService historyService;

    @Autowired
    private TaskService taskService;

    @Override
    public boolean applyLeave(Leave leave) {
        if (leaveMapper.insertSelective(leave) > 0) {
            return true;
        }
        return false;
    }

    @Override
    public boolean commitLeaveProcess(Integer leaveId) {

        boolean ifOk = true;
        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put("leaveId", leaveId);

        String key = Leave.class.getSimpleName();
        String businessKey = key + "." + leaveId;

        try {
            // 根据请假的ID启动流程
            ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(key, businessKey, variables);
            // 得到流程实例Id
            String processInstanceId = processInstance.getProcessInstanceId();
            // 根据流程实例Id查询任务
            Task task = taskService.createTaskQuery().processInstanceId(processInstanceId).singleResult();
            // 完成员工填写请假单任务
            taskService.complete(task.getId());
        } catch (Exception e) {
            e.printStackTrace();
            ifOk = false;
        }

        return ifOk;
    }

    @Override
    public List<Leave> listLeave(String userId) {

        Example example = new Example(Leave.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("userId", userId);

        return leaveMapper.selectByExample(example);
    }

    @Override
    public List<Leave> listWaitMatterPage(String userName) {
        // 根据processIntanceId查询所有的办事集合
        List<Leave> listLeaveApplication = new ArrayList<>();
        try {
            // 获得角色所对应所有的task
            List<Task> listWaitMatterPage = taskService.createTaskQuery().taskAssignee(userName).list();
            int size = taskService.createTaskQuery().taskAssignee(userName).list().size();
            System.err.println("所有的task"+listWaitMatterPage);
            // 根据task查询对应的processIntanceId
            List<String> listProcessInstanceIds = new ArrayList<>();
            if (listWaitMatterPage != null) {
                for (Task task : listWaitMatterPage) {
                    listProcessInstanceIds.add(task.getProcessInstanceId());
                }
            }
            // 遍历根据流程实例id查询所有的事项
            if (listProcessInstanceIds != null) {
                for (String processInstanceId : listProcessInstanceIds) {
                    ProcessInstance pi = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
                    //4.使用流程实例对象获取BusinessKey
                    String business_key = pi.getBusinessKey();
                    String id = "";
                    if(StringUtils.isNotBlank(business_key)){
                        //截取字符串
                        id = business_key.split("\\.")[1].toString();

                    }
                    Leave leave = leaveMapper.selectByPrimaryKey(Integer.parseInt(id));
                    listLeaveApplication.add(leave);
                }
            }
            System.out.println("总记录数："+size);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return listLeaveApplication;
    }

    @Override
    public List<HistoricTaskInstance> listHistoryMatterPage(String userName) {
        List<HistoricTaskInstance> listHistoryMatterPage = new ArrayList<>();
        try {
            listHistoryMatterPage = historyService.createHistoricTaskInstanceQuery()
                    .taskAssignee(userName).list();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return listHistoryMatterPage;
    }

    @Override
    public boolean examineByPersonnelVariableMsg(String userName, String processInstanceId, String msg) {
        Boolean ifOk = true;
        Map<String, Object> variables = new HashMap<>();
        variables.put("msg", msg);
        try {
            Task task = taskService // 任务相关Service
                    .createTaskQuery().processInstanceId(processInstanceId) // 创建任务查询
                    .taskAssignee(userName) // 指定某个人
                    .singleResult();
            System.out.println("任务ID:" + task.getId());
            System.out.println("任务名称:" + task.getName());
            System.out.println("任务创建时间:" + task.getCreateTime());
            System.out.println("任务委派人:" + task.getAssignee());
            System.out.println("流程实例ID:" + task.getProcessInstanceId());
            // 完成项目组长的审核任务
            taskService.complete(task.getId(), variables);
        } catch (Exception e) {
            ifOk = false;
        }
        return ifOk;
    }

    @Override
    public boolean deleteProcessByprocessInstanceId(String processInstanceId) {
        boolean ifOk = true;
        try {
            // 根据流程实例得到当前任务
            Task task = taskService // 任务相关Service
                    .createTaskQuery().processInstanceId(processInstanceId) // 创建任务查询
                    .singleResult();
            // 根据任务得到流程定义id
            String processDefinitionId = task.getProcessDefinitionId();
            // 得到流程定义id得到流程定义
            ProcessDefinition processDefinition = repositoryService.getProcessDefinition(processDefinitionId);
            // 根据流程定义得到流程部署id
            String deploymentId = processDefinition.getDeploymentId();
            // 级联删除 已经在使用的流程实例信息也会被级联删除 (一般项目都使用级联删除)
            repositoryService.deleteDeployment(deploymentId, true);
        } catch (Exception e) {
            e.printStackTrace();
            ifOk = false;
        }
        return ifOk;
    }
}
