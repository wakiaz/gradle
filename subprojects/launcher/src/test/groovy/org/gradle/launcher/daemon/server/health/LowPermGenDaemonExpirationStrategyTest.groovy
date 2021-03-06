/*
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gradle.launcher.daemon.server.health

import org.gradle.launcher.daemon.server.expiry.DaemonExpirationResult
import spock.lang.Specification

import static org.gradle.launcher.daemon.server.expiry.DaemonExpirationStatus.GRACEFUL_EXPIRE

class LowPermGenDaemonExpirationStrategyTest extends Specification {
    private final DaemonMemoryStatus status = Mock(DaemonMemoryStatus)

    def "daemon is expired when perm gen space is low" () {
        LowPermGenDaemonExpirationStrategy strategy = new LowPermGenDaemonExpirationStrategy(status)

        when:
        DaemonExpirationResult result = strategy.checkExpiration()

        then:
        1 * status.isPermGenSpaceExhausted() >> true

        and:
        result.status == GRACEFUL_EXPIRE
        result.reason == LowPermGenDaemonExpirationStrategy.EXPIRATION_REASON
    }

    def "daemon is not expired when tenured space is fine" () {
        LowPermGenDaemonExpirationStrategy strategy = new LowPermGenDaemonExpirationStrategy(status)

        when:
        DaemonExpirationResult result = strategy.checkExpiration()

        then:
        1 * status.isPermGenSpaceExhausted() >> false

        and:
        result == DaemonExpirationResult.NOT_TRIGGERED
    }
}
