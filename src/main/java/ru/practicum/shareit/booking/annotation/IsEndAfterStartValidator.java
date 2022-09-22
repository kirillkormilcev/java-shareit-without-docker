package ru.practicum.shareit.booking.annotation;


import ru.practicum.shareit.booking.dto.BookingDtoIn;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class IsEndAfterStartValidator implements ConstraintValidator<IsEndAfterStart, BookingDtoIn> {

    @Override
    public boolean isValid(BookingDtoIn bookingDtoIn, ConstraintValidatorContext context) {
        return !bookingDtoIn.getEnding().isBefore(bookingDtoIn.getStart());
    }
}
