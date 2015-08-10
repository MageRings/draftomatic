module Magic.App.Tournament {
    export class Format {
        public LIMITED_DRAFT = "LIMITED_DRAFT";
        public LIMITED_TEAM_DRAFT = "LIMITED_TEAM_DRAFT";
        public LIMITED_SEALED = "LIMITED_SEALED";
        public LIMITED_TEAM_SEALED = "LIMITED_TEAM_SEALED";
        public CONSTRUCTED_CASUAL = "CONSTRUCTED_CASUAL";
        public CONSTRUCTED_STANDARD = "CONSTRUCTED_STANDARD";
        public CONSTRUCTED_MODERN = "CONSTRUCTED_MODERN";
        public CONSTRUCTED_LEGACY = "CONSTRUCTED_LEGACY";
        public CONSTRUCTED_VINTAGE = "CONSTRUCTED_VINTAGE";
    }
    
    export interface IPlayer {
        name: string;
    }

    export interface IPairing {
        player1: IPlayer;
        player2: IPlayer;
    }
    
    export interface IResult {
        pairing: IPairing;
        p1wins: number;
        p2wins: number
        draws: number;        
    }
    
    export interface ITournamentService {
        registerTournament(rounds: number, format: string, code: string, players: IPlayer[]): ng.IHttpPromise<string>;
        getPairings(tournamentId: string, round: number): ng.IHttpPromise<any>;
    }
    
    export class TournamentService implements ITournamentService {
    
        private $q: ng.IQService;
        private $http: ng.IHttpService
        
        public static $inject = ["$http", "$q"];
        constructor($http: ng.IHttpService, $q: ng.IQService) {
            this.$http = $http;
            this.$q = $q;
        }
        
        public get<T>(methodName: string, params?: any): ng.IHttpPromise<T> {
            if (params) {
                return this.$http.get(methodName, { params: params });
            } else {
                return this.$http.get(methodName);
            }
        }
    
        public post<T>(methodName: string, data: any, params?: any): ng.IHttpPromise<T> {
            return this.$http.post(methodName, data, params);
        }

        public registerTournament(rounds: number, format: string, code: string, players: IPlayer[]) {
            return this.post("/magic/api/tournament/register", players, {rounds: rounds, format: format, code: code, players: players});
        }

        public getPairings(tournamentId: string, round: number) {
            return this.get("/magic/api/tournament/pairings", {tournamentId: tournamentId, round: round});       
        }
    }
    magic.service("tournament", TournamentService);
}