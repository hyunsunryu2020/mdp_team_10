package com.team37.mdpandroid.gui.util

import org.json.JSONObject

class JSONBuilder {

    companion object{
        var json = JSONObject()
        fun refreshJson(): JSONBuilder.Companion{
            json = JSONObject()
            return this
        }
        fun addParameter(key: String, value: String): JSONBuilder.Companion{
            json.put(key, value)
            return this
        }
        fun getJsonObject(): JSONObject{
            return json
        }
    }

}