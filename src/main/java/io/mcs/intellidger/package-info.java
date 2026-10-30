/**
 * Intellidger, a Windows desktop app: JavaFX screens over Spring services, its data in one SQLite file on this
 * computer.
 *
 * <ul>
 *   <li>{@code ui}: the JavaFX front: the window, and one FXML controller per screen, by area;</li>
 *   <li>{@code services}: what the app does, by area; the only callers of repositories;</li>
 *   <li>{@code repositories}: Spring Data repositories, by area;</li>
 *   <li>{@code entities}: what is kept, by area;</li>
 *   <li>{@code shared}: what every area uses, such as the folder the database lives in.</li>
 * </ul>
 */
package io.mcs.intellidger;
