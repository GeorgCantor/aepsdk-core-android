/*
  Copyright 2023 Adobe. All rights reserved.
  This file is licensed to you under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License. You may obtain a copy
  of the License at http://www.apache.org/licenses/LICENSE-2.0
  Unless required by applicable law or agreed to in writing, software distributed under
  the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR REPRESENTATIONS
  OF ANY KIND, either express or implied. See the License for the specific language
  governing permissions and limitations under the License.
*/

package com.adobe.marketing.mobile.services.ui

import android.app.Application
import com.adobe.marketing.mobile.services.ui.alert.AlertPresentable
import com.adobe.marketing.mobile.services.ui.common.AppLifecycleProvider
import com.adobe.marketing.mobile.services.ui.floatingbutton.FloatingButtonPresentable
import com.adobe.marketing.mobile.services.ui.floatingbutton.FloatingButtonViewModel
import com.adobe.marketing.mobile.services.ui.message.InAppMessagePresentable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

/**
 * UI Service implementation for AEP SDK
 */
internal class AEPUIService : UIService {
    private var presentationDelegate: PresentationDelegate? = null

    @Suppress("UNCHECKED_CAST")
    override fun <T : Presentation<T>> create(
        presentation: T,
        presentationUtilityProvider: PresentationUtilityProvider
    ): Presentable<T> {
        // start the app lifecycle provider if not started. Calling this multiple times is safe.
        val application: Application = presentationUtilityProvider.getApplication()
            ?: throw IllegalStateException("Application is null. Please provide a valid application instance.")

        AppLifecycleProvider.INSTANCE.start(application)

        when (presentation) {
            is InAppMessage -> {
                return InAppMessagePresentable(
                    presentation,
                    presentationDelegate,
                    presentationUtilityProvider,
                    AppLifecycleProvider.INSTANCE,
                    CoroutineScope(Dispatchers.Main)
                ) as Presentable<T>
            }

            is Alert -> {
                return AlertPresentable(
                    presentation,
                    presentationDelegate,
                    presentationUtilityProvider,
                    AppLifecycleProvider.INSTANCE
                ) as Presentable<T>
            }

            is FloatingButton -> {
                return FloatingButtonPresentable(
                    presentation,
                    FloatingButtonViewModel(presentation.settings),
                    presentationDelegate,
                    presentationUtilityProvider,
                    AppLifecycleProvider.INSTANCE
                ) as Presentable<T>
            }

            else -> {
                throw IllegalArgumentException("Presentation type: $presentation not supported")
            }
        }
    }

    override fun setPresentationDelegate(presentationDelegate: PresentationDelegate) {
        this.presentationDelegate = presentationDelegate
    }
}
