<?xml version="1.0"?>

<policy xmlns="http://www.wyona.org/security/1.0">

  <role id="view">
    <world permission="true"/>
  </role>

  <role id="open">
    <group id="editor" permission="true"/>
  </role>

  <role id="write">
    <group id="editor" permission="true"/>
  </role>

  <role id="create">
    <group id="editor" permission="true"/>
  </role>

  <role id="toolbar">
    <group id="editor" permission="true"/>
    <group id="reviewer" permission="true"/>
    <group id="admin" permission="true"/>
  </role>

  <role id="introspection">
    <group id="editor" permission="true"/>
    <group id="reviewer" permission="true"/>
    <group id="admin" permission="true"/>
  </role>

  <role id="review">
    <group id="reviewer" permission="true"/>
  </role>

  <role id="policy.read">
    <group id="admin" permission="true"/>
  </role>

</policy>
