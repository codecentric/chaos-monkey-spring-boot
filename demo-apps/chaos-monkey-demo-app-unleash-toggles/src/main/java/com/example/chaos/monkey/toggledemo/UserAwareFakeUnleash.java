/*
 * Copyright 2021-2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.chaos.monkey.toggledemo;

import io.getunleash.FakeUnleash;
import io.getunleash.MoreOperations;
import io.getunleash.Unleash;
import io.getunleash.UnleashContext;
import io.getunleash.UnleashContextProvider;
import io.getunleash.Variant;
import java.util.List;

/**
 * Note implementing your own Unleash isn't typically needed. But for the
 * purpose of this demo I am creating one so we can use FakeUnleash but still
 * demonstrate using a Context.
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

        if (toggleName.equals("chaos.monkey.howdy") && context.getUserId().orElse("").equals("chaosuser")) {
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
