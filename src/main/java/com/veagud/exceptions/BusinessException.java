package com.veagud.exceptions;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BusinessException extends RuntimeException {

    public BusinessException(String description) {
        super(description);
    }
}
