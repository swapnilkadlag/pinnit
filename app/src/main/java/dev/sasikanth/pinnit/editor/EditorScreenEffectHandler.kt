package dev.sasikanth.pinnit.editor

import com.spotify.mobius.functions.Consumer
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import dev.sasikanth.pinnit.mobius.CoroutineConnectable
import dev.sasikanth.pinnit.notifications.NotificationRepository
import dev.sasikanth.pinnit.utils.DispatcherProvider
import dev.sasikanth.pinnit.utils.notification.NotificationUtil

class EditorScreenEffectHandler @AssistedInject constructor(
  private val notificationRepository: NotificationRepository,
  private val dispatcherProvider: DispatcherProvider,
  private val notificationUtil: NotificationUtil,
  @Assisted private val viewEffectConsumer: Consumer<EditorScreenViewEffect>
) : CoroutineConnectable<EditorScreenEffect, EditorScreenEvent>(dispatcherProvider.main) {

  @AssistedInject.Factory
  interface Factory {
    fun create(viewEffectConsumer: Consumer<EditorScreenViewEffect>): EditorScreenEffectHandler
  }

  override suspend fun handler(effect: EditorScreenEffect, dispatchEvent: (EditorScreenEvent) -> Unit) {
    when (effect) {
      is LoadNotification -> {
        val notification = notificationRepository.notification(effect.uuid)
        dispatchEvent(NotificationLoaded(notification))
        viewEffectConsumer.accept(SetTitle(notification.title))
        viewEffectConsumer.accept(SetContent(notification.content))
      }

      is SaveNotificationAndCloseEditor -> {
        val notification = notificationRepository.save(effect.title, effect.content)
        notificationUtil.showNotification(notification)
        viewEffectConsumer.accept(CloseEditorView)
      }

      is UpdateNotificationAndCloseEditor -> {
        val notification = notificationRepository.notification(effect.notificationUuid)
        val updatedNotification = notification.copy(
          title = effect.title,
          content = effect.content
        )
        val notificationUpdated = notificationRepository.updateNotification(updatedNotification)
        notificationUtil.showNotification(notificationUpdated)
        viewEffectConsumer.accept(CloseEditorView)
      }

      CloseEditor -> {
        viewEffectConsumer.accept(CloseEditorView)
      }
    }
  }
}
