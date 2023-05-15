package com.mysoft.devtools.Inspections;

/**
 * 变量名称检查
 * 1、针对@Resource注入的变量名称推荐和类型命名一致，防止出现错误
 * 2、依赖注入的类型是否有@Service或Component注解
 * 3、不能同时有Service和Component注解
 * 4、类名必须全局唯一，或配置了自定义名称
// * todo 需要检查spring ioc容器所有的bean才准确，但是bean注入规则多又复杂一时怕搞不全，另外性能也差，代码检查中不应该有耗性能的操作
// * @author hezd 2023/4/27
// */
//@ExtensionMethod({PsiClassExtension.class, ProjectExtension.class, StringExtension.class})
//public class ResourceInspection extends AbstractBaseJavaLocalInspectionTool {
//    private final RemoveResourceQuickFix removeResourceQuickFix = new RemoveResourceQuickFix();
//    private final AddServiceAnnotationQuickFix addServiceAnnotationQuickFix = new AddServiceAnnotationQuickFix();
//
//    @Override
//    public @NotNull PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {
//        return new JavaElementVisitor() {
//            @Override
//            public void visitField(PsiField field) {
//                if (!field.hasAnnotation(QualifiedNames.AUTOWIRED_QUALIFIED_NAME) && !field.hasAnnotation(QualifiedNames.RESOURCE_QUALIFIED_NAME)) {
//                    super.visitField(field);
//                    return;
//                }
//
//                PsiType fieldType = field.getType();
//                PsiClass fieldTypeClass = PsiUtil.resolveClassInType(fieldType);
//                if (fieldTypeClass == null) {
//                    holder.registerProblem(field, getProblemName(), ProblemHighlightType.ERROR, removeResourceQuickFix, addServiceAnnotationQuickFix);
//                    return;
//                }
//                Project project = fieldTypeClass.getProject();
//
//                //非当前项目的接口或接口不处理，例如平台的BizParamService接口子类没有Service注解
//
//                if (fieldTypeClass.isInterface()) {
//                    boolean isBaseDao = fieldTypeClass.isInheritors(QualifiedNames.BASE_DAO_QUALIFIED_NAME, project);
//                    if (isBaseDao || fieldTypeClass.getAnnotation(QualifiedNames.REMOTE_SERVICE_QUALIFIED_NAME) != null) {
//                        super.visitField(field);
//                        return;
//                    } else {
//                        List<PsiClass> psiClasses = project.deepSearchInheritorClass(fieldTypeClass.getQualifiedName());
//                        boolean isService = psiClasses.stream().anyMatch(x -> isServiceOrComponent(x));
//                        if (isService) {
//                            holder.registerProblem(field, getProblemName(), ProblemHighlightType.ERROR, removeResourceQuickFix, addServiceAnnotationQuickFix);
//                            return;
//                        }
//                    }
//
//                    super.visitField(field);
//                    return;
//                }
//
//
//                if (isServiceOrComponent(fieldTypeClass)) {
//                    holder.registerProblem(field, getProblemName(), ProblemHighlightType.ERROR, removeResourceQuickFix, addServiceAnnotationQuickFix);
//                }
//
//                //字段命名规范
//                if (!Objects.equals(fieldTypeClass.getName().firstLowerCase(), field.getName())) {
//                    holder.registerProblem(field, getProblemName(), ProblemHighlightType.WARNING, new RenameFieldQuickFix(field));
//                }
//            }
//        };
//    }
//
//    private String getProblemName() {
//        return InspectionBundle.message("inspection.platform.service.resource.display.name") + "：" + System.lineSeparator() + InspectionBundle.message("inspection.platform.service.resource.problem.descriptor");
//    }
//
//    private boolean isServiceOrComponent(PsiClass psiClass) {
//        PsiAnnotation serviceAnnotation = psiClass.getAnnotation(QualifiedNames.SERVICE_QUALIFIED_NAME);
//        PsiAnnotation componentAnnotation = psiClass.getAnnotation(QualifiedNames.COMPONENT_QUALIFIED_NAME);
//
//        return serviceAnnotation == null && componentAnnotation == null;
//    }
//
//    private static class RemoveResourceQuickFix implements LocalQuickFix {
//        @Override
//        public @IntentionFamilyName @NotNull String getFamilyName() {
//            return InspectionBundle.message("inspection.platform.service.resource.use.quickfix_remove");
//        }
//
//        @Override
//        public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
//            PsiField field = (PsiField) descriptor.getPsiElement();
//            PsiAnnotation resourceAnnotation = field.getAnnotation(QualifiedNames.RESOURCE_QUALIFIED_NAME);
//            PsiAnnotation autowiredAnnotation = field.getAnnotation(QualifiedNames.AUTOWIRED_QUALIFIED_NAME);
//            if (resourceAnnotation != null) {
//                resourceAnnotation.delete();
//            }
//            if (autowiredAnnotation != null) {
//                autowiredAnnotation.delete();
//            }
//        }
//    }
//
//    private static class AddServiceAnnotationQuickFix implements LocalQuickFix {
//        @Override
//        public @IntentionFamilyName @NotNull String getFamilyName() {
//            return InspectionBundle.message("inspection.platform.service.resource.use.quickfix_add_annotation");
//        }
//
//        @Override
//        public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
//            IdeaNotifyUtil.dialogWarn(InspectionBundle.message("inspection.platform.service.resource.use.quickfix_add_annotation_warn"));
//        }
//    }
//
//    private static class RenameFieldQuickFix implements LocalQuickFix {
//        private final PsiField psiField;
//
//        public RenameFieldQuickFix(PsiField psiField) {
//            this.psiField = psiField;
//        }
//
//        @Override
//        public @IntentionFamilyName @NotNull String getFamilyName() {
//            return InspectionBundle.message("inspection.platform.service.resource.use.quickfix_rename", psiField.getName().firstLowerCase());
//        }
//
//        @Override
//        public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
//
//        }
//    }
//}
