package io.mcs.intellidger.shared.audit;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import org.hibernate.envers.RevisionEntity;
import org.hibernate.envers.RevisionNumber;
import org.hibernate.envers.RevisionTimestamp;

/**
 * One Envers revision: when a change was saved. Every audit table points here. The app has one user on one computer,
 * so there is no "who" to record.
 */
@Entity
@Table(name = "revision_info")
@RevisionEntity
@Getter
public class RevisionInfo {

    /** Numbered by SQLite as rows go in: it has no sequences, which Envers' own revision table would need. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @RevisionNumber
    @Column(name = "rev")
    private Integer id;

    @RevisionTimestamp
    @Column(name = "revtstmp", nullable = false, updatable = false)
    private long timestamp;
}
