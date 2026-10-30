/**
 * What is kept, by area: one entity per real kind of thing, each declaring its own id and version (no base entity;
 * see CLAUDE.md). Their tables are made by the Flyway migrations in {@code db/migration}, which Hibernate checks them
 * against at start.
 *
 * <p>Two lifetimes. Teachers, rooms, sections and courses are kept across terms and reused. The week plan, blocks,
 * loads, offerings and timetables belong to one term.
 */
package io.mcs.intellidger.entities;
