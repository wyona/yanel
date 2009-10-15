/*
	This is a root namespace for Yanel UI components.
	The component is implemented in the way that it reads configuration
	by looking in Yanel.component.configurations[...]  

	This might have been defined in other modules
*/
if (typeof Yanel == 'undefined') {
  /// define the global Yanel namespace
  Yanel = {};
}

/*
	Function for checking if the object is an Array
*/
Yanel.isArray = function (obj) {
   if (obj.constructor.toString().indexOf("Array") == -1)
      return false;
   else
      return true;
}