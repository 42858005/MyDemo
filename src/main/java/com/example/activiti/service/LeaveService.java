package com.example.activiti.service;

import com.example.activiti.model.Leave;
import org.activiti.engine.history.HistoricTaskInstance;

import java.io.InputStream;
import java.util.List;

public interface LeaveService {

    /** 申请请假 */
    boolean applyLeave(Leave leave);
    /** 提交请假申请*/
    boolean commitLeaveProcess(Integer leaveId);
    /** 查询请假列表*/
    public List<Leave> listLeave(String userId);
    /** 查询待办事项*/
    List<Leave> listWaitMatterPage(String userName);
    /** 查询历史事项*/
    List<HistoricTaskInstance> listHistoryMatterPage(String userName);
    /** 项目组长审核通过还是不通过*/
    boolean examineByPersonnelVariableMsg(String userName,String processInstanceId,String msg);
    /**根据流程实例Id删除流程*/
    boolean deleteProcessByprocessInstanceId(String processInstanceId);

}
