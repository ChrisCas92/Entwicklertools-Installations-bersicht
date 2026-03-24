# Quarkus Config Doc Plugin Lab

This project is a dedicated **test lab** for the `io.quarkus:quarkus-config-doc-maven-plugin`.
It is intentionally focused on plugin validation and configuration/documentation generation behavior.

## Goals

- Provide a reproducible Quarkus project for plugin experiments.
- Cover both plugin goals:
  - `generate-config-doc`
  - `generate-asciidoc` (legacy but still supported)
- Validate multiple plugin options and output targets.
- Include automated test cases that verify:
  - Quarkus config mapping behavior.
  - Profile-specific overrides.
  - Generated config documentation artifacts.

## Tech Stack

- Java 17
- Maven
- Quarkus `3.34.0`
- Quarkus Config Doc Maven Plugin `3.34.0`
- JUnit 5 + `@QuarkusTest`

## Project Structure

```text
quarkus-config-doc-plugin-lab/
├── pom.xml
├── README.MD
├── scripts/generate-config-previews.sh
└── src
    ├── main
    │   ├── java/com/example/config/FeatureFlags.java
    │   └── resources/application.properties
    └── test
        └── java/com/example/config
            ├── FeatureFlagsConfigTest.java
            ├── FeatureFlagsDevProfile.java
            ├── FeatureFlagsDevProfileTest.java
            └── ConfiguredPropertiesDocTest.java
```

## Plugin Configuration Matrix

The plugin is executed in `generate-resources` with three executions:

1. **Asciidoc via `generate-config-doc`**
   - `format=asciidoc`
   - `theme=default`
   - output: `target/quarkus-config-doc/asciidoc-default`

2. **Markdown via `generate-config-doc`**
   - `format=markdown`
   - `theme=compact`
   - `enableEnumTooltips=true`
   - output: `target/quarkus-config-doc/markdown-compact`

3. **Asciidoc via `generate-asciidoc` (legacy goal)**
   - `format=asciidoc`
   - `theme=default`
   - output: `target/quarkus-config-doc/asciidoc-legacy-goal`

All three executions use:

- `generateAllConfig=true`


## Additional Generated Preview Documentation

Besides the plugin outputs, this project also generates a **configured properties preview** from `application.properties` during `generate-resources`:

- `target/quarkus-config-doc/configured-properties/configured-quarkus-properties.md`
- `target/quarkus-config-doc/configured-properties/configured-quarkus-properties.adoc`

This makes it easy to quickly inspect many configured `quarkus.*` keys in table form.

## Runtime Config Test Cases

### 1) Default profile config mapping (`FeatureFlagsConfigTest`)

Validates:

- boolean config
- `Duration`
- `List<String>`
- `Map<String,Integer>`
- `Optional<String>` absent value

### 2) Dev profile override test (`FeatureFlagsDevProfileTest`)

Validates `%dev` profile overrides from `application.properties`:

- `lab.feature.enabled=false`
- `lab.feature.owner=dev-team`

## Generated Documentation Verification Cases

Generated documentation is verified with file and content checks after `mvn generate-resources`:

- Asciidoc output exists: `target/quarkus-config-doc/asciidoc-default/quarkus-all-config.adoc`
- Markdown output exists: `target/quarkus-config-doc/markdown-compact/quarkus-all-config.md`
- Legacy goal output exists: `target/quarkus-config-doc/asciidoc-legacy-goal/quarkus-all-config.adoc`
- Each file contains the shared section header `About the Duration format` to confirm content generation.

## How to Run

### Run all checks (recommended)

```bash
mvn test
```

This triggers:

1. `generate-resources` (plugin documentation generation)
2. test compile
3. JUnit test execution for config mapping + profile behavior

### Verify generated docs and content

```bash
mvn -q generate-resources
find target/quarkus-config-doc -maxdepth 3 -type f
test -f target/quarkus-config-doc/asciidoc-default/quarkus-all-config.adoc
test -f target/quarkus-config-doc/markdown-compact/quarkus-all-config.md
test -f target/quarkus-config-doc/asciidoc-legacy-goal/quarkus-all-config.adoc
test -f target/quarkus-config-doc/configured-properties/configured-quarkus-properties.md
test -f target/quarkus-config-doc/configured-properties/configured-quarkus-properties.adoc
rg -q "About the Duration format" target/quarkus-config-doc/asciidoc-default/quarkus-all-config.adoc
rg -q "About the Duration format" target/quarkus-config-doc/markdown-compact/quarkus-all-config.md
rg -q "About the Duration format" target/quarkus-config-doc/asciidoc-legacy-goal/quarkus-all-config.adoc
rg -q "quarkus.datasource.jdbc.url" target/quarkus-config-doc/configured-properties/configured-quarkus-properties.md
rg -q "quarkus.smallrye-openapi.info-title" target/quarkus-config-doc/configured-properties/configured-quarkus-properties.md
```

## Troubleshooting

- If no docs appear, verify plugin executions in `pom.xml` are bound to `generate-resources`.
- If profile tests fail, confirm `%dev.*` values exist in `src/main/resources/application.properties`.
- If CI is slow on first run, it is usually due to initial dependency downloads from Maven Central.

## Why this project exists

This lab isolates plugin behavior from larger application concerns, making it easy to test and tweak:

- output formats,
- themes,
- goal compatibility,
- and regression behavior across Quarkus upgrades.

