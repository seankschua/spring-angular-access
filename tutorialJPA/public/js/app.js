var starterApp = angular.module('starterApp', ['ngRoute','ngMaterial','ngMessages','ngToast','angular-storage']);

starterApp.config(['$routeProvider',
  function($routeProvider) {
    $routeProvider.
      when('/register', {
        templateUrl: 'views/register.html',
        controller: 'registerCtrl'
      }).
      when('/login', {
	      templateUrl: 'views/login.html',
	      controller: 'loginCtrl'
	    }).
      otherwise({
        redirectTo: '/'
      });
  }]);

var logger = function(data){
	if(true){
		console.log(data);
	}
}

var msgServeLoad = "Loading..."
var msgServeUnError = "An unknown error has occured."	
var msgServeError = "A server error has occured."