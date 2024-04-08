package ru.rickheadle.welldungeons;

import java.util.HashMap;
import java.util.Map;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests;
import org.camunda.bpm.extension.process_test_coverage.junit5.ProcessEngineCoverageExtension;
import org.camunda.community.mockito.DelegateExpressions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(ProcessEngineCoverageExtension.class)
@Deployment(resources = "bpmn/process3_formsAndExceptions.bpmn")
public class FormsAndExceptionsProcessTest {

  static {
    DelegateExpressions.autoMock("bpmn/process3_formsAndExceptions.bpmn");
  }

  private static final String PROCESS_DEFINITION_KEY = "Process_1wfkkej";
  public static ProcessEngine processEngine;

  @Test
  public void processStart_thenCompletedSuccessfully() {
    // nothing is done here, as we just want to check for exceptions during deployment
  }

  @Test
  public void testSuccessPath() {
    RuntimeService runtimeService = processEngine.getRuntimeService();
    ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(PROCESS_DEFINITION_KEY);

    TaskService taskService = processEngine.getTaskService();
    Task task = taskService.createTaskQuery().singleResult();
    BpmnAwareTests.assertThat(task).isNotNull();
    BpmnAwareTests.assertThat(task).hasName("Service1");

    Map<String, Object> variables = new HashMap<>();
    variables.put("result", "success");
    taskService.complete(task.getId(), variables);

    BpmnAwareTests.assertThat(processInstance).hasPassedInOrder(
        "StartEvent_1",
        "Activity_03fnlbv",
        "Gateway_1gjcmu9",
        "Event_1llrevq"
    );
    BpmnAwareTests.assertThat(processInstance).hasNotPassed("Event_04sjwwd", "Event_1ga924i");
    BpmnAwareTests.assertThat(processInstance).hasPassed("Event_1llrevq").isEnded();
  }

  @Test
  public void testFailurePath() {
    RuntimeService runtimeService = processEngine.getRuntimeService();
    ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(PROCESS_DEFINITION_KEY);

    TaskService taskService = processEngine.getTaskService();
    Task task = taskService.createTaskQuery().singleResult();
    BpmnAwareTests.assertThat(task).isNotNull();
    BpmnAwareTests.assertThat(task).hasName("Service1");

    Map<String, Object> variables = new HashMap<>();
    variables.put("result", "failure");
    taskService.complete(task.getId(), variables);

    BpmnAwareTests.assertThat(processInstance).hasPassedInOrder(
        "StartEvent_1",
        "Activity_03fnlbv",
        "Gateway_1gjcmu9",
        "Event_04sjwwd"
    );
    BpmnAwareTests.assertThat(processInstance).hasNotPassed("Flow_1xok49y", "Event_1ga924i");
    BpmnAwareTests.assertThat(processInstance).hasPassed("Event_04sjwwd").isEnded();
  }

  @Test
  public void testErrorPath() {
    RuntimeService runtimeService = processEngine.getRuntimeService();
    ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(PROCESS_DEFINITION_KEY);

    TaskService taskService = processEngine.getTaskService();
    Task task = taskService.createTaskQuery().singleResult();
    BpmnAwareTests.assertThat(task).isNotNull();
    BpmnAwareTests.assertThat(task).hasName("Service1");

    Map<String, Object> variables = new HashMap<>();
    variables.put("result", "error");
    taskService.complete(task.getId(), variables);

    BpmnAwareTests.assertThat(processInstance).hasPassedInOrder(
        "StartEvent_1",
        "Activity_03fnlbv",
        "Event_1ga924i"
    );
    BpmnAwareTests.assertThat(processInstance).hasNotPassed("Gateway_1gjcmu9", "Event_1llrevq", "Event_04sjwwd");
    BpmnAwareTests.assertThat(processInstance).hasPassed("Event_1ga924i").isEnded();
  }
}
