# Design principles

- Elegant Design over Boilerplate: Strive for expressive, clean, and declarative code. Use language features and
  patterns (like metaprogramming concepts) to reduce repetitive code.
- Leverage Proven Dependencies: Don't reinvent the wheel. Use well-maintained, reliable third-party libraries for common
  problems like CLI parsing, configuration, and serialization.
- Pragmatic Tooling, Not Framework Worship: Use powerful tools, but don't let them dictate the application's
  architecture. The design should be driven by our principles, not by a framework's conventions. The goal is clean,
  decoupled modules.
- Clarity and Explicit DI: Prefer clear, traceable dependency injection via constructors. This makes the code easier to
  understand and maintain than relying on the "magic" of a DI framework.
- Simplicity and Standards: Favor simple, standard formats for data and configuration, like JSON, to ensure
  interoperability and ease of use.

# Code Guidelines

- Elegance
    - abstract, modularize
    - syntax: ternary, switch, etc
    - consolidate
    - deduplicate
- No comments: rely purely on self-documenting code and meaningful identifiers
- Latest versions of JavaScript and dependencies
- Professional-grade, not explanatory/educational
- Don't repeat yourself (DRY)
