(defproject reactive-clojure-webapp "0.1.0-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/clojurescript "0.0-3211"]
                 [reagent "0.5.1"]
                 [re-frame "0.4.1"]
                 [re-com "0.6.2"]]

  :source-paths ["src/clj"]

  :plugins [[lein-cljsbuild "1.0.6"]
            [lein-figwheel "0.3.3" :exclusions [cider/cider-nrepl]]  ]

  :clean-targets ^{:protect false} ["resources/public/js/compiled" "target"  ]
  
  :cljsbuild {:builds [{:id "dev"
                        :source-paths ["src/cljs"]

                        :figwheel {:on-jsload "reactive-clojure-webapp.core/mount-root"}

                        :compiler {:main reactive-clojure-webapp.core
                                   :output-to "resources/public/js/compiled/app.js"
                                   :output-dir "resources/public/js/compiled/out"
                                   :asset-path "js/compiled/out"
                                   :source-map-timestamp true}}

                       {:id "min"
                        :source-paths ["src/cljs"]
                        :compiler {:main reactive-clojure-webapp.core
                                   :output-to "resources/public/js/compiled/app.js"
                                   :optimizations :advanced
                                   :pretty-print false}}]})
