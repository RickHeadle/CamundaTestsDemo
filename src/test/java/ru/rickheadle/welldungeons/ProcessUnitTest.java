package ru.rickheadle.welldungeons;

import static org.camunda.bpm.engine.test.assertions.bpmn.AbstractAssertions.init;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.assertThat;

import java.sql.SQLException;
import org.apache.ibatis.logging.LogFactory;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.extension.process_test_coverage.junit5.ProcessEngineCoverageExtension;
import org.camunda.community.mockito.DelegateExpressions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Test case starting an in-memory database-backed Process Engine.
 */
@ExtendWith(ProcessEngineCoverageExtension.class)
@Deployment(resources = "process.bpmn")
public class ProcessUnitTest {

  private static final String PROCESS_DEFINITION_KEY = "CamundaTestsDemo";
  public static ProcessEngine processEngine;

  static {
    LogFactory.useSlf4jLogging(); // MyBatis
    DelegateExpressions.autoMock("process.bpmn");
  }


  @BeforeAll
  public static void setup() {
    init(processEngine);
  }

  /**
   * Just tests if the process definition is deployable.
   */

  @Test
  @Deployment(resources = "process.bpmn")
  public void testParsingAndDeployment() {
    // nothing is done here, as we just want to check for exceptions during deployment
  }

  @Test
  @Deployment(resources = "process.bpmn")
  public void testHappyPath() throws SQLException {
    ProcessInstance processInstance = processEngine.getRuntimeService()
        .startProcessInstanceByKey(PROCESS_DEFINITION_KEY);

    assertThat(processInstance).isEnded();

    // To inspect the DB, run the following line in the debugger
    // then connect your browser to: http://localhost:8082
    // and enter the JDBC URL: jdbc:h2:mem:camunda
//    org.h2.tools.Server.createWebServer("-web").start();

  }

}
