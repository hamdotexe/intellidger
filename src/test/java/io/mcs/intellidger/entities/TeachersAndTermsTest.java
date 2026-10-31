package io.mcs.intellidger.entities;

import io.mcs.intellidger.entities.teachers.LoadLimits;
import io.mcs.intellidger.entities.teachers.Teacher;
import io.mcs.intellidger.entities.teachers.TeacherBlock;
import io.mcs.intellidger.entities.teachers.TeachingLoad;
import io.mcs.intellidger.entities.terms.Term;
import io.mcs.intellidger.entities.weekplan.BlockStrength;
import io.mcs.intellidger.entities.weekplan.Period;
import jakarta.persistence.EntityManager;
import jakarta.validation.ConstraintViolationException;
import org.hibernate.envers.AuditReaderFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.support.TransactionTemplate;

import java.nio.file.Path;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Teachers, their blocks and teaching loads, and the terms and periods those hang on, go into SQLite and come back as
 * they were, with a revision for each save; and the database and the entities refuse what they should.
 */
@SpringBootTest
@DirtiesContext
class TeachersAndTermsTest {

    @TempDir
    static Path dataFolder;

    @DynamicPropertySource
    static void freshDatabase(DynamicPropertyRegistry registry) {
        registry.add("intellidger.data-folder", () -> dataFolder.toString());
    }

    @Autowired
    EntityManager entityManager;

    @Autowired
    TransactionTemplate transaction;

    @Test
    void aTeachersLoadForATermComesBackAsSaved() {
        UUID loadId = transaction.execute(status -> {
            Term term = new Term();
            term.setName("Fall 2026");
            Teacher teacher = teacher("Dr Abdul Razzaq");
            entityManager.persist(term);
            entityManager.persist(teacher);
            TeachingLoad load = new TeachingLoad();
            load.setTerm(term);
            load.setTeacher(teacher);
            load.setPerDay(limits(4, 2, 3));
            load.setPerWeek(limits(18, 9, 12));
            entityManager.persist(load);
            return load.getId();
        });

        transaction.executeWithoutResult(status -> {
            TeachingLoad load = entityManager.find(TeachingLoad.class, loadId);
            assertThat(load.getTerm().getName()).isEqualTo("Fall 2026");
            assertThat(load.getTeacher().getName()).isEqualTo("Dr Abdul Razzaq");
            assertThat(load.getPerDay().getMax()).isEqualTo(4);
            assertThat(load.getPerWeek().getPreferredMax()).isEqualTo(12);
            assertThat(AuditReaderFactory.get(entityManager).getRevisions(TeachingLoad.class, loadId)).hasSize(1);
        });
    }

    @Test
    void aTeachersBlockOnAPeriodComesBackAsSaved() {
        UUID blockId = transaction.execute(status -> {
            Term term = new Term();
            term.setName("Fall 2027");
            Period period = new Period();
            period.setTerm(term);
            period.setDay(DayOfWeek.FRIDAY);
            period.setNumber(2);
            period.setStartsAt(LocalTime.of(8, 55));
            period.setEndsAt(LocalTime.of(9, 45));
            Teacher teacher = teacher("Lt Col Khawir");
            TeacherBlock block = new TeacherBlock();
            block.setTeacher(teacher);
            block.setPeriod(period);
            block.setStrength(BlockStrength.HARD);
            entityManager.persist(term);
            entityManager.persist(period);
            entityManager.persist(teacher);
            entityManager.persist(block);
            return block.getId();
        });

        transaction.executeWithoutResult(status -> {
            TeacherBlock block = entityManager.find(TeacherBlock.class, blockId);
            assertThat(block.getStrength()).isEqualTo(BlockStrength.HARD);
            assertThat(block.getTeacher().getName()).isEqualTo("Lt Col Khawir");
            assertThat(block.getPeriod().getDay()).isEqualTo(DayOfWeek.FRIDAY);
            assertThat(block.getPeriod().getNumber()).isEqualTo(2);
            assertThat(block.getPeriod().getStartsAt()).isEqualTo(LocalTime.of(8, 55));
            assertThat(block.getPeriod().getEndsAt()).isEqualTo(LocalTime.of(9, 45));
            assertThat(block.getPeriod().getTerm().getName()).isEqualTo("Fall 2027");
        });
    }

    @Test
    void aPreferredLoadAboveTheMaximumIsRefused() {
        assertThatThrownBy(() -> transaction.executeWithoutResult(status -> {
            Term term = new Term();
            term.setName("Spring 2027");
            Teacher teacher = teacher("Lec Hira Khan");
            entityManager.persist(term);
            entityManager.persist(teacher);
            TeachingLoad load = new TeachingLoad();
            load.setTerm(term);
            load.setTeacher(teacher);
            load.setPerDay(limits(4, 2, 5));
            load.setPerWeek(limits(18, 9, 12));
            entityManager.persist(load);
        })).rootCause()
                .isInstanceOf(ConstraintViolationException.class)
                .hasMessageContaining("preferred load must lie within the maximum");
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

    private static Teacher teacher(String name) {
        Teacher teacher = new Teacher();
        teacher.setName(name);
        return teacher;
    }

    private static LoadLimits limits(int max, int preferredMin, int preferredMax) {
        LoadLimits limits = new LoadLimits();
        limits.setMax(max);
        limits.setPreferredMin(preferredMin);
        limits.setPreferredMax(preferredMax);
        return limits;
    }
}
