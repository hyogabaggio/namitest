package com.nami

/**
 * Created by hyoga on 02/11/2015.
 *
 * https://dzone.com/articles/adding-properties-domain
 *
 */
class GenericDomainExtends extends GenericDomainHelper {

    def dynamicProperties = [:]

    //setter
    def propertyMissing(String name, value) {
        dynamicProperties[name] = value
    }

    //getter
    def propertyMissing(String name) { return dynamicProperties[name] }
}
