# Contributing to RAKCHA

First off, thank you for considering contributing to RAKCHA! 🎉 It's people like you that make RAKCHA such a great entertainment platform.

Following these guidelines helps to communicate that you respect the developers' time and effort managing and developing this open-source project. In return, they show respect in addressing your issue, assessing changes, and helping you finalize your pull requests.

---

## 📋 Table of Contents

- [Code of Conduct](#code-of-conduct)
- [What Can I Contribute?](#what-can-i-contribute)
- [Getting Started](#getting-started)
- [Development Setup](#development-setup)
- [Development Workflow](#development-workflow)
- [Commit Guidelines](#commit-guidelines)
- [Pull Request Process](#pull-request-process)
- [Style Guides](#style-guides)
- [Testing Requirements](#testing-requirements)
- [Documentation](#documentation)
- [Community](#community)

---

## 🤝 Code of Conduct

This project and everyone participating in it is governed by the [RAKCHA Code of Conduct](CODE_OF_CONDUCT.md). By participating, you are expected to uphold this code. Please report unacceptable behavior to [maintainers].

---

## 💡 What Can I Contribute?

### 🐛 Bug Fixes
- Fix existing bugs in any application (desktop, mobile, or web)
- Improve error handling
- Fix security vulnerabilities
- Performance improvements

### ✨ Features
- New functionality
- API enhancements
- UI/UX improvements
- Additional integrations

### 📖 Documentation
- Improve README files
- Add API documentation
- Create tutorials and guides
- Fix typos and clarify explanations
- Add code examples

### 🧪 Tests
- Write unit tests
- Write integration tests
- Add end-to-end tests
- Improve test coverage

### 🎨 UI/UX
- Design improvements
- Accessibility enhancements
- Responsive design fixes
- Animation improvements

### ♻️ Refactoring
- Code cleanup
- Architecture improvements
- Dependency updates
- Type safety improvements

### 🌍 Localization
- Translations
- Internationalization support
- Regional customizations

**Good issues to start with:**
- `good-first-issue` — Perfect for your first contribution
- `help-wanted` — Community help appreciated
- `documentation` — Improve our docs
- `bug` — Known issues to fix

---

## 🚀 Getting Started

### Prerequisites

Before you start, ensure you have:

- **Git** — Version control
- **GitHub Account** — To fork and submit PRs
- **IDE** — VS Code, IntelliJ, Android Studio, etc.
- **Platform Tools**:
  - Desktop: Java 21+, Maven
  - Mobile: Flutter SDK, Dart
  - Web: PHP 8.2+, Node.js 18+

### Fork & Clone

1. **Fork the repository**
   ```bash
   Click "Fork" on GitHub (top right)
   ```

2. **Clone your fork**
   ```bash
   git clone https://github.com/YOUR_USERNAME/rakcha.git
   cd rakcha
   ```

3. **Add upstream remote**
   ```bash
   git remote add upstream https://github.com/aliammari1/rakcha.git
   git remote -v  # Verify both origin and upstream
   ```

---

## 🛠️ Development Setup

### Option 1: Full Stack Setup

```bash
# Install dependencies for all applications
cd rakcha

# Using Task runner
npm install -g @go-task/cli
task desktop:build
task web:install
task mobile:get

# Or manually:
# Desktop
cd apps/desktop && mvn clean install -DskipTests

# Web
cd apps/web && composer install && npm install

# Mobile
cd apps/mobile && flutter pub get
```

### Option 2: Single Application Setup

**Desktop Only:**
```bash
cd apps/desktop
mvn clean install
mvn javafx:run
```

**Web Only:**
```bash
cd apps/web
composer install
npm install
symfony server:start
# Visit http://localhost:8000
```

**Mobile Only:**
```bash
cd apps/mobile
flutter pub get
flutter run
```

### Option 3: Docker Setup

```bash
docker-compose -f apps/web/compose.yaml up -d
# Services available at http://localhost
```

---

## 📝 Development Workflow

### 1. Create a Feature Branch

```bash
git fetch upstream
git checkout -b feature/amazing-feature upstream/main
```

Branch naming conventions:
- `feature/` — New features
- `fix/` — Bug fixes
- `docs/` — Documentation
- `refactor/` — Code refactoring
- `test/` — Test improvements
- `perf/` — Performance improvements

### 2. Make Your Changes

- Write clean, readable code
- Follow project code style
- Add tests for new functionality
- Update documentation
- Keep commits logical and atomic

### 3. Test Your Changes

```bash
# Desktop
cd apps/desktop && mvn test

# Web
cd apps/web && php bin/phpunit

# Mobile
cd apps/mobile && flutter test
```

### 4. Keep in Sync with Upstream

```bash
git fetch upstream
git rebase upstream/main
# If conflicts: resolve them, then git rebase --continue
```

### 5. Push to Your Fork

```bash
git push origin feature/amazing-feature
```

---

## 📌 Commit Guidelines

We follow [Conventional Commits](https://www.conventionalcommits.org/) format:

```
<type>(<scope>): <subject>

<body>

<footer>
```

### Types

- `feat:` — New feature
- `fix:` — Bug fix
- `docs:` — Documentation only
- `style:` — Code style (formatting, etc.)
- `refactor:` — Code refactoring
- `perf:` — Performance improvement
- `test:` — Test additions/changes
- `chore:` — Build, dependencies, etc.
- `ci:` — CI/CD changes

### Scope

Specify what part of the codebase is affected:
- `desktop`, `mobile`, `web`, `api`, `database`, `auth`, etc.

### Examples

```bash
# Good commits
git commit -m "feat(cinema): add seat selection feature"
git commit -m "fix(auth): resolve JWT token expiration bug"
git commit -m "docs(api): update authentication endpoints"
git commit -m "test(mobile): add widget test for booking screen"

# Bad commits
git commit -m "fixed stuff"
git commit -m "updated code"
git commit -m "WIP"
```

### Commit Best Practices

- ✅ Keep commits focused and logical
- ✅ Write descriptive commit messages
- ✅ Separate concerns into different commits
- ✅ Test before committing
- ✅ Don't mix formatting with functional changes

---

## 🔀 Pull Request Process

### Before Submitting

1. **Check existing PRs** — Avoid duplicate work
2. **Create an issue first** — For major features, discuss first
3. **Update from upstream** — Ensure you're up to date
4. **Run tests** — All tests must pass
5. **Review your own code** — Self-review first
6. **Update documentation** — Reflect your changes in docs

### Submitting a PR

1. **Go to the main repository** (not your fork)
2. **Click "New Pull Request"** or **"Compare & Pull Request"**
3. **Select your branch** against `main`
4. **Fill out the PR template** (see below)
5. **Submit for review**

### PR Template

```markdown
## Description
Brief description of changes

## Type of Change
- [ ] Bug fix
- [ ] New feature
- [ ] Breaking change
- [ ] Documentation update

## Related Issues
Closes #123

## How Has This Been Tested?
Describe testing performed

## Screenshots (if applicable)
Add screenshots for UI changes

## Checklist
- [ ] My code follows the style guidelines
- [ ] I have performed a self-review
- [ ] I have commented complex code
- [ ] I have made corresponding documentation changes
- [ ] My changes generate no new warnings
- [ ] I have added tests that prove my fix/feature works
- [ ] New and existing unit tests pass
```

### PR Review Process

1. **Automated Checks** — CI/CD pipeline must pass
2. **Code Review** — Maintainers review your code
3. **Feedback** — Address feedback and make changes
4. **Approval** — Get approval from 1-2 maintainers
5. **Merge** — PR is merged into main branch

---

## 🎨 Style Guides

### Java Code Style (Desktop)

```java
// Follow Google Java Style Guide
// https://google.github.io/styleguide/javaguide.html

// Example:
public class CinemaManager {
    private static final Logger logger = LoggerFactory.getLogger(CinemaManager.class);
    
    public void createCinema(Cinema cinema) {
        validateCinema(cinema);
        // Implementation
    }
    
    private void validateCinema(Cinema cinema) {
        if (cinema == null) {
            throw new IllegalArgumentException("Cinema cannot be null");
        }
    }
}
```

**Rules:**
- Use 4 spaces for indentation
- Place opening braces on same line
- Maximum line length: 100 characters
- Use meaningful variable names
- Add @Override annotations
- Document public methods with JavaDoc

### Dart/Flutter Code Style (Mobile)

```dart
// Follow Dart Style Guide
// https://dart.dev/guides/language/effective-dart/style

// Example:
class CinemaScreen extends StatefulWidget {
  const CinemaScreen({required this.cinemaId});
  
  final String cinemaId;
  
  @override
  State<CinemaScreen> createState() => _CinemaScreenState();
}

class _CinemaScreenState extends State<CinemaScreen> {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Cinema')),
      body: Center(child: Text('Cinema: ${widget.cinemaId}')),
    );
  }
}
```

**Rules:**
- Use 2 spaces for indentation
- Use `const` whenever possible
- Document public classes and methods
- Use meaningful names
- Prefer `late` over nullable types when safe

### PHP Code Style (Web)

```php
// Follow PSR-12 Extended Coding Style
// https://www.php-fig.org/psr/psr-12/

namespace App\Service;

class CinemaService
{
    public function createCinema(Cinema $cinema): Cinema
    {
        $this->validateCinema($cinema);
        // Implementation
        return $cinema;
    }
    
    private function validateCinema(Cinema $cinema): void
    {
        if (empty($cinema->getName())) {
            throw new \InvalidArgumentException('Cinema name is required');
        }
    }
}
```

**Rules:**
- Use 4 spaces for indentation
- Follow PSR-12 standard
- Use type hints everywhere
- Add PHPDoc comments
- Namespace everything

### Naming Conventions

| Element | Convention | Example |
|---------|-----------|---------|
| **Classes** | PascalCase | `CinemaManager` |
| **Functions/Methods** | camelCase | `createCinema()` |
| **Constants** | UPPER_SNAKE_CASE | `MAX_SEATS` |
| **Variables** | camelCase | `cinemaName` |
| **Booleans** | is/has prefix | `isActive`, `hasSeats` |
| **Files** | PascalCase (classes) | `CinemaManager.java` |

---

## 🧪 Testing Requirements

### Unit Tests

- Minimum 80% code coverage for new code
- Test both happy path and edge cases
- Use descriptive test names

### Desktop (Java/JUnit)

```java
@Test
public void testCreateCinema_ValidInput_Success() {
    Cinema cinema = new Cinema("Test Cinema", 5);
    assertNotNull(cinema);
    assertEquals("Test Cinema", cinema.getName());
}

@Test(expected = IllegalArgumentException.class)
public void testCreateCinema_NullInput_ThrowsException() {
    new Cinema(null, 5);
}
```

### Mobile (Flutter)

```dart
testWidgets('CinemaScreen displays cinema name', (WidgetTester tester) async {
  await tester.pumpWidget(
    MaterialApp(
      home: CinemaScreen(cinemaId: '123'),
    ),
  );
  
  expect(find.text('Cinema'), findsOneWidget);
});
```

### Web (PHP/PHPUnit)

```php
public function testCreateCinema_ValidInput_Success(): void
{
    $service = new CinemaService();
    $cinema = $service->createCinema(new Cinema('Test Cinema'));
    
    $this->assertNotNull($cinema);
    $this->assertEquals('Test Cinema', $cinema->getName());
}
```

### Running Tests

```bash
# Desktop
cd apps/desktop && mvn test

# Mobile
cd apps/mobile && flutter test

# Web
cd apps/web && php bin/phpunit
```

---

## 📚 Documentation

### When to Update Documentation

- ✅ New features
- ✅ API changes
- ✅ Configuration changes
- ✅ Architecture decisions
- ✅ Setup/installation changes

### Documentation Locations

| Type | Location |
|------|----------|
| **README** | Root & app-specific directories |
| **API Docs** | `shared/api-spec/openapi.yaml` |
| **Architecture** | `docs/` directory |
| **Code Comments** | Inline in source files |
| **Setup Guide** | `docs/SETUP.md` |

### Documentation Standards

```markdown
# Feature Name

## Overview
Brief description of what feature does

## Usage
How to use the feature

### Example
Code example

## Configuration
Configuration options

## Troubleshooting
Common issues and solutions
```

---

## 🌍 Community

- **Discussions** — Use GitHub Discussions for ideas
- **Issues** — Report bugs here
- **PRs** — Submit contributions
- **Discord/Slack** — [Join our community]
- **Mailing List** — Stay updated

---

## ❓ FAQ

**Q: How long does PR review take?**
A: Typically 2-7 days depending on complexity.

**Q: Can I work on multiple features?**
A: Yes, use separate branches for each feature.

**Q: What if my PR is rejected?**
A: Request feedback, make changes, resubmit. Rejection isn't personal!

**Q: Do I need to sign a CLA?**
A: No, but your contribution must be original.

**Q: Can commercial entities contribute?**
A: Yes! Check [LICENSE](LICENSE) for commercial use terms.

---

## 📮 Questions or Need Help?

- Check [existing issues](../../issues)
- Create a new discussion
- Email: contact@aliammari.com
- Read the [documentation](./docs/)

---

<div align="center">

**Thank you for contributing to RAKCHA!** 🚀

Your contributions make RAKCHA better for everyone.

[⬆ Back to top](#contributing-to-rakcha)

</div>
