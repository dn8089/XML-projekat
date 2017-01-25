(function (angular) {
	angular.module('act', ['act.resource'])
	.controller('proposedActsCtrl', function($scope, Act, $location) {
		$scope.propAct = true;
		var collId = '/parliament/acts/proposed';
		
		var loadActs = function () {
			Act.findByCollection({collId:collId}, function (data) {
				//console.log(data);
				$scope.results = data;
			});
		};
		loadActs();
		
		$scope.deleteAct = function (docId) {
			//console.log(docId);
			Act.delete({docId:docId}, loadActs);
		};
		
		$scope.acceptAct = function (docId) {
			Act.acceptAct({docId:docId}, function (data) {
				console.log(data);
			Act.acceptAct({docId:docId}, function () {
				$location.path('/usvojena-akta');
			});
		};
	})
	.controller('accepteddActsCtrl', function($scope, Act) {
		$scope.propAct = false;
		var collId = '/parliament/acts/accepted';
		
		Act.findByCollection({collId:collId}, function (data) {
			//console.log(data);
			$scope.results = data;
		});
	})
	.controller('readActCtrl', function ($scope, $stateParams, Act) {
		var docId = $stateParams.docId;
		//console.log(docId);
		
		Act.get({docId:docId}, function (data) {
			//console.log(data);
			$scope.act = data;
		});
		
	})
	.controller('createActCtrl', function ($scope, Act, $location) {
		$scope.act = new Act();
		$scope.act.id = 'a2';
		$scope.act.type='decision';
		$scope.act.article = [];
		$scope.articleExists = false;
		
		$scope.addArticle = function () {
			$scope.articleExists = false;
			$scope.act.article.push({});
			$scope.act.article[$scope.act.article.length-1].content = {};
			$scope.act.article[$scope.act.article.length-1].content.content = [];
		};
		
		$scope.save = function () {
			//console.log($scope.act);
			if ($scope.act.type=='decision' && $scope.act.article.length==0) {
				$scope.articleExists = true;
			} else {
				$scope.act.$save(function () {
					$location.path('/predlozena-akta');
				}, function(err) {
					console.log(err);
				});
			}
		};
	})
	.controller('searchCtrl', function($scope, Act) {
		$scope.words;
		
		$scope.search = function () {
			console.log('search word: ' + $scope.words);
			Act.search({criteria:$scope.words}, function (data) {
				//console.log(data);
				$scope.results = data;
			});
		};
	})
	.directive('myArticle', function () {
		return {
			templateUrl: 'acts/createArticle.html'
		};
	});
}(angular));