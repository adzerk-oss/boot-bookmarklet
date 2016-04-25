# boot-bookmarklet

[![Clojars Project](http://clojars.org/adzerk/boot-bookmarklet/latest-version.svg)](http://clojars.org/adzerk/boot-bookmarklet)

A [Boot](http://boot-clj.com) task for generating [bookmarklets](https://en.wikipedia.org/wiki/Bookmarklet) from ClojureScript namespaces, allowing you to write bookmarklets in ClojureScript! :book: :bookmark:

## Usage

Add `adzerk/boot-bookmarklet` to your `build.boot` dependencies and require/refer in the task:

```clojure
(set-env! :dependencies '[[adzerk/boot-bookmarklet "X.Y.Z" :scope "test"]])
(require '[adzerk.boot-bookmarklet :refer (bookmarklet external-bookmarklet)])
```

### Tasks

* `bookmarklet` compiles your ClojureScript into JavaScript and stuffs the code for each namespace into a `javascript:<CODE HERE>` bookmarklet link.

  `bookmarklet` is convenient if your bookmarklet happens to be small enough that the compiled, URL-encoded JavaScript can fit in the bookmarklet link. [The character limit for a bookmarklet link varies from browser to browser](http://subsimple.com/bookmarklets/rules.php#CharLimit).

* `external-bookmarklet` takes any number of URLs to hosted .js files and generates a bookmarklet link for each file that sources and runs it.

  Once your code reaches a certain size, you will hit the character limit and your bookmarklet won't work anymore. To get around this, you can host your compiled .js file somewhere (e.g. Dropbox, S3) and use `external-bookmarklet` to generate a small bookmarklet that simply loads the hosted .js file.

### `bookmarklet`

The `bookmarklet` task takes an (optional) option `:ids` which is a set of strings identifying cljs files on the source path:

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

When run, this task will compile the namespace of each ClojureScript file into a standalone JavaScript file (compiled with advanced optimizations), read the contents of the file, and generate an HTML file called `bookmarklets.html`, containing a bookmarklet link for each ClojureScript file identified in `:ids`.

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

### `external-bookmarklet`

In order to use `external-bookmarklet`, your bookmarklet code must already be compiled to JavaScript and hosted somewhere. Once you have the URL to your hosted .js file, you can provide it to `external-bookmarklet` via the `-u/--urls` option, and it will generate a `bookmarklets.html` file containing a bookmarklet link which, when clicked, will fetch and run your hosted .js file in the context of the current page.

```
boot external-bookmarklet -u http://path.to/my/hosted.js
```

```
(deftask link
  (external-bookmarklet :urls #{"http://path.to/my/hosted.js"}))
```

If you provide multiple URLs, the generated HTML file will contain one bookmarklet link per .js file.

*TODO: example build task that watches for changes, updates the hosted.js, and generates bookmarklets.html with an external bookmarklet*

## Example

The simplest of ClojureScript bookmarklets might look something like this:

```clojure
(ns example.bookmarklet)

(js/alert "o hai!")
```

*A more complex example project is in the works. Stay tuned!*

## Contributing

There are probably all kinds of ways `boot-bookmarklet` could be improved. Pull Requests welcome!

## License

Copyright © 2016 Adzerk

Distributed under the Eclipse Public License version 1.0.
