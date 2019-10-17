package com.susu;

import com.susu.annotation.Log;
import com.susu.service.MyService;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.annotation.AnnotationList;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.bind.annotation.SuperCall;
import net.bytebuddy.matcher.ElementMatcher;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static net.bytebuddy.matcher.ElementMatchers.named;

/**
 * @author SuyuZhuang
 * @date 2019/10/17 10:45 下午
 */
public class Main {

    public static void main(String[] args) throws InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
//        MyService service = new MyService();
        MyService service = enhanceByAnnotation();

        service.queryDatabase();
        service.provideHttpResponse();
        service.noLog();
    }

    public static class LoggerInterceptor {
        public static void log(@SuperCall Callable<Void> zuper)
                throws Exception {
            System.out.println("---before---");
            try {
                zuper.call();
            } finally {
                System.out.println("---after---");
            }
        }
    }




    private static MyService enhanceByAnnotation() throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {

        return new ByteBuddy()
                .subclass(MyService.class)
                .method(new FilterMethodWithLogAnnotation())
                .intercept(MethodDelegation.to(LoggerInterceptor.class))
                .make()
                .load(Main.class.getClassLoader())
                .getLoaded()
                .getConstructor()
                .newInstance();
    }

    public static class FilterMethodWithLogAnnotation implements ElementMatcher<MethodDescription> {

        public boolean matches(MethodDescription target) {
            AnnotationList declaredAnnotations = target.getDeclaredAnnotations();
            return declaredAnnotations.stream().anyMatch(anno -> anno.getAnnotationType().getCanonicalName().equals("com.susu.annotation.Log"));

//            Method[] methodsWithLog = MyService.class.getDeclaredMethods();
//            return Stream.of(methodsWithLog).anyMatch(method -> method.getName().equals(target.getName()) && method.isAnnotationPresent(Log.class));
//        List<String> methodNameWithLog = Stream.of(MyService.class.getDeclaredMethods())
//                .filter(method -> method.isAnnotationPresent(Log.class))
//                .map(Method::getName)
//                .collect(Collectors.toList());
//        return methodNameWithLog.contains(target.getName());

        }
    }
}
