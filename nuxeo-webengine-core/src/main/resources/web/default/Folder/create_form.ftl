<#assign name="${context.getFirstUnresolvedSegment()}">
<@extends src="/default/base.ftl">
<@block name="content">
<h2>Create Wiki Page : ${this.title} </h2>
<form method="POST" action="${this.docURL}/${name}@@create">
Document Type:
<input type="text" name="doctype" value="Note"/>
<p>
<textarea name="note:note" cols="80" rows="20"></textarea>
</p>
<p>
Title: <input type="text" name="dc:title" value="${name}"/>
</p>
<p>
Description: <input type="text" name="dc:description" value=""/>
</p>
<input type="submit"/>
</form>
</@block>
</@extends>