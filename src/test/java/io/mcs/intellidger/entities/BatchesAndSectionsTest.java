package io.mcs.intellidger.entities;

import io.mcs.intellidger.DatabaseTestBase;
import io.mcs.intellidger.entities.batches.Batch;
import io.mcs.intellidger.entities.courses.Course;
import io.mcs.intellidger.entities.offerings.InstructorPriority;
import io.mcs.intellidger.entities.offerings.Offering;
import io.mcs.intellidger.entities.offerings.TheoryOffering;
import io.mcs.intellidger.entities.sections.ElectiveSection;
import io.mcs.intellidger.entities.sections.RegularSection;
import io.mcs.intellidger.entities.sections.Section;
import io.mcs.intellidger.entities.teachers.Teacher;
import io.mcs.intellidger.entities.terms.Term;
import jakarta.persistence.EntityManager;
import org.hibernate.Hibernate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * BESE-30 as Fall 2026 had it: regular sections A, B and C, and its electives as elective sections drawn from the
 * whole batch, Internet of Things in two groups and Quantum Computing in one. They go into SQLite and come back as
 * their kinds, and an elective section is taught through offerings like any other section.
 */
class BatchesAndSectionsTest extends DatabaseTestBase {

    @Autowired
    EntityManager entityManager;

    @Autowired
    TransactionTemplate transaction;

    @Test
    void aBatchsRegularAndElectiveSectionsComeBackAsTheirKinds() {
        UUID[] ids = transaction.execute(status -> {
            Term term = term("Fall 2026");
            Batch batch = batch("BESE-30");
            for (String name : List.of("BESE-30-A", "BESE-30-B", "BESE-30-C")) {
                RegularSection section = new RegularSection();
                section.setBatch(batch);
                section.setName(name);
                entityManager.persist(section);
            }
            Course iot = new Course();
            iot.setName("Internet of Things");
            entityManager.persist(iot);
            Teacher teacher = new Teacher();
            teacher.setName("Maj Ahmed Bilal");
            entityManager.persist(teacher);
            for (String name : List.of("IoT-1", "IoT-2")) {
                TheoryOffering theory = new TheoryOffering();
                theory.setTerm(term);
                theory.setCourse(iot);
                theory.setSection(elective(term, batch, name));
                theory.getSessionLengthsInPeriods().addAll(List.of(1, 2));
                InstructorPriority instructor = new InstructorPriority();
                instructor.setTeacher(teacher);
                instructor.setPriority(1);
                theory.getInstructors().add(instructor);
                theory.setInstructorPreferenceWeight(0);
                theory.setRoomPreferenceWeight(0);
                entityManager.persist(theory);
            }
            elective(term, batch, "Quantum-1");
            return new UUID[] {batch.getId(), iot.getId()};
        });

        transaction.executeWithoutResult(status -> {
            List<Section> sections = entityManager.createQuery(
                            "select section from Section section where section.batch.id = :batch", Section.class)
                    .setParameter("batch", ids[0])
                    .getResultList();
            assertThat(sections.stream()
                    .filter(section -> section instanceof RegularSection)
                    .map(section -> section.getName())
                    .toList()).containsExactlyInAnyOrder("BESE-30-A", "BESE-30-B", "BESE-30-C");
            assertThat(sections.stream()
                    .filter(section -> section instanceof ElectiveSection)
                    .map(section -> section.getName())
                    .toList()).containsExactlyInAnyOrder("IoT-1", "IoT-2", "Quantum-1");

            List<Offering> iot = entityManager.createQuery(
                            "select offering from Offering offering where offering.course.id = :course", Offering.class)
                    .setParameter("course", ids[1])
                    .getResultList();
            assertThat(iot).allSatisfy(offering -> assertThat(Hibernate.unproxy(offering.getSection()))
                    .isInstanceOf(ElectiveSection.class));
            assertThat(iot.stream().map(offering -> offering.getSection().getName()).toList())
                    .containsExactlyInAnyOrder("IoT-1", "IoT-2");
        });
    }

    @Test
    void anElectiveSectionsNameIsUniqueWithinItsTerm() {
        transaction.executeWithoutResult(status -> {
            Batch batch = batch("BESE-31");
            elective(term("Spring 2027"), batch, "IoT-1");
            elective(term("Fall 2027"), batch, "IoT-1");
        });

        assertThatThrownBy(() -> transaction.executeWithoutResult(status -> {
            Term term = term("Spring 2028");
            Batch batch = batch("BESE-32");
            elective(term, batch, "Quantum-1");
            elective(term, batch, "Quantum-1");
        })).hasMessageContaining("UNIQUE constraint failed: elective_sections.term_id, elective_sections.name");
    }

    private Term term(String name) {
        Term term = new Term();
        term.setName(name);
        term.setInstructorsAttendLabs(false);
        term.setElectivesMutuallyExclusive(true);
        entityManager.persist(term);
        return term;
    }

    private Batch batch(String name) {
        Batch batch = new Batch();
        batch.setName(name);
        entityManager.persist(batch);
        return batch;
    }

    private ElectiveSection elective(Term term, Batch batch, String name) {
        ElectiveSection section = new ElectiveSection();
        section.setTerm(term);
        section.setBatch(batch);
        section.setName(name);
        entityManager.persist(section);
        return section;
    }
}
