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
@Deployment(resources = "bpmn/process1_basic.bpmn")
public class BasicProcessTest {

  static {
    DelegateExpressions.autoMock("bpmn/process1_basic.bpmn");
  }

  public ProcessEngine processEngine;

  @Test
  public void processStart_thenCompletedSuccessfully() {
    RuntimeService runtimeService = processEngine.getRuntimeService();
    ProcessInstance processInstance = runtimeService
        .startProcessInstanceByKey("CamundaTestsDemo");

    BpmnAwareTests.assertThat(processInstance).isStarted();
    BpmnAwareTests.assertThat(processInstance).isEnded();
  }
}
