package com.example.chaos.monkey.toggledemo;

import io.getunleash.FakeUnleash;
import io.getunleash.MoreOperations;
import io.getunleash.Unleash;
import io.getunleash.UnleashContext;
import io.getunleash.UnleashContextProvider;
import io.getunleash.Variant;
import java.util.List;

/**
 * Note implementing your own Unleash isn't typically needed. But for the purpose of this demo I am
 * creating one so we can use FakeUnleash but still demonstrate using a Context.
 */
public class UserAwareFakeUnleash implements Unleash {

  private final FakeUnleash fakeUnleash = new FakeUnleash();
  private final UnleashContextProvider contextProvider;

  public UserAwareFakeUnleash(UnleashContextProvider contextProvider) {
    this.contextProvider = contextProvider;
  }

  public void enable(String featureName) {
    fakeUnleash.enable(featureName);
  }

  @Override
  public boolean isEnabled(String toggleName) {
    UnleashContext context = this.contextProvider.getContext();

    if (toggleName.equals("chaos.monkey.howdy")
        && context.getUserId().orElse("").equals("chaosuser")) {
      return true;
    }

    return fakeUnleash.isEnabled(toggleName, context);
  }

  @Override
  public boolean isEnabled(String s, boolean b) {
    return fakeUnleash.isEnabled(s, b);
  }

  @Override
  public Variant getVariant(String s, UnleashContext unleashContext) {
    return fakeUnleash.getVariant(s, unleashContext);
  }

  @Override
  public Variant getVariant(String s, UnleashContext unleashContext, Variant variant) {
    return fakeUnleash.getVariant(s, unleashContext);
  }

  @Override
  public Variant getVariant(String s) {
    return fakeUnleash.getVariant(s);
  }

  @Override
  public Variant getVariant(String s, Variant variant) {
    return fakeUnleash.getVariant(s, variant);
  }

  @Override
  public List<String> getFeatureToggleNames() {
    return null;
  }

  @Override
  public MoreOperations more() {
    return null;
  }
}
