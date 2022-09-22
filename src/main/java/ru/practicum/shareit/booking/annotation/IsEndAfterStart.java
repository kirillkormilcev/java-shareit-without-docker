package ru.practicum.shareit.booking.annotation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({ TYPE })
@Retention(RUNTIME)
@Constraint(validatedBy = IsEndAfterStartValidator.class)
public @interface IsEndAfterStart {
    String message() default "Окончание аренды должно быть позже ее начала.";
    Class<?>[] groups() default { };
    Class<? extends Payload>[] payload() default {};
}

