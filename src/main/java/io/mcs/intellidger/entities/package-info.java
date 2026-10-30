/**
 * What is kept, by area: one entity per real kind of thing, extending {@code BaseEntity}. Their tables are made by
 * the Flyway migrations in {@code db/migration}, which Hibernate checks them against at start.
 *
 * <p>Two lifetimes. Teachers, rooms, sections and courses are kept across terms and reused. The week plan, blocks,
 * loads, offerings and timetables belong to one term.
 */
package io.mcs.intellidger.entities;
