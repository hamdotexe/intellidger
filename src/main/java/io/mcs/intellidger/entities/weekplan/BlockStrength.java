package io.mcs.intellidger.entities.weekplan;

/** How firmly a period is blocked. */
public enum BlockStrength {

    /** Unavailable: no session may use the period. */
    HARD,

    /** Not preferred: a session may use the period, but the timetable is worse for it. */
    SOFT
}
