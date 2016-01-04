package com.nami

/**
 * Created by hyoga on 03/01/2016.
 */
class GenericDomainHelper {

    boolean checkConvertible(Class classDestination, String value) {    //todo blob ou photo (byte) à gerer
        try {
            if (classDestination.is(Date)) {
                //TODO on fixe le format pour le moment. Pour l'exemple. Pour gerer tous les formats, i.e si on ne connait pas le format à l'avance, utiliser jChronic
                Date.parse("dd/MM/yyyy HH:mm:ss", value)
                return true
            } else {
                value.asType(classDestination)
                return true
            }
        } catch (Exception e) {
            return false
        }
    }

    /**
     * Methode qui recoit un string, "java.lang.Integer", par exemple,
     * et renvoie la classe correspondante java.lang.Integer.
     * Si ClassNotFoundException est atteint, il s'agit probablement
     * d'une classe dans un autre service (une relation entre 2 classes).
     * Dans ce cas là, vu que grails ne stocke que l'id, on renvoie un Long
     */
    Class getClassFromName(String className) {
        try {
            return (Class) Class.forName(className)
        } catch (ClassNotFoundException ex) {
            //Si la classe n'est pas trouvée, on suppose qu'il s'agit d'une classe dans un des services distants. Et comme ce sont les id qui sont tout le temps envoyé dans les jointures, on les set en Long déjà.
        }
        return (Class) Class.forName("java.lang.Long")
    }
}
