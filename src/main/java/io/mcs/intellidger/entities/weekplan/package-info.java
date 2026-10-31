/**
 * The week plan: a term's teaching days and the periods of each, in order and with their times. A day with no periods
 * is not taught. Breaks are not stored; they are the gaps between periods, and a session runs across them: only a free
 * period breaks a run.
 *
 * <p>Also what every block shares: one period marked on the week of a teacher, a room or a section. Each owner's
 * blocks are hard (never used) or soft (used only at the block's own weight); a period not blocked is open.
 */
package io.mcs.intellidger.entities.weekplan;
