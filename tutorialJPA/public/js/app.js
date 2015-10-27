var starterApp = angular.module('starterApp', ['ngRoute','ngMaterial','ngMessages','ngPassword','ngToast','angular-storage']);

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
var msgServeError = "An unknown error has occured."