((nil . ((projectile-project-compilation-cmd . "gradle -q --console=plain build")
         (projectile-project-run-cmd . "./gradlew -q --console=plain run")
         (eval . (setq eclimd-default-workspace
                       (concat
                        (locate-dominating-file default-directory ".dir-locals.el")
                        "..")))
         (eval . (global-set-key [f5] '(lambda () (interactive)
                                         (eclim-java-run-run "Run PlateDecoder")))
               )
         )
      )
 )
