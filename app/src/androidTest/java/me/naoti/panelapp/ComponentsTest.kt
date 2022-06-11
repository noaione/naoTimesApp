package me.naoti.panelapp

import androidx.compose.foundation.layout.Column
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import me.naoti.panelapp.network.models.*
import me.naoti.panelapp.state.rememberAppContextState
import me.naoti.panelapp.state.rememberAppState
import me.naoti.panelapp.ui.components.*
import me.naoti.panelapp.ui.theme.NaoTimesTheme
import org.junit.Rule
import org.junit.Test

class ComponentsTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testAssignmentBox() {
        val textStatus = StatusRole.TL
        composeTestRule.setContent {
            NaoTimesTheme {
                Column {
                    AssignmentBox(type = textStatus, userName = null)
                    AssignmentBox(type = textStatus, userName = "Test User")
                }
            }
        }

        composeTestRule.onNodeWithText(
            "${textStatus.getFull()}: Unknown"
        ).assertExists()
        composeTestRule.onNodeWithText(
            "${textStatus.getFull()}: Test User"
        ).assertExists()
    }

    @Test
    fun testDashboardCard() {
        val sampleKeyVal = AssignmentKeyValueProject(
            "466469077444067372",
            "N4O"
        )
        val project = Project(
            "105914",
            "Sewayaki Kitsune no Senko-san",
            "https://s4.anilist.co/file/anilistcdn/media/anime/cover/medium/bx105914-VXKB0ZA2aVZF.png",
            1554854400,
            StatusProject(
                airtime = 1555507800,
                episode = 2,
                isDone = false,
                progress = StatusTickProject()
            ),
            AssignmentProject(
                sampleKeyVal,
                sampleKeyVal,
                sampleKeyVal,
                sampleKeyVal,
                sampleKeyVal,
                sampleKeyVal,
                sampleKeyVal
            )
        )
        composeTestRule.setContent {
            NaoTimesTheme {
                DashboardProjectCard(project = project)
            }
        }

        composeTestRule.onNodeWithText(project.title).assertExists()
        val progressText = listOf(
            StatusRole.TL.getShort(),
            StatusRole.TLC.getShort(),
            StatusRole.ENC.getShort(),
            StatusRole.ED.getShort(),
            StatusRole.TM.getShort(),
            StatusRole.TS.getShort(),
            StatusRole.QC.getShort(),
        )
        composeTestRule.onNodeWithText("Episode ${project.status.episode} needs").assertExists()
        progressText.forEach { status ->
            composeTestRule.onNodeWithText(status).assertExists()
        }
    }

    @Test
    fun testDeleteDialog() {
        composeTestRule.setContent {
            NaoTimesTheme {
                DeleteDialog()
            }
        }
        composeTestRule.onNodeWithText("Are you sure?").assertExists()
    }
    @Test
    fun testDeleteDialogWithExtraText() {
        val extra = "This will delete episode 2!"
        composeTestRule.setContent {
            NaoTimesTheme {
                DeleteDialog(extraText = extra)
            }
        }
        composeTestRule.onNodeWithText(extra).assertExists()
    }
    @Test
    fun testDeleteDialogParaphrase() {
        composeTestRule.setContent {
            NaoTimesTheme {
                DeleteDialog(verificationWord = "this-is-a-test")
            }
        }
        composeTestRule.onNodeWithText("this-is-a-test").assertExists()
    }
    @Test
    fun testDeleteDialogParaphraseWithExtra() {
        composeTestRule.setContent {
            NaoTimesTheme {
                DeleteDialog(verificationWord = "this-is-a-test", extraText = "Extra text here")
            }
        }
        composeTestRule.onNodeWithText("this-is-a-test").assertExists()
        composeTestRule.onNodeWithText("Extra text here").assertExists()
    }

    @Test
    fun testEditableStaff() {
        composeTestRule.setContent {
            NaoTimesTheme {
                EditableStaff(
                    role = StatusRole.TL,
                    staff = AssignmentKeyValueProject(null, null),
                    projectId = "123",
                    appCtx = rememberAppState()
                )
            }
        }

        composeTestRule.onNodeWithText("Unknown").assertExists()
        composeTestRule.onNode(hasTestTag("EditableStaffEditBtn")).performClick()
        composeTestRule.onNode(hasTestTag("EditableStaffEditBox")).assertExists()
        composeTestRule.onNode(hasTestTag("EditableStaffEditDoneBtn")).performClick()
        composeTestRule.onNodeWithText("Unknown").assertExists()
    }

    @Test
    fun testEditableStaffHasName() {
        composeTestRule.setContent {
            NaoTimesTheme {
                EditableStaff(
                    role = StatusRole.TL,
                    staff = AssignmentKeyValueProject("1234", "N4O"),
                    projectId = "123",
                    appCtx = rememberAppState()
                )
            }
        }

        composeTestRule.onNodeWithText("N4O").assertExists()
        composeTestRule.onNode(hasTestTag("EditableStaffEditBtn")).performClick()
        composeTestRule.onNode(hasTestTag("EditableStaffEditBox")).assertExists()
        composeTestRule.onNodeWithText("1234").assertExists()
        composeTestRule.onNode(hasTestTag("EditableStaffEditDoneBtn")).performClick()
        composeTestRule.onNodeWithText("N4O").assertExists()
    }

    @Test
    fun testEpisodeCardAllProcess() {
        composeTestRule.setContent {
            NaoTimesTheme {
                EpisodeCard(
                    projectId = "123",
                    status = StatusProject(
                        airtime = 1555507800,
                        episode = 1,
                        isDone = false,
                        progress = StatusTickProject()
                    ),
                    appState = rememberAppState(),
                    onStateEdited = {},
                    onRemove = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Episode 1").assertExists()
        composeTestRule.onNodeWithText("On Process").assertExists()
        composeTestRule.onNodeWithText("Finished").assertDoesNotExist()
        composeTestRule.onNode(hasTestTag("EpisodeCardEditBtn")).performClick()
        composeTestRule.onNodeWithText("On Process").assertDoesNotExist()
        composeTestRule.onNode(hasTestTag("EpisodeCardEditBtn")).performClick()
        composeTestRule.onNodeWithText("On Process").assertExists()
    }

    @Test
    fun testEpisodeCardBoth() {
        composeTestRule.setContent {
            NaoTimesTheme {
                EpisodeCard(
                    projectId = "123",
                    status = StatusProject(
                        airtime = 1555507800,
                        episode = 1,
                        isDone = false,
                        progress = StatusTickProject(
                            translated = true
                        )
                    ),
                    appState = rememberAppState(),
                    onStateEdited = {},
                    onRemove = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Episode 1").assertExists()
        composeTestRule.onNodeWithText("On Process").assertExists()
        composeTestRule.onNodeWithText("Finished").assertExists()
        composeTestRule.onNode(hasTestTag("EpisodeCardEditBtn")).performClick()
        composeTestRule.onNodeWithText("On Process").assertDoesNotExist()
        composeTestRule.onNode(hasTestTag("EpisodeCardEditBtn")).performClick()
        composeTestRule.onNodeWithText("On Process").assertExists()
    }

    @Test
    fun testEpisodeCardFinished() {
        composeTestRule.setContent {
            NaoTimesTheme {
                EpisodeCard(
                    projectId = "123",
                    status = StatusProject(
                        airtime = 1555507800,
                        episode = 1,
                        isDone = false,
                        progress = StatusTickProject(
                            translated = true,
                            translateChecked = true,
                            encoded = true,
                            edited = true,
                            timed = true,
                            typeset = true,
                            qualityChecked = true
                        )
                    ),
                    appState = rememberAppState(),
                    onStateEdited = {},
                    onRemove = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Episode 1").assertExists()
        composeTestRule.onNodeWithText("On Process").assertDoesNotExist()
        composeTestRule.onNodeWithText("Finished").assertExists()
        composeTestRule.onNode(hasTestTag("EpisodeCardEditBtn")).performClick()
        composeTestRule.onNodeWithText("Finished").assertDoesNotExist()
        composeTestRule.onNode(hasTestTag("EpisodeCardEditBtn")).performClick()
        composeTestRule.onNodeWithText("Finished").assertExists()
    }
}