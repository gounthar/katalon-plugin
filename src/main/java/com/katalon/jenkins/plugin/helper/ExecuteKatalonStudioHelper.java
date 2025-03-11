package com.katalon.jenkins.plugin.helper;

import com.google.common.base.Throwables;
import com.katalon.utils.KatalonUtils;
import com.katalon.utils.Logger;
import hudson.EnvVars;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.TaskListener;
import jenkins.security.MasterToSlaveCallable;

import java.util.HashMap;
import java.util.Map;

public class ExecuteKatalonStudioHelper {

    @SuppressWarnings("NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE")
    public static boolean executeKatalon(
            FilePath workspace,
            EnvVars buildEnvironment,
            Launcher launcher,
            TaskListener taskListener,
            String version,
            String location,
            String executeArgs,
            String x11Display,
            String xvfbConfiguration) {
        Logger logger = new JenkinsLogger(taskListener);
        try {
            if (launcher == null || launcher.getChannel() == null) {
                logger.info("Launcher channel is null");
                return false;
            }
            return launcher.getChannel().call(new MasterToSlaveCallable<Boolean, Exception>() {
                @Override
                public Boolean call() throws Exception {

                    Logger logger = new JenkinsLogger(taskListener);

                    if (workspace != null) {
                        String workspaceLocation = workspace.getRemote();

                        if (workspaceLocation != null) {
                            Map<String, String> environmentVariables = new HashMap<>();
                            environmentVariables.putAll(System.getenv());
                            buildEnvironment.entrySet()
                                    .forEach(entry -> environmentVariables.put(entry.getKey(), entry.getValue()));
                            return KatalonUtils.executeKatalon(
                                    logger,
                                    version,
                                    location,
                                    workspaceLocation,
                                    executeArgs,
                                    x11Display,
                                    xvfbConfiguration,
                                    environmentVariables);
                        } else {
                            logger.info("Workspace location is null");
                            return false;
                        }
                    } else {
                        logger.info("Workspace is null");
                        return false;
                    }
                }
            });
        } catch (Exception e) {
            String stackTrace = Throwables.getStackTraceAsString(e);
            logger.info(stackTrace);
            return false;
        }
    }
}
