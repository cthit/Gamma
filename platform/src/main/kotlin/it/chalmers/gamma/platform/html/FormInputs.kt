package it.chalmers.gamma.platform.html

import kotlinx.html.FlowContent
import kotlinx.html.InputType
import kotlinx.html.hiddenInput

fun FlowContent.csrfInput(token: String) {
    hiddenInput(name = "_csrf") { value = token }
}

fun FlowContent.methodOverrideInput(method: String) {
    hiddenInput(name = "_method") {
        type = InputType.hidden
        value = method.uppercase()
    }
}
