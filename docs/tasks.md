# Improvement Tasks Checklist

Note: Each item is actionable and can be checked off when complete. The list is ordered to address foundational architecture first, then code quality, testing, and developer experience.

1. [ ] Establish high-level architecture overview doc (modules, responsibilities, data flow) in docs/architecture.md.
2. [ ] Introduce clear package/module boundaries and naming (e.g., ui, core, io, config, domain, trackmate, tables), and move classes accordingly.
3. [ ] Extract domain models (Experiment, Project, Recording, Square, Track) and define their relationships to reduce ad-hoc string/CSV usage.
4. [ ] Introduce a service layer for business logic (e.g., DirectoryClassificationService, SquareGenerationService) decoupled from UI and I/O.
5. [ ] Create a small ports/adapters boundary for file system access (DirectoryReader interface) to facilitate testing and alternate sources.
6. [ ] Remove UI logic from core services (separate Swing-specific code from validation, config, and classification logic).
7. [ ] Centralize constants (CSV column names, required filenames/dirs) into a single Constants class or enum set to avoid string duplication.
8. [ ] Replace System.out/System.err in production code with the AppLogger or java.util.logging consistently.
9. [ ] Replace hardcoded absolute paths (e.g., image resources) with classpath resource loading via getResource to ensure portability.
10. [ ] Standardize configuration handling: inject a ConfigProvider (wraps JsonConfig) and avoid passing raw string paths across the codebase.
11. [ ] Improve JsonConfig: validate schema, handle missing sections/keys explicitly, and add typed accessors with Optional and exception variants.
12. [ ] Make JsonConfig path handling robust (use Path everywhere; support environment override/location discovery for PaintUtilities.json).
13. [ ] Add error domain: define custom exceptions (e.g., ConfigurationException, ClassificationException) and use them instead of generic IOException where appropriate.
14. [ ] Ensure DirectoryClassifier is pure and side-effect free: return rich result, no logging at error level for expected UNKNOWN cases; move feedback logging to callers.
15. [ ] Refine DirectoryClassifier feedback: structured details (missingFiles, missingDirs, unexpectedFiles) in the result object instead of a single feedback string.
16. [ ] Optimize DirectoryClassifier child scanning with controlled depth and error handling; avoid repeated I/O where possible.
17. [ ] Introduce a Validator utility for UI inputs (integers, doubles, ranges) reused by Swing and any CLI.
18. [ ] In GenerateSquareDialog, decouple view construction from actions by wiring via controllers/listeners; consider MVC pattern.
19. [ ] Prevent tight coupling to frame lifecycle: use JDialog or parent-aware dialogs and proper disposal; avoid EXIT_ON_CLOSE for a dialog utility.
20. [ ] Replace synchronous file chooser and heavy work on EDT with background tasks (SwingWorker) and show progress/disable controls during classification.
21. [ ] Add null/empty path guarding and user guidance in GenerateSquareDialog before classification.
22. [ ] Replace magic numbers/defaults in UI with centrally defined defaults from config and document them.
23. [ ] Ensure UI respects locale (number formats) and add basic i18n hooks for labels and messages.
24. [ ] Replace ImageIcon path in UI with Toolkit/Resource-based icon loading and fallback when resource missing.
25. [ ] Introduce a Resources helper to resolve icons and text messages from classpath.
26. [ ] Add unit tests for JsonConfig (get/set/save/reload, defaults, error cases) with temp files.
27. [ ] Add unit tests for GenerateSquareDialog controller/validation logic (extracted) without requiring Swing components.
28. [ ] Expand DirectoryClassifier tests to cover edge cases: hidden files, Output file vs dir mismatch, non-readable subdirs, symlinks.
29. [ ] Add tests for Tracks and Squares utilities (tracksInSquare boundaries, frequency distribution correctness, getRowAndColumn correctness).
30. [ ] Add integration test that simulates a minimal project directory structure and validates end-to-end classification and reporting.
31. [ ] Configure CI (e.g., GitHub Actions) to run mvn -B -q -DskipITs=false test on pushes and PRs.
32. [ ] Enforce a code style (e.g., Google Java Format or Spotless plugin) and apply across the project.
33. [ ] Enable static analysis (SpotBugs, Checkstyle) with a sensible baseline and fix high/medium priority findings.
34. [ ] Clean up pom.xml: declare plugin versions explicitly, remove unused dependencies, and configure surefire/junit-jupiter properly.
35. [ ] Ensure reproducible builds and set Java release/encoding; configure maven-compiler-plugin with target/source from a single property.
36. [ ] Add logging initialization at application entry points and avoid multiple logger reinitializations.
37. [ ] Ensure AppLogger supports log level configuration and rotating file handler to prevent large logs.
38. [ ] Replace printf/println usages in sample/demo classes with logger or remove from library code.
39. [ ] Hide internal tablesaw dependency behind PaintTable API; avoid leaking raw Tablesaw Table out of core modules.
40. [ ] Review performance hotspots in Tablesaw usage (e.g., frequencyDistribution creates TreeMap; consider Tablesaw groupBy or specialized histograms where large datasets are expected).
41. [ ] Clarify column name contracts and centralize as enums/constants to avoid runtime failures from typos.
42. [ ] Ensure immutability where possible: make fields final, return unmodifiable views, and avoid exposing mutable state.
43. [ ] Add equals/hashCode/toString to domain models to assist testing and logging.
44. [ ] Replace public mutable fields in result objects with private final fields and getters (e.g., ClassificationResult) and consider builder for optional parts.
45. [ ] Standardize exception messages and user-facing messages; avoid exposing internal paths/stack traces in UI dialogs.
46. [ ] Replace JFrame default close operation in GenerateSquareDialog with DISPOSE_ON_CLOSE and return results to caller if part of a larger app.
47. [ ] Add a small CLI entry point to run classification and printing, helpful for automation and testing without UI.
48. [ ] Introduce a versioned CHANGELOG.md and CONTRIBUTING.md describing setup, build, test, and code style.
49. [ ] Add docs/usage.md with examples for loading CSVs, filtering paintTracks, and generating paintSquares.
50. [ ] Audit resource casing and names (PaintUtilities.json vs paint.json); standardize to one canonical name and update code and resources.
51. [ ] Load configuration from classpath resource with fallback to user home app directory; provide a Save As path chooser.
52. [ ] Guard file operations with try-with-resources and informative catch handling; avoid swallowing exceptions.
53. [ ] Use Path and Files APIs consistently (no mixing raw strings) and validate existence/permissions before operations.
54. [ ] Remove dead code and stubs or paintTrack them with TODO tags that reference tasks in this checklist.
55. [ ] Add JavaDocs to public classes/methods explaining responsibilities and expected behavior.
56. [ ] Review access modifiers: reduce visibility to package-private/private where appropriate.
57. [ ] Introduce a small Result/Either type or use Optional where methods can fail without exceptions, to reduce null handling.
58. [ ] Ensure thread-safety where components may be reused; document single-threaded assumptions (e.g., Swing-only access to UI state).
59. [ ] Provide sample test data directories under src/test/resources to support classification tests.
60. [ ] Add a release profile in Maven to create an executable jar with dependencies and proper manifest; avoid committing built artifacts under target/.
