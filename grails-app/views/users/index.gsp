<%@ page import="com.nami.Users" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main">
    <g:set var="entityName" value="${message(code: 'users.label', default: 'Users')}"/>
    <title><g:message code="default.list.label" args="[entityName]"/></title>
</head>

<body>
<a href="#list-users" class="skip" tabindex="-1"><g:message code="default.link.skip.label"
                                                            default="Skip to content&hellip;"/></a>

<div class="nav" role="navigation">
    <ul>
        <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
        <li><g:link class="create" action="create"><g:message code="default.new.label"
                                                              args="[entityName]"/></g:link></li>
    </ul>
</div>

<div id="list-users" class="content scaffold-list" role="main">
    <h1><g:message code="default.list.label" args="[entityName]"/></h1>
    <g:if test="${flash.message}">
        <div class="message" role="status">${flash.message}</div>
    </g:if>




    <table>
        <thead>
        <tr>
            <g:each in="${domainInstanceList[0]?.dynamicProperties?.keySet()}" var="domainInstanceKey">
                <th><g:message code="bourseBailleur.parent.label" default="${domainInstanceKey}"/></th>
            </g:each>
        </tr>
        </thead>

        <tbody>
        <g:each in="${domainInstanceList}" status="i" var="domainInstance">
            <tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
            <g:each in="${domainInstanceList[0]?.dynamicProperties?.keySet()}" var="domainInstanceKey">
            %{--<td><g:link action="show"--}%
            %{--id="${domainInstance.id}">${fieldValue(bean: usersInstance, field: "dateCreated")}</g:link></td>--}%
                <td>${domainInstance."${domainInstanceKey}"}</td>
            </g:each>

        </g:each>
        </tbody>

    </table>


    %{--<table>--}%
        %{--<thead>--}%
        %{--<tr>--}%

            %{--<g:sortableColumn property="dateCreated"--}%
                              %{--title="${message(code: 'users.dateCreated.label', default: 'Date Created')}"/>--}%

            %{--<g:sortableColumn property="email" title="${message(code: 'users.email.label', default: 'Email')}"/>--}%

            %{--<g:sortableColumn property="nom" title="${message(code: 'users.nom.label', default: 'Nom')}"/>--}%

            %{--<g:sortableColumn property="prenom" title="${message(code: 'users.prenom.label', default: 'Prenom')}"/>--}%

            %{--<g:sortableColumn property="sexe" title="${message(code: 'users.sexe.label', default: 'Sexe')}"/>--}%

            %{--<g:sortableColumn property="username"--}%
                              %{--title="${message(code: 'users.username.label', default: 'Username')}"/>--}%

        %{--</tr>--}%
        %{--</thead>--}%
        %{--<tbody>--}%
        %{--<g:each in="${domainInstanceList}" status="i" var="usersInstance">--}%
            %{--<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">--}%

                %{--<td><g:link action="show"--}%
                            %{--id="${usersInstance.id}">${fieldValue(bean: usersInstance, field: "dateCreated")}</g:link></td>--}%

                %{--<td>${fieldValue(bean: usersInstance, field: "email")}</td>--}%

                %{--<td>${fieldValue(bean: usersInstance, field: "nom")}</td>--}%

                %{--<td>${fieldValue(bean: usersInstance, field: "prenom")}</td>--}%

                %{--<td>${fieldValue(bean: usersInstance, field: "sexe")}</td>--}%

                %{--<td>${fieldValue(bean: usersInstance, field: "username")}</td>--}%

            %{--</tr>--}%
        %{--</g:each>--}%
        %{--</tbody>--}%
    %{--</table>--}%

    <div class="pagination">
        <g:paginate total="${usersInstanceCount ?: 0}"/>
    </div>
</div>
</body>
</html>
