/**
 * The JavaFX front: {@code DesktopApp} starts Spring before the first window and closes it with the last, and every
 * screen is an FXML controller here, by area in sub-packages, a prototype-scoped Spring bean with its FXML in
 * resources at the same package path. A screen calls services, never a repository.
 */
package io.mcs.intellidger.ui;
