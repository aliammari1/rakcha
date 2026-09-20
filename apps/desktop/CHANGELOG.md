# Changelog

All
notable
changes
to
the
RAKCHA
Desktop
project
will
be
documented
in
this
file.

The
format
is
based
on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and
this
project
adheres
to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.1.1](https://github.com/aliammari1/rakcha/compare/desktop-v1.1.0...desktop-v1.1.1) (2026-09-20)


### Bug Fixes

* **deps:** update dependency org.asynchttpclient:async-http-client to v3.0.12 [security] ([6c49205](https://github.com/aliammari1/rakcha/commit/6c49205c00cde361e54bc4fcf92fea609ff7eb66))
* **deps:** update dependency org.asynchttpclient:async-http-client to v3.0.12 [security] ([63f479a](https://github.com/aliammari1/rakcha/commit/63f479a0d5990b028ac470fcb5a9ba37cfc9f497))

## [1.1.0](https://github.com/aliammari1/rakcha/compare/desktop-v1.0.10...desktop-v1.1.0) (2026-09-17)


### Features

* Add issue and pull request templates for better contribution guidelines ([3ded3fc](https://github.com/aliammari1/rakcha/commit/3ded3fc204e68dc32c05b218f5f206b2322abd19))
* **desktop:** jpackage release matrix + wire tests/coverage in CI ([a8688e0](https://github.com/aliammari1/rakcha/commit/a8688e046f081138e167a6c441fa65542c798019))


### Bug Fixes

* close remaining SBOM dependency findings ([ddaeabc](https://github.com/aliammari1/rakcha/commit/ddaeabcf79c64bbf47b102386d5d919b31b43a51))
* **desktop:** add SQLite fallback, avoid fatal payment init, and fix test assertions ([17a2844](https://github.com/aliammari1/rakcha/commit/17a2844f4fb9a0bd4bbd78c9259f4d16c92f588c))
* **desktop:** remove privileged payment execution from client ([f22a2da](https://github.com/aliammari1/rakcha/commit/f22a2da218b983cefa33446f4ab92d8bf165736f))
* migrate embedded websocket server to Jetty 12 ([cbef9a3](https://github.com/aliammari1/rakcha/commit/cbef9a3e9b0f9ee5d6e2e052ada2bea85f6b3111))
* remediate SBOM dependency findings ([de77eaa](https://github.com/aliammari1/rakcha/commit/de77eaa5bf53ce4ba1a306d15cb29ae933f2bc5d))
* update Jetty security dependency ([b4b1666](https://github.com/aliammari1/rakcha/commit/b4b16669e5879ff96a67d7decc649956b301042f))

## [Unreleased]

### Added

-

Comprehensive
codebase
analysis
and
recommendations
document

-

Logging
configuration
with
`logback.xml`

-

Docker
Compose
setup
for
containerized
deployment

-

GitHub
Actions
CI/CD
pipeline

-

EditorConfig
for
consistent
code
formatting

-

Environment
variable
validation (
planned)

-

AI-powered
film
recommendation
service (
planned)

-

Content
moderation
service (
planned)

-

Enhanced
security
measures
documentation

### Changed

-

Improved
environment
configuration
documentation

-

Enhanced
security
recommendations

### Fixed

-

Documented
security
vulnerabilities
to
be
addressed

-

Identified
missing
test
coverage

### Security

-

Password
reset
vulnerability
identified (
to
be
fixed)

-

Added
recommendations
for
password
pepper
implementation

-

Rate
limiting
recommendations
added

## [1.0.10] - 2024-XX-XX

### Added

-

Multi-database
support (
SQLite,
MySQL,
PostgreSQL)

-

Face
recognition
authentication
with
OpenCV

-

Social
authentication (
Google,
Microsoft)

-

Payment
integration (
Stripe,
PayPal)

-

YouTube
trailer
integration

-

IMDB
data
integration

-

Email
and
SMS
notifications

-

PDF
generation
for
tickets
and
invoices

-

Cloud
storage
with
Cloudinary

### Changed

-

Updated
JavaFX
to
version
21

-

Improved
UI/UX
with
MaterialFX
and
JFoenix

-

Enhanced
database
connection
management

### Fixed

-

Various
bug
fixes
and
performance
improvements

## [1.0.5] - 2024-XX-XX

### Added

-

Cinema
management
module

-

Film
and
series
cataloging

-

User
rating
and
review
system

-

Seat
selection
and
booking

-

Product
marketplace

### Changed

-

Improved
codebase
structure

-

Enhanced
error
handling

## [1.0.0] - 2024-XX-XX

### Added

-

Initial
release

-

Basic
cinema
management
features

-

User
authentication

-

Film
database

-

Ticket
booking
system

---

## Version Categories

### Added

-

New
features

### Changed

-

Changes
to
existing
functionality

### Deprecated

-

Features
that
will
be
removed
in
upcoming
releases

### Removed

-

Features
that
have
been
removed

### Fixed

-

Bug
fixes

### Security

-

Security-related
changes

---

## Upgrade Notes

### Upgrading to 1.0.10

1.

Update
environment
variables
in
`.env`
file

2.

Run
database
migrations

3.

Clear
application
cache

4.

Restart
application

---

## Contributors

See [CONTRIBUTING.md](CONTRIBUTING.md)
for
information
on
how
to
contribute
to
this
project.

---

## Links

- [Project Repository](https://github.com/aliammari1/rakcha-desktop)
- [Issue Tracker](https://github.com/aliammari1/rakcha-desktop/issues)
- [Documentation](docs/)
