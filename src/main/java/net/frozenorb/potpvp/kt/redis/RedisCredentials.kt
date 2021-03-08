package net.frozenorb.potpvp.kt.redis

import net.frozenorb.potpvp.PotPvPSI

data class RedisCredentials(
    var host: String = PotPvPSI.getInstance().config.getString("Redis.Host"),
    var port: Int = PotPvPSI.getInstance().config.getInt("Redis.Port"),
    var password: String = PotPvPSI.getInstance().config.getString("Redis.Authentication.Password"),
    var dbId: Int = 0
) {

    fun shouldAuthenticate(): Boolean {
        return password.isNotEmpty() && password.isNotBlank() && PotPvPSI.getInstance().config.getBoolean("LocalRedis.Authentication.Enabled")
    }

    class Builder {
        val credentials: RedisCredentials = RedisCredentials()

        fun host(host: String): Builder {
            credentials.host = host
            return this
        }

        fun port(port: Int): Builder {
            credentials.port = port
            return this
        }

        fun password(password: String): Builder {
            credentials.password = password
            return this
        }

        fun dbId(dbId: Int): Builder {
            credentials.dbId = dbId
            return this
        }

        fun build(): RedisCredentials {
            return credentials
        }
    }

}
