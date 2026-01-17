Android Development Best Practices (Corporate Standard)

Purpose
This document defines mandatory Android development standards to ensure high code quality, security, performance, scalability, and consistency across corporate Android applications. All Android developers must comply with these guidelines.

----------------------------------------------------------------

1. Project Architecture

1.1 Architecture Pattern
- Follow MVVM strictly
- UI → ViewModel → UseCase → Repository → Data Source
- Activities and Fragments must only handle UI logic
- Business logic in UI layers is forbidden

Recommended package structure:
ui/
activity/
fragment/
viewmodel/
domain/
usecase/
model/
data/
repository/
datasource/

1.2 Responsibility Rules
- One class must have one responsibility
- ViewModels must not reference Android UI classes or Context

----------------------------------------------------------------

2. Language and Tooling

2.1 Kotlin Usage
- Kotlin is mandatory for all new code
- Java allowed only for legacy modules
- Prefer data class, sealed class, and object
- Avoid enums unless strictly required

2.2 Coroutines
- Use kotlinx.coroutines
- GlobalScope is forbidden
- Use structured concurrency:
    - viewModelScope
    - lifecycleScope

----------------------------------------------------------------

3. UI Development

3.1 UI Framework
- Prefer Jetpack Compose for new features
- If XML is used:
    - No hardcoded strings
    - Use styles and themes
    - Avoid deep layout nesting
    - Prefer ConstraintLayout

3.2 View Binding
- ViewBinding is mandatory
- findViewById() is forbidden

----------------------------------------------------------------

4. State Management
- Use StateFlow or Flow
- Avoid LiveData for new implementations

----------------------------------------------------------------

5. Dependency Injection
- Hilt is mandatory
- Manual singleton creation is forbidden
- Service Locator pattern is not allowed

----------------------------------------------------------------

6. Networking

6.1 API Layer
- Use Retrofit with OkHttp
- Use DTOs for network responses
- Map DTOs to domain models

6.2 Error Handling
- Never expose raw exceptions to UI
- Wrap responses using sealed result classes
- Provide user-friendly error messages

----------------------------------------------------------------

7. Data Storage
- Use Room for structured data
- Use DataStore instead of SharedPreferences
- Encrypt sensitive user data

----------------------------------------------------------------

8. Security Standards
- No secrets, keys, or credentials in source code or Git
- Use Android Keystore and EncryptedSharedPreferences
- Enable Proguard or R8 for release builds
- Implement SSL pinning where required

----------------------------------------------------------------

9. Logging
- Use Timber
- No Log.d() in production builds
- Never log sensitive or personal data

----------------------------------------------------------------

10. Performance Guidelines
- Avoid memory leaks
- Do not store Context in ViewModels
- Use lazy initialization
- Optimize RecyclerView using DiffUtil

----------------------------------------------------------------

11. Testing

11.1 Mandatory Tests
- Unit tests required for ViewModels and UseCases
- UI tests required for critical user flows

11.2 Testing Tools
- JUnit
- Mockito or MockK
- Espresso or Compose UI Test

----------------------------------------------------------------

12. Code Quality Standards

12.1 Formatting
- Follow official Kotlin coding conventions
- Use ktlint and detekt
- Max function length: 40 lines

12.2 Naming Conventions
- Classes: PascalCase
- Functions and variables: camelCase
- Constants: UPPER_SNAKE_CASE

----------------------------------------------------------------

13. Git and Pull Request Rules

13.1 Commits
- Small, focused, meaningful commits
- Clear and descriptive commit messages

13.2 Pull Requests
- Clear PR description required
- Screenshots mandatory for UI changes
- All tests must pass before merge
- Direct commits to main branch are forbidden

----------------------------------------------------------------

14. Documentation
- Public functions must include KDoc
- Each module must have a README
- Architecture diagrams must be updated regularly

----------------------------------------------------------------

15. Compliance
- Non-compliant code may be rejected in PR review
- Refactoring may be mandated
- Release may be blocked

----------------------------------------------------------------

Final Note
These rules ensure Android applications are secure, scalable, maintainable, and enterprise-ready.
Compliance is mandatory.

---
type: "manual"
---
