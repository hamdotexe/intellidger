package io.mcs.intellidger;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.nio.file.Path;

/**
 * Starts the app's Spring side on a new, empty database: Flyway runs every migration and Hibernate checks every
 * entity against the tables they made. Drift between an entity and its migration fails here, not on a user's
 * computer. No window opens.
 *
 * <p>The context is closed after the class, so its connection lets go of the database file before JUnit deletes the
 * folder: Windows refuses to delete a file that is still open.
 */
@SpringBootTest
@DirtiesContext
class FoundationTest {

    @TempDir
    static Path dataFolder;

    @DynamicPropertySource
    static void freshDatabase(DynamicPropertyRegistry registry) {
        registry.add("intellidger.data-folder", dataFolder::toString);
    }

    @Test
    void startsOnAFreshDatabase() {
    }
}
