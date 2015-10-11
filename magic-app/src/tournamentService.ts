module Magic.App.Tournament {
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
    
    export class TournamentService {
    
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
    
        public post<T>(methodName: string, params?: any): ng.IHttpPromise<T> {
            return this.$http.post(methodName, params);
        }
        
        public setTournamentResults() {
            
        }
        /*
        public ng.I registerTournament() {
            return this.post("/magic/register"
        }
        */
    }
    services.service("tournament", TournamentService);
}