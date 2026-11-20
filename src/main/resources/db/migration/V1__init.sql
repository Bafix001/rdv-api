-- Extension nécessaire pour empêcher deux rendez-vous qui se chevauchent (voir docs/conception.md)
CREATE EXTENSION IF NOT EXISTS btree_gist;