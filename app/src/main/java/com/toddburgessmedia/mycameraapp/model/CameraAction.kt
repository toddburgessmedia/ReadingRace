package com.toddburgessmedia.mycameraapp.model

sealed class CameraAction

object CameraStart : CameraAction()
object CameraNothingFound : CameraAction()
object CameraFail : CameraAction()

