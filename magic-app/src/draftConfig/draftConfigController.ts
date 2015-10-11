module Magic.App.DraftConfig {

    export interface ITournamentModel {
        numRounds: number;
        players: IPlayer[];
        format: string;
        set: string;
    }

    export interface IPlayer {
        name: string;
    }
    
    export interface ISet {
        name: string;
        value: string;
    }
    
    export interface IDraftConfigController {

    }

    export interface IDraftConfigScope extends ng.IScope {
        draftConfigController: IDraftConfigController;
        tournamentModel: ITournamentModel;
    }

    export class DraftConfigController implements IDraftConfigController {
        private $scope: IDraftConfigScope;
        public formats: string[];
        public sets: ISet[];
        public pendingPlayerName: string;
        private currentId: number;
        public static $inject = ["$scope", "$http"];
        constructor($scope: IDraftConfigScope, $http: ng.IHttpService) {
            $http.get<any>("api/player/list").then((response) => console.log(response));
            this.$scope = $scope;
            this.$scope.draftConfigController = this;
            this.sets = [
                {
                    name: "Magic: Origins",
                    value: "ori"
                },
                {
                    name: "Dragons of Tarkir",
                    value: "dtk"
                },
                {
                    name: "Battle for Zendikar",
                    value: "bfz"
                },
                {
                    name: "Modern Masters 2015",
                    value: "mmb"
                }
            ];
            this.formats = ["LIMITED_DRAFT", "LIMITED_TEAM_DRAFT", "LIMITED_SEALED",
            "LIMITED_TEAM_SEALED", "CONSTRUCTED_CASUAL", "CONSTRUCTED_STANDARD", "CONSTRUCTED_MODERN",
            "CONSTRUCTED_LEGACY", "CONSTRUCTED_VINTAGE"]
            this.currentId = 1;
        }

        public addPendingPlayer() {
            if (this.pendingPlayerName.trim().length > 0) {
                var newPlayer = { name: this.pendingPlayerName, id: this.currentId };
                this.currentId += 1;
                this.$scope.tournamentModel.players.push(newPlayer);
                this.pendingPlayerName = "";
            };
        }

        public removePlayer(player: IPlayer) {
            _.remove(this.$scope.tournamentModel.players, player);
        }
    }

    magic.controller("draftConfigController", DraftConfigController);
}
