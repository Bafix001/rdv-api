# rdv-api

![CI](https://github.com/Bafix001/rdv-api/actions/workflows/ci.yml/badge.svg)

API de **prise de rendez-vous en ligne** pour les prestataires indépendants et les petits commerces (coiffeurs, barbiers, esthéticiennes…).
Le prestataire partage un **lien ou un QR code** ; ses clients réservent **sans compte**, parmi des créneaux **calculés automatiquement** à partir de ses disponibilités.

> 🚧 **Projet en cours de développement.** Le socle technique est en place (Sprint 0) ; les fonctionnalités métier arrivent dans les prochains sprints.

---

## Fonctionnalités prévues (MVP)

- Inscription et connexion du prestataire
- Configuration de l'activité : prestations (durée, prix), horaires d'ouverture, indisponibilités, réglages de réservation
- Page publique de réservation accessible par lien (`/{slug}`) ou QR code
- **Calcul automatique des heures disponibles** selon les horaires, la durée de la prestation, les rendez-vous et les indisponibilités
- Réservation sans compte (nom + téléphone, email facultatif)
- Annulation par le client via un lien sécurisé, ou par le prestataire
- Tableau de bord des rendez-vous du prestataire

Le détail des choix (modèle de données, règles métier, API) est dans le **[document de conception](docs/conception.md)**.

---

## Stack technique

| Domaine | Outils |
|---|---|
| Langage / framework | Java 21, Spring Boot 4 (Web MVC, Data JPA, Validation, Security, Mail, Actuator) |
| Base de données | PostgreSQL 16 |
| Tests | JUnit 5, Testcontainers (vraie base PostgreSQL pendant les tests) |
| Environnement local | Docker Compose (PostgreSQL + MailDev) |
| CI | GitHub Actions (build + tests à chaque Pull Request) |
| Organisation | GitHub Issues, GitHub Projects (Kanban), Pull Requests obligatoires sur `main` |

---

## Lancer le projet en local

### Prérequis
- Java 21
- Docker (Docker Desktop, OrbStack…)

### Étapes

```bash
# 1. Cloner le dépôt
git clone https://github.com/Bafix001/rdv-api.git
cd rdv-api

# 2. Créer son fichier d'environnement (jamais committé)
cp .env.example .env
# puis renseigner POSTGRES_PASSWORD dans .env

# 3. Démarrer PostgreSQL et MailDev
docker compose up -d

# 4. Lancer l'application (profil "dev" par défaut)
./mvnw spring-boot:run
```

### Vérifier

| Service | Adresse |
|---|---|
| Santé de l'application | http://localhost:8080/actuator/health → `{"status":"UP"}` |
| Emails envoyés en développement (MailDev) | http://localhost:1080 |

---

## Tests

```bash
./mvnw verify
```

Les tests démarrent automatiquement un conteneur PostgreSQL avec **Testcontainers** : Docker doit être lancé, mais aucune base locale ni fichier `.env` n'est nécessaire.

---

## Configuration

| Fichier | Rôle |
|---|---|
| `application.yaml` | Configuration commune à tous les environnements |
| `application-dev.yaml` | Développement local : PostgreSQL et MailDev, identifiants lus depuis `.env` |
| `.env.example` | Modèle des variables d'environnement à copier dans `.env` |

Aucun secret n'est stocké dans le dépôt.

## Base de données et migrations

Le schéma est géré par **Flyway**, et non par Hibernate (`ddl-auto: validate`).

- Les scripts sont dans `src/main/resources/db/migration`.
- Ils sont nommés `V<numéro>__<description>.sql`, avec **deux** underscores, par exemple `V2__creer_prestataire.sql`.
- Ils s'exécutent automatiquement au démarrage, dans l'ordre des numéros.
- **Une migration déjà fusionnée sur `main` ne se modifie jamais** : pour corriger, on ajoute une nouvelle migration.

---

## Méthode de travail

- **Tickets** : chaque tâche est une [issue](https://github.com/Bafix001/rdv-api/issues), suivie sur le [tableau du projet](https://github.com/users/Bafix001/projects/2).
- **Branches** : une branche par ticket, nommée `type/numéro-description` (ex. `feat/12-reservation`).
- **Commits** : convention [Conventional Commits](https://www.conventionalcommits.org/fr/) (`feat:`, `fix:`, `docs:`, `ci:`, `chore:`…).
- **Pull Requests** : obligatoires pour modifier `main` ; la CI doit passer avant la fusion (*squash and merge*).

---

## Feuille de route

- [x] **Sprint 0** — socle technique : projet Spring Boot, environnement Docker, configuration par profils, CI, conception
- [ ] Gestion de l'activité, des prestations et des horaires
- [ ] Calcul des disponibilités et réservation
- [ ] Authentification du prestataire
- [ ] Notifications par email
- [ ] Conteneurisation de l'application, déploiement (Terraform, Ansible), supervision

---

## Licence

[MIT](LICENSE)
