package com.nami

import groovyx.gpars.GParsPool
import org.codehaus.groovy.grails.validation.ConstrainedProperty
import org.springframework.validation.MapBindingResult

import java.util.concurrent.ConcurrentMap

/**
 * Modele generique, dont les propriétés sont sous forme de Map.
 * Il a toutes les propriétés, et est utilisé comme un modele classique.
 * Exemple: GenericDomainInstance.age = 10
 * => GenericDomainInstance.age renvoie 10,
 *  et GenericDomainInstance.sexe renvoie null
 */
class GenericDomain extends GenericDomainExtends {

    Long id
    Long version

    static constraints = {
    }

    /**
     *
     * @param domainMetadatas : les metadatas reçus depuis le microservice, et representant le model en json (i.e les propriétés les contraintes)
     * @param params : les parametres saisis depuis l'interface web, à valider
     * @return l'instance GenericDomain sur laquelle le binding est effectué
     *
     * La methode effectue un binding classic, mais realise avant une vérification du type.
     * I.e, disons que le domain ait un champ 'age', de type Integer. Cette propriété et son type sont définis par les metadatas reçus.
     * La methode, avant de faire le binding, verifie d'abord que dans les params, la valeur saisie pour 'age' est bien de type Integer.
     * Si les types concordent, le binding est effectué.
     * Sinon, un message d'erreur 'domain.wrong.type' est renvoyé en erreur
     */
    def binding(GenericDomainExtends domainMetadatas, Map params) {
        //On boucle sur les valeurs saisies
        params.each {
            //on n'effectue le binding que si une valeur a été saisie // todo bien tester ça
            if (it.value.toString().trim()) {
                // on vérifie d'abord que pour l'element à verifier, un type a bien été défini dans les metadatas.
                if (domainMetadatas."${it.key}"?.class) {
                    // on verifie ensuite que la valeur saisie est bien convertible vers le type detecté, i.e qu'ils sont de même type basiquement
                    boolean isConvertible = checkConvertible(getClassFromName(domainMetadatas."${it.key}"?.class), it.value)
                    if (!isConvertible) this.errors.rejectValue(it.key.toString(), "domain.wrong.type")
                    else
                        this."${it.key}" = it.value
                }
            }
        }
    }

    /**
     *
     * @param domainMetadatas
     * Exemple:
     *      "age": {
     *      "class": "java.lang.Integer",
     *      "optional": false,
     *      "associationType": ""
     *},
     *      "dateNaissance": {
     *      "class": "java.util.Date",
     *      "optional": false,
     *      "associationType": ""
     *},
     *      "constraints": {
     *          "age": {
     *          "max": 65,
     *          "blank": true,
     *          "attributes": {},
     *          "min": 1,
     *          "nullable": false,
     *          "password": false
     *}*} ,
     * @param params : ce sont les données saisies dans les pages
     * @return
     */
    //version avec eachParallel, en test. Voir validateproperties_2 pour la version avec each
    def validateproperties(GenericDomainExtends domainMetadatas, Map params) {
        // on active le stockage des erreurs dans le model
        this.setErrors(new MapBindingResult(this.dynamicProperties, GenericDomain.class.getName()))
        // on bind les propriétés reçues au model en cours, en verifiant que les types concordent bien
        this.binding(domainMetadatas, params)

        // une fois que le binding est effectué, on boucle sur les proriétés du model

        GParsPool.withPool {
            log.info("######### withPool start " + new Date())
            this.dynamicProperties.eachParallel { property ->
                log.info("######### eachparallel start " + new Date())
                log.info("###### withPool " + property)
                def currentPropertyField = property.key
                def currentPropertyValue = property.value
                // on recupere le type de la propriété en cours depuis les metadatas, mais sous format string
                def assignedClass = domainMetadatas."${currentPropertyField}"?.class

                if (assignedClass != null) {
                    /*
                    on transforme le type au format string en sa classe d'origine.
                    Par exemple, si assignedClass = "java.lang.String", classCurrentProperty sera la Class java.lang.String
                       */
                    def classCurrentProperty = getClassFromName(assignedClass)
                    /*
                    Les données sont toutes des string.
                    On transforme donc la valeur de la propriété en cours en son type d'origine.
                    Ex: on a saisi sur la page age="10".
                    Au niveau des metadatas, age est "java.lang.Integer". On le transforme donc en age=10
                    En gros, on fait un cast
                    !!!!!! NB: voir le bootstrap pour la definition de la méthode asType !!!!!!!!
                     */
                    def propertyDefined = currentPropertyValue.toString().asType(classCurrentProperty)

                    // on instancie la classe ConstrainedProperty
                    ConstrainedProperty constraintProperty = new ConstrainedProperty(GenericDomain, currentPropertyField.toString(), classCurrentProperty)
                    // on recupere les contraintes associées à la propriété en cours.
                    // Puis on boucle dessus
                    // userMetadata.json.domain.constraints?."${currentPropertyField}"?.each { constraint ->
                    domainMetadatas.constraints?."${currentPropertyField}"?.each { constraint ->
                        if (!(constraint?.key in ["min", "max"]) && constraintProperty.supportsContraint(constraint?.key))// !!!!! bug lorsque la contrainte est min ou max
                        /*
                        size et range sont des cas particuliers.
                        Ils attendent un range en parametre, alors que le json renvoyé est une liste
                        On transforme donc la list reçue en range
                         */
                            if (constraint?.key in ["range", "size"]) {
                                if (constraint?.value?.first()?.toString().isInteger()) {
                                    IntRange range = new IntRange(true, constraint?.value?.first(), constraint?.value?.last())
                                    constraintProperty.applyConstraint(constraint?.key, range)
                                } else {
                                    ObjectRange range = new ObjectRange(constraint?.value?.first(), constraint?.value?.last())
                                    constraintProperty.applyConstraint(constraint?.key, range)
                                }
                            } else
                                constraintProperty.applyConstraint(constraint?.key, constraint?.value)
                        else {
                            log.debug("contrainte non supportée = " + constraint?.key)
                        }
                    }
                    // on verifie que la contrainte est respectée par la valeur saisie
                    constraintProperty.validate(GenericDomain, propertyDefined, this.getErrors())
                }
                log.info("######### eachparallel end " + new Date())

            }
            log.info("######### withPool end " + new Date())
        }
    }


    def validateproperties_2(GenericDomainExtends domainMetadatas, Map params) {
        // on active le stockage des erreurs dans le model
        this.setErrors(new MapBindingResult(this.dynamicProperties, GenericDomain.class.getName()))
        // on bind les propriétés reçues au model en cours, en verifiant que les types concordent bien
        this.binding(domainMetadatas, params)

        // une fois que le binding est effectué, on boucle sur les proriétés du model

        log.info("######### withPool start " + new Date())
        this.dynamicProperties.each { property ->     //todo try avec eachParallel
            log.info("######### each start " + new Date())
            def currentPropertyField = property.key
            def currentPropertyValue = property.value
            // on recupere le type de la propriété en cours depuis les metadatas, mais sous format string
            def assignedClass = domainMetadatas."${currentPropertyField}"?.class

            if (assignedClass != null) {
                /*
                on transforme le type au format string en sa classe d'origine.
                Par exemple, si assignedClass = "java.lang.String", classCurrentProperty sera la Class java.lang.String
                   */
                def classCurrentProperty = getClassFromName(assignedClass)
                /*
                Les données sont toutes des string.
                On transforme donc la valeur de la propriété en cours en son type d'origine.
                Ex: on a saisi sur la page age="10".
                Au niveau des metadatas, age est "java.lang.Integer". On le transforme donc en age=10
                En gros, on fait un cast
                !!!!!! NB: voir le bootstrap pour la definition de la méthode asType !!!!!!!!
                 */
                def propertyDefined = currentPropertyValue.toString().asType(classCurrentProperty)

                // on instancie la classe ConstrainedProperty
                ConstrainedProperty constraintProperty = new ConstrainedProperty(GenericDomain, currentPropertyField.toString(), classCurrentProperty)
                // on recupere les contraintes associées à la propriété en cours.
                // Puis on boucle dessus
                // userMetadata.json.domain.constraints?."${currentPropertyField}"?.each { constraint ->
                domainMetadatas.constraints?."${currentPropertyField}"?.each { constraint ->
                    if (!(constraint?.key in ["min", "max"]) && constraintProperty.supportsContraint(constraint?.key))// !!!!! bug lorsque la contrainte est min ou max
                    /*
                    size et range sont des cas particuliers.
                    Ils attendent un range en parametre, alors que le json renvoyé est une liste
                    On transforme donc la list reçue en range
                     */
                        if (constraint?.key in ["range", "size"]) {
                            if (constraint?.value?.first()?.toString().isInteger()) {
                                IntRange range = new IntRange(true, constraint?.value?.first(), constraint?.value?.last())
                                constraintProperty.applyConstraint(constraint?.key, range)
                            } else {
                                ObjectRange range = new ObjectRange(constraint?.value?.first(), constraint?.value?.last())
                                constraintProperty.applyConstraint(constraint?.key, range)
                            }
                        } else
                            constraintProperty.applyConstraint(constraint?.key, constraint?.value)
                    else {
                        log.debug("contrainte non supportée = " + constraint?.key)
                    }
                }
                // on verifie que la contrainte est respectée par la valeur saisie
                constraintProperty.validate(GenericDomain, propertyDefined, this.getErrors())
            }
        }
        log.info("######### each end " + new Date())

    }

}
