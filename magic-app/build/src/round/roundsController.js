var Magic;
(function (Magic) {
    var App;
    (function (App) {
        var Round;
        (function (Round) {
            var RoundsController = (function () {
                function RoundsController($scope, $stateParams, $http) {
                    var _this = this;
                    this.tournamentId = $stateParams["id"];
                    this.scope = $scope;
                    this.http = $http;
                    this.http.get("api/tournament/status/" + this.tournamentId).then(function (response) {
                        console.log(response);
                        _this.tournament = response.data.tournamentData;
                        _this.tournament.complete = response.data.complete;
                        _this.tournament.currentRound = response.data.currentRound;
                        _this.tournament.finalStandings = response.data.finalStandings;
                        _this.seatings = new Array(response.data.seatings.length);
                        for (var i = 0; i < _this.seatings.length; i++) {
                            _this.seatings[i] = response.data.seatings[i].name;
                        }
                    });
                }
                RoundsController.prototype.pairNextRound = function () {
                    var _this = this;
                    var latestRound = this.tournament.rounds[this.tournament.currentRound - 1];
                    latestRound.complete = true;
                    this.http.put("api/tournament/results/" + this.tournamentId, latestRound.matches).then(function (response) {
                        _this.tournament.rounds.push(response.data);
                        _this.tournament.currentRound = response.data.number;
                    });
                };
                RoundsController.prototype.undoLastRound = function () {
                    var _this = this;
                    this.http.delete("api/tournament/round/" + this.tournamentId).then(function (response) {
                        _this.tournament.complete = false;
                        var latestRound = _this.tournament.rounds[_this.tournament.currentRound - 1];
                        if (!latestRound.complete) {
                            _this.tournament.rounds.pop();
                        }
                        _this.tournament.rounds.pop();
                        _this.tournament.rounds.push(response.data);
                        _this.tournament.currentRound = _this.tournament.rounds.length;
                    });
                };
                RoundsController.prototype.getFinalStandings = function () {
                    var _this = this;
                    this.tournament.complete = true;
                    var latestRound = this.tournament.rounds[this.tournament.currentRound - 1];
                    latestRound.complete = true;
                    this.http.put("api/tournament/results/" + this.tournamentId, latestRound.matches).then(function () {
                        _this.http.get("api/tournament/standings/" + _this.tournamentId).then(function (response) {
                            _this.tournament.finalStandings = response.data;
                        });
                    });
                };
                RoundsController.prototype.downloadTournamentData = function () {
                    window.location.href = "api/tournament/export/" + this.tournamentId;
                };
                RoundsController.$inject = ["$scope", "$stateParams", "$http"];
                return RoundsController;
            })();
            Round.RoundsController = RoundsController;
            App.magic.controller("roundsController", RoundsController);
        })(Round = App.Round || (App.Round = {}));
    })(App = Magic.App || (Magic.App = {}));
})(Magic || (Magic = {}));

//# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJzb3VyY2VzIjpbInJvdW5kL3JvdW5kc0NvbnRyb2xsZXIudHMiXSwibmFtZXMiOlsiTWFnaWMiLCJNYWdpYy5BcHAiLCJNYWdpYy5BcHAuUm91bmQiLCJNYWdpYy5BcHAuUm91bmQuUm91bmRzQ29udHJvbGxlciIsIk1hZ2ljLkFwcC5Sb3VuZC5Sb3VuZHNDb250cm9sbGVyLmNvbnN0cnVjdG9yIiwiTWFnaWMuQXBwLlJvdW5kLlJvdW5kc0NvbnRyb2xsZXIucGFpck5leHRSb3VuZCIsIk1hZ2ljLkFwcC5Sb3VuZC5Sb3VuZHNDb250cm9sbGVyLnVuZG9MYXN0Um91bmQiLCJNYWdpYy5BcHAuUm91bmQuUm91bmRzQ29udHJvbGxlci5nZXRGaW5hbFN0YW5kaW5ncyIsIk1hZ2ljLkFwcC5Sb3VuZC5Sb3VuZHNDb250cm9sbGVyLmRvd25sb2FkVG91cm5hbWVudERhdGEiXSwibWFwcGluZ3MiOiJBQUFBLElBQU8sS0FBSyxDQWtFWDtBQWxFRCxXQUFPLEtBQUs7SUFBQ0EsSUFBQUEsR0FBR0EsQ0FrRWZBO0lBbEVZQSxXQUFBQSxHQUFHQTtRQUFDQyxJQUFBQSxLQUFLQSxDQWtFckJBO1FBbEVnQkEsV0FBQUEsS0FBS0EsRUFBQ0EsQ0FBQ0E7WUFFcEJDO2dCQVFJQywwQkFBWUEsTUFBaUJBLEVBQUVBLFlBQXVDQSxFQUFFQSxLQUFzQkE7b0JBUmxHQyxpQkE2RENBO29CQXBET0EsSUFBSUEsQ0FBQ0EsWUFBWUEsR0FBR0EsWUFBWUEsQ0FBQ0EsSUFBSUEsQ0FBQ0EsQ0FBQ0E7b0JBQ3ZDQSxJQUFJQSxDQUFDQSxLQUFLQSxHQUFHQSxNQUFNQSxDQUFDQTtvQkFDcEJBLElBQUlBLENBQUNBLElBQUlBLEdBQUdBLEtBQUtBLENBQUNBO29CQUNsQkEsSUFBSUEsQ0FBQ0EsSUFBSUEsQ0FBQ0EsR0FBR0EsQ0FBTUEsd0JBQXdCQSxHQUFHQSxJQUFJQSxDQUFDQSxZQUFZQSxDQUFDQSxDQUFDQSxJQUFJQSxDQUFDQSxVQUFDQSxRQUFRQTt3QkFDM0VBLE9BQU9BLENBQUNBLEdBQUdB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QUF3QkEsR0FBR0EsSUFBSUEsQ0FBQ0EsWUFBWUEsQ0FBQ0E7Z0JBQ3hFQSxDQUFDQTtnQkF4RGFMLHdCQUFPQSxHQUFHQSxDQUFDQSxRQUFRQSxFQUFFQSxjQUFjQSxFQUFFQSxPQUFPQSxDQUFDQSxDQUFDQTtnQkF5RGhFQSx1QkFBQ0E7WUFBREEsQ0E3REFELEFBNkRDQyxJQUFBRDtZQTdEWUEsc0JBQWdCQSxtQkE2RDVCQSxDQUFBQTtZQUVEQSxTQUFLQSxDQUFDQSxVQUFVQSxDQUFDQSxrQkFBa0JBLEVBQUVBLGdCQUFnQkEsQ0FBQ0EsQ0FBQ0E7UUFDM0RBLENBQUNBLEVBbEVnQkQsS0FBS0EsR0FBTEEsU0FBS0EsS0FBTEEsU0FBS0EsUUFrRXJCQTtJQUFEQSxDQUFDQSxFQWxFWUQsR0FBR0EsR0FBSEEsU0FBR0EsS0FBSEEsU0FBR0EsUUFrRWZBO0FBQURBLENBQUNBLEVBbEVNLEtBQUssS0FBTCxLQUFLLFFBa0VYIiwiZmlsZSI6InJvdW5kL3JvdW5kc0NvbnRyb2xsZXIuanMiLCJzb3VyY2VzQ29udGVudCI6WyJtb2R1bGUgTWFnaWMuQXBwLlJvdW5kIHtcblxuICAgIGV4cG9ydCBjbGFzcyBSb3VuZHNDb250cm9sbGVyIHtcbiAgICAgICAgcHJpdmF0ZSBzY29wZTogbmcuSVNjb3BlO1xuICAgICAgICBwdWJsaWMgdG91cm5hbWVudDogYW55O1xuICAgICAgICBwdWJsaWMgc2VhdGluZ3M6IGFueTtcbiAgICAgICAgcHVibGljIHN0YXRpYyAkaW5qZWN0ID0gW1wiJHNjb3BlXCIsIFwiJHN0YXRlUGFyYW1zXCIsIFwiJGh0dHBcIl07XG4gICAgICAgIHByaXZhdGUgdG91cm5hbWVudElkIDogc3RyaW5nO1xuICAgICAgICBwcml2YXRlIGh0dHAgOiBuZy5JSHR0cFNlcnZpY2U7XG4gICAgICAgIFxuICAgICAgICBjb25zdHJ1Y3Rvcigkc2NvcGU6IG5nLklTY29wZSwgJHN0YXRlUGFyYW1zOiBuZy51aS5JU3RhdGVQYXJhbXNTZXJ2aWNlLCAkaHR0cDogbmcuSUh0dHBTZXJ2aWNlKSB7XG4gICAgICAgICAgICB0aGlzLnRvdXJuYW1lbnRJZCA9ICRzdGF0ZVBhcmFtc1tcImlkXCJdO1xuICAgICAgICAgICAgdGhpcy5zY29wZSA9ICRzY29wZTtcbiAgICAgICAgICAgIHRoaXMuaHR0cCA9ICRodHRwO1xuICAgICAgICAgICAgdGhpcy5odHRwLmdldDxhbnk+KFwiYXBpL3RvdXJuYW1lbnQvc3RhdHVzL1wiICsgdGhpcy50b3VybmFtZW50SWQpLnRoZW4oKHJlc3BvbnNlKSA9PiB7XG4gICAgICAgICAgICAgICAgY29uc29sZS5sb2cocmVzcG9uc2UpO1xuICAgICAgICAgICAgICAgIHRoaXMudG91cm5hbWVudCA9IHJlc3BvbnNlLmRhdGEudG91cm5hbWVudERhdGE7XG4gICAgICAgICAgICAgICAgdGhpcy50b3VybmFtZW50LmNvbXBsZXRlID0gcmVzcG9uc2UuZGF0YS5jb21wbGV0ZTtcbiAgICAgICAgICAgICAgICB0aGlzLnRvdXJuYW1lbnQuY3VycmVudFJvdW5kID0gcmVzcG9uc2UuZGF0YS5jdXJyZW50Um91bmQ7XG4gICAgICAgICAgICAgICAgdGhpcy50b3VybmFtZW50LmZpbmFsU3RhbmRpbmdzID0gcmVzcG9uc2UuZGF0YS5maW5hbFN0YW5kaW5ncztcbiAgICAgICAgICAgICAgICB0aGlzLnNlYXRpbmdzID0gbmV3IEFycmF5KHJlc3BvbnNlLmRhdGEuc2VhdGluZ3MubGVuZ3RoKTtcbiAgICAgICAgICAgICAgICBmb3IgKHZhciBpID0gMDsgaSA8IHRoaXMuc2VhdGluZ3MubGVuZ3RoIDsgaSsrKSB7XG4gICAgICAgICAgICAgICAgICAgIHRoaXMuc2VhdGluZ3NbaV0gPSByZXNwb25zZS5kYXRhLnNlYXRpbmdzW2ldLm5hbWU7XG4gICAgICAgICAgICAgICAgfVxuICAgICAgICAgICAgfSk7XG4gICAgICAgIH1cbiAgICAgICAgXG4gICAgICAgIHB1YmxpYyBwYWlyTmV4dFJvdW5kKCkge1xuICAgICAgICAgICAgdmFyIGxhdGVzdFJvdW5kID0gdGhpcy50b3VybmFtZW50LnJvdW5kc1t0aGlzLnRvdXJuYW1lbnQuY3VycmVudFJvdW5kLTFdO1xuICAgICAgICAgICAgbGF0ZXN0Um91bmQuY29tcGxldGUgPSB0cnVlO1xuICAgICAgICAgICAgdGhpcy5odHRwLnB1dDxhbnk+KFwiYXBpL3RvdXJuYW1lbnQvcmVzdWx0cy9cIiArIHRoaXMudG91cm5hbWVudElkLCBsYXRlc3RSb3VuZC5tYXRjaGVzKS50aGVuKChyZXNwb25zZSkgPT4ge1xuICAgICAgICAgICAgICAgIHRoaXMudG91cm5hbWVudC5yb3VuZHMucHVzaChyZXNwb25zZS5kYXRhKTtcbiAgICAgICAgICAgICAgICB0aGlzLnRvdXJuYW1lbnQuY3VycmVudFJvdW5kID0gcmVzcG9uc2UuZGF0YS5udW1iZXI7XG4gICAgICAgICAgICB9KTtcbiAgICAgICAgfVxuICAgICAgICBcbiAgICAgICAgcHVibGljIHVuZG9MYXN0Um91bmQoKSB7XG4gICAgICAgICAgICB0aGlzLmh0dHAuZGVsZXRlPGFueT4oXCJhcGkvdG91cm5hbWVudC9yb3VuZC9cIiArIHRoaXMudG91cm5hbWVudElkKS50aGVuKChyZXNwb25zZSkgPT4ge1xuICAgICAgICAgICAgICAgIHRoaXMudG91cm5hbWVudC5jb21wbGV0ZSA9IGZhbHNlO1xuICAgICAgICAgICAgICAgIHZhciBsYXRlc3RSb3VuZCA9IHRoaXMudG91cm5hbWVudC5yb3VuZHNbdGhpcy50b3VybmFtZW50LmN1cnJlbnRSb3VuZC0xXTtcbiAgICAgICAgICAgICAgICBpZiAoIWxhdGVzdFJvdW5kLmNvbXBsZXRlKSB7XG4gICAgICAgICAgICAgICAgICAgIHRoaXMudG91cm5hbWVudC5yb3VuZHMucG9wKCk7XG4gICAgICAgICAgICAgICAgfVxuICAgICAgICAgICAgICAgIHRoaXMudG91cm5hbWVudC5yb3VuZHMucG9wKCk7XG4gICAgICAgICAgICAgICAgdGhpcy50b3VybmFtZW50LnJvdW5kcy5wdXNoKHJlc3BvbnNlLmRhdGEpO1xuICAgICAgICAgICAgICAgIHRoaXMudG91cm5hbWVudC5jdXJyZW50Um91bmQgPSB0aGlzLnRvdXJuYW1lbnQucm91bmRzLmxlbmd0aDtcbiAgICAgICAgICAgIH0pO1xuICAgICAgICB9XG4gICAgICAgIFxuICAgICAgICBwdWJsaWMgZ2V0RmluYWxTdGFuZGluZ3MoKSB7XG4gICAgICAgICAgICB0aGlzLnRvdXJuYW1lbnQuY29tcGxldGUgPSB0cnVlO1xuICAgICAgICAgICAgdmFyIGxhdGVzdFJvdW5kID0gdGhpcy50b3VybmFtZW50LnJvdW5kc1t0aGlzLnRvdXJuYW1lbnQuY3VycmVudFJvdW5kLTFdO1xuICAgICAgICAgICAgbGF0ZXN0Um91bmQuY29tcGxldGUgPSB0cnVlO1xuICAgICAgICAgICAgdGhpcy5odHRwLnB1dDxhbnk+KFwiYXBpL3RvdXJuYW1lbnQvcmVzdWx0cy9cIiArIHRoaXMudG91cm5hbWVudElkLCBsYXRlc3RSb3VuZC5tYXRjaGVzKS50aGVuKCgpID0+IHtcbiAgICAgICAgICAgICAgICB0aGlzLmh0dHAuZ2V0PGFueT4oXCJhcGkvdG91cm5hbWVudC9zdGFuZGluZ3MvXCIgKyB0aGlzLnRvdXJuYW1lbnRJZCkudGhlbigocmVzcG9uc2UpID0+IHtcbiAgICAgICAgICAgICAgICAgICAgdGhpcy50b3VybmFtZW50LmZpbmFsU3RhbmRpbmdzID0gcmVzcG9uc2UuZGF0YVxuICAgICAgICAgICAgICAgIH0pO1xuICAgICAgICAgICAgfSk7XG4gICAgICAgIH1cbiAgICAgICAgXG4gICAgICAgIHB1YmxpYyBkb3dubG9hZFRvdXJuYW1lbnREYXRhKCkge1xuICAgICAgICAgICAgd2luZG93LmxvY2F0aW9uLmhyZWYgPSBcImFwaS90b3VybmFtZW50L2V4cG9ydC9cIiArIHRoaXMudG91cm5hbWVudElkO1xuICAgICAgICB9XG4gICAgfVxuXG4gICAgbWFnaWMuY29udHJvbGxlcihcInJvdW5kc0NvbnRyb2xsZXJcIiwgUm91bmRzQ29udHJvbGxlcik7XG59XG4iXSwic291cmNlUm9vdCI6Ii9zb3VyY2UvIn0=