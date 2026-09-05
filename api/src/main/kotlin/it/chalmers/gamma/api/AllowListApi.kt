package it.chalmers.gamma.api

import it.chalmers.gamma.users.ActivationCodes
import it.chalmers.gamma.users.Cid
import java.util.concurrent.CancellationException

class AllowListApi(
    private val activationCodes: ActivationCodes,
) {
    fun allowedCids(): List<String> = activationCodes.allowedCids().map { it.value }

    // The batch reports ordinary persistence failures per CID; control-flow exceptions must escape.
    @Suppress("TooGenericExceptionCaught")
    fun allow(rawCids: List<String>): List<String> {
        val failures = mutableListOf<String>()
        for (rawCid in rawCids) {
            val cid =
                try {
                    Cid(rawCid)
                } catch (_: IllegalArgumentException) {
                    failures += rawCid
                    continue
                }
            try {
                activationCodes.allow(cid)
            } catch (failure: CancellationException) {
                throw failure
            } catch (failure: InterruptedException) {
                throw failure
            } catch (_: Exception) {
                // The released batch contract reports ordinary per-CID failures and continues.
                failures += rawCid
            }
        }
        return failures
    }
}
