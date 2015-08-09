
module Magic.App.Tournament {

    export interface MatchResult {
        p1wins: number;
        p2wins: number;
    }

    export class Swiss {
        public players: string[];
        public rounds: Match[][]
        public currentRound: number;
        public numRounds: number;
        public bestOf: number;
        public possibleMatchResults: MatchResult[];

        constructor(bestOf: number, players: string[]) {
            this.bestOf = bestOf;
            this.initPossibleMatchResults();
            this.players = players;
            this.numRounds = Math.ceil(Math.log(players.length) / Math.log(2));
            this.rounds = _.range(this.numRounds).map(function () { return <any>[] });
            this.currentRound = 0;
        }

        public pairInitial() {
            this.players = _.shuffle(this.players);
            var initPairings = this.rounds[0];
            var halfPairings = Math.floor(this.players.length / 2);
            for (var i=0; i < halfPairings; i++) {
                initPairings.push(new Match(this.players[i], this.players[i+halfPairings]));
            }
            // If we have an odd number of players, give one a bye.
            if ((this.players.length % 2) != 0) {
                initPairings.push(new Match(this.players[halfPairings], "bye"));
            }
            return this;
        }

        pairRound() { 
                      
        }
                       
        reportResult(player: string, wins: number, draws: number) {
            for (var match in this.rounds[this.currentRound]) {
                match.setWins(player, wins, draws);
            }
        }


        public initPossibleMatchResults() {
            this.possibleMatchResults = [];
            var maxWins = Math.ceil(this.bestOf / 2);
            for (var i = maxWins; i > 0; i--) {
                for (var j = 0; j < i + Number(i != maxWins) ; j++) {
                    this.possibleMatchResults.push({p1wins:i,p2wins:j});
                    if (i != j) {
                        this.possibleMatchResults.push({p1wins:j,p2wins:i});
                    }
                }
            }
            this.possibleMatchResults.push({p1wins:0,p2wins:0});
        }
    }

    export class Match {
        public player1: string;
        public player2: string;
        public p1wins: number;
        public p2wins: number;
        public draws: number;

        constructor(player1: string, player2: string) {
            this.player1 = player1;
            this.player2 = player2;
            this.p1wins = 0;
            this.p2wins = 0;
            this.draws = 0;
        }

        setWins(player: string, wins: number, draws: number) {
            if (player === this.player1) {
                this.p1wins = wins;
                this.draws = draws;
            }   else if (player === this.player2) {
                this.p2wins = wins;
                this.draws = draws;
            }
        }

        matchPoints(player: string) {
            if (player === this.player1) {
                return this.draws + this.p1wins * 3;
            }   else if (player === this.player2) {
                return this.draws + this.p2wins * 3;
            }

            return -1; // This is bad?
        }
    }
}
