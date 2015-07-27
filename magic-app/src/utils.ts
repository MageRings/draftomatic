
module Magic.App.Tournament {

    export class Swiss {
        public players: string[];
        //public matches: {[player: string] : Match } [];
        //public pairings: { [player: string]: string } [];
        public rounds: Match[][]
        public currentRound: number;
        public numRounds: number;
        public bestOf: number;

        constructor(bestOf: number, players: string[]) {
            this.bestOf = bestOf;
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
                initPairings.push(new Match(this.players[halfPairings], null));
            }
            return this;
        }
        
        // To pair the round, we take a look at the most recent matches and  
        pairRound() {            
            var playersAndPoints: any = {};
            
            for (var i = 0; i < this.currentRound; i++) {
                var thisRound = this.rounds[i];
                thisRound.forEach((match) => {                
                    
                    var helper = (player: string) => {
                        if (!playersAndPoints[player]) {
                            playersAndPoints[player] = match.matchPoints(player);    
                        } else {
                            playersAndPoints[player] += match.matchPoints(player);
                        }
                    };
                    
                    helper(match.player1);
                    helper(match.player2);                    
                });   
            }
            
            var buckets: any = {};
            
            for (var pp in playersAndPoints) {
                var points = playersAndPoints[pp];
                if (!buckets[points]) {
                    buckets[points] = [pp];                    
                } else {
                    buckets[points].push(pp);    
                }
            }
            
            var sortedPlayers : any[] = [];
            
            for (var bucket in buckets) {                
                sortedPlayers = sortedPlayers.concat(_.shuffle(bucket));    
            }                        
            
            this.currentRound++;
            var nextRound = this.rounds[this.currentRound];
                   
            var lonePairings: string[] = [];
            sortedPlayers.forEach((player) => {            
                if (lonePairings.length = 0) {
                    lonePairings.push(player);    
                } else {
                    for (var loner in lonePairings) {
                        if (!this.hasBeenMatched(player, lonePairings[loner])) {
                            _.remove(lonePairings, lonePairings[loner]);
                            nextRound.push(new Match(player, lonePairings[loner]));
                            break;    
                        }
                    }
                }
            });
            
            if (lonePairings.length === 1) {
                nextRound.push(new Match(lonePairings[0], null));    
            }
                        
            return nextRound;
                      
        }
        
        
        hasBeenMatched(player1: string, player2: string) {
            this.rounds.forEach((round) => {
                round.forEach((match) => {
                    if ((match.player1 === player1 && match.player2 === player2) ||
                       (match.player1 === player2 && match.player2 === player1)) {
                        return true;
                    }
                })
            });
            return false;    
            
        }
                       
        reportResult(player: string, wins: number, draws: number) {
            for (var match in this.rounds[this.currentRound]) {
                match.setWins(player, wins, draws);
            }
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
