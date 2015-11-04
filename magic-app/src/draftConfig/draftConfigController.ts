module Magic.App.DraftConfig {

    export interface ITournamentModel {
        numRounds: number;
        players: IPlayer[];
        format: string;
        set: string;
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
        allPlayers: IPlayer[];
    }

    export class DraftConfigController implements IDraftConfigController {
        private $scope: IDraftConfigScope;
        public formats: string[];
        public sets: ISet[];
        public pendingPlayer: any;
        public static $inject = ["$scope", "$http"];
        constructor($scope: IDraftConfigScope, $http: ng.IHttpService) {
            this.$scope = $scope;
            this.$scope.draftConfigController = this;
            $http.get<any>("api/players/list").then((response) => {
                console.log(response)
                this.$scope.allPlayers = response.data;
            });
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
        }

        public addPendingPlayer() {
            console.log(this.pendingPlayer);
            if (typeof this.pendingPlayer === 'string') {
                // if the new player is specified by the user, it will show up as a string
                if ((this.pendingPlayer).length > 0) {
                    this.$scope.tournamentModel.players.push({
                        name: this.pendingPlayer,
                        id: -1,
                    });
                }
            } else if (this.pendingPlayer.name.trim().length > 0) {
                this.$scope.tournamentModel.players.push({
                    name: this.pendingPlayer.name,
                    id: this.pendingPlayer.id
                });
            };
            this.pendingPlayer = "";
        }

        public removePlayer(player: IPlayer) {
            _.remove(this.$scope.tournamentModel.players, player);
        }
    }

    magic.controller("draftConfigController", DraftConfigController);
}
