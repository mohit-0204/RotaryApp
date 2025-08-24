package com.rotary.hospital.feature.home.data.repository

import com.rotary.hospital.feature.home.domain.repository.TermsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import kotlinx.io.IOException
import org.jetbrains.compose.resources.ExperimentalResourceApi
import rotaryhospital.composeapp.generated.resources.Res

@OptIn(ExperimentalResourceApi::class)
class TermsRepositoryImpl : TermsRepository {
    override suspend fun getTermsHtml(): String = withContext(Dispatchers.IO) {
        return@withContext try {
            // ensure this file exists in the specified resource path
            Res.readBytes("files/t&c.html").decodeToString()
        } catch (_: Exception) {
            // Return a default value, an empty string, or rethrow as a custom exception
            "Unable to load Terms and Conditions."
        }
    }
}

