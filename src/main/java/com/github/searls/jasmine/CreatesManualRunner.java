package com.github.searls.jasmine;

import java.io.File;
import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.logging.Log;

import com.github.searls.jasmine.io.FileUtilsWrapper;
import com.github.searls.jasmine.io.scripts.ProjectDirScripResolver;
import com.github.searls.jasmine.runner.ReporterType;
import com.github.searls.jasmine.runner.SpecRunnerHtmlGenerator;
import com.github.searls.jasmine.runner.SpecRunnerHtmlGeneratorFactory;

public class CreatesManualRunner {

  private final FileUtilsWrapper fileUtilsWrapper = new FileUtilsWrapper();
  private final AbstractJasmineMojo config;

  private Log log;

  public CreatesManualRunner(AbstractJasmineMojo config) {
    this.config = config;
    log = config.getLog();
  }

  public void create() throws IOException {
    File runnerDestination = new File(config.jasmineTargetDir,config.manualSpecRunnerHtmlFileName);

    ProjectDirScripResolver projectDirScripResolver = new ProjectDirScripResolver(config);
    
    SpecRunnerHtmlGenerator generator = new SpecRunnerHtmlGeneratorFactory().create(ReporterType.HtmlReporter, config, projectDirScripResolver);

    String newRunnerHtml = generator.generateWitRelativePaths();
    if(newRunnerDiffersFromOldRunner(runnerDestination, newRunnerHtml)) {
      saveRunner(runnerDestination, newRunnerHtml);
    } else {
      log.info("Skipping spec runner generation, because an identical spec runner already exists.");
    }
  }

  private String existingRunner(File destination) throws IOException {
    String existingRunner = null;
    try {
      if(destination.exists()) {
        existingRunner = fileUtilsWrapper.readFileToString(destination);
      }
    } catch(Exception e) {
      log.warn("An error occurred while trying to open an existing manual spec runner. Continuing.");
    }
    return existingRunner;
  }

  private boolean newRunnerDiffersFromOldRunner(File runnerDestination, String newRunner) throws IOException {
    return !StringUtils.equals(newRunner, existingRunner(runnerDestination));
  }

  private void saveRunner(File runnerDestination, String newRunner) throws IOException {
    fileUtilsWrapper.writeStringToFile(runnerDestination, newRunner, config.sourceEncoding);
  }

  public void setLog(Log log) {
    this.log = log;
  }
}
