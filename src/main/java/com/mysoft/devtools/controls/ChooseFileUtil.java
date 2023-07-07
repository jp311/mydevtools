package com.mysoft.devtools.controls;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.ui.components.fields.ExtendableTextComponent;
import com.intellij.util.Consumer;
import com.mysoft.devtools.utils.idea.psi.IdeaContext;

/**
 * @author hezd   2023/6/7
 */
public class ChooseFileUtil {
    /**
     * 选择单个目录
     */
    public static ExtendableTextComponent.Extension getChooseSingleFolderExtension(String title, Consumer<String> consumer) {
        return ExtendableTextComponent.Extension.create(AllIcons.General.OpenDisk, AllIcons.General.OpenDiskHover,
                title, () -> {
                    FileChooserDescriptor dirChooser = FileChooserDescriptorFactory.createSingleFolderDescriptor()
                            .withShowHiddenFiles(true)
                            .withShowFileSystemRoots(true)
                            .withTitle(title);
                    FileChooser.chooseFiles(dirChooser, IdeaContext.getActiveProject(), null, paths -> {
                        if (paths.size() == 0) {
                            return;
                        }
                        consumer.consume(paths.get(0).getPath());
                    });
                });
    }

    /**
     * 选择单个文件
     */
    public static ExtendableTextComponent.Extension getChooseSingeFileExtension(String title, Consumer<String> consumer) {
        return ExtendableTextComponent.Extension.create(AllIcons.General.OpenDisk, AllIcons.General.OpenDiskHover,
                title, () -> {
                    FileChooserDescriptor dirChooser = FileChooserDescriptorFactory.createSingleFileDescriptor()
                            .withShowHiddenFiles(true)
                            .withShowFileSystemRoots(true)
                            .withTitle(title);

                    FileChooser.chooseFiles(dirChooser, IdeaContext.getActiveProject(), null, paths -> {
                        if (paths.size() == 0) {
                            return;
                        }
                        consumer.consume(paths.get(0).getPath());
                    });
                });
    }

}
