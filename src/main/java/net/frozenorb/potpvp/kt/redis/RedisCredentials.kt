package net.frozenorb.potpvp.kt.redis

import net.frozenorb.potpvp.PotPvPND

data class RedisCredentials(
    var host: String = PotPvPND.getInstance().mainConfig.getString("Redis.Host"),
    var port: Int = PotPvPND.getInstance().mainConfig.getInteger("Redis.Port"),
    var password: String = PotPvPND.getInstance().mainConfig.getString("Redis.Authentication.Password"),
    var dbId: Int = 0
) {

    fun shouldAuthenticate(): Boolean {
        return password.isNotEmpty() && password.isNotBlank() && PotPvPND.getInstance().config.getBoolean("Redis.Authentication.Enabled")
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
