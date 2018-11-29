package ee.veikokaap.idea.plugins.netbeans

import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.options.ConfigurationException
import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.LabeledComponent
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.ui.PanelWithAnchor
import com.intellij.util.io.exists
import com.intellij.util.io.isDirectory
import com.intellij.util.io.systemIndependentPath
import com.intellij.util.ui.UIUtil
import ee.veikokaap.idea.plugins.base.runconfig.BasicJavaApplicationSettingsPanel
import ee.veikokaap.idea.plugins.base.util.fillHorizontally
import ee.veikokaap.idea.plugins.base.util.fillVertically
import ee.veikokaap.idea.plugins.base.util.gridBagConstraints
import ee.veikokaap.idea.plugins.base.util.nextRow
import java.awt.BorderLayout
import java.awt.GridBagLayout
import java.io.IOException
import java.nio.file.InvalidPathException
import java.nio.file.Paths
import javax.swing.JComponent
import javax.swing.JPanel

class NetBeansPluginSettingsEditor(project: Project) : SettingsEditor<NetBeansPluginRunConfiguration>(), PanelWithAnchor {
  
  private val netBeansInstallDirComponent = LabeledComponent.create(TextFieldWithBrowseButton(), "NetBeans installation directory", BorderLayout.WEST)
  private val netBeansUserDir = LabeledComponent.create(TextFieldWithBrowseButton(), "NetBeans user directory", BorderLayout.WEST)
  private val javaAppSettingsPanel = BasicJavaApplicationSettingsPanel(project)
  private var anchor: JComponent? = UIUtil.mergeComponentsWithAnchor(netBeansInstallDirComponent, netBeansUserDir, javaAppSettingsPanel)
  
  override fun createEditor(): JComponent {
    val panel = JPanel()
    panel.layout = GridBagLayout()
    
    netBeansInstallDirComponent.component.addBrowseFolderListener(
        "Select NetBeans Installation Directory", "Select NetBeans Installation Directory", null, FileChooserDescriptorFactory.createSingleFolderDescriptor()
    )
    
    netBeansUserDir.component.addBrowseFolderListener(
        "Select NetBeans User Directory", "Select NetBeans User Directory", null, FileChooserDescriptorFactory.createSingleFolderDescriptor()
    )
    
    val constraints = gridBagConstraints(gridx = 0, gridy = 0).fillHorizontally(weightx = 1.0)
    panel.add(netBeansInstallDirComponent, constraints)
    panel.add(netBeansUserDir, constraints.nextRow())
    panel.add(javaAppSettingsPanel, constraints.nextRow().fillVertically(weighty = 1.0))
    
    return panel
  }
  
  override fun resetEditorFrom(configuration: NetBeansPluginRunConfiguration) {
    javaAppSettingsPanel.reset(configuration)
    netBeansInstallDirComponent.component.text = configuration.netbeansInstallationDir
    netBeansUserDir.component.text = configuration.netbeansUserDir
  }
  
  override fun applyEditorTo(configuration: NetBeansPluginRunConfiguration) {
    javaAppSettingsPanel.applyTo(configuration)
    try {
      val netBeansInstallDirPath = getPathIfExistsAndDirectory(netBeansInstallDirComponent.component.text, "NetBeans installation directory")
      configuration.netbeansInstallationDir = netBeansInstallDirPath
      
      val netBeansUserDirPath = getPathIfExistsAndDirectory(netBeansUserDir.component.text, "Preferred NetBeans user directory")
      configuration.netbeansUserDir = netBeansUserDirPath
    }
    catch (e: IOException) {
      throw ConfigurationException(e.message)
    }
  }
  
  override fun getAnchor(): JComponent? {
    return anchor
  }
  
  override fun setAnchor(anchor: JComponent?) {
    this.anchor = anchor
    netBeansInstallDirComponent.anchor = anchor
    netBeansUserDir.anchor = anchor
    javaAppSettingsPanel.anchor = anchor
  }
  
  @Throws(ConfigurationException::class)
  private fun getPathIfExistsAndDirectory(pathText: String, directoryNameForErrors: String): String {
    val trimmedPathText = pathText.trim()
    if (trimmedPathText.isEmpty()) {
      throw ConfigurationException("$directoryNameForErrors path is empty!")
    }
    
    if (trimmedPathText.contains("$")) {
      return trimmedPathText
    }
    
    try {
      val dirPath = Paths.get(trimmedPathText)
      if (!dirPath.exists()) {
        throw ConfigurationException("$directoryNameForErrors does not exist!")
      }
      if (!dirPath.isDirectory()) {
        throw ConfigurationException("$directoryNameForErrors is not a directory!")
      }
      return dirPath.toAbsolutePath().systemIndependentPath
    }
    catch (e: InvalidPathException) {
      throw ConfigurationException(e.message)
    }
  }
}
