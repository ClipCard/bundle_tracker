# Bundle Tracker #

At time of writing, there is no dedicated public listing of Mac OS X bundle/package directory types. It's possible to detect these types within OS X native code, but identifying a bundle or package on other platforms is either a frustrating excursion into tribal knowledge or simply not done at all. Bundle Tracker intends to be a repository to collect bundle/package listings from contributors for all to use.

## What is a bundle or package? ##

Many types of "files" on OS X are actually directories that are treated specially by the OS. Users interact with these types every day, often without realizing it. The most common types are actually dedicated system resources: applications, plug-ins and so on.

On OS X, a bundle or package is registered with the LaunchServices system database, with types registered as conforming to either `com.apple.bundle` or `com.apple.package`. But in reality they are simply directories stored on a filesystem, typically named with an extension reflecting their type so that they will be handled by the appropriate application or service.

For simplicity, we'll refer to both types as "bundles"; for the purposes of developers the upshot is the same: a "bundle" is a directory in a filesystem, intended to be treated by end-users as a singular resource.

### Detecting bundles ###

On OS X, it is possible to access the LaunchServices database and determine that a directory conforms to one of the bundle types on the system. On other platforms, typically the only metadata available will be a directory name extension. A directory named "Frobnicator.app" is not guaranteed to be a Mac application, but it probably is.

The purpose of this project is to crowd-source evidence-based detection by running the same tracker on multiple Mac systems, and collecting the cumulative listings as static resources to be shared. For now, this means a mapping of type descriptions to directory name extensions. These listings can be used by other projects to provide more intelligent handling of directories that are likely to be bundles.

Bundle Tracker works by reading a dump from the local LaunchServices database, identifying locally registered directory name extensions which are treated as bundles.

## Dependencies ##

To run the Bundle Tracker, you need [Leiningen](http://leiningen.org/) (the Clojure project manager). It can be installed from [Homebrew](http://brew.sh/) or by following the instructions on the Leiningen website.

### Homebrew example ###

```bash
brew install leiningen
```

## Compatibility ##

Bundle Tracker has been tested on OS X 10.10.x ("Yosemite"). It's possible that LaunchServices versions from earlier OS X releases are incompatible. Bug reports and corrections are welcome!

## Contributing ##

The simplest way to contribute is to run the tracker on your own Mac, and open a Pull Request with the changes produced by running it.

You can perform a dry run with the following command:

```bash
lein run
```

Changes are always additive. Every run will combine bundle types detected on the running system with those stored in the repository. To store these changes, run:

```bash
lein run :save
```

Simply open a PR to propose additions.

Of course, other types of contributions are also welcome:

- Are we missing types on your system?
- Is Bundle Tracker misinterpreting a LaunchServices dump? The format is not documented, and the reader may be fragile.
- Are there better descriptions for a bundle type we don't already know about? Overrides can be specified in `/src/bundle_tracker/overrides.clj`

## Tests ##

But of course.

```bash
lein midje
```

## Known types ##

You can view the current listing of known types in the following formats:

- [EDN](resources/known_types.edn)
- [JSON](resources/known_types.json)
- [Markdown](resources/known_types.md)

## License ##

The MIT License (MIT)

Copyright (c) 2015 ClipCard

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
