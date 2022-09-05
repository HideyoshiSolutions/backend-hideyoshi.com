package com.hideyoshi.backendportfolio.util.guard;

import java.lang.annotation.*;

@Target( ElementType.METHOD )
@Retention( RetentionPolicy.RUNTIME )
@Documented
public @interface UserResourceGuard {

    String denialMessage() default "Operation not permitted. You don't have access to this Resource.";

    UserResourceGuardEnum accessType();

}
