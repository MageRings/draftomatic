module Magic.App.DraftConfig {

    export interface ITournamentModel {
        numRounds: number;
        players: IPlayer[];
        format: string;
        set: string;
        bestOf: string;
    }

    export interface IPlayer {
        name: string;
        id: number;
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
        public bestOfs: number[];
        public formats: string[];
        public sets: ISet[];
        public pendingPlayerName: string;
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
            this.formats = ["Draft", "Sealed", "Team Draft", "Constructed"];
            this.bestOfs = [1,3,5];
            this.pendingPlayerName = "Mike";
            this.addPendingPlayer();
            this.pendingPlayerName = "asdf";
            this.addPendingPlayer();
            this.pendingPlayerName = "brian";
            this.addPendingPlayer();
            this.pendingPlayerName = "sam";
            this.addPendingPlayer();
        }

        public addPendingPlayer() {
            if (this.pendingPlayerName.trim().length > 0) {
                
                var newPlayer = { name: this.pendingPlayerName, id: this.hash(this.pendingPlayerName) };
                this.$scope.tournamentModel.players.push(newPlayer);
                this.pendingPlayerName = "";
            };
        }

        private hash(name: string) {
            var hash = 0;
            if (name.length) {
                for (var i = 0; i < name.length; i++) {
                    var character = name.charCodeAt(i);
                    hash = ((hash<<5)-hash)+character;
                    hash = hash & hash; // Convert to 32bit integer
                }
            }
            return hash;
        }

        public removePlayer(player: IPlayer) {
            _.remove(this.$scope.tournamentModel.players, player);
        }
    }

    magic.controller("draftConfigController", DraftConfigController);
}
