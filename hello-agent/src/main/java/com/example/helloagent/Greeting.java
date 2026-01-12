package com.example.helloagent;

import com.embabel.agent.domain.library.HasContent;

public record Greeting(String text) implements HasContent {

  @Override
  public String getContent() {
    return text;
  }

}
