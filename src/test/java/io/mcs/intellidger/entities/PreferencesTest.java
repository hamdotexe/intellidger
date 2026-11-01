package io.mcs.intellidger.entities;

import io.mcs.intellidger.DatabaseTestBase;
import io.mcs.intellidger.entities.batches.Batch;
import io.mcs.intellidger.entities.preferences.sections.SectionDayOff;
import io.mcs.intellidger.entities.preferences.sections.SectionFreeDays;
import io.mcs.intellidger.entities.preferences.sections.SectionMinDailyLoad;
import io.mcs.intellidger.entities.preferences.sections.SectionNoIdlePeriods;
import io.mcs.intellidger.entities.preferences.sections.SectionOffBy;
import io.mcs.intellidger.entities.preferences.sections.SectionPreference;
import io.mcs.intellidger.entities.preferences.teachers.TeacherMaxWeeklyLoad;
import io.mcs.intellidger.entities.preferences.teachers.TeacherMinWeeklyLoad;
import io.mcs.intellidger.entities.preferences.teachers.TeacherPeriodsInARow;
import io.mcs.intellidger.entities.preferences.teachers.TeacherPreference;
import io.mcs.intellidger.entities.sections.RegularSection;
import io.mcs.intellidger.entities.teachers.Teacher;
import io.mcs.intellidger.entities.terms.Term;
import jakarta.persistence.EntityManager;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * A final-year section's wishes and a teacher's go into SQLite and come back as their own kinds, days, times and
 * weights intact; and a load of no periods is refused.
 */
class PreferencesTest extends DatabaseTestBase {

    @Autowired
    EntityManager entityManager;

    @Autowired
    TransactionTemplate transaction;

    @Test
    void aSectionsPreferencesComeBackAsTheirKinds() {
        UUID sectionId = transaction.execute(status -> {
            Term term = term("Fall 2026");
            Batch batch = new Batch();
            batch.setName("BESE-27");
            entityManager.persist(batch);
            RegularSection section = new RegularSection();
            section.setBatch(batch);
            section.setName("BESE-27-A");
            entityManager.persist(section);

            SectionOffBy offBy = new SectionOffBy();
            offBy.getOffByPerDay().put(DayOfWeek.THURSDAY, LocalTime.of(13, 0));
            offBy.getOffByPerDay().put(DayOfWeek.MONDAY, LocalTime.of(15, 0));
            SectionMinDailyLoad minDailyLoad = new SectionMinDailyLoad();
            for (DayOfWeek day : EnumSet.range(DayOfWeek.MONDAY, DayOfWeek.THURSDAY)) {
                minDailyLoad.getMinPeriodsPerDay().put(day, 3);
            }
            minDailyLoad.getMinPeriodsPerDay().put(DayOfWeek.FRIDAY, 2);
            SectionFreeDays freeDays = new SectionFreeDays();
            freeDays.setHowManyDays(1);
            SectionDayOff fridayOff = new SectionDayOff();
            fridayOff.setDay(DayOfWeek.FRIDAY);
            for (SectionPreference preference : List.of(offBy, minDailyLoad, freeDays, fridayOff,
                    new SectionNoIdlePeriods())) {
                preference.setTerm(term);
                preference.setSection(section);
                preference.setWeight(3);
                entityManager.persist(preference);
            }
            return section.getId();
        });

        transaction.executeWithoutResult(status -> {
            List<SectionPreference> preferences = entityManager.createQuery(
                            "select preference from SectionPreference preference where preference.section.id = :section",
                            SectionPreference.class)
                    .setParameter("section", sectionId)
                    .getResultList();
            assertThat(preferences).hasSize(5);
            assertThat(preferences).allSatisfy(preference -> assertThat(preference.getWeight()).isEqualTo(3));

            SectionOffBy offBy = only(preferences, SectionOffBy.class);
            assertThat(offBy.getOffByPerDay()).containsExactlyInAnyOrderEntriesOf(
                    Map.of(DayOfWeek.MONDAY, LocalTime.of(15, 0), DayOfWeek.THURSDAY, LocalTime.of(13, 0)));
            SectionMinDailyLoad minDailyLoad = only(preferences, SectionMinDailyLoad.class);
            assertThat(minDailyLoad.getMinPeriodsPerDay()).hasSize(5)
                    .containsEntry(DayOfWeek.MONDAY, 3)
                    .containsEntry(DayOfWeek.FRIDAY, 2);
            assertThat(only(preferences, SectionFreeDays.class).getHowManyDays()).isEqualTo(1);
            assertThat(only(preferences, SectionDayOff.class).getDay()).isEqualTo(DayOfWeek.FRIDAY);
            only(preferences, SectionNoIdlePeriods.class);
        });
    }

    @Test
    void aTeachersPreferencesComeBackAsTheirKinds() {
        UUID teacherId = transaction.execute(status -> {
            Term term = term("Fall 2027");
            Teacher teacher = teacher("Dr Saima Latif");
            TeacherMinWeeklyLoad atLeast = new TeacherMinWeeklyLoad();
            atLeast.setMinPeriodsPerWeek(9);
            atLeast.setWeight(1);
            TeacherMaxWeeklyLoad atMost = new TeacherMaxWeeklyLoad();
            atMost.setMaxPeriodsPerWeek(12);
            atMost.setWeight(4);
            TeacherPeriodsInARow periodsInARow = new TeacherPeriodsInARow();
            periodsInARow.setMaxPeriods(4);
            periodsInARow.setWeight(2);
            for (TeacherPreference preference : List.<TeacherPreference>of(atLeast, atMost, periodsInARow)) {
                preference.setTerm(term);
                preference.setTeacher(teacher);
                entityManager.persist(preference);
            }
            return teacher.getId();
        });

        transaction.executeWithoutResult(status -> {
            List<TeacherPreference> preferences = entityManager.createQuery(
                            "select preference from TeacherPreference preference where preference.teacher.id = :teacher",
                            TeacherPreference.class)
                    .setParameter("teacher", teacherId)
                    .getResultList();
            TeacherMinWeeklyLoad atLeast = only(preferences, TeacherMinWeeklyLoad.class);
            assertThat(atLeast.getMinPeriodsPerWeek()).isEqualTo(9);
            assertThat(atLeast.getWeight()).isEqualTo(1);
            TeacherMaxWeeklyLoad atMost = only(preferences, TeacherMaxWeeklyLoad.class);
            assertThat(atMost.getMaxPeriodsPerWeek()).isEqualTo(12);
            assertThat(atMost.getWeight()).isEqualTo(4);
            assertThat(only(preferences, TeacherPeriodsInARow.class).getMaxPeriods()).isEqualTo(4);
        });
    }

    @Test
    void aLoadOfNoPeriodsIsRefused() {
        assertThatThrownBy(() -> transaction.executeWithoutResult(status -> {
            TeacherMaxWeeklyLoad atMost = new TeacherMaxWeeklyLoad();
            atMost.setTerm(term("Spring 2027"));
            atMost.setTeacher(teacher("Lec Fawad Khan"));
            atMost.setWeight(1);
            atMost.setMaxPeriodsPerWeek(0);
            entityManager.persist(atMost);
        })).rootCause()
                .isInstanceOf(ConstraintViolationException.class)
                .hasMessageContaining("maxPeriodsPerWeek");
    }

    private Term term(String name) {
        Term term = new Term();
        term.setName(name);
        term.setInstructorsAttendLabs(false);
        term.setLabsCountAsInstructorLoad(false);
        term.setElectivesMutuallyExclusive(true);
        entityManager.persist(term);
        return term;
    }

    private Teacher teacher(String name) {
        Teacher teacher = new Teacher();
        teacher.setName(name);
        entityManager.persist(teacher);
        return teacher;
    }

    private static <K> K only(List<?> preferences, Class<K> kind) {
        List<K> ofKind = preferences.stream()
                .filter(preference -> kind.isInstance(preference))
                .map(preference -> kind.cast(preference))
                .toList();
        assertThat(ofKind).hasSize(1);
        return ofKind.get(0);
    }
}
