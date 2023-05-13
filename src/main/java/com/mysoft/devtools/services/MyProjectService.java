package com.mysoft.devtools.services;

import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;

/**
 * @author hezd 2023/4/22
 */
@Service(Service.Level.PROJECT)
public class MyProjectService {
    public MyProjectService(Project project) {


    }
}
