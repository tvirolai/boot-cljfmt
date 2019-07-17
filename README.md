# boot-cljfmt

[![License](https://img.shields.io/badge/License-EPL%201.0-red.svg)](https://opensource.org/licenses/EPL-1.0)
[![CircleCI](https://img.shields.io/circleci/project/github/siili-core/boot-cljfmt.svg)](https://circleci.com/gh/siilisolutions/boot-cljfmt)
[![Downloads](https://jarkeeper.com/siili-core/boot-cljfmt/downloads.svg)](https://jarkeeper.com/siili-core/boot-cljfmt)
[![cljdoc badge](https://cljdoc.xyz/badge/boot-cljfmt/boot-cljfmt)](https://cljdoc.xyz/d/boot-cljfmt/boot-cljfmt/CURRENT)

A port of [lein-cljfmt](https://github.com/weavejester/cljfmt) for Boot.
That is, a library for checking and fixing the formatting of Clojure files.

## Latest version

[![Clojars Project](https://img.shields.io/clojars/v/boot-cljfmt.svg)](https://clojars.org/boot-cljfmt)

## Installation

If you use Boot as your build tool, you can add boot-cljfmt as a dependency to your project by adding 
the following to your `build.boot`:

```clojure
(set-env! :dependencies [boot-cljfmt "0.1.3" :scope "test"])
```

Then you can import the tasks you need:
```clojure
(require '[boot-cljfmt.core :refer [check fix]])
```

With this set up, you can check your project by running `boot check -f .` in the
root of your project.

It also possible to install `boot-cljfmt` globally and run it with Boot on your project:

```clojure
boot -d boot-cljfmt check -f .
```

## Usage

`boot-cljfmt` can currently be used for checking and fixing the formatting of single files or folders.

Check all files in the current project:
```clojure
boot -d boot-cljfmt check -f .
```

Checking a single files is done in a similar way:
```clojure
boot -d boot-cljfmt check -f somefile.clj
```

Fixing files with the `fix` task works the same way:
```clojure
boot -d boot-cljfmt fix -f .
```

## License

Copyright © 2018-2019 Siili Solutions

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
