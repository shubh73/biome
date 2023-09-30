package com.github.biomejs.intellijbiome.pages

import com.intellij.remoterobot.RemoteRobot
import com.intellij.remoterobot.data.RemoteComponent
import com.intellij.remoterobot.fixtures.*
import com.intellij.remoterobot.stepsProcessing.step
import com.intellij.remoterobot.utils.waitFor
import java.time.Duration

fun RemoteRobot.idea(function: IdeaFrame.() -> Unit) {
	find<IdeaFrame>(timeout = Duration.ofSeconds(10)).apply(function)
}

@FixtureName("Idea frame")
@DefaultXpath("IdeFrameImpl type", "//div[@class='IdeFrameImpl']")
class IdeaFrame(remoteRobot: RemoteRobot, remoteComponent: RemoteComponent) :
	CommonContainerFixture(remoteRobot, remoteComponent) {
	val menuBar: JMenuBarFixture
		get() = step("Menu...") {
			return@step remoteRobot.find(JMenuBarFixture::class.java, JMenuBarFixture.byType())
		}

	@JvmOverloads
	fun dumbAware(timeout: Duration = Duration.ofMinutes(5), function: () -> Unit) {
		step("Wait for smart mode") {
			waitFor(duration = timeout, interval = Duration.ofSeconds(5)) {
				runCatching { isDumbMode().not() }.getOrDefault(false)
			}
			function()
			step("..wait for smart mode again") {
				waitFor(duration = timeout, interval = Duration.ofSeconds(5)) {
					isDumbMode().not()
				}
			}
		}
	}

	fun isDumbMode(): Boolean {
		return callJs(
			"""
            const frameHelper = com.intellij.openapi.wm.impl.ProjectFrameHelper.getFrameHelper(component)
            if (frameHelper) {
                const project = frameHelper.getProject()
                project ? com.intellij.openapi.project.DumbService.isDumb(project) : true
            } else {
                true
            }
        """, true
		)
	}

	fun openFile(path: String) {
		runJs(
			"""
            importPackage(com.intellij.openapi.fileEditor)
            importPackage(com.intellij.openapi.vfs)
            importPackage(com.intellij.openapi.wm.impl)

            const path = '$path'
            const frameHelper = ProjectFrameHelper.getFrameHelper(component)
            if (frameHelper) {
                const project = frameHelper.getProject()
                const projectPath = project.getBasePath()
                const file = LocalFileSystem.getInstance().findFileByPath(projectPath + '/' + path)
                FileEditorManager.getInstance(project).openTextEditor(
                    new OpenFileDescriptor(
                        project,
                        file
                    ), true
                )
            }
        """, true
		)
	}
}
