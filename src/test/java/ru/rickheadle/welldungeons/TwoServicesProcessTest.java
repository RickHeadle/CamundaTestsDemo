package ru.rickheadle.welldungeons;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests;
import org.camunda.bpm.extension.process_test_coverage.junit5.ProcessEngineCoverageExtension;
import org.camunda.community.mockito.DelegateExpressions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(ProcessEngineCoverageExtension.class)
@Deployment(resources = "bpmn/process2_twoServices.bpmn")
public class TwoServicesProcessTest {

  static {
    DelegateExpressions.autoMock("bpmn/process2_twoServices.bpmn");
  }

  private static final String PROCESS_DEFINITION_KEY = "Process_1ionbjg";
  public static ProcessEngine processEngine;

  @Test
  public void processStart_thenCompletedSuccessfully() {
    // nothing is done here, as we just want to check for exceptions during deployment
  }

  @Test
  public void testHappyPath() {
    RuntimeService runtimeService = processEngine.getRuntimeService();
    ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(PROCESS_DEFINITION_KEY);

    BpmnAwareTests.assertThat(processInstance).hasPassed("Activity_1ipqssb", "Activity_1arbtdv");
    BpmnAwareTests.assertThat(processInstance).isEnded();
  }
}
