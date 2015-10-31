var Magic;
(function (Magic) {
    var App;
    (function (App) {
        var Match;
        (function (Match) {
            var MatchDirective = (function () {
                function MatchDirective() {
                    this.restrict = "E";
                    this.scope = {
                        match: "=",
                        round: "="
                    };
                    this.templateUrl = "match/matchTemplate.html";
                }
                return MatchDirective;
            })();
            Match.MatchDirective = MatchDirective;
            App.magic.directive("match", function () { return new MatchDirective(); });
        })(Match = App.Match || (App.Match = {}));
    })(App = Magic.App || (Magic.App = {}));
})(Magic || (Magic = {}));

//# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJzb3VyY2VzIjpbIm1hdGNoL21hdGNoRGlyZWN0aXZlLnRzIl0sIm5hbWVzIjpbIk1hZ2ljIiwiTWFnaWMuQXBwIiwiTWFnaWMuQXBwLk1hdGNoIiwiTWFnaWMuQXBwLk1hdGNoLk1hdGNoRGlyZWN0aXZlIiwiTWFnaWMuQXBwLk1hdGNoLk1hdGNoRGlyZWN0aXZlLmNvbnN0cnVjdG9yIl0sIm1hcHBpbmdzIjoiQUFBQSxJQUFPLEtBQUssQ0FZWDtBQVpELFdBQU8sS0FBSztJQUFDQSxJQUFBQSxHQUFHQSxDQVlmQTtJQVpZQSxXQUFBQSxHQUFHQTtRQUFDQyxJQUFBQSxLQUFLQSxDQVlyQkE7UUFaZ0JBLFdBQUFBLEtBQUtBLEVBQUNBLENBQUNBO1lBRXBCQztnQkFBQUM7b0JBQ1dDLGFBQVFBLEdBQUdBLEdBQUdBLENBQUNBO29CQUNmQSxVQUFLQSxHQUFHQTt3QkFDWEEsS0FBS0EsRUFBRUEsR0FBR0E7d0JBQ1ZBLEtBQUtBLEVBQUVBLEdBQUdBO3FCQUNiQSxDQUFDQTtvQkFDS0EsZ0JBQVdBLEdBQUdBLDBCQUEwQkEsQ0FBQ0E7Z0JBQ3BEQSxDQUFDQTtnQkFBREQscUJBQUNBO1lBQURBLENBUEFELEFBT0NDLElBQUFEO1lBUFlBLG9CQUFjQSxpQkFPMUJBLENBQUFBO1lBRURBLFNBQUtBLENBQUNBLFNBQVNBLENBQUNBLE9BQU9BLEVBQUVBLGNBQU1BLE9BQUFBLElBQUlBLGNBQWNBLEVBQUVBLEVBQXBCQSxDQUFvQkEsQ0FBQ0EsQ0FBQ0E7UUFDekRBLENBQUNBLEVBWmdCRCxLQUFLQSxHQUFMQSxTQUFLQSxLQUFMQSxTQUFLQSxRQVlyQkE7SUFBREEsQ0FBQ0EsRUFaWUQsR0FBR0EsR0FBSEEsU0FBR0EsS0FBSEEsU0FBR0EsUUFZZkE7QUFBREEsQ0FBQ0EsRUFaTSxLQUFLLEtBQUwsS0FBSyxRQVlYIiwiZmlsZSI6Im1hdGNoL21hdGNoRGlyZWN0aXZlLmpzIiwic291cmNlc0NvbnRlbnQiOlsibW9kdWxlIE1hZ2ljLkFwcC5NYXRjaCB7XG5cbiAgICBleHBvcnQgY2xhc3MgTWF0Y2hEaXJlY3RpdmUgaW1wbGVtZW50cyBuZy5JRGlyZWN0aXZlIHtcbiAgICAgICAgcHVibGljIHJlc3RyaWN0ID0gXCJFXCI7XG4gICAgICAgIHB1YmxpYyBzY29wZSA9IHtcbiAgICAgICAgICAgIG1hdGNoOiBcIj1cIixcbiAgICAgICAgICAgIHJvdW5kOiBcIj1cIlxuICAgICAgICB9O1xuICAgICAgICBwdWJsaWMgdGVtcGxhdGVVcmwgPSBcIm1hdGNoL21hdGNoVGVtcGxhdGUuaHRtbFwiO1xuICAgIH1cblxuICAgIG1hZ2ljLmRpcmVjdGl2ZShcIm1hdGNoXCIsICgpID0+IG5ldyBNYXRjaERpcmVjdGl2ZSgpKTtcbn1cblxuXG4iXSwic291cmNlUm9vdCI6Ii9zb3VyY2UvIn0=