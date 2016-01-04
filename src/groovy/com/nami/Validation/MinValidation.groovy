package com.nami.Validation

import org.codehaus.groovy.grails.commons.GrailsClassUtils
import org.codehaus.groovy.grails.validation.ConstrainedProperty
import org.codehaus.groovy.grails.validation.Constraint
import org.codehaus.groovy.grails.validation.MinConstraint
import org.codehaus.groovy.grails.validation.exceptions.ConstraintException

/**
 * Created by hyoga on 17/12/2015.
 */
class MinValidation extends ConstrainedProperty {

  //  protected Map<String, Constraint> appliedConstraints = new LinkedHashMap<String, Constraint>();

/**
 * Constructs a new ConstrainedProperty for the given arguments.
 *
 * @param clazz The owning class
 * @param propertyName The name of the property
 * @param propertyType The property type
 */
    MinValidation(Class<?> clazz, String propertyName, Class<?> propertyType) {
        super(clazz, propertyName, propertyType)
    }


    /**
     * Methode qui verifie que la propriété à valider (propertyClass) est de même type que la valeur donnée à la contrainte (valueParameter)
     */
    public boolean CheckValidationTypes(Class<?> propertyClass, Object valueParameter) {
        System.out.println("propertyClass = " + propertyClass)
        System.out.println("valueParameter = " + valueParameter.getClass())
       return GrailsClassUtils.isAssignableOrConvertibleFrom(valueParameter.getClass(), propertyClass)

    }


    @Override
    public void applyConstraint(String constraintName, Object constrainingValue) {

        if (constraints.containsKey(constraintName)) {
            if (constrainingValue == null) {
                appliedConstraints.remove(constraintName);
                super.appliedConstraints
            }
            else {
                try {
                    Constraint c = instantiateConstraint(constraintName, true);
                    if (c != null) {
                        c.setParameter(constrainingValue);  // ***** methode à changer: celle qui bug
                        appliedConstraints.put(constraintName, c);
                    }
                }
                catch (Exception e) {
                    LOG.error("Exception thrown applying constraint [" + constraintName +
                            "] to class [" + owningClass + "] for value [" + constrainingValue + "]: " + e.getMessage(), e);
                    throw new ConstraintException("Exception thrown applying constraint [" + constraintName +
                            "] to class [" + owningClass + "] for value [" + constrainingValue + "]: " + e.getMessage(), e);
                }
            }
        }
        else if (bean.isWritableProperty(constraintName)) {
            bean.setPropertyValue(constraintName, constrainingValue);
        }
        else {
            throw new ConstraintException("Constraint [" + constraintName + "] is not supported for property [" +
                    propertyName + "] of class [" + owningClass + "] with type [" + propertyType + "]");
        }
    }
}
