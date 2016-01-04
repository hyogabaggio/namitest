<%@ page import="com.nami.Person" %>



<div class="fieldcontain ${hasErrors(bean: personInstance, field: 'age', 'error')} required">
	<label for="age">
		<g:message code="person.age.label" default="Age" />
		<span class="required-indicator">*</span>
	</label>
	%{--<g:select name="age" from="${1..65}" class="range" required="" value="${fieldValue(bean: personInstance, field: 'age')}"/>--}%
	<g:textField name="age" maxlength="30"  value="${personInstance?.age}"/>


</div>

<div class="fieldcontain ${hasErrors(bean: personInstance, field: 'username', 'error')} required">
	<label for="username">
		<g:message code="person.username.label" default="Username" />
		<span class="required-indicator">*</span>
	</label>
	<g:textField name="username" maxlength="30" required="" value="${personInstance?.username}"/>

</div>

<div class="fieldcontain ${hasErrors(bean: personInstance, field: 'email', 'error')} required">
	<label for="email">
		<g:message code="person.email.label" default="Email" />
		<span class="required-indicator">*</span>
	</label>
	<g:field type="email" name="email" required="" value="${personInstance?.email}"/>

</div>

<div class="fieldcontain ${hasErrors(bean: personInstance, field: 'sexe', 'error')} required">
	<label for="sexe">
		<g:message code="person.sexe.label" default="Sexe" />
		<span class="required-indicator">*</span>
	</label>
	%{--<g:select name="sexe" from="${personInstance.constraints.sexe.inList}" required="" value="${personInstance?.sexe}" valueMessagePrefix="person.sexe"/>--}%
	<g:textField name="sexe" maxlength="30" required="" value="${personInstance?.sexe}"/>


</div>

<div class="fieldcontain ${hasErrors(bean: personInstance, field: 'sequence', 'error')} ">
	<label for="sequence">
		<g:message code="person.sequence.label" default="Sequence" />
		
	</label>
	<g:field name="sequence" type="number" value="${personInstance.sequence}"/>

</div>

<div class="fieldcontain ${hasErrors(bean: personInstance, field: 'dateNaissance', 'error')} required">
	<label for="dateNaissance">
		<g:message code="person.dateNaissance.label" default="Date Naissance" />
		<span class="required-indicator">*</span>
	</label>
	<g:datePicker name="dateNaissance" precision="day"  value="${personInstance?.dateNaissance}"  />

</div>

<div class="fieldcontain ${hasErrors(bean: personInstance, field: 'actif', 'error')} ">
	<label for="actif">
		<g:message code="person.actif.label" default="Actif" />
		
	</label>
	<g:checkBox name="actif" value="${personInstance?.actif}" />

</div>

