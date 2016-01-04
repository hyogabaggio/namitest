package com.nami.Validation

import com.nami.GenericDomain

/**
 * Created by hyoga on 17/12/2015.
 */
class CustomValidationClass {

    GenericDomain setErrors(GenericDomain domainInstance, String field, String errorCode) {
        domainInstance.errors.rejectValue(field, errorCode)
        return domainInstance
    }
}
