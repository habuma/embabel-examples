package com.example.helloagent;

import com.embabel.agent.api.annotation.AchievesGoal;
import com.embabel.agent.api.annotation.Action;
import com.embabel.agent.api.annotation.Agent;
import com.embabel.agent.api.annotation.Export;
import com.embabel.agent.api.common.Ai;
import com.embabel.agent.domain.io.UserInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Agent(name = "helloAgent",
    description = "Says hello to someone.",
    version = "1.0.0")
public class HelloAgent {

  private static final Logger logger = LoggerFactory.getLogger(HelloAgent.class);

  @Action(description = "Extract person from input")
  public Person extractPerson(UserInput userInput, Ai ai) {
    logger.info("Extracting person from user input: " + userInput.getContent());

    var prompt = """
        From the user input, what is the name of the person to say hello to?
        
        User input: 
        """;

    return ai.withDefaultLlm()
        .createObject(
            prompt + userInput.getContent(), Person.class);
  }

  @Action(description = "Say hello to someone")
  @AchievesGoal(
      description = "Someone has been greeted",
      export = @Export(
          name = "sayHello",
          remote = true,
          startingInputTypes = Person.class))
  public Greeting sayHello(Person person) {
    logger.info("Saying hello to " + person.name());
    return new Greeting(String.format("Hello, %s!", person.name()));
  }

}