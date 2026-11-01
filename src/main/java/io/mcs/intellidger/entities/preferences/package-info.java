/**
 * What sections and teachers would like from their weeks in a term, and how much each wish matters. Every preference
 * is one row with its owner, its term and its own weight, and each kind of wish is its own class. A timetable pays the
 * weight for each period or day it misses a wish by. There are no global weights.
 * <p>
 * A section's wishes are in {@code sections}, a teacher's in {@code teachers}; the load range both use is here.
 */
package io.mcs.intellidger.entities.preferences;
