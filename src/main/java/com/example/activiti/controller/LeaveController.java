package com.example.activiti.controller;

import com.example.activiti.model.Leave;
import com.example.activiti.service.LeaveService;
import com.example.activiti.utils.Constant;
import com.example.activiti.utils.ReturnValue;
import org.activiti.engine.history.HistoricTaskInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@RestController
@RequestMapping("/leave")
@CrossOrigin(origins = "*", maxAge = 3600)
public class LeaveController {

	@Autowired
	private LeaveService leaveService;

	/** 申请请假 */
	@PostMapping(value = "/applyLeave")
	public ReturnValue applyLeave(Leave leave, HttpServletRequest request) {
		Boolean isTrue = leaveService.applyLeave(leave);
		if (isTrue) {
			return new ReturnValue(Constant.SUCCESS, "填写请假单成功");
		} else {
			return new ReturnValue(Constant.ERROR, "填写请假单失败");
		}
	}
	
	/** 根据角色查询请假列表 */
	@PostMapping(value="/leaveList")
	public ReturnValue leaveList(String userId) {
		List<Leave> list = leaveService.listLeave(userId);
		if(list == null) {
			return new ReturnValue(Constant.SUCCESS, "空");
		}
		return new ReturnValue(Constant.SUCCESS, "查询成功", list);
	}

	/** 提交請假流程申請 */
	@PostMapping(value="/commitLeave")
	public ReturnValue commitLeave(Integer leaveId) throws Exception {
		Boolean ifTrue = leaveService.commitLeaveProcess(leaveId);
		if (ifTrue) {
			return new ReturnValue(Constant.SUCCESS, "提交申请成功");
		} else {
			return new ReturnValue(Constant.ERROR, "提交申请失败");
		}
	}

	/** 根据角色查询待办事项 */
	@PostMapping(value="/listWaitMatterPage")
	public ReturnValue listWaitMatterPage(String userName) {
		List<Leave> listWaitMatterPage = leaveService.listWaitMatterPage(userName);
		//listWaitMatterPage =null;
		if(listWaitMatterPage == null) {
			return new ReturnValue(Constant.SUCCESS, "空");
		}
		return new ReturnValue(Constant.SUCCESS, "查询成功", listWaitMatterPage);
	}

	/** 查询历史事项 */
	@PostMapping("/listHistoryMatterPage")
	public ReturnValue listHistoryMatterPage(String userName) {
		List<HistoricTaskInstance> listHistoryMatterPage = leaveService.listHistoryMatterPage(userName);
		for (HistoricTaskInstance t : listHistoryMatterPage) {
			System.out.println(t.toString());
			System.out.println();
		}
		return new ReturnValue(Constant.SUCCESS, "查询成功", listHistoryMatterPage);
	}

	/** 有分叉审批,根据流程实例Id得到当前任务 然后获得task 项目组长审核通过还是不通过 */
	@PostMapping("/examineByHeadmanVariableMsg")
	public ReturnValue examineByHeadmanVariableMsg(String userName,
                                                   String processInstanceId, String msg) {
		Boolean ifTrue = leaveService.examineByPersonnelVariableMsg(userName, processInstanceId, msg);
		if (ifTrue)
			return new ReturnValue(Constant.SUCCESS, userName + "审核成功", null);
		else
			return new ReturnValue(Constant.ERROR, userName + "审核失败", null);
	}


	/** 根据流程实例id删除流程 */
	@PostMapping("/deleteProcessByProcessInstanceId")
	public ReturnValue deleteProcessByProcessInstanceId(HttpServletRequest request, HttpServletResponse response,
                                                        String processInstanceId) throws IOException {
		boolean ifTrue = leaveService.deleteProcessByprocessInstanceId(processInstanceId);
		if (ifTrue)
			return new ReturnValue(Constant.SUCCESS, "删除流程成功", null);
		else
			return new ReturnValue(Constant.ERROR, "删除流程失败", null);
	}

}
