var starterApp = angular.module('starterApp', ['ngRoute','ngMaterial','ngMessages','ngPassword','ngToast']);

starterApp.config(['$routeProvider',
  function($routeProvider) {
    $routeProvider.
      when('/register', {
        templateUrl: 'views/register.html',
        controller: 'registerCtrl'
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

var msgServeError = "An unknown error has occured."