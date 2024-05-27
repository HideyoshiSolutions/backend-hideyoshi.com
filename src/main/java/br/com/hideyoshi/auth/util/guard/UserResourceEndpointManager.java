package br.com.hideyoshi.auth.util.guard;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@Component
@RequiredArgsConstructor
public class UserResourceEndpointManager {
    private final ListableBeanFactory beanFactory;

    public List<String> getGuardedPaths() {
        return this.extractPathsFromMethods(this.getGuardedResources());
    }

    public List<String> getOpenPaths() {
        return this.extractPathsFromMethods(this.getOpenResources());
    }

    private List<String> extractPathsFromMethods(List<Method> methods) {
        final List<String> paths = new ArrayList<>();
        for (final Method method : methods) {
            String[] parentPath = new String[0];

            RequestMapping classAnnotation = method.getDeclaringClass().getAnnotation(RequestMapping.class);
            if (classAnnotation != null) {
                parentPath = this.getPathFromAnnotation(classAnnotation);
            }

            List<Annotation> annotations = List.of(method.getAnnotations());

            for (Annotation annotation : annotations) {
                final String[] path = this.getPathFromAnnotation(annotation);

                if (path != null)
                    paths.add(String.join("/", parentPath) + String.join("/", path));
            }
        }
        return paths;
    }

    private List<Method> getGuardedResources() {
        final List<UserResourceGuardEnum> guardedAccessTypes = Arrays.asList(
                UserResourceGuardEnum.USER,
                UserResourceGuardEnum.SAME_USER,
                UserResourceGuardEnum.ADMIN_USER
        );
        final List<Method> methods = new ArrayList<>();

        for (final Class<?> controllerClass : this.getControllerClasses()) {
            methods.addAll(this.getMethodsByAccessType(controllerClass, guardedAccessTypes));
        }

        return methods;
    }

    private List<Method> getOpenResources() {
        final List<UserResourceGuardEnum> openAccessTypes = List.of(UserResourceGuardEnum.OPEN);
        final List<Method> methods = new ArrayList<>();

        for (final Class<?> controllerClass : this.getControllerClasses()) {
            methods.addAll(this.getMethodsByAccessType(controllerClass, openAccessTypes));
        }

        return methods;
    }

    private List<Method> getMethodsByAccessType(final Class<?> controllerClass, List<UserResourceGuardEnum> accessTypes) {
        final List<Method> methods = new ArrayList<>();
        for (final Method method : controllerClass.getDeclaredMethods()) {
            if (!method.isAnnotationPresent(UserResourceGuard.class)) {
                continue;
            }
            UserResourceGuard annotation = method.getAnnotation(UserResourceGuard.class);
            if (!accessTypes.contains(annotation.accessType())) {
                continue;
            }

            methods.add(method);
        }
        return methods;
    }

    private List<Class<?>> getControllerClasses() {
        final List<Class<?>> controllerClasses = new ArrayList<>();
        for (final String beanName : this.beanFactory.getBeanNamesForAnnotation(Controller.class)) {
            controllerClasses.add(this.beanFactory.getType(beanName));
        }
        return controllerClasses;
    }

    private String[] getPathFromAnnotation(Annotation annotation) {
        String[] path; String[] value;

        try {
            value = (String[]) annotation.annotationType().getMethod("value").invoke(annotation);
            path = (String[]) annotation.annotationType().getMethod("path").invoke(annotation);
        } catch (Exception e) {
            return null;
        }

        return value.length > 0 ? value : path;
    }
}
