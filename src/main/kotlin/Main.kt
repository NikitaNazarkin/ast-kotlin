package custom.project

import custom.project.model.CallTreeBuildResult
import custom.project.service.CallChainBuilder
import custom.project.service.ProjectBuilderV2
import custom.project.service.prompt.PromptBuilder
import java.awt.BorderLayout
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.Insets
import javax.swing.*
import javax.swing.border.EmptyBorder

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
fun main() {
    SwingUtilities.invokeLater {
        createAndShowGUI()
    }
}

fun createAndShowGUI() {
    val frame = JFrame("Call Tree Builder")
    frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
    frame.setSize(700, 600)
    frame.layout = BorderLayout()

    // Панель для ввода данных
    val inputPanel = JPanel()
    inputPanel.layout = GridBagLayout()
    inputPanel.border = EmptyBorder(10, 10, 10, 10)
    val c = GridBagConstraints()
    c.insets = Insets(5, 5, 5, 5)
    c.fill = GridBagConstraints.HORIZONTAL
    c.gridx = 0
    c.gridy = 0

    // Класс
    inputPanel.add(JLabel("Класс:"), c)
    c.gridx = 1
    val classField = JTextField(20)
    inputPanel.add(classField, c)

    // Метод
    c.gridx = 0
    c.gridy++
    inputPanel.add(JLabel("Метод:"), c)
    c.gridx = 1
    val methodField = JTextField(20)
    inputPanel.add(methodField, c)

    // Путь к проекту + кнопка выбора
    c.gridx = 0
    c.gridy++
    inputPanel.add(JLabel("Путь к проекту:"), c)
    c.gridx = 1
    val pathField = JTextField(20)
    inputPanel.add(pathField, c)
    c.gridx = 2
    val chooseButton = JButton("Выбрать...")
    inputPanel.add(chooseButton, c)

    chooseButton.addActionListener {
        val chooser = JFileChooser().apply {
            fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
        }
        val result = chooser.showOpenDialog(frame)
        if (result == JFileChooser.APPROVE_OPTION) {
            val selectedDir = chooser.selectedFile
            pathField.text = selectedDir.absolutePath
            println("Выбран проект: ${selectedDir.absolutePath}")
        }
    }

    // Кнопка запуска анализа
    c.gridx = 0
    c.gridy++
    c.gridwidth = 3
    val generateButton = JButton("Построить дерево вызовов")
    inputPanel.add(generateButton, c)

    frame.add(inputPanel, BorderLayout.NORTH)

    // Панель для вывода результатов и логов
    val resultArea = JTextArea()
    resultArea.isEditable = false
    resultArea.lineWrap = true
    resultArea.wrapStyleWord = true
    val resultScroll = JScrollPane(resultArea)
    resultScroll.border = EmptyBorder(10, 10, 10, 10)

    val logArea = JTextArea(5, 20)
    logArea.isEditable = false
    logArea.lineWrap = true
    logArea.wrapStyleWord = true
    val logScroll = JScrollPane(logArea)
    logScroll.border = EmptyBorder(10, 10, 10, 10)

    val splitPane = JSplitPane(JSplitPane.VERTICAL_SPLIT, resultScroll, logScroll)
    splitPane.resizeWeight = 0.7
    frame.add(splitPane, BorderLayout.CENTER)

    // Функция для логирования
    fun log(message: String) {
        logArea.append(
            "[${
                java.time.LocalTime.now().withNano(0)
            }] $message\n"
        )
        logArea.caretPosition = logArea.document.length
        println(message) // также выводим в консоль
    }

    // Действие при нажатии кнопки
    generateButton.addActionListener {
        val className = classField.text
        val methodName = methodField.text
        val projectPath = pathField.text

        if (className.isBlank() || methodName.isBlank() || projectPath.isBlank()) {
            JOptionPane.showMessageDialog(frame, "Пожалуйста, заполните все поля.")
            return@addActionListener
        }

        log("Запуск анализа для $className.$methodName в проекте $projectPath")

        try {
            val project = ProjectBuilderV2.analyzeProject(projectPath)
            when (val callNode = CallChainBuilder.buildCallTreeForMethod(className, methodName, project)) {
                is CallTreeBuildResult.Success -> {
                    val output = PromptBuilder.generatePrompt(callNode.node)
                    resultArea.text = output
                    log("✅ Дерево вызовов построено успешно")
                }

                is CallTreeBuildResult.Error -> {
                    resultArea.text = "❌ Ошибка: ${callNode.reason}"
                    log("❌ Ошибка при построении дерева: ${callNode.reason}")
                }
            }
        } catch (e: Exception) {
            resultArea.text = "Ошибка: ${e.message}"
            log("❌ Исключение: ${e.message}")
        }
    }

    frame.isVisible = true
}