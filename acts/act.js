(function (angular) {
	angular.module('act', ['act.resource'])
	.controller('readActCtrl', function ($scope, $stateParams, Act) {
		var docId = $stateParams.docId;
		console.log(docId);
		
		Act.get({docId:docId}, function (data) {
			console.log(data);
			$scope.act = data;
		});
		
	})
	.controller('createActCtrl', function ($scope, Act, $location) {
		$scope.act = new Act();
		$scope.act.id = 'a1';
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
					$location.path('/main');
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