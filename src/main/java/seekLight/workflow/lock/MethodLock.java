package seekLight.workflow.lock;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface MethodLock {
    String  key();
    long expireTime() default 60000L;
    long waitTime() default 3000L;
    String desc()  default "";
}
