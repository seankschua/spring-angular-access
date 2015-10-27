
starterApp.controller('loginCtrl', function($scope, $http, ngToast, store) {
    $scope.input = {
    	      email: 'sean@expedia.com',
    	      password: 'helloworld',
    };
    
    $scope.login = function(){
    	var toastLoad = ngToast.create(msgServeLoad);
    	$http.post("/loginPOST", $scope.input)
    		.success(function(data){
    			ngToast.dismiss(toastLoad);
    			if(data.success==false){
    				for (i in data.data){
        				ngToast.danger(data.data[i]);
    				}
    			} else {
    				ngToast.success('User logged in!');
    				store.set('jwt', data.data[0]);
    			}
    			logger(data);
    		})
    		.error(function(data){
    			ngToast.dismiss(toastLoad);
    			ngToast.danger(msgServeError);
    			logger(data);
    		})
    };
    
    $scope.checkLogged = function(){
    	var toastLoad = ngToast.create(msgServeLoad);
    	var submit = {
    			input: store.get('jwt')
    	};
    	$http.post("/checkLoginPOST", submit)
    		.success(function(data){
    			ngToast.dismiss(toastLoad);
    			if(data.success==false){
    				for (i in data.data){
        				ngToast.danger(data.data[i]);
    				}
    			} else {
    				ngToast.success(data.data[0]);
    			}
    			logger(data);
    		})
    		.error(function(data){
    			ngToast.dismiss(toastLoad);
    			ngToast.danger(msgServeError);
    			logger(data);
    		})
    };
    
})

