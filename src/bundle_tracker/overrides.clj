(ns bundle-tracker.overrides)

(def ^{:dynamic true
       :doc "Map of descriptions (either `description` or `uti` from a
            LaunchServices item) to overridden value. Values should be
            provided in English and more friendly than the provided value."}
  *description*
  {"bundle"                               "Bundle"
   "com.apple.application-bundle"         "Application"
   "com.apple.dt.document.snapshot"       "Xcode Snapshot"
   "com.apple.dt.document.workspace"      "Xcode Workspace"
   "com.apple.dt.dvt.plug-in"             "Xcode Development Plug-in"
   "com.apple.dt.ide.plug-in"             "Xcode IDE Plug-in"
   "com.apple.interfacebuilder.document"  "Interface Builder file"
   "com.apple.xcode.archive"              "Xcode Archive"
   "com.apple.xcode.docset"               "Xcode Docset"
   "com.apple.xcode.dsym"                 "Xcode Debug Symbols"
   "com.apple.xcode.model.data"           "Xcode Model Data"
   "com.apple.xcode.model.data-mapping"   "Xcode Data Mapping"
   "com.apple.xcode.model.data-version"   "Xcode Data Version"
   "com.apple.xcode.plugin"               "Xcode Plug-in"
   "com.apple.xcode.project"              "Xcode Project"
   "framework"                            "Framework"
   "localized PDF"                        "Localized PDF"
   "plug-in"                              "Plug-in"
   "rich text with attachments (RTFD)"    "Rich Text Format"})
