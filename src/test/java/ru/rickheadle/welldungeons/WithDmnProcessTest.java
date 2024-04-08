package ru.rickheadle.welldungeons;

import java.util.stream.Stream;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests;
import org.camunda.bpm.engine.variable.VariableMap;
import org.camunda.bpm.engine.variable.Variables;
import org.camunda.bpm.extension.process_test_coverage.junit5.ProcessEngineCoverageExtension;
import org.camunda.bpm.scenario.ProcessScenario;
import org.camunda.bpm.scenario.Scenario;
import org.camunda.bpm.scenario.delegate.ExternalTaskDelegate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

@ExtendWith(ProcessEngineCoverageExtension.class)
@Deployment(resources = {
    WithDmnProcessTest.BPMN_FILE,
    "dmn/process5_chooseDrink.dmn"
})
public class WithDmnProcessTest {

  private static final String PROCESS_DEFINITION_KEY = "Process_1gigsms";
  public static final String BPMN_FILE = "bpmn/process5_withDMN.bpmn";
  private static final ProcessScenario externalLoadScenario = Mockito.mock(ProcessScenario.class);
  private static final ProcessScenario externalSaveScenario = Mockito.mock(ProcessScenario.class);

  @BeforeEach
  public void setUp() {
    Mockito.when(externalLoadScenario.waitsAtServiceTask("Activity_0novjqy"))
        .thenReturn((ExternalTaskDelegate externalTaskDelegate) -> {
          BpmnAwareTests.assertThat(externalTaskDelegate).hasTopicName("loadDrink");
          externalTaskDelegate.complete();
        });

    Mockito.when(externalSaveScenario.waitsAtServiceTask("Activity_116mhlk"))
        .thenReturn((ExternalTaskDelegate externalTaskDelegate) -> {
          BpmnAwareTests.assertThat(externalTaskDelegate).hasTopicName("saveDrink");
          externalTaskDelegate.complete();
        });
  }

  @AfterEach
  public void tearDown() {
    /*Один из немногих способов подружить Scenario и ParameterizedTest
    - иначе ругается на 1+ прогоне что на EndEvent слишком много сценариев */
    Mockito.reset(externalLoadScenario, externalSaveScenario);
  }

  /**
   * Проверка сценариев, когда заданы условия для DMN-таблицы
   */
  @ParameterizedTest
  @MethodSource(value = "provideVariablesForSuccessfulRun")
  public void processStart_thenCompletedSuccessfully(String programmerCondition,
      String isDeadlineSoon, String expectedDrinkSelection) {
    VariableMap variableMap = Variables.createVariables()
        .putValue("programmer_condition", programmerCondition)
        .putValue("is_deadline_soon", isDeadlineSoon);

    Scenario scenario = Scenario.run(externalLoadScenario)
        .startByKey(PROCESS_DEFINITION_KEY, variableMap).execute();

    BpmnAwareTests.assertThat(scenario.instance(externalLoadScenario)).variables()
        .containsEntry("drink_selection", expectedDrinkSelection);

    Mockito.verify(externalLoadScenario, Mockito.times(1))
        .hasCompleted("Event_0zhityi");
  }

  /**
   * Проверяем сценарий, когда не заданы условия для DMN-таблицы
   */

  @ParameterizedTest
  @MethodSource(value = "provideVariablesForUnsuccessfulRun")
  public void processStart_thenCompletedUnsuccessfully(String programmerCondition,
      String isDeadlineSoon, String expectedDrinkSelection) {
    VariableMap variableMap = Variables.createVariables()
        .putValue("programmer_condition", programmerCondition)
        .putValue("is_deadline_soon", isDeadlineSoon);

    Scenario scenario = Scenario.run(externalSaveScenario)
        .startByKey(PROCESS_DEFINITION_KEY, variableMap).execute();

    BpmnAwareTests.assertThat(scenario.instance(externalSaveScenario)).variables()
        .containsEntry("drink_selection", expectedDrinkSelection);

    Mockito.verify(externalSaveScenario, Mockito.times(1))
        .hasCompleted("Event_0zu8ht7");
  }

  private static Stream<Arguments> provideVariablesForSuccessfulRun() {
    return Stream.of(
        Arguments.of("Бодрое", "Скоро дедлайн", "Чёрный чай"),
        Arguments.of("Бодрое", "Дедлайн не скоро", "Чёрный чай"),
        Arguments.of("Бодрое", "Дедлайн был вчера", "Чёрный чай"),
        Arguments.of("Бодрое", "", "Чёрный чай"),
        Arguments.of("Бодрое", null, "Чёрный чай"),
        Arguments.of("Сонное", "Скоро дедлайн", "Зелёный чай"),
        Arguments.of("Сонное", "Дедлайн не скоро", "Какао"),
        Arguments.of("Сонное", "Дедлайн был вчера", "Кофе"),
        Arguments.of("", "Дедлайн не скоро", "Какао"),
        Arguments.of(null, "Дедлайн не скоро", "Какао")
    );
  }

  private static Stream<Arguments> provideVariablesForUnsuccessfulRun() {
    return Stream.of(
        Arguments.of(null, null, "Напиток не выбран"),
        Arguments.of("", null, "Напиток не выбран"),
        Arguments.of(null, "", "Напиток не выбран"),
        Arguments.of("", "", "Напиток не выбран")
    );
  }
}
