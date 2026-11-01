/**
 * What a section would like from its week in a term, one class per kind of wish, each row with its own weight: when
 * its days end and start, how its load is spread over the week, how its courses fall, and keeping one room between
 * back-to-back sessions. How much an offering's instructor and room priorities matter is the offering's own.
 * <p>
 * Wishes that contradict each other are refused when saved, whichever is saved second, a week plan changed later
 * included. For one section in one term:
 * <ul>
 *     <li><b>Never together:</b> {@link SectionEvenDays} with {@link SectionFreeDays}, {@link SectionDayOff},
 *     {@link SectionMinDailyLoad} or {@link SectionMaxDailyLoad}. Even days already says how much each day gets, an
 *     even share of every teaching day; the others give some day nothing or a number of its own.</li>
 *     <li><b>Together only when they agree:</b> a day's minimum load above its maximum; a minimum on a day it wants
 *     off; a day's not-before time at or after its off-by time; a day's minimum needing more periods than the week
 *     plan has between those two times; and minimums on so many days that fewer teaching days are left than the free
 *     days it wants.</li>
 *     <li><b>One of each:</b> even days, no idle periods, same room, free days, periods in a row and course spread
 *     once each; a day off once for each day. Daily loads, not-before and off-by may take several rows, to weigh
 *     days differently, but each day in only one row of a kind.</li>
 * </ul>
 * Everything else may stand together, and the weights decide where they pull apart: a day off with free days (they
 * stack), no idle periods with periods in a row, even days with not-before or off-by, course spread with fewer days.
 * A section's wishes never refuse a teacher's; they meet only in the solver.
 */
package io.mcs.intellidger.entities.preferences.sections;
