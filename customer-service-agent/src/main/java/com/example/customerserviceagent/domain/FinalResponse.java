package com.example.customerserviceagent.domain;

import com.embabel.agent.domain.library.HasContent;
import org.jetbrains.annotations.NotNull;

public record FinalResponse(String resolutionSummary) implements HasContent {

  @Override
  public @NotNull String getContent() {
    return resolutionSummary;
  }

}
