<%@ page import="com.nami.Users" %>



<div class="fieldcontain ${hasErrors(bean: usersInstance, field: 'email', 'error')} required">
	<label for="email">
		<g:message code="users.email.label" default="Email" />
		<span class="required-indicator">*</span>
	</label>
	<g:textField name="email" required="" value="${usersInstance?.email}"/>

</div>

<div class="fieldcontain ${hasErrors(bean: usersInstance, field: 'nom', 'error')} required">
	<label for="nom">
		<g:message code="users.nom.label" default="Nom" />
		<span class="required-indicator">*</span>
	</label>
	<g:textField name="nom" required="" value="${usersInstance?.nom}"/>

</div>

<div class="fieldcontain ${hasErrors(bean: usersInstance, field: 'prenom', 'error')} required">
	<label for="prenom">
		<g:message code="users.prenom.label" default="Prenom" />
		<span class="required-indicator">*</span>
	</label>
	<g:textField name="prenom" required="" value="${usersInstance?.prenom}"/>

</div>

<div class="fieldcontain ${hasErrors(bean: usersInstance, field: 'sexe', 'error')} required">
	<label for="sexe">
		<g:message code="users.sexe.label" default="Sexe" />
		<span class="required-indicator">*</span>
	</label>
	<g:textField name="sexe" required="" value="${usersInstance?.sexe}"/>

</div>

<div class="fieldcontain ${hasErrors(bean: usersInstance, field: 'username', 'error')} required">
	<label for="username">
		<g:message code="users.username.label" default="Username" />
		<span class="required-indicator">*</span>
	</label>
	<g:textField name="username" required="" value="${usersInstance?.username}"/>

</div>

