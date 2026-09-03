package it.chalmers.gamma.throttling

internal object RedisThrottleScripts {
    private const val MAXIMUM_COUNTER_ATTEMPTS = 2_147_483_647
    private const val RESERVATION_MARKER_VERSION = "1"

    val RESERVE =
        """
        ${stateValidationFunctions()}
        if redis.call('EXISTS', KEYS[1]) == 1 then return -1 end
        if ((#KEYS - 1) % 2) ~= 0 then return -1 end
        local limitCount = (#KEYS - 1) / 2
        if limitCount < 1 or #ARGV ~= (limitCount * 4) + 1 then return -1 end
        local counts = {}
        local counterTtls = {}
        local generations = {}
        local writeGeneration = {}
        local reservationTtl = tonumber(ARGV[#ARGV])
        if not reservationTtl or reservationTtl < 1 then return -1 end
        for index = 1, limitCount do
            local keyOffset = ((index - 1) * 2) + 2
            local argumentOffset = (index - 1) * 4
            local counterKey = KEYS[keyOffset]
            local generationKey = KEYS[keyOffset + 1]
            local current = redis.call('GET', counterKey)
            local attempts = 0
            local counterTtl = tonumber(ARGV[argumentOffset + 2])
            local proposedGeneration = ARGV[argumentOffset + 3]
            local generation = nil
            if not counterTtl or counterTtl < 1 or not isCanonicalGeneration(proposedGeneration) then return -1 end
            if current then
                attempts = decodeCounter(current)
                if not attempts then return -1 end
                local generationIsValid
                local generationTtl
                generation, generationIsValid, generationTtl = readGeneration(generationKey)
                if not generationIsValid then return -1 end
                counterTtl = redis.call('PTTL', counterKey)
                if counterTtl < 1 then return -1 end
                if generation and generationTtl < counterTtl then return -1 end
            end
            if not generation then
                generation = proposedGeneration
                writeGeneration[index] = true
            end
            local refundable = ARGV[argumentOffset + 4]
            if refundable ~= '0' and refundable ~= '1' then return -1 end
            local maximum = tonumber(ARGV[argumentOffset + 1])
            if not maximum or maximum < 1 or maximum > $MAXIMUM_COUNTER_ATTEMPTS then return -1 end
            if attempts >= maximum then return 0 end
            counts[index] = attempts
            counterTtls[index] = counterTtl
            generations[index] = generation
            reservationTtl = math.min(reservationTtl, counterTtl)
        end
        local marker = '$RESERVATION_MARKER_VERSION'
        for index = 1, limitCount do
            local argumentOffset = (index - 1) * 4
            if ARGV[argumentOffset + 4] == '1' then
                marker = marker .. '|' .. generations[index]
            end
        end
        if not redis.call('SET', KEYS[1], marker, 'PX', reservationTtl, 'NX') then return -1 end
        for index = 1, limitCount do
            local keyOffset = ((index - 1) * 2) + 2
            local attempts = counts[index] + 1
            if counts[index] == 0 then
                redis.call('SET', KEYS[keyOffset], tostring(attempts), 'PX', counterTtls[index])
            else
                redis.call('SET', KEYS[keyOffset], tostring(attempts), 'KEEPTTL')
            end
            if writeGeneration[index] then
                redis.call('SET', KEYS[keyOffset + 1], generations[index], 'PX', counterTtls[index])
            end
        end
        return 1
        """.trimIndent()

    val REFUND =
        """
        ${stateValidationFunctions()}
        ${markerValidationFunction()}
        if #ARGV ~= 0 or ((#KEYS - 1) % 2) ~= 0 then return -1 end
        local marker = redis.call('GET', KEYS[1])
        if not marker then return 0 end
        local refundableCount = (#KEYS - 1) / 2
        local markerParts = decodeMarker(marker, refundableCount)
        if not markerParts then return -1 end
        local attemptsByIndex = {}
        for index = 1, refundableCount do
            local keyOffset = ((index - 1) * 2) + 2
            local current = redis.call('GET', KEYS[keyOffset])
            if current then
                local attempts = decodeCounter(current)
                if not attempts then return -1 end
                local generation = redis.call('GET', KEYS[keyOffset + 1])
                local generationTtl = nil
                if generation then
                    if not isCanonicalGeneration(generation) then return -1 end
                    generationTtl = redis.call('PTTL', KEYS[keyOffset + 1])
                    if generationTtl < 1 then return -1 end
                end
                local counterTtl = redis.call('PTTL', KEYS[keyOffset])
                if counterTtl < 1 then return -1 end
                if generation then
                    if generationTtl < counterTtl then return -1 end
                    if generation == markerParts[index + 1] then
                        attemptsByIndex[index] = attempts
                    end
                end
            end
        end
        redis.call('UNLINK', KEYS[1])
        for index = 1, refundableCount do
            local attempts = attemptsByIndex[index]
            if attempts then
                local keyOffset = ((index - 1) * 2) + 2
                if attempts <= 1 then
                    redis.call('UNLINK', KEYS[keyOffset], KEYS[keyOffset + 1])
                else
                    redis.call('SET', KEYS[keyOffset], tostring(attempts - 1), 'KEEPTTL')
                end
            end
        end
        return 1
        """.trimIndent()

    val COMPLETE_RESERVATION =
        """
        if #KEYS ~= 1 or #ARGV ~= 0 then return -1 end
        if not redis.call('GET', KEYS[1]) then return 0 end
        redis.call('UNLINK', KEYS[1])
        return 1
        """.trimIndent()

    val INCREMENT_COUNTER =
        """
        ${stateValidationFunctions()}
        if #KEYS ~= 2 or #ARGV ~= 2 then return -1 end
        local window = tonumber(ARGV[1])
        local proposedGeneration = ARGV[2]
        if not window or window < 1 or not isCanonicalGeneration(proposedGeneration) then return -1 end
        local current = redis.call('GET', KEYS[1])
        local attempts = 1
        if current then
            local decodedAttempts = decodeCounter(current)
            if not decodedAttempts then return -1 end
            local generation, generationIsValid, generationTtl = readGeneration(KEYS[2])
            if not generationIsValid then return -1 end
            local counterTtl = redis.call('PTTL', KEYS[1])
            if counterTtl < 1 then return -1 end
            if generation and generationTtl < counterTtl then return -1 end
            attempts = decodedAttempts + 1
            local storedAttempts = math.min(attempts, $MAXIMUM_COUNTER_ATTEMPTS)
            redis.call('SET', KEYS[1], tostring(storedAttempts), 'KEEPTTL')
            if not generation then
                redis.call('SET', KEYS[2], proposedGeneration, 'PX', counterTtl)
            end
        else
            redis.call('SET', KEYS[1], '1', 'PX', window)
            redis.call('SET', KEYS[2], proposedGeneration, 'PX', window)
        end
        return attempts
        """.trimIndent()

    private fun stateValidationFunctions(): String =
        """
        local function decodeCounter(payload)
            if not payload or string.sub(payload, 1, 1) == '0' or string.find(payload, '[^0-9]') then
                return nil
            end
            local attempts = tonumber(payload)
            if not attempts or attempts < 1 or attempts > $MAXIMUM_COUNTER_ATTEMPTS then return nil end
            return attempts
        end

        local function isCanonicalGeneration(value)
            if not value or string.len(value) ~= 36 then return false end
            if string.sub(value, 9, 9) ~= '-' or string.sub(value, 14, 14) ~= '-' or
                string.sub(value, 19, 19) ~= '-' or string.sub(value, 24, 24) ~= '-' then
                return false
            end
            local compact = string.gsub(value, '-', '')
            if string.len(compact) ~= 32 or string.find(compact, '[^0-9a-f]') then return false end
            if string.sub(value, 15, 15) ~= '4' or not string.find('89ab', string.sub(value, 20, 20), 1, true) then
                return false
            end
            return true
        end

        local function readGeneration(generationKey)
            local generation = redis.call('GET', generationKey)
            if not generation then return nil, true, nil end
            if not isCanonicalGeneration(generation) then return nil, false, nil end
            local generationTtl = redis.call('PTTL', generationKey)
            if generationTtl < 1 then return nil, false, nil end
            return generation, true, generationTtl
        end
        """.trimIndent()

    private fun markerValidationFunction(): String =
        """
        local function decodeMarker(marker, expectedGenerations)
            if expectedGenerations == 0 then
                if marker == '$RESERVATION_MARKER_VERSION' then return {} end
                return nil
            end
            local expectedPrefix = '$RESERVATION_MARKER_VERSION|'
            if string.sub(marker, 1, string.len(expectedPrefix)) ~= expectedPrefix or
                string.sub(marker, -1) == '|' or string.find(marker, '||', 1, true) then
                return nil
            end
            local parts = {}
            for part in string.gmatch(marker, '([^|]+)') do
                table.insert(parts, part)
            end
            if #parts ~= expectedGenerations + 1 or parts[1] ~= '$RESERVATION_MARKER_VERSION' then return nil end
            for index = 2, #parts do
                if not isCanonicalGeneration(parts[index]) then return nil end
            end
            return parts
        end
        """.trimIndent()
}
