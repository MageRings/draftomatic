module Magic.App.Match {

    export interface IMatchScope extends ng.IScope {
        round: Magic.App.Tournament.Match[];
        roundNumber: number;
        matchController: MatchController;
    }

    export class MatchController {
        private $scope: IMatchScope;
        public static $inject = ["$scope", "$http"];
        constructor($scope: IMatchScope, $http: ng.IHttpService) {
        }
    }

    magic.controller("matchController", MatchController);
}
