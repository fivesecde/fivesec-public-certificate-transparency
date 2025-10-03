package de.fivesec.ca_stream.shared.entity

import de.fivesec.ca_stream.shared.util.InstantUtil
import java.time.Instant

class BaseEntityProvider {

    companion object {
        fun createdAt(): Instant {
            return InstantUtil.now()
        }

        fun lastUpdatedAt(): Instant {
            return InstantUtil.now()
        }

        fun createdBy(): String {
            return getUserName()
        }

        fun lastUpdatedBy(): String {
            return getUserName()
        }

        private fun getUserName(): String {
            return "system"
        }
    }
}
