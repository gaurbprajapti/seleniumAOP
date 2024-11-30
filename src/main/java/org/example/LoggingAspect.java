package org.example;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.aspectj.lang.reflect.MethodSignature;

@Aspect
public class LoggingAspect {

    private static ExtentReports extent;
    private static ExtentSparkReporter extentReporter;
    private static ThreadLocal<ExtentTest> extentTest = new ThreadLocal<>();

    // Initialize Extent Reports only once
    static {
        extent = new ExtentReports();
        extentReporter = new ExtentSparkReporter("ExtentReport.html");
        extent.attachReporter(extentReporter);
    }

    // This will run before each test case and create a unique ExtentTest instance
//    @Before("execution(* org.example.SeleniumAspectSupportTest.*(..)) && @annotation(org.testng.annotations.Test)")
//    public void startTest(JoinPoint joinPoint) {
//        ExtentTest test = extent.createTest("Test Case: " + joinPoint.getSignature().getName());
//        extentTest.set(test);
//    }
    
    // only exception and getting correct line of code exception
    
    /*
    @AfterThrowing(pointcut = "execution(* org.example.*.*(..))", throwing = "ex")
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
        if (test != null) {
            test.fail("Method threw an exception: " + signature.toShortString() + " at line " + lineNumber);
            test.fail("Exception: " + ex);
        }
        System.out.println("Method has some issue: " + signature.toShortString() + " at line " + lineNumber);
        System.out.println("Exception: " + ex);
    }
    */
    
    
    
    
    // retry logic with n time 
    /*
    private static final int MAX_RETRIES = 3;

    @AfterThrowing(pointcut = "execution(* org.example.*.*(..))", throwing = "ex")
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
        if (test != null) {
            test.fail("Method threw an exception: " + signature.toShortString() + " at line " + lineNumber);
            test.fail("Exception: " + ex);
        }
        System.out.println("Method has some issue: " + signature.toShortString() + " at line " + lineNumber);
        System.out.println("Exception: " + ex);

        // Retry logic
        for (int i = 0; i < MAX_RETRIES; i++) {
            try {
                method.invoke(joinPoint.getTarget(), joinPoint.getArgs());
                break; // Exit loop if method succeeds
            } catch (Exception retryEx) {
                System.out.println("Retry " + (i + 1) + " failed: " + retryEx.getMessage());
                if (i == MAX_RETRIES - 1) {
                    System.out.println("Max retries reached. Giving up.");
                }
            }
        }
    }
    
    
    */
    
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
    
    

//    @AfterReturning(pointcut = "execution(* org.example.*.*(..))", returning = "result")
//    public void logAfterReturning(JoinPoint joinPoint, Object result) {
//        ExtentTest test = extentTest.get();
//        if (test != null) {
//            test.pass("Method successful: " + joinPoint.getSignature().toShortString());
//            test.pass("Result: " + result);
//        }
//        System.out.println("Method successful: " + joinPoint.getSignature().toShortString());
//        System.out.println("Result: " + result);
//    }
//
    
    
    
    
    
    //
//  @Before("execution(* org.example.*.*(..))")
//  public void logBefore(JoinPoint joinPoint) {
//      ExtentTest test = extentTest.get();
//      if (test != null) {
//          test.info("Method called: " + joinPoint.getSignature().toShortString());
//      }
//      System.out.println("Method called: " + joinPoint.getSignature().toShortString());
//  }
//
//  @After("execution(* org.example.*.*(..))")
//  public void logAfter(JoinPoint joinPoint) {
//      ExtentTest test = extentTest.get();
//      if (test != null) {
//          test.info("Method executed: " + joinPoint.getSignature().toShortString());
//      }
//      System.out.println("Method Executed: " + joinPoint.getSignature().toShortString());
//  }

//  @AfterThrowing(pointcut = "execution(* org.example.*.*(..))", throwing = "ex")
//  public void logAfterThrowing(JoinPoint joinPoint, Throwable ex) {
//      ExtentTest test = extentTest.get();
//      if (test != null) {
//          test.fail("Method threw an exception: " + joinPoint.getSignature().toShortString());
//          test.fail("Exception: " + ex);
//      }
//      System.out.println("Method has some issue: " + joinPoint.getSignature().toShortString());
//      System.out.println("Exception: " + ex);
//  }
    
    // This will run after each test case and ensure the logs are flushed
    @After("execution(* org.example.SeleniumAspectSupportTest.*(..)) && @annotation(org.testng.annotations.Test)")
//    @AfterTest
    public void endTest() {
        extent.flush();
    }
}
