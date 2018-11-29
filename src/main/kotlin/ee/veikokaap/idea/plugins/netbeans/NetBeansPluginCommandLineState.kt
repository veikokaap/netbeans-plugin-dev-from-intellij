package ee.veikokaap.idea.plugins.netbeans

import com.intellij.execution.ExecutionException
import com.intellij.execution.Executor
import com.intellij.execution.application.BaseJavaApplicationCommandLineState
import com.intellij.execution.configurations.JavaParameters
import com.intellij.execution.process.OSProcessHandler
import com.intellij.execution.process.ProcessAdapter
import com.intellij.execution.process.ProcessEvent
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.execution.util.JavaParametersUtil
import com.intellij.util.containers.forEachGuaranteed
import org.jetbrains.idea.maven.execution.MavenExternalParameters
import org.jetbrains.idea.maven.execution.MavenRunConfiguration
import org.jetbrains.idea.maven.project.MavenProjectsManager

class NetBeansPluginCommandLineState(runConfiguration: NetBeansPluginRunConfiguration, executor: Executor, environment: ExecutionEnvironment)
  : BaseJavaApplicationCommandLineState<NetBeansPluginRunConfiguration>(environment, runConfiguration) {
  
  @Throws(ExecutionException::class)
  override fun createJavaParameters(): JavaParameters {
    val mavenSettings = MavenRunConfiguration.MavenSettings(configuration.project)
    val javaParameters = MavenExternalParameters.createJavaParameters(configuration.project, mavenSettings.myRunnerParameters)
  
    val jrePath = if (configuration.isAlternativeJrePathEnabled) configuration.alternativeJrePath else null
    javaParameters.jdk = JavaParametersUtil.createProjectJdk(configuration.project, jrePath)
    
    val previousVmArgs = javaParameters.vmParametersList.clone()
    javaParameters.vmParametersList.clearAll()
    val previousProgramArgs = javaParameters.programParametersList.clone()
    javaParameters.programParametersList.clearAll()
    
    this.setupJavaParameters(javaParameters)
  
    val newVmArgs = javaParameters.vmParametersList.clone()
    javaParameters.vmParametersList.clearAll()
    val newProgramArgs = javaParameters.programParametersList.clone()
    javaParameters.programParametersList.clearAll()
  
    javaParameters.programParametersList.addAll(previousProgramArgs.parameters)
    previousProgramArgs.paramsGroups.forEachGuaranteed { javaParameters.programParametersList.addParamsGroup(it) }
    javaParameters.vmParametersList.addAll(previousVmArgs.parameters)
    previousVmArgs.paramsGroups.forEachGuaranteed { javaParameters.vmParametersList.addParamsGroup(it) }
    
    javaParameters.programParametersList.addAll(
        "nbm:run-ide",
        "-Dnetbeans.installation=${configuration.netbeansInstallationDir}"
    )
    
    val nbArgs = newProgramArgs.parameters
        .map { it.trim() }
        .filter { it.isNotEmpty() }
        .joinToString(separator = " ")
    
    val vmArgs = newVmArgs.parameters
        .map { it.trim() }
        .filter { it.isNotEmpty() }
        .map { "-J$it" }
        .joinToString(separator = " ")
    
    if (vmArgs.isNotEmpty() || nbArgs.isNotEmpty()) {
      val runParams = "-Dnetbeans.run.params=$nbArgs $vmArgs"
      javaParameters.programParametersList.add(runParams)
    }
    
    return javaParameters
  }
  
  @Throws(ExecutionException::class)
  override fun startProcess(): OSProcessHandler {
    val result = super.startProcess()
    result.setShouldDestroyProcessRecursively(true)
    result.addProcessListener(object : ProcessAdapter() {
      override fun processTerminated(event: ProcessEvent) {
        MavenProjectsManager.getInstance(configuration.project).updateProjectTargetFolders()
      }
    })
    
    return result
  }
}