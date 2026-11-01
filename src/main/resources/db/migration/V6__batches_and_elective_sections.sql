-- Batches arrive, such as BESE-30, and a section becomes one of two kinds: a batch's regular section (BESE-30-C),
-- kept across terms, or an elective section for a term (IoT-1), drawing its students from the whole batch. The name
-- moves to each kind, since a regular section's is unique for good and an elective section's only within its term.
-- No section was ever entered (there is no screen for sections yet), so sections is dropped and made again; the
-- tables that refer to it (section blocks, offerings, section preferences) find the new one by its name.

create table batches (
    id   char(36)     not null primary key,
    name varchar(255) not null,
    constraint uk_batches_name unique (name)
);

create table batches_aud (
    id      char(36) not null,
    rev     integer  not null references revision_info (rev),
    revtype tinyint,
    name    varchar(255),
    primary key (id, rev)
);

drop table sections_aud;
drop table sections;

create table sections (
    id           char(36)    not null primary key,
    section_kind varchar(31) not null,
    batch_id     char(36)    not null references batches (id)
);

create table sections_aud (
    id           char(36)    not null,
    rev          integer     not null references revision_info (rev),
    revtype      tinyint,
    section_kind varchar(31) not null,
    batch_id     char(36),
    primary key (id, rev)
);

create table regular_sections (
    id   char(36)     not null primary key references sections (id),
    name varchar(255) not null,
    constraint uk_regular_sections_name unique (name)
);

create table regular_sections_aud (
    id   char(36) not null,
    rev  integer  not null references revision_info (rev),
    name varchar(255),
    primary key (id, rev)
);

create table elective_sections (
    id      char(36)     not null primary key references sections (id),
    term_id char(36)     not null references terms (id),
    name    varchar(255) not null,
    constraint uk_elective_sections_term_name unique (term_id, name)
);

create table elective_sections_aud (
    id      char(36) not null,
    rev     integer  not null references revision_info (rev),
    term_id char(36),
    name    varchar(255),
    primary key (id, rev)
);
