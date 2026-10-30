/**
 * Intellidger, a Windows desktop app: JavaFX screens over Spring services, its data in one SQLite file on this
 * computer.
 *
 * <ul>
 *   <li>{@code desktop}: the JavaFX shell, and where the app keeps its data on Windows;</li>
 *   <li>{@code controllers}: one FXML controller per screen, by area, its FXML beside it in resources;</li>
 *   <li>{@code services}: what the app does, by area; the only callers of repositories;</li>
 *   <li>{@code repositories}: Spring Data repositories, by area;</li>
 *   <li>{@code entities}: what is kept, by area;</li>
 *   <li>{@code shared}: what every area uses, such as the base entity.</li>
 * </ul>
 */
package io.mcs.intellidger;
