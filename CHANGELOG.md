# Changelog

## Version 1.0.0
_2026.04.19_
### Removed
* Removed `isEmpty` property from `Mailbox` interface, emptiness is already signalled by `poll()` returning `null`
### Fixed
* Fixed concurrent access to Mailbox by consolidating receive and process into a single coroutine
* Fixed `invokeOnClose(...)` on aktor `SendChannel` to follow the default `SendChannel` behavior

## Version 0.2.0
_2023.02.20_
### New
* Added possibility to add custom close handlers to aktor `SendChannel` via `invokeOnClose(...)`

## Version 0.1.1
_2023.02.19_
### Fixed
* Fixed transitive dependencies scope

## Version 0.1.0
_2023.02.19_
### New
* Initial release