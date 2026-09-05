package it.chalmers.gamma.oauth.server

internal object OAuthAuthorizationRedisScripts {
    val SAVE =
        """
        local current = redis.call('GET', KEYS[1])
        if ARGV[1] == '<absent>' then
            if current then return 0 end
        elseif current ~= ARGV[1] then
            return 0
        end
        local oldCount = tonumber(ARGV[5])
        local newCount = tonumber(ARGV[6])
        if not oldCount or not newCount then return -2 end
        local newStart = 2 + oldCount
        for index = newStart, newStart + newCount - 1 do
            local owner = redis.call('GET', KEYS[index])
            if owner and owner ~= ARGV[4] then return -1 end
        end
        for index = 2, 1 + oldCount do
            if redis.call('GET', KEYS[index]) == ARGV[4] then redis.call('DEL', KEYS[index]) end
        end
        redis.call('SET', KEYS[1], ARGV[2], 'PX', ARGV[3])
        for index = newStart, newStart + newCount - 1 do
            redis.call('SET', KEYS[index], ARGV[4], 'PX', ARGV[3])
        end
        return 1
        """.trimIndent()

    val REMOVE =
        """
        local current = redis.call('GET', KEYS[1])
        if current ~= ARGV[1] then return 0 end
        redis.call('DEL', KEYS[1])
        for index = 2, #KEYS do
            if redis.call('GET', KEYS[index]) == ARGV[2] then redis.call('DEL', KEYS[index]) end
        end
        return 1
        """.trimIndent()
}
