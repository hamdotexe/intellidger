package io.mcs.intellidger.shared.persistence;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Where Intellidger keeps its data on this computer: the database and the logs, for the signed-in Windows user, in
 * {@code %LOCALAPPDATA%\Intellidger}. Local rather than roaming, so a growing database is never copied around with
 * the user's profile; never beside the program, which an installer puts under {@code Program Files} where the user
 * may not write.
 */
public final class AppDataFolder {

    public static final String NAME = "Intellidger";

    private AppDataFolder() {
    }

    /** The folder, made if this is the first start: SQLite makes the database file but not the folders above it. */
    public static Path create() {
        String localAppData = System.getenv("LOCALAPPDATA");
        Path folder = localAppData != null
                ? Path.of(localAppData, NAME)
                : Path.of(System.getProperty("user.home"), "." + NAME.toLowerCase());
        try {
            return Files.createDirectories(folder);
        } catch (IOException e) {
            throw new UncheckedIOException("Could not make the data folder " + folder, e);
        }
    }
}
