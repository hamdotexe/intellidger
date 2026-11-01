/**
 * What a teacher would like from their week in a term, one class per kind of wish, each row with its own weight: when
 * their days end and start, and how much they teach and how it is spread over the week.
 * <p>
 * Wishes that contradict each other are refused when saved, whichever is saved second, a week plan or teaching load
 * changed later included. For one teacher in one term:
 * <ul>
 *     <li><b>Never together:</b> {@link TeacherEvenDays} with {@link TeacherFreeDays}, {@link TeacherDayOff},
 *     {@link TeacherMinDailyLoad} or {@link TeacherMaxDailyLoad}. Even days already says how much each day gets, an
 *     even share of every teaching day; the others give some day nothing or a number of its own.</li>
 *     <li><b>Together only when they agree:</b> a day's minimum load above its maximum; a minimum on a day they want
 *     off; a day's not-before time at or after its off-by time; a day's minimum needing more periods than the week
 *     plan has between those two times; minimums on so many days that fewer teaching days are left than the free
 *     days they want; the weekly minimum above the weekly maximum; and the daily minimums adding up to more than the
 *     weekly maximum.</li>
 *     <li><b>Against their teaching load:</b> a daily minimum above its most periods per day, or the weekly minimum,
 *     or the daily minimums added up, above its most per week. Maximums need no check: above the teaching load's they
 *     never matter.</li>
 *     <li><b>One of each:</b> even days, no idle periods, free days, periods in a row, weekly minimum and weekly
 *     maximum once each; a day off once for each day. Daily loads, not-before and off-by may take several rows, to
 *     weigh days differently, but each day in only one row of a kind.</li>
 * </ul>
 * Everything else may stand together, and the weights decide where they pull apart: a day off with free days (they
 * stack), no idle periods with periods in a row, even days with not-before or off-by. A teacher's wishes never refuse
 * a section's; they meet only in the solver.
 */
package io.mcs.intellidger.entities.preferences.teachers;
