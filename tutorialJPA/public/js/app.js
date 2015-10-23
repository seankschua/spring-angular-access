var starterApp = angular.module('starterApp', ['ngRoute','ngMaterial']);

starterApp.config(['$routeProvider',
  function($routeProvider) {
    $routeProvider.
      when('/register', {
        templateUrl: 'views/register.html'
      }).
      otherwise({
        redirectTo: '/'
      });
  }]);