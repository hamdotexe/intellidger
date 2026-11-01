/**
 * What Timefold works on: a term's draft and everything it is solved against, copied out of the database into plain
 * objects held in memory for the length of a run. Each class is the copy of the entity of the same name, in the
 * package of the same name, keeping what the solver reads; what it means is documented on the entity.
 * <p>
 * Each class fetches its own table through its repository ({@code fetch}, called from a service inside its
 * transaction), and brings the rules its rows state, each a static method returning one constraint, which
 * {@link io.mcs.intellidger.timefold.TimetableConstraints} lists. What a rule weighs for one row is worked out by that
 * row's own instance methods, called from inside the rule. Everything is read once and never changes during a
 * run, except what the solver decides: each {@link io.mcs.intellidger.timefold.timetables.Session}'s start period and
 * room, and the teacher of each {@link io.mcs.intellidger.timefold.timetables.InstructorAssignment} and
 * {@link io.mcs.intellidger.timefold.timetables.LabEngineerAssignment}. What each may be given is worked out when it is
 * built, so an unlisted teacher, an impossible room or a start too late in the day is never even tried.
 * <p>
 * A hierarchy's topmost class takes its constructor from Lombok; each class below it writes its own, passing the
 * parent's fields up first.
 */
package io.mcs.intellidger.timefold;
