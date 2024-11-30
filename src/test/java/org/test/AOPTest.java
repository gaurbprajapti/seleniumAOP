package org.test;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.reflect.MethodSignature;
import org.testng.annotations.Test;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

public class AOPTest {
	
    private static ExtentReports extent;
    private static ExtentSparkReporter extentReporter;
    private static ThreadLocal<ExtentTest> extentTest = new ThreadLocal<>();

    // Initialize Extent Reports only once
    static {
        extent = new ExtentReports();
        extentReporter = new ExtentSparkReporter("ExtentReport.html");
        extent.attachReporter(extentReporter);
    }
    private static final ThreadLocal<Boolean> retryFlag = ThreadLocal.withInitial(() -> Boolean.FALSE);

    @AfterThrowing(pointcut = "execution(* org.example..*(..)) || execution(* org.test..*(..))", throwing = "ex")
  //  @AfterThrowing(pointcut = "execution(* org.example.*.*(..))", throwing = "ex")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable ex) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        if (signature.getMethod().isAnnotationPresent((Class<? extends Annotation>) Test.class)) {
            // Skip logging if the method has @Test annotation
            return;
        }

        // Retrieve line number using stack trace
        int lineNumber = -1;
        try {
            // Get the declaring class and its name
            String className = method.getDeclaringClass().getName();
            StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();

            // Find the stack trace element that matches the class and method
            for (StackTraceElement element : stackTraceElements) {
                if (element.getClassName().equals(className) && element.getMethodName().equals(method.getName())) {
                    lineNumber = element.getLineNumber();
                    break;
                }
            }
        } catch (Exception e) {
            System.out.println("Could not determine line number: " + e.getMessage());
        }

        ExtentTest test = extentTest.get();
//        if (test != null) {
//            test.fail("Method threw an exception: " + signature.toShortString() + " at line " + lineNumber);
//            test.fail("Exception: " + ex);
//        }
//        System.out.println("Method has some issue: " + signature.toShortString() + " at line " + lineNumber);
//        System.out.println("Exception: " + ex);

        // Retry logic
        if (!retryFlag.get()) {
            retryFlag.set(Boolean.TRUE);
            try {
                method.invoke(joinPoint.getTarget(), joinPoint.getArgs());
            } catch (Exception retryEx) {
                System.out.println("Retry failed: " + retryEx.getMessage());
                // Log the retry failure
                if (test != null) {
                    test.fail("Retry failed for method: " + signature.toShortString() + " at line " + lineNumber);
                    test.fail("Retry Exception: " + retryEx);
                }
                System.out.println("Retry failed for method: " + signature.toShortString() + " at line " + lineNumber);
                System.out.println("Retry Exception: " + retryEx);
            } finally {
                retryFlag.remove();
            }
        }
    }
    

}
