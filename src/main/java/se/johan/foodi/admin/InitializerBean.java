/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.johan.foodi.admin;

import java.nio.file.Files;
import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import org.slf4j.LoggerFactory;

/**
 *
 * @author johan
 */
@Singleton
@Startup
public class InitializerBean {
    
   @PostConstruct
   public void onStartup() {
       LoggerFactory.getLogger(InitializerBean.class).info("invoked from initializer bean!");
       
       checkUploadedFilesDir();
   }
   
   /**
    * Asserts that the upload dir is writable.
    */
   public void checkUploadedFilesDir() {
       
        if (!Files.isDirectory(RecipeBean.UPLOADED_FILES_TARGET_DIR)
                || !Files.isWritable(RecipeBean.UPLOADED_FILES_TARGET_DIR)) {
            throw new RuntimeException("Uploaded files' dir must be a writable dir: "
                    + RecipeBean.UPLOADED_FILES_TARGET_DIR.toString()
            );
        }
   }
}
