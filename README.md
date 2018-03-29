# boot-cljfmt

[![License](https://img.shields.io/badge/License-EPL%201.0-red.svg)](https://opensource.org/licenses/EPL-1.0)

A port of [lein-cljfmt](https://github.com/weavejester/cljfmt) for Boot.
That is, a library for checking and fixing the formatting of Clojure files.

## Usage

`boot-cljfmt` can currently be used for checking the formatting of single files
or folders. Support for fixing as well as custom indent rules will be added soon.

Check all files in the current project:
```clojure
boot fmt -f .
```

Checking a single files is done in a similar way:
```clojure
boot fmt -f build.boot
```

## License

Copyright © 2018 Siili Solutions

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
