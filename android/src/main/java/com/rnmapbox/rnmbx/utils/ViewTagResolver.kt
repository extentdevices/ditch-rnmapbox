package com.rnmapbox.rnmbx.utils

import android.view.View
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.UIManager
import com.facebook.react.uimanager.IllegalViewOperationException
import com.facebook.react.uimanager.UIManagerHelper
import com.facebook.react.uimanager.common.UIManagerType
import com.rnmapbox.rnmbx.BuildConfig

data class ViewTagWaiter<V>(
    val fn: (V) -> Unit,
    val reject: Promise?
)

const val LOG_TAG = "ViewTagResolver"

typealias ViewRefTag = Int
// see https://github.com/rnmapbox/maps/pull/3074
open class ViewTagResolver(val context: ReactApplicationContext) {
    private val createdViews: HashSet<Int> = hashSetOf<Int>()
    private val viewWaiters: HashMap<Int, MutableList<ViewTagWaiter<View>>> = hashMapOf()

    // to be called from view.setId
    fun tagAssigned(viewTag: Int) {
        // mark this tag as "created"
        createdViews.add(viewTag)

        // if anyone is waiting for this tag, fire their callbacks
        val waiters = viewWaiters.remove(viewTag) ?: return
        context.runOnUiQueueThread {
            try {
                // resolveView returns a nullable View
                val resolved: View? = manager.resolveView(viewTag)
                // if it's still null, bail out of this lambda
                if (resolved == null) {
                    return@runOnUiQueueThread
                }
                // invoke each waiter with a non-null View
                waiters.forEach { it.fn(resolved) }
            } catch (err: IllegalViewOperationException) {
                // reject all promises
                waiters.forEach { it.reject?.reject(err) }
            }
        }
    }


    fun viewRemoved(viewTag: Int) {
        viewWaiters.remove(viewTag)
        createdViews.remove(viewTag)
    }

    private val manager : UIManager
        get() =
            if (BuildConfig.IS_NEW_ARCHITECTURE_ENABLED) {
                UIManagerHelper.getUIManager(context, UIManagerType.FABRIC)!!
            } else {
                UIManagerHelper.getUIManager(context, UIManagerType.DEFAULT)!!
            }

    // calls on UiQueueThread with resolved view
    fun <V>withViewResolved(viewTag: Int, reject: Promise? = null, fn: (V) -> Unit) {
        context.runOnUiQueueThread() {
            try {
                val resolvedView: View? = manager.resolveView(viewTag)
                val view = resolvedView as? V
                if (view != null) {
                    fn(view)
                } else {
                    Logger.e(LOG_TAG, "view: $resolvedView found with tag: $viewTag but it's either null or not the correct type")
                    reject?.reject(Throwable("view: $resolvedView found with tag: $viewTag but it's either null or not the correct type"))
                }
            } catch (err: IllegalViewOperationException) {
                if (!createdViews.contains(viewTag)) {
                    viewWaiters.getOrPut(viewTag) { mutableListOf<ViewTagWaiter<View>>() }.add(ViewTagWaiter<View>({ view -> fn(view as V) }, reject))
                } else {
                    reject?.reject(err)
                }
            }
        }
    }
}