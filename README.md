# boot-bookmarklet

[![Clojars Project](http://clojars.org/adzerk/boot-bookmarklet/latest-version.svg)](http://clojars.org/adzerk/boot-bookmarklet)

A [Boot](http://boot-clj.com) task for generating [bookmarklets](https://en.wikipedia.org/wiki/Bookmarklet) from ClojureScript namespaces, allowing you to write bookmarklets in ClojureScript! :book: :bookmark:

## Usage

Add `adzerk/boot-bookmarklet` to your `build.boot` dependencies and require/refer in the task:

```clojure
(set-env! :dependencies '[[adzerk/boot-bookmarklet "X.Y.Z" :scope "test"]])
(require '[adzerk.boot-bookmarklet :refer :all])
```

This makes the `bookmarklet` task available. This task takes an (optional) option `:id` which is a set of strings identifying cljs files on the source path:

```clojure
(deftask build
  []
  (comp
    (watch)
    (speak)
    (bookmarklet :ids #{"lobster"})
    (target)))
```

This will match a file `lobster.cljs` if it exists in the source paths.

When run, this task will compile the namespace of each ClojureScript file into a standalone JavaScript file (compiled with advanced optimizations), read the contents of the file, and generate an HTML file at the root of the fileset called `bookmarklets.html`, containing a bookmarklet link for each ClojureScript file identified in `:ids`.

You can then open `target/bookmarklets.html` in your browser and drag the bookmarklet link into your bookmarks.

```clojure
(deftask build
  []
  (comp
    (watch)
    (speak)
    (bookmarklet)
    (target)))
```

If no `:ids` argument is provided, the `bookmarklets` task will generate a bookmarklet for every ClojureScript file found in the source paths. This may be more convenient if you want to create a project that is just a collection of bookmarklets, and you want `bookmarklets` to generate a single HTML page containing a bookmarklet link for each ClojureScript namespace.

You can, of course, also run the `bookmarklet` task from the command-line:

```
boot bookmarklet -i foo -i bar target
```

## Example

*An example project is in the works. Stay tuned!*

## Contributing

There are probably all kinds of ways `boot-bookmarklet` could be improved. Pull Requests welcome!

## License

Copyright © 2016 Adzerk

Distributed under the Eclipse Public License version 1.0.
