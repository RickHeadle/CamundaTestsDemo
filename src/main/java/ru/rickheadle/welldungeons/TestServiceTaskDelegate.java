package ru.rickheadle.welldungeons;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

@Slf4j
public class TestServiceTaskDelegate implements JavaDelegate {

  @Override
  public void execute(DelegateExecution delegateExecution) {
    log.info("TestServiceTaskDelegate called");
  }
}
