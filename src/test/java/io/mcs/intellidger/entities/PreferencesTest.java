package io.mcs.intellidger.entities;

import io.mcs.intellidger.DatabaseTestBase;
import io.mcs.intellidger.entities.preferences.LoadRange;
import io.mcs.intellidger.entities.preferences.sections.SectionDailyLoad;
import io.mcs.intellidger.entities.preferences.sections.SectionDayOff;
import io.mcs.intellidger.entities.preferences.sections.SectionFreeDays;
import io.mcs.intellidger.entities.preferences.sections.SectionNoIdlePeriods;
import io.mcs.intellidger.entities.preferences.sections.SectionOffBy;
import io.mcs.intellidger.entities.preferences.sections.SectionPreference;
import io.mcs.intellidger.entities.preferences.teachers.TeacherPeriodsInARow;
import io.mcs.intellidger.entities.preferences.teachers.TeacherPreference;
import io.mcs.intellidger.entities.preferences.teachers.TeacherWeeklyLoad;
import io.mcs.intellidger.entities.sections.Section;
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
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * A final-year section's wishes and a teacher's go into SQLite and come back as their own kinds, days and times
 * intact; and a load range upside down is refused.
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
            Section section = new Section();
            section.setName("BESE-27-A");
            entityManager.persist(section);

            SectionOffBy offBy = new SectionOffBy();
            offBy.setDays(EnumSet.of(DayOfWeek.THURSDAY, DayOfWeek.MONDAY));
            offBy.setTime(LocalTime.of(13, 0));
            SectionDailyLoad dailyLoad = new SectionDailyLoad();
            dailyLoad.setDays(EnumSet.range(DayOfWeek.MONDAY, DayOfWeek.THURSDAY));
            dailyLoad.setLoad(range(3, 6));
            SectionFreeDays freeDays = new SectionFreeDays();
            freeDays.setHowManyDays(1);
            SectionDayOff fridayOff = new SectionDayOff();
            fridayOff.setDay(DayOfWeek.FRIDAY);
            for (SectionPreference preference : List.of(offBy, dailyLoad, freeDays, fridayOff,
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
            assertThat(offBy.getDays()).containsExactlyInAnyOrder(DayOfWeek.MONDAY, DayOfWeek.THURSDAY);
            assertThat(offBy.getTime()).isEqualTo(LocalTime.of(13, 0));
            SectionDailyLoad dailyLoad = only(preferences, SectionDailyLoad.class);
            assertThat(dailyLoad.getDays()).hasSize(4).doesNotContain(DayOfWeek.FRIDAY);
            assertThat(dailyLoad.getLoad().getMinPeriods()).isEqualTo(3);
            assertThat(dailyLoad.getLoad().getMaxPeriods()).isEqualTo(6);
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
            TeacherWeeklyLoad weeklyLoad = new TeacherWeeklyLoad();
            weeklyLoad.setLoad(range(9, 12));
            TeacherPeriodsInARow periodsInARow = new TeacherPeriodsInARow();
            periodsInARow.setMaxPeriods(4);
            for (TeacherPreference preference : List.<TeacherPreference>of(weeklyLoad, periodsInARow)) {
                preference.setTerm(term);
                preference.setTeacher(teacher);
                preference.setWeight(2);
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
            assertThat(only(preferences, TeacherWeeklyLoad.class).getLoad().getMaxPeriods()).isEqualTo(12);
            assertThat(only(preferences, TeacherPeriodsInARow.class).getMaxPeriods()).isEqualTo(4);
        });
    }

    @Test
    void aLoadWhoseMinimumIsAboveItsMaximumIsRefused() {
        assertThatThrownBy(() -> transaction.executeWithoutResult(status -> {
            TeacherWeeklyLoad weeklyLoad = new TeacherWeeklyLoad();
            weeklyLoad.setTerm(term("Spring 2027"));
            weeklyLoad.setTeacher(teacher("Lec Fawad Khan"));
            weeklyLoad.setWeight(1);
            weeklyLoad.setLoad(range(12, 9));
            entityManager.persist(weeklyLoad);
        })).rootCause()
                .isInstanceOf(ConstraintViolationException.class)
                .hasMessageContaining("minimum can't be above its maximum");
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

    private static LoadRange range(int min, int max) {
        LoadRange range = new LoadRange();
        range.setMinPeriods(min);
        range.setMaxPeriods(max);
        return range;
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
