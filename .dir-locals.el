((nil . ((projectile-project-compilation-cmd . "gradle -q --console=plain build")
         (projectile-project-run-cmd . "./gradlew -q --console=plain run")
         (eval . (global-set-key [S-f5]
                                 '(lambda () (interactive)
                                    (dap-debug (list :type "java"
                                                     :request "launch"
                                                     :args "--skip-sane-init"
                                                     :vmArgs "-Djava.library.path=target/lib"
                                                     :cwd "/home/nelson/src/cbsr/java/platedecoder"
                                                     :stopOnEntry :json-false
                                                     :host "localhost"
                                                     :request "launch"
                                                     :classPaths nil
                                                     :projectName ""
                                                     :mainClass "org.biobank.platedecoder.ui.PlateDecoder")))))
         ;; (eval . (setq eclimd-default-workspace
         ;;               (concat
         ;;                (locate-dominating-file default-directory ".dir-locals.el")
         ;;                "..")))
         ;; (eval . (global-set-key [f5]
         ;;                         '(lambda () (interactive)
         ;;                            (eclim-run-configuartion "Run PlateDecoder"))))
         ;; (eval . (global-set-key [(control f5)]
         ;;                         '(lambda () (interactive)
         ;;                            (nl/gradle-javadoc))))
         )))
