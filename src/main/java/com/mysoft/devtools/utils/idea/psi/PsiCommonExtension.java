package com.mysoft.devtools.utils.idea.psi;

import com.intellij.psi.PsiElement;
import com.intellij.refactoring.listeners.RefactoringElementListener;
import com.intellij.refactoring.rename.RenameUtil;
import com.intellij.usageView.UsageInfo;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.Collections;
import java.util.Map;

/**
 * @author hezd 2023/5/25
 */
public class PsiCommonExtension {
    /**
     * 重命名操作
     *
     * @param element                    需要重命名的元素
     * @param newName                    新名称
     * @param searchInStringsAndComments 注释是否需要重命名
     * @param searchForTextOccurrences   一个布尔值，指示是否搜索文本出现次数。如果为 true，则 findUsages() 将告诉 IDEA 在项目范围内搜索所有使用情况，即使没有直接关联到代码元素；如果为 false，则只搜索与代码元素直接相关的使用情况。
     */
    public static void rename(PsiElement element, String newName, boolean searchInStringsAndComments, boolean searchForTextOccurrences) {
        if (!EventQueue.isDispatchThread()) {
            return;
        }
        //一个 PsiElement -> String 映射，其中包含与当前代码元素相关联的所有可能的重命名。如果命名映射中存在一个给定代码元素的新名称，则 findUsages() 将使用它来替换所有使用情况。
        Map<? extends PsiElement, String> allRenames = Collections.emptyMap();
        UsageInfo[] usages = RenameUtil.findUsages(element, newName, searchInStringsAndComments, searchForTextOccurrences, allRenames);
        RenameUtil.doRename(element, newName, usages, element.getProject(), new RefactoringElementListener() {
            @Override
            public void elementMoved(@NotNull PsiElement newElement) {

            }

            @Override
            public void elementRenamed(@NotNull PsiElement newElement) {

            }
        });
    }
}
