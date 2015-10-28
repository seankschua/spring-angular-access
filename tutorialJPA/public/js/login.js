
starterApp.controller('loginCtrl', function($scope, $http, ngToast, store, $window) {
    $scope.input = {
    	      email: 'sean@expedia.com',
    	      password: 'helloworld',
    	      device: $window.navigator.userAgent
    };
    
    $scope.login = function(){
    	if(store.get('exp-jwt')!=null){
    		ngToast.warning("You are already logged in!");
    		return;
    	}
    	var toastLoad = ngToast.info(msgServeLoad);
    	$http.post("/loginPOST", $scope.input)
    		.success(function(data){
    			ngToast.dismiss(toastLoad);
    			if(data.data.length==0){
    				ngToast.danger(msgServeUnError);
    				return;
    			}
    			if(data.success==false){
    				for (i in data.data){
        				ngToast.warning(data.data[i]);
    				}
    			} else {
    				ngToast.success('User logged in!');
    				store.set('exp-jwt', data.data[0]);
    				store.set('exp-user', data.data[1]);
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
    	var toastLoad = ngToast.info(msgServeLoad);
    	var submit = {
    			inputs: [store.get('exp-jwt'), store.get('exp-user')]
    	};
    	$http.post("/checkLoginPOST", submit)
    		.success(function(data){
    			ngToast.dismiss(toastLoad);
    			if(data.data.length==0){
    				ngToast.danger(msgServeUnError);
    				return;
    			}
    			if(data.success==false){
    				for (i in data.data){
        				ngToast.warning(data.data[i]);
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
    
    $scope.logout = function(){
    	ngToast.info(store.get('exp-user') + " logged out.");
    	store.remove('exp-jwt');
    	store.remove('exp-user');
    };
    
})

