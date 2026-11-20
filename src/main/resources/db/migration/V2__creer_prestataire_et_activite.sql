CREATE TABLE prestataire (
                             id            BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                             nom           VARCHAR(100) NOT NULL,
                             email         VARCHAR(255) NOT NULL UNIQUE,
                             mot_de_passe  VARCHAR(100) NOT NULL,
                             date_creation TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE TABLE activite (
                          id                                BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                          prestataire_id                    BIGINT       NOT NULL UNIQUE REFERENCES prestataire (id),
                          nom                               VARCHAR(100) NOT NULL,
                          slug                              VARCHAR(60)  NOT NULL UNIQUE,
                          telephone                         VARCHAR(20)  NOT NULL,
                          adresse                           VARCHAR(255),
                          fuseau_horaire                    VARCHAR(50)  NOT NULL,
                          devise                            VARCHAR(3)   NOT NULL,
                          delai_minimum_reservation_minutes INTEGER      NOT NULL DEFAULT 60,
                          horizon_reservation_jours         INTEGER      NOT NULL DEFAULT 60,
                          pas_creneaux_minutes              INTEGER      NOT NULL DEFAULT 15,
                          delai_annulation_heures           INTEGER      NOT NULL DEFAULT 24,

                          CONSTRAINT ck_activite_slug_format       CHECK (slug ~ '^[a-z0-9]+(-[a-z0-9]+)*$'),
    CONSTRAINT ck_activite_delai_minimum     CHECK (delai_minimum_reservation_minutes BETWEEN 0 AND 2880),
    CONSTRAINT ck_activite_horizon           CHECK (horizon_reservation_jours BETWEEN 1 AND 365),
    CONSTRAINT ck_activite_pas_creneaux      CHECK (pas_creneaux_minutes IN (5, 10, 15, 30, 60)),
    CONSTRAINT ck_activite_delai_annulation  CHECK (delai_annulation_heures BETWEEN 0 AND 168)
);