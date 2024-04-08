package ru.rickheadle.welldungeons;

import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests;
import org.camunda.bpm.engine.variable.Variables;
import org.camunda.bpm.extension.process_test_coverage.junit5.ProcessEngineCoverageExtension;
import org.camunda.bpm.scenario.ProcessScenario;
import org.camunda.bpm.scenario.Scenario;
import org.camunda.bpm.scenario.delegate.TaskDelegate;
import org.camunda.community.mockito.DelegateExpressions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

@ExtendWith(ProcessEngineCoverageExtension.class)
@Deployment(resources = AsyncServicesProcessTest.BPMN_FILE)
public class AsyncServicesProcessTest {

  public static final String BPMN_FILE = "bpmn/process4_asyncServices.bpmn";
  private static final String PROCESS_DEFINITION_KEY = "Process_1ej5hys";
  private static final ProcessScenario processScenario = Mockito.mock(ProcessScenario.class);

  static {
    DelegateExpressions.autoMock(AsyncServicesProcessTest.BPMN_FILE);
  }

  @BeforeEach
  public void stubs() {
    // stubs for async services
    Mockito.when(processScenario.waitsAtUserTask("Activity_12p8mih"))
        .thenReturn(TaskDelegate::complete);
  }

  @Test
  void processStart_thenCompletedSuccessfully() {
    Scenario scenario = Scenario.run(processScenario)
        .startByKey(PROCESS_DEFINITION_KEY, "businessKey", Variables.createVariables())
        .execute();

    BpmnAwareTests.assertThat(scenario.instance(processScenario))
        .hasPassed("Activity_0o0hcu9");
    Mockito.verify(processScenario).waitsAtUserTask("Activity_12p8mih");
    Mockito.verify(processScenario).hasCompleted("Event_0e2p83n");
  }
}
