package com.dtu.ddd.ecommerce.utils;

import java.util.List;
import java.util.function.Consumer;
import org.mockito.ArgumentCaptor;

import static org.assertj.core.api.Assertions.assertThat;

public final class Assertions {
  public static <A> void assertCaptureSatisfies(Consumer<ArgumentCaptor<A>> captors, Consumer<A> a, Class<A> aa) {
    final ArgumentCaptor<A> captor = ArgumentCaptor.forClass(aa);
    captors.accept(captor);

    assertThat(captor.getAllValues()).as("captor should capture something").isNotEmpty();

    for (A value : captor.getAllValues()) {
      assertThat(value).satisfies(a);
    }
  }

  public static <A> void assertCaptureSatisfiesList(Consumer<ArgumentCaptor<A>> captors, Consumer<List<? extends A>> a, Class<A> aa) {
    final ArgumentCaptor<A> captor = ArgumentCaptor.forClass(aa);
    captors.accept(captor);

    assertThat(captor.getAllValues()).satisfies(a);
  }
}
