package dev.sasikanth.pinnit.editor

import java.util.UUID

sealed class EditorScreenEffect

data class LoadNotification(val uuid: UUID) : EditorScreenEffect()

data class SaveNotificationAndCloseEditor(val title: String, val content: String?) : EditorScreenEffect()

data class UpdateNotificationAndCloseEditor(val notificationUuid: UUID, val title: String, val content: String?) : EditorScreenEffect()

object CloseEditor : EditorScreenEffect()

object ShowConfirmExitEditor : EditorScreenEffect()

object SetEmptyTitleAndContent : EditorScreenEffect()
