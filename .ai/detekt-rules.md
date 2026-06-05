# Detekt Rules Summary

Derived from `detekt.yml`. Update this file when `detekt.yml` changes.

## Function signatures (ktlint `FunctionSignature`)
≥ 2 parameters → multiline, trailing comma required:
```kotlin
fun foo(
    a: Int,
    b: String,
): Unit
```

## Named arguments (`NamedArguments`, allowedArguments = 1)
> 1 argument → named arguments required (unless variable name matches parameter name):
```kotlin
NetworkLog(method = request.method, url = request.url, statusCode = 200)
```

## Trailing commas
Required at declaration and call sites.

## Magic numbers
Not allowed outside `@Composable`, constants, companion objects. Allowed literals: `-1`, `0`, `1`, `2`.

## Boolean property naming
Must start with: `is`, `has`, `are`, `was`, `were`, `will`, `can`, `should`, `must`, `do`, `does`, `did`, `show`, `hide`, `enable`, `disable`.

## Braces on if
- Single-line body → no braces
- Multi-line body → always braces

## Empty catch blocks
Exception must be named `_` or match `(ignore|expected).*`.

## Coroutines
- `suspend fun` must not return `Flow`.
- Do not swallow `CancellationException`.
- Do not reference `Dispatchers.IO/Default/Unconfined` directly in library code.

## Comments
- `FIXME:` and `STOPSHIP:` are forbidden.
- Write no comments unless the WHY is non-obvious.

## Line length
Max 120 characters.

## Complexity limits (non-Composable)
- Method: 60 lines, cyclomatic complexity 15, cognitive complexity 15.
- Class: 600 lines.
- Parameters: 8 max.
