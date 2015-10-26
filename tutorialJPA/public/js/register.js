
starterApp.controller('registerCtrl', function($scope, $http, ngToast) {
    $scope.input = {
    	      name: 'seanksc',
    	      email: 'sean@expedia.com',
    	      password: 'helloworld',
    	      passwordCfm: 'helloworld'
    };
    
    $scope.register = function(){
    	$http.post("/createPOST", $scope.input)
    		.success(function(data){
    			ngToast.create('User created!');
    			console.log(data);
    		})
    		.error(function(data){
    			console.log($scope.input);
    			console.log(data);
    		})
    }
    
})

starterApp.config(function($mdThemingProvider) {
    // Configure a dark theme with primary foreground yellow
    $mdThemingProvider.theme('docs-dark', 'default')
      .primaryPalette('yellow')
      .dark();
  });

