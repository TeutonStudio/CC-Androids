package com.thunderbear06.config

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.stream.JsonReader
import net.neoforged.fml.loading.FMLPaths
import java.io.IOException
import java.nio.file.Files

object ConfigLoader {
    @JvmStatic
    fun <T : ConfigState> loadConfig(modID: String, state: T): T {
        val configPath = FMLPaths.CONFIGDIR.get().resolve("$modID.json")
        try {
            if (!Files.exists(configPath)) {
                Files.createDirectories(configPath.parent)
                Files.writeString(configPath, GsonBuilder().setPrettyPrinting().create().toJson(state))
                return state
            }

            JsonReader(Files.newBufferedReader(configPath)).use { reader ->
                val loaded: T? = Gson().fromJson(reader, state.javaClass)
                return loaded ?: state
            }
        } catch (e: IOException) {
            throw RuntimeException("Failed to load config file $configPath", e)
        }
    }

    interface ConfigState
}
