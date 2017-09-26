var Magic;
(function (Magic) {
    var App;
    (function (App) {
        var DraftConfig;
        (function (DraftConfig) {
            var DraftConfigController = (function () {
                function DraftConfigController($scope, $http) {
                    var _this = this;
                    this.$scope = $scope;
                    this.$scope.draftConfigController = this;
                    $http.get("api/players/list").then(function (response) {
                        console.log(response);
                        _this.$scope.allPlayers = response.data;
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
                            value: "mm2"
                        },
                        {
                            name: "Oath of the Gatewatch",
                            value: "otg"
                        },
                        {
                            name: "Shadows Over Innistrad",
                            value: "soi"
                        },
                        {
                            name: "Eternal Masters",
                            value: "ema"
                        },
                        {
                            name: "Eldritch Moon",
                            value: "emn"
                        },
                        {
                            name: "Kaladesh",
                            value: "kld"
                        },
                        {
                            name: "Amonkhet",
                            value: "akh"
                        },
                        {
                            name: "Hour of Devastation",
                            value: "hou"
                        },
                        {
                            name: "Ixalan",
                            value: "XLN"
                        },
                        {
                            name: "Iconic Masters",
                            value: "IMA"
                        },
                        {
                            name: "Unstable",
                            value: "UST"
                        },
                        {
                            name: "Rivals of Ixalan",
                            value: "RIX"
                        },
                        {
                            name: "Masters 25",
                            value: "A25"
                        },
                        {
                            name: "Dominaria",
                            value: "DOM"
                        },
                        {
                            name: "Core 2019",
                            value: "M19"
                        }
                    ];
                    this.formats = ["LIMITED_DRAFT", "LIMITED_TEAM_DRAFT", "LIMITED_SEALED",
                        "LIMITED_TEAM_SEALED", "LIMITED_2HG_SEALED", "LIMITED_2HG_DRAFT",
                        "CONSTRUCTED_CASUAL", "CONSTRUCTED_STANDARD", "CONSTRUCTED_MODERN",
                        "CONSTRUCTED_LEGACY", "CONSTRUCTED_VINTAGE"];
                }
                DraftConfigController.prototype.addPendingPlayer = function () {
                    console.log(this.pendingPlayer);
                    if (typeof this.pendingPlayer === 'string') {
                        // if the new player is specified by the user, it will show up as a string
                        if ((this.pendingPlayer).length > 0) {
                            this.$scope.tournamentModel.players.push({
                                name: this.pendingPlayer,
                                id: -1,
                            });
                        }
                    }
                    else if (this.pendingPlayer.name.trim().length > 0) {
                        this.$scope.tournamentModel.players.push({
                            name: this.pendingPlayer.name,
                            id: this.pendingPlayer.id
                        });
                    }
                    ;
                    this.pendingPlayer = "";
                };
                DraftConfigController.prototype.removePlayer = function (player) {
                    _.remove(this.$scope.tournamentModel.players, player);
                };
                DraftConfigController.$inject = ["$scope", "$http"];
                return DraftConfigController;
            })();
            DraftConfig.DraftConfigController = DraftConfigController;
            App.magic.controller("draftConfigController", DraftConfigController);
        })(DraftConfig = App.DraftConfig || (App.DraftConfig = {}));
    })(App = Magic.App || (Magic.App = {}));
})(Magic || (Magic = {}));

//# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJzb3VyY2VzIjpbImRyYWZ0Q29uZmlnL2RyYWZ0Q29uZmlnQ29udHJvbGxlci50cyJdLCJuYW1lcyI6WyJNYWdpYyIsIk1hZ2ljLkFwcCIsIk1hZ2ljLkFwcC5EcmFmdENvbmZpZyIsIk1hZ2ljLkFwcC5EcmFmdENvbmZpZy5EcmFmdENvbmZpZ0NvbnRyb2xsZXIiLCJNYWdpYy5BcHAuRHJhZnRDb25maWcuRHJhZnRDb25maWdDb250cm9sbGVyLmNvbnN0cnVjdG9yIiwiTWFnaWMuQXBwLkRyYWZ0Q29uZmlnLkRyYWZ0Q29uZmlnQ29udHJvbGxlci5hZGRQZW5kaW5nUGxheWVyIiwiTWFnaWMuQXBwLkRyYWZ0Q29uZmlnLkRyYWZ0Q29uZmlnQ29udHJvbGxlci5yZW1vdmVQbGF5ZXIiXSwibWFwcGluZ3MiOiJBQUFBLElBQU8sS0FBSyxDQStHWDtBQS9HRCxXQUFPLEtBQUs7SUFBQ0EsSUFBQUEsR0FBR0EsQ0ErR2ZBO0lBL0dZQSxXQUFBQSxHQUFHQTtRQUFDQyxJQUFBQSxXQUFXQSxDQStHM0JBO1FBL0dnQkEsV0FBQUEsV0FBV0EsRUFBQ0EsQ0FBQ0E7WUE2QjFCQztnQkFNSUMsK0JBQVlBLE1BQXlCQSxFQUFFQSxLQUFzQkE7b0JBTmpFQyxpQkErRUNBO29CQXhFT0EsSUFBSUEsQ0FBQ0EsTUFBTUEsR0FBR0EsTUFBTUEsQ0FBQ0E7b0JBQ3JCQSxJQUFJQSxDQUFDQSxNQUFNQSxDQUFDQSxxQkFBcUJBLEdBQUdBLElBQUlBLENBQUNBO29CQUN6Q0EsS0FBS0EsQ0FBQ0EsR0FBR0EsQ0FBTUEsa0JBQWtCQSxDQUFDQSxDQUFDQSxJQUFJQSxDQUFDQSxVQUFDQSxRQUFRQTt3QkFDN0NBLE9BQU9BLENBQUNBLEdBQUdBLENBQUNBLFFBQVFBLENBQUNBLENBQUFBO3dCQUNyQkEsS0FBSUEsQ0FBQ0EsTUFBTUEsQ0FBQ0EsVUFBVUEsR0FBR0EsUUFBUUEsQ0FBQ0EsSUFBSUEsQ0FBQ0E7b0JBQzNDQSxDQUFDQSxDQUFDQSxDQUFDQTtvQkFDSEEsSUFBSUEsQ0FBQ0EsSUFBSUEs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QUFFQSxNQUFNQSxDQUFDQSxDQUFDQTtnQkFDMURBLENBQUNBO2dCQXpFYUgsNkJBQU9BLEdBQUdBLENBQUNBLFFBQVFBLEVBQUVBLE9BQU9BLENBQUNBLENBQUNBO2dCQTBFaERBLDRCQUFDQTtZQUFEQSxDQS9FQUQsQUErRUNDLElBQUFEO1lBL0VZQSxpQ0FBcUJBLHdCQStFakNBLENBQUFBO1lBRURBLFNBQUtBLENBQUNBLFVBQVVBLENBQUNBLHVCQUF1QkEsRUFBRUEscUJBQXFCQSxDQUFDQSxDQUFDQTtRQUNyRUEsQ0FBQ0EsRUEvR2dCRCxXQUFXQSxHQUFYQSxlQUFXQSxLQUFYQSxlQUFXQSxRQStHM0JBO0lBQURBLENBQUNBLEVBL0dZRCxHQUFHQSxHQUFIQSxTQUFHQSxLQUFIQSxTQUFHQSxRQStHZkE7QUFBREEsQ0FBQ0EsRUEvR00sS0FBSyxLQUFMLEtBQUssUUErR1giLCJmaWxlIjoiZHJhZnRDb25maWcvZHJhZnRDb25maWdDb250cm9sbGVyLmpzIiwic291cmNlc0NvbnRlbnQiOlsibW9kdWxlIE1hZ2ljLkFwcC5EcmFmdENvbmZpZyB7XG5cbiAgICBleHBvcnQgaW50ZXJmYWNlIElUb3VybmFtZW50TW9kZWwge1xuICAgICAgICBudW1Sb3VuZHM6IG51bWJlcjtcbiAgICAgICAgcGxheWVyczogSVBsYXllcltdO1xuICAgICAgICBmb3JtYXQ6IHN0cmluZztcbiAgICAgICAgc2V0OiBzdHJpbmc7XG4gICAgfVxuXG4gICAgZXhwb3J0IGludGVyZmFjZSBJUGxheWVyIHtcbiAgICAgICAgbmFtZTogc3RyaW5nO1xuICAgICAgICBpZDogbnVtYmVyO1xuICAgIH1cblxuICAgIGV4cG9ydCBpbnRlcmZhY2UgSVNldCB7XG4gICAgICAgIG5hbWU6IHN0cmluZztcbiAgICAgICAgdmFsdWU6IHN0cmluZztcbiAgICB9XG5cbiAgICBleHBvcnQgaW50ZXJmYWNlIElEcmFmdENvbmZpZ0NvbnRyb2xsZXIge1xuXG4gICAgfVxuXG4gICAgZXhwb3J0IGludGVyZmFjZSBJRHJhZnRDb25maWdTY29wZSBleHRlbmRzIG5nLklTY29wZSB7XG4gICAgICAgIGRyYWZ0Q29uZmlnQ29udHJvbGxlcjogSURyYWZ0Q29uZmlnQ29udHJvbGxlcjtcbiAgICAgICAgdG91cm5hbWVudE1vZGVsOiBJVG91cm5hbWVudE1vZGVsO1xuICAgICAgICBhbGxQbGF5ZXJzOiBJUGxheWVyW107XG4gICAgfVxuXG4gICAgZXhwb3J0IGNsYXNzIERyYWZ0Q29uZmlnQ29udHJvbGxlciBpbXBsZW1lbnRzIElEcmFmdENvbmZpZ0NvbnRyb2xsZXIge1xuICAgICAgICBwcml2YXRlICRzY29wZTogSURyYWZ0Q29uZmlnU2NvcGU7XG4gICAgICAgIHB1YmxpYyBmb3JtYXRzOiBzdHJpbmdbXTtcbiAgICAgICAgcHVibGljIHNldHM6IElTZXRbXTtcbiAgICAgICAgcHVibGljIHBlbmRpbmdQbGF5ZXI6IGFueTtcbiAgICAgICAgcHVibGljIHN0YXRpYyAkaW5qZWN0ID0gW1wiJHNjb3BlXCIsIFwiJGh0dHBcIl07XG4gICAgICAgIGNvbnN0cnVjdG9yKCRzY29wZTogSURyYWZ0Q29uZmlnU2NvcGUsICRodHRwOiBuZy5JSHR0cFNlcnZpY2UpIHtcbiAgICAgICAgICAgIHRoaXMuJHNjb3BlID0gJHNjb3BlO1xuICAgICAgICAgICAgdGhpcy4kc2NvcGUuZHJhZnRDb25maWdDb250cm9sbGVyID0gdGhpcztcbiAgICAgICAgICAgICRodHRwLmdldDxhbnk+KFwiYXBpL3BsYXllcnMvbGlzdFwiKS50aGVuKChyZXNwb25zZSkgPT4ge1xuICAgICAgICAgICAgICAgIGNvbnNvbGUubG9nKHJlc3BvbnNlKVxuICAgICAgICAgICAgICAgIHRoaXMuJHNjb3BlLmFsbFBsYXllcnMgPSByZXNwb25zZS5kYXRhO1xuICAgICAgICAgICAgfSk7XG4gICAgICAgICAgICB0aGlzLnNldHMgPSBbXG4gICAgICAgICAgICAgICAge1xuICAgICAgICAgICAgICAgICAgICBuYW1lOiBcIk1hZ2ljOiBPcmlnaW5zXCIsXG4gICAgICAgICAgICAgICAgICAgIHZhbHVlOiBcIm9yaVwiXG4gICAgICAgICAgICAgICAgfSxcbiAgICAgICAgICAgICAgICB7XG4gICAgICAgICAgICAgICAgICAgIG5hbWU6IFwiRHJhZ29ucyBvZiBUYXJraXJcIixcbiAgICAgICAgICAgICAgICAgICAgdmFsdWU6IFwiZHRrXCJcbiAgICAgICAgICAgICAgICB9LFxuICAgICAgICAgICAgICAgIHtcbiAgICAgICAgICAgICAgICAgICAgbmFtZTogXCJCYXR0bGUgZm9yIFplbmRpa2FyXCIsXG4gICAgICAgICAgICAgICAgICAgIHZhbHVlOiBcImJmelwiXG4gICAgICAgICAgICAgICAgfSxcbiAgICAgICAgICAgICAgICB7XG4gICAgICAgICAgICAgICAgICAgIG5hbWU6IFwiTW9kZXJuIE1hc3RlcnMgMjAxNVwiLFxuICAgICAgICAgICAgICAgICAgICB2YWx1ZTogXCJtbTJcIlxuICAgICAgICAgICAgICAgIH0sXG4gICAgICAgICAgICAgICAge1xuICAgICAgICAgICAgICAgICAgICBuYW1lOiBcIk9hdGggb2YgdGhlIEdhdGV3YXRjaFwiLFxuICAgICAgICAgICAgICAgICAgICB2YWx1ZTogXCJvdGdcIlxuICAgICAgICAgICAgICAgIH0sXG4gICAgICAgICAgICAgICAge1xuICAgICAgICAgICAgICAgICAgICBuYW1lOiBcIlNoYWRvd3MgT3ZlciBJbm5pc3RyYWRcIixcbiAgICAgICAgICAgICAgICAgICAgdmFsdWU6IFwic29pXCJcbiAgICAgICAgICAgICAgICB9LFxuICAgICAgICAgICAgICAgIHtcbiAgICAgICAgICAgICAgICAgICAgbmFtZTogXCJFdGVybmFsIE1hc3RlcnNcIixcbiAgICAgICAgICAgICAgICAgICAgdmFsdWU6IFwiZW1hXCJcbiAgICAgICAgICAgICAgICB9LFxuICAgICAgICAgICAgICAgIHtcbiAgICAgICAgICAgICAgICAgICAgbmFtZTogXCJFbGRyaXRjaCBNb29uXCIsXG4gICAgICAgICAgICAgICAgICAgIHZhbHVlOiBcImVtblwiXG4gICAgICAgICAgICAgICAgfSxcbiAgICAgICAgICAgICAgICB7XG4gICAgICAgICAgICAgICAgICAgIG5hbWU6IFwiS2FsYWRlc2hcIixcbiAgICAgICAgICAgICAgICAgICAgdmFsdWU6IFwia2xkXCJcbiAgICAgICAgICAgICAgICB9XG4gICAgICAgICAgICBdO1xuICAgICAgICAgICAgdGhpcy5mb3JtYXRzID0gW1wiTElNSVRFRF9EUkFGVFwiLCBcIkxJTUlURURfVEVBTV9EUkFGVFwiLCBcIkxJTUlURURfU0VBTEVEXCIsXG4gICAgICAgICAgICBcIkxJTUlURURfVEVBTV9TRUFMRURcIiwgXCJMSU1JVEVEXzJIR19TRUFMRURcIiwgXCJMSU1JVEVEXzJIR19EUkFGVFwiLFxuICAgICAgICAgICAgXCJDT05TVFJVQ1RFRF9DQVNVQUxcIiwgXCJDT05TVFJVQ1RFRF9TVEFOREFSRFwiLCBcIkNPTlNUUlVDVEVEX01PREVSTlwiLFxuICAgICAgICAgICAgXCJDT05TVFJVQ1RFRF9MRUdBQ1lcIiwgXCJDT05TVFJVQ1RFRF9WSU5UQUdFXCJdXG4gICAgICAgIH1cblxuICAgICAgICBwdWJsaWMgYWRkUGVuZGluZ1BsYXllcigpIHtcbiAgICAgICAgICAgIGNvbnNvbGUubG9nKHRoaXMucGVuZGluZ1BsYXllcik7XG4gICAgICAgICAgICBpZiAodHlwZW9mIHRoaXMucGVuZGluZ1BsYXllciA9PT0gJ3N0cmluZycpIHtcbiAgICAgICAgICAgICAgICAvLyBpZiB0aGUgbmV3IHBsYXllciBpcyBzcGVjaWZpZWQgYnkgdGhlIHVzZXIsIGl0IHdpbGwgc2hvdyB1cCBhcyBhIHN0cmluZ1xuICAgICAgICAgICAgICAgIGlmICgodGhpcy5wZW5kaW5nUGxheWVyKS5sZW5ndGggPiAwKSB7XG4gICAgICAgICAgICAgICAgICAgIHRoaXMuJHNjb3BlLnRvdXJuYW1lbnRNb2RlbC5wbGF5ZXJzLnB1c2goe1xuICAgICAgICAgICAgICAgICAgICAgICAgbmFtZTogdGhpcy5wZW5kaW5nUGxheWVyLFxuICAgICAgICAgICAgICAgICAgICAgICAgaWQ6IC0xLFxuICAgICAgICAgICAgICAgICAgICB9KTtcbiAgICAgICAgICAgICAgICB9XG4gICAgICAgICAgICB9IGVsc2UgaWYgKHRoaXMucGVuZGluZ1BsYXllci5uYW1lLnRyaW0oKS5sZW5ndGggPiAwKSB7XG4gICAgICAgICAgICAgICAgdGhpcy4kc2NvcGUudG91cm5hbWVudE1vZGVsLnBsYXllcnMucHVzaCh7XG4gICAgICAgICAgICAgICAgICAgIG5hbWU6IHRoaXMucGVuZGluZ1BsYXllci5uYW1lLFxuICAgICAgICAgICAgICAgICAgICBpZDogdGhpcy5wZW5kaW5nUGxheWVyLmlkXG4gICAgICAgICAgICAgICAgfSk7XG4gICAgICAgICAgICB9O1xuICAgICAgICAgICAgdGhpcy5wZW5kaW5nUGxheWVyID0gXCJcIjtcbiAgICAgICAgfVxuXG4gICAgICAgIHB1YmxpYyByZW1vdmVQbGF5ZXIocGxheWVyOiBJUGxheWVyKSB7XG4gICAgICAgICAgICBfLnJlbW92ZSh0aGlzLiRzY29wZS50b3VybmFtZW50TW9kZWwucGxheWVycywgcGxheWVyKTtcbiAgICAgICAgfVxuICAgIH1cblxuICAgIG1hZ2ljLmNvbnRyb2xsZXIoXCJkcmFmdENvbmZpZ0NvbnRyb2xsZXJcIiwgRHJhZnRDb25maWdDb250cm9sbGVyKTtcbn1cbiJdLCJzb3VyY2VSb290IjoiL3NvdXJjZS8ifQ==
