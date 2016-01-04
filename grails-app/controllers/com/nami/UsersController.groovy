package com.nami

import grails.async.Promise
import grails.plugins.rest.client.RestBuilder

//import grails.plugins.rest.client.RestBuilder
import groovyx.net.http.RESTClient
import org.codehaus.groovy.grails.commons.DefaultGrailsDomainClass
import org.codehaus.groovy.grails.validation.ConstrainedProperty
import org.codehaus.groovy.grails.web.json.JSONElement
import org.codehaus.groovy.grails.web.json.JSONObject
import org.grails.databinding.BindUsing
import org.springframework.validation.MapBindingResult

import groovy.transform.Field

import static grails.async.Promises.task

import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional

@Transactional(readOnly = true)
class UsersController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def redisService
    def rest = new RestBuilder()/*.restTemplate.messageConverters.removeAll
            { it.class.name == 'org.springframework.http.converter.json.GsonHttpMessageConverter' }   */


    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)

        String url = "http://localhost:8085/testRemote/users/"
        rest.restTemplate.messageConverters.removeAll
                { it.class.name == 'org.springframework.http.converter.json.GsonHttpMessageConverter' }

        def users = rest.get(url)
        List<GenericDomain> usersList = []
        // log.info("users.json = " + users.json.class)

        users.json.each {
            GenericDomain user = new GenericDomain()
            log.info("it = " + it)

            it.keys().each { key ->
                if (key != "class") {
                    user."${key}" = it.get(key)
                }
            }
            log.info("user = " + user)
            log.info("user dateCreated = " + user.dateCreated.class)
            log.info("user home = " + user.home.class)
            usersList.add(user)
        }


        log.info("usersList = " + usersList.size())

        // ******** remote call

        //  def promise =  testTas()
        // println "testTask =  $promise"
        // ******** ending remote call

        // respond Users.list(params), model: [usersInstanceCount: Users.count()]
        // TODO à check: dans le respond usersList n'est pas considéré dans la vue, on est obligé d'envoyer usersInstanceList dans le model. UserList est pourtant considéré quand on fait un scaffold
        respond usersList, model: [usersInstanceCount: usersList.size(), usersInstanceList: usersList]
    }

    def testTask() {

        Promise p = task {
            /*    def restBuilder  = new RestBuilder()
                restBuilder.restTemplate.messageConverters.removeAll { it.class.name == 'org.springframework.http.converter.json.GsonHttpMessageConverter' }

                def respUsersList = restBuilder.get("http://localhost:8085/bravos/user")  */
            // def respUsersList = new URL("http://localhost:8085/bravos/user").content
            // respUsersList.text
            //   def jsonResponse = grails.converters.JSON.parse(respUsersList.text)

            def client = new RESTClient('http://localhost:8085/bravos/user')
            def respUsersList = client.get()
            println("users json = " + respUsersList.text)
        }
        p.onError { Throwable err ->
            println "ERROR = ${err.message}"
        }
        p.onComplete { result ->
            println "CALL RESPONSE = $result"
        }

        println("ended remote")
        return p

    }

    def show(Users usersInstance) {


        respond usersInstance
    }

    def create() {
        String url = "http://localhost:8085/testRemote/users/metadata"
        rest.restTemplate.messageConverters.removeAll
                { it.class.name == 'org.springframework.http.converter.json.GsonHttpMessageConverter' }
        def userMetadata = rest.get(url)
        //TODO: generer un GrailsDomainClass à partir du userMetadata reçu

        GenericDomain user = new GenericDomain()

        userMetadata.json.domain.keys().each {
            if (it != "class" && it != "constraints") {
                user."${it}" = userMetadata.json.domain.get(it)
            }
        }

        log.info("user home = " + user.home)
        log.info("user home = " + user.home.class)

        // log.info("user constraints= " + userMetadata.json.domain.constraints)

        respond new Users(params), model: [userMetadata: userMetadata]
    }


    def create2() {
        respond new Users(params)
    }

    @Transactional
    def save(Users usersInstance) {
        if (usersInstance == null) {
            notFound()
            return
        }

        if (usersInstance.hasErrors()) {
            respond usersInstance.errors, view: 'create'
            return
        }
        usersInstance.save flush: true

        /*   String key = "user:${usersInstance.id}:hash"
           Users user = redisService.memoizeHash(Users, key) {
               Users user = usersInstance
               return user
           }                     */

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'users.label', default: 'Users'), usersInstance.id])
                redirect usersInstance
            }
            '*' { respond usersInstance, [status: CREATED] }
        }
    }

    @Transactional
    def save1() {
        log.info("params recus = " + params)
        GenericDomain userInstance = new GenericDomain() // il s'agit de l'instance à save

        String url = "http://localhost:8085/testRemote/users/metadata"
        rest.restTemplate.messageConverters.removeAll
                { it.class.name == 'org.springframework.http.converter.json.GsonHttpMessageConverter' }
        def userMetadata = rest.get(url)
        GenericDomainExtends userDomain = new GenericDomainExtends()
        //il s'agit de l'instance representant les metadatas de la classe

        userMetadata.json.domain.keys().each {
            if (it != "class" && it != "constraints") {
                userDomain."${it}" = userMetadata.json.domain.get(it)
            }
        }

        userInstance.setErrors(new MapBindingResult(userInstance.dynamicProperties, GenericDomain.class.getName()))
        //binding
        userInstance.binding(userDomain, params)

        userInstance.dynamicProperties.each { property ->     //todo try avec eachParallel
            def currentPropertyField = property.key
            def currentPropertyValue = property.value
            def assignedClass = userDomain."${currentPropertyField}"?.class

            if (assignedClass != null) {
                def classCurrentProperty = getClassFromName(assignedClass)
                def propertyDefined = currentPropertyValue.toString().asType(classCurrentProperty)

                ConstrainedProperty constraintProperty = new ConstrainedProperty(GenericDomain, currentPropertyField.toString(), classCurrentProperty)
                userMetadata.json.domain.constraints?."${currentPropertyField}"?.each { constraint ->
                    if (!(constraint?.key in ["min", "max"]) && constraintProperty.supportsContraint(constraint?.key))// !!!!!!!! bug lorsque la contrainte est min ou max
                    //size et range sont des cas particuliers.
                    //ils attendent un range en parametre, alors que le json renvoyé est une liste
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
                        log.info("contrainte non supportée = " + constraint?.key)
                    }
                }
                constraintProperty.validate(GenericDomain, propertyDefined, userInstance.getErrors())
            }

        }
//Test
        log.info("userInstance errors total = " + userInstance.errors.errorCount)
        userInstance.errors.each {
            log.info("error = " + it)
        }
    }


    @Transactional
    def save2() {
        log.info("params recus = " + params)
        GenericDomain userInstance = new GenericDomain() // il s'agit de l'instance à save
        // todo ne pas avoir à aller dans le reseau pour recup les metadatas constemment
        String url = "http://localhost:8085/testRemote/users/metadata"
        rest.restTemplate.messageConverters.removeAll
                { it.class.name == 'org.springframework.http.converter.json.GsonHttpMessageConverter' }
        def userMetadata = rest.get(url)
        GenericDomainExtends userDomainMetadatas = new GenericDomainExtends()
        //il s'agit de l'instance representant les metadatas de la classe

        userMetadata.json.domain.keys().each {
            if (it != "class") {
                userDomainMetadatas."${it}" = userMetadata.json.domain.get(it)
            }
        }

        userDomainMetadatas.constraints.each {
            log.info("contrainte = " + it.key + "; value = " + it.value)
        }

        userInstance.validateproperties(userDomainMetadatas, params)

//Test
        log.info("userInstance errors total = " + userInstance.errors.errorCount)
        userInstance.errors.each {
            log.info("error = " + it)
        }
    }


    public Class getClassFromName(String className) {
        try {
            return (Class) Class.forName(className)
        } catch (ClassNotFoundException ex) {
            return (Class) Class.forName("java.lang.Long")
        }
    }

    public boolean checkConvertible(Class classDestination, String value) {    //todo blob ou photo (byte) à gerer
        try {
            if (classDestination.is(Date)) {
                log.info("is date")
                //TODO on fixe le format pour le moment. Pour l'exemple. Pour gerer tous les formats, i.e si on ne connait pas le format à l'avance, utiliser jChronic
                def newdate = Date.parse("dd/MM/yyyy HH:mm:ss", value)
                log.info("newdate = " + newdate)
                return true
            } else {
                log.info("is not date")
                value.asType(classDestination)
                return true
            }

        } catch (Exception e) {
            log.info("erreur = " + e.message)
            return false
        }
    }


    def edit(Users usersInstance) {
        respond usersInstance
    }

    @Transactional
    def update(Users usersInstance) {
        if (usersInstance == null) {
            notFound()
            return
        }

        if (usersInstance.hasErrors()) {
            respond usersInstance.errors, view: 'edit'
            return
        }

        usersInstance.save flush: true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'Users.label', default: 'Users'), usersInstance.id])
                redirect usersInstance
            }
            '*' { respond usersInstance, [status: OK] }
        }
    }

    @Transactional
    def delete(Users usersInstance) {

        if (usersInstance == null) {
            notFound()
            return
        }

        usersInstance.delete flush: true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'Users.label', default: 'Users'), usersInstance.id])
                redirect action: "index", method: "GET"
            }
            '*' { render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'users.label', default: 'Users'), params.id])
                redirect action: "index", method: "GET"
            }
            '*' { render status: NOT_FOUND }
        }
    }
}
