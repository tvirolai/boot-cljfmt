# boot-cljfmt

[![License](https://img.shields.io/badge/License-EPL%201.0-red.svg)](https://opensource.org/licenses/EPL-1.0)
[![CircleCI](https://img.shields.io/circleci/project/github/siilisolutions/boot-cljfmt.svg)](https://circleci.com/gh/siilisolutions/boot-cljfmt)

A port of [lein-cljfmt](https://github.com/weavejester/cljfmt) for Boot.
That is, a library for checking and fixing the formatting of Clojure files.

## Usage

`boot-cljfmt` can currently be used for checking and fixing the formatting of single files or folders.

Check all files in the current project:
```clojure
boot check -f .
```

Checking a single files is done in a similar way:
```clojure
boot check -f somefile.clj
```

Fixing files with the `fix` task works the same way:
```clojure
boot fix -f .
```

You can run the tests the usual way:
```clojure
boot test
```

If you're interested in test coverage:
```clojure
boot bat-test -c
```

## License

Copyright © 2018 Siili Solutions

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
