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

## [1.1.1](https://github.com/aliammari1/rakcha/compare/desktop-v1.1.0...desktop-v1.1.1) (2026-11-25)


### Bug Fixes

* **deps:** update dependency org.asynchttpclient:async-http-client to v3.0.12 [security] ([6303f1e](https://github.com/aliammari1/rakcha/commit/6303f1e19770e04a175942cdad0a5fe07076bb2e))
* **deps:** update dependency org.asynchttpclient:async-http-client to v3.0.12 [security] ([274e587](https://github.com/aliammari1/rakcha/commit/274e58731473bf60cf07f2e9b7c90134dd3908be))
* **desktop:** copy the application jar into jpackage's input directory ([699aa6c](https://github.com/aliammari1/rakcha/commit/699aa6cfcfb1af78782dd9a00e3f902729bd8ad0))
* **desktop:** polish client, manager, and admin workflows ([6205bea](https://github.com/aliammari1/rakcha/commit/6205bea29caba70d5a822bafbd019b1f43b47102))
* **release:** remove the jpackage launcher properties template ([3e16d93](https://github.com/aliammari1/rakcha/commit/3e16d93cf49b0e57a235d0408464d3c850f1b273))
* **release:** select MSI jpackage type on Windows ([dcbe577](https://github.com/aliammari1/rakcha/commit/dcbe577a4ca9ac167824027b54aa1efc322d1f2a))
* **release:** use a jpackage-safe app version and pass the tag version ([d4e6ea4](https://github.com/aliammari1/rakcha/commit/d4e6ea43aee7df6b2ce99a2a5cb2cceac8fceaf2))
* **release:** use four-component installer version for Windows MSI ([a9e3de2](https://github.com/aliammari1/rakcha/commit/a9e3de25a883949d5a6916bd120d8ed76e5a9460))
* **release:** use platform-appropriate installer version components ([fd29f9c](https://github.com/aliammari1/rakcha/commit/fd29f9caaeb53f85f93ea6a71d42187bf9640973))

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
