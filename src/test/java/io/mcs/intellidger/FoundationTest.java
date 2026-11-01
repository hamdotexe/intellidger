package io.mcs.intellidger;

import org.junit.jupiter.api.Test;

/**
 * Starts the app's Spring side on a new, empty database. Drift between an entity and its migration fails here, not on
 * a user's computer.
 */
class FoundationTest extends DatabaseTestBase {

    @Test
    void startsOnAFreshDatabase() {
    }
}
