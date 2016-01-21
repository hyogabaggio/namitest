<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main">
    <g:set var="entityName" value="${message(code: domain + '.label', default: domain)}"/>
    <title><g:message code="${domain}.list.label" default="${domain}"/></title>
</head>

<body>
<a href="#create-users" class="skip" tabindex="-1"><g:message code="default.link.skip.label"
                                                              default="Skip to content&hellip;"/></a>

<div class="nav" role="navigation">
    <ul>
        <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
        <li><g:link class="create" controller="${domain}" action="create"><g:message code="${domain}.new.label"
                                                                                     default="${domain}"/></g:link></li>
    </ul>
</div>

<div id="create-users" class="content scaffold-create" role="main">
    <h1><g:message code="default.create.label" args="[domain]"/></h1>
    <g:if test="${flash.message}">
        <div class="message" role="status">${flash.message}</div>
    </g:if>
    <g:hasErrors bean="${usersInstance}">
        <ul class="errors" role="alert">
            <g:eachError bean="${usersInstance}" var="error">
                <li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message
                        error="${error}"/></li>
            </g:eachError>
        </ul>
    </g:hasErrors>
%{--<g:form url="[resource: usersInstance, action: 'save']">--}%
    <g:form url="[action: 'save2', controller: domain]">
        <fieldset class="form">

            <g:each in="${userMetadata.json.domain}" var="user">

                <%
                    def requiredClassStyle = ""
                    def requiredIndicatorClass = ""
                    if (!user.value.optional) {
                        requiredClassStyle = "required"
                        requiredIndicatorClass = "required-indicator"
                    }
                %>
                <div class="fieldcontain ${requiredClassStyle}">
                    <label for="${user.key}">
                        <g:message code="users.${user.key}.label" default="${user.key}"/>
                        <span class="${requiredIndicatorClass}">*</span>
                    </label>
                    <g:if test="${!user.value.optional}">
                        <g:textField name="${user.key}" required="" value=""/>
                    </g:if>
                    <g:else>
                        <g:textField name="${user.key}" value=""/>
                    </g:else>

                </div>

            </g:each>






        %{--<g:render template="form"/>--}%
        </fieldset>
        <fieldset class="buttons">
            <g:submitButton name="create" class="save"
                            value="${message(code: 'default.button.create.label', default: 'Create')}"/>
        </fieldset>
    </g:form>
</div>
</body>
</html>
