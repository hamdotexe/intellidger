package io.mcs.intellidger.entities;

import io.mcs.intellidger.DatabaseTestBase;
import io.mcs.intellidger.entities.teachers.Teacher;
import io.mcs.intellidger.entities.teachers.TeacherBlock;
import io.mcs.intellidger.entities.teachers.TeacherHardBlock;
import io.mcs.intellidger.entities.teachers.TeacherSoftBlock;
import io.mcs.intellidger.entities.teachers.TeachingLoad;
import io.mcs.intellidger.entities.terms.Term;
import io.mcs.intellidger.entities.weekplan.Period;
import jakarta.persistence.EntityManager;
import org.hibernate.envers.AuditReaderFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Teachers, their blocks and teaching loads, and the terms and periods those hang on, go into SQLite and come back as
 * they were, with a revision for each save; and the database refuses what it should.
 */
class TeachersAndTermsTest extends DatabaseTestBase {

    @Autowired
    EntityManager entityManager;

    @Autowired
    TransactionTemplate transaction;

    @Test
    void aTeachersLoadForATermComesBackAsSaved() {
        UUID loadId = transaction.execute(status -> {
            Term term = term("Fall 2026");
            Teacher teacher = teacher("Dr Abdul Razzaq");
            TeachingLoad load = new TeachingLoad();
            load.setTerm(term);
            load.setTeacher(teacher);
            load.setMaxPeriodsPerDay(4);
            load.setMaxPeriodsPerWeek(18);
            entityManager.persist(load);
            return load.getId();
        });

        transaction.executeWithoutResult(status -> {
            TeachingLoad load = entityManager.find(TeachingLoad.class, loadId);
            assertThat(load.getTerm().getName()).isEqualTo("Fall 2026");
            assertThat(load.getTeacher().getName()).isEqualTo("Dr Abdul Razzaq");
            assertThat(load.getMaxPeriodsPerDay()).isEqualTo(4);
            assertThat(load.getMaxPeriodsPerWeek()).isEqualTo(18);
            assertThat(AuditReaderFactory.get(entityManager).getRevisions(TeachingLoad.class, loadId)).hasSize(1);
        });
    }

    @Test
    void aTeachersBlocksComeBackAsTheirKinds() {
        UUID teacherId = transaction.execute(status -> {
            Term term = term("Fall 2027");
            Teacher teacher = teacher("Lt Col Khawir");
            TeacherHardBlock hard = new TeacherHardBlock();
            hard.setTeacher(teacher);
            hard.setPeriod(period(term, DayOfWeek.FRIDAY, 1, LocalTime.of(8, 0), LocalTime.of(8, 50)));
            TeacherSoftBlock soft = new TeacherSoftBlock();
            soft.setTeacher(teacher);
            soft.setPeriod(period(term, DayOfWeek.FRIDAY, 2, LocalTime.of(8, 55), LocalTime.of(9, 45)));
            soft.setWeight(5);
            entityManager.persist(hard);
            entityManager.persist(soft);
            return teacher.getId();
        });

        transaction.executeWithoutResult(status -> {
            List<TeacherBlock> blocks = entityManager.createQuery(
                            "select block from TeacherBlock block where block.teacher.id = :teacher"
                                    + " order by block.period.number", TeacherBlock.class)
                    .setParameter("teacher", teacherId)
                    .getResultList();
            assertThat(blocks).hasSize(2);
            assertThat(blocks.get(0)).isInstanceOf(TeacherHardBlock.class);
            assertThat(blocks.get(1)).isInstanceOf(TeacherSoftBlock.class);
            assertThat(((TeacherSoftBlock) blocks.get(1)).getWeight()).isEqualTo(5);
            Period friday2nd = blocks.get(1).getPeriod();
            assertThat(friday2nd.getDay()).isEqualTo(DayOfWeek.FRIDAY);
            assertThat(friday2nd.getStartsAt()).isEqualTo(LocalTime.of(8, 55));
            assertThat(friday2nd.getEndsAt()).isEqualTo(LocalTime.of(9, 45));
        });
    }

    @Test
    void aTeacherIsBlockedAtMostOnceOnAPeriod() {
        assertThatThrownBy(() -> transaction.executeWithoutResult(status -> {
            Term term = term("Spring 2027");
            Teacher teacher = teacher("Lec Hira Khan");
            Period monday1st = period(term, DayOfWeek.MONDAY, 1, LocalTime.of(8, 0), LocalTime.of(8, 50));
            TeacherHardBlock hard = new TeacherHardBlock();
            hard.setTeacher(teacher);
            hard.setPeriod(monday1st);
            TeacherSoftBlock soft = new TeacherSoftBlock();
            soft.setTeacher(teacher);
            soft.setPeriod(monday1st);
            soft.setWeight(1);
            entityManager.persist(hard);
            entityManager.persist(soft);
        })).hasMessageContaining("UNIQUE constraint failed: teacher_blocks.teacher_id, teacher_blocks.period_id");
    }

    /**
     * SQLite gives no SQL state, so Spring can't tell a broken unique constraint from any other failure: the refusal
     * comes as a general error naming the column, not a {@code DataIntegrityViolationException}.
     */
    @Test
    void twoTeachersCannotShareAName() {
        transaction.executeWithoutResult(status -> entityManager.persist(teacher("Maj Ahmed Bilal")));

        assertThatThrownBy(() -> transaction.executeWithoutResult(
                status -> entityManager.persist(teacher("Maj Ahmed Bilal"))))
                .hasMessageContaining("UNIQUE constraint failed: teachers.name");
    }

    private Term term(String name) {
        Term term = new Term();
        term.setName(name);
        entityManager.persist(term);
        return term;
    }

    private Teacher teacher(String name) {
        Teacher teacher = new Teacher();
        teacher.setName(name);
        entityManager.persist(teacher);
        return teacher;
    }

    private Period period(Term term, DayOfWeek day, int number, LocalTime startsAt, LocalTime endsAt) {
        Period period = new Period();
        period.setTerm(term);
        period.setDay(day);
        period.setNumber(number);
        period.setStartsAt(startsAt);
        period.setEndsAt(endsAt);
        entityManager.persist(period);
        return period;
    }
}
