/*
 * Copyright (C) 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package android.platform.test.rule;

import androidx.annotation.VisibleForTesting;

import org.junit.runner.Description;
import org.junit.runners.model.InitializationError;

/**
 * This rule will kill the provided apps before/after running each test method.
 */
public class KillAppsRule extends TestWatcher {
    private final String[] mApplications;
    private boolean mKillAppsBeforeTest;

    @VisibleForTesting
    static final String KILL_APP = "kill-app";

    public KillAppsRule() throws InitializationError {
        throw new InitializationError("Must supply an application to kill.");
    }

    public KillAppsRule(String... applications) {
        this(true, applications);
    }

    public KillAppsRule(boolean before, String... applications) {
        mApplications = applications;
        mKillAppsBeforeTest = before;
    }

    @Override
    protected void starting(Description description) {
        if (mKillAppsBeforeTest) {
            killApp();
        }

    }

    @Override
    protected void finished(Description description) {
        if (!mKillAppsBeforeTest) {
            killApp();
        }
    }

    private void killApp() {
        // Check if killing the app after launch is selected or not.
        boolean killApp = Boolean.parseBoolean(getArguments().getString(KILL_APP, "true"));
        if (!killApp) {
            return;
        }

        // Stop app processes of each application in sequence if the kill app option is selected.
        for (String app : mApplications) {
            executeShellCommand(String.format("am stop-app --user current %s", app));
        }
    }
}
