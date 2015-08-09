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
    /*
        public getUrlPrefix() {
            return Utils.Rest.createUrlPrefix(DatasetService.API_PREFIX);
        }
    
        public columnsView(datasetName: string) {
            return this.restApi.get<IColumnsViewResponse>("columnsView/" + Utils.filePathToId(datasetName));
        }
    
        public deleteArchetype(archetype: string, datasetName: string, columnName: string) {
            return this.restApi.delete<void>("deleteArchetype/" + archetype + "/" + Utils.filePathToId(datasetName) + "/" + columnName);
        }
    
        public filesView(datasetName: string, query: string) {
            return this.restApi.get<IFilesView>("view/" + Utils.filePathToId(datasetName), { query: query });
        }
    
        public getAdvanced(datasetName: string) {
            return this.restApi.get<IAdvancedResponse>("advanced/" + Utils.filePathToId(datasetName));
        }
    
        public getDependencyGraph(datasetName: string) {
            return this.restApi.get<IDependencyGraphResponse>("graph/" + Utils.filePathToId(datasetName));
        }
    
        public getHistory(datasetName: string) {
            return this.restApi.get<IHistoryResponse>("history/" + Utils.filePathToId(datasetName)).success((response) => {
                // https://jira.yojoe.local/browse/UDP-2375
                response.metadatas.forEach((metadata) => {
                    if (metadata.udpTransaction.timeCommitted.reference === undefined) {
                        metadata.udpTransaction.timeCommitted = JSON.parse(<string><any>(metadata.udpTransaction.timeCommitted));
                    }
                });
            });
        }
    
        public getParents(datasetName: string, transactionId: string) {
            return this.restApi.get<IDatasetNamesResponse>("parents/" + Utils.filePathToId(datasetName) + "/" + transactionId);
        }
    
        public getRecomputeLogsRelativePaths(datasetName: string, transactionId: string) {
            return this.restApi.get<string[]>("recomputeLogsRelativePaths/" + Utils.filePathToId(datasetName) + "/" + transactionId);
        }
    
        public preview(datasetName: string) {
            return this.restApi.get<IPreviewResponse>("preview/" + Utils.filePathToId(datasetName));
        }
    
        public retrieveFile(datasetName: string, transactionId: string, type: string, filename: string, download: boolean) {
            return this.restApi.get("retrieveFile/" + Utils.filePathToId(datasetName) + "/" + transactionId + "/" + type + "/" + filename, {
                download: download
            });
        }
    
        public summary(datasetName: string) {
            return this.restApi.get<ISummaryResponse>("summary/" + Utils.filePathToId(datasetName));
        }
    
        public updateAttribute(datasetName: string, attributeName: string, attributeValues: string[]) {
            return this.restApi.put<void>("updateAttribute", {
                datasetName: Utils.filePathToId(datasetName),
                attributeName: attributeName,
                attributeValues: attributeValues
            });
        }
    
        public updateColumnMetadata(datasetName: string, columnName: string, metadata: IColumnMetadata) {
            var processedMetadata: Dataset.IColumnMetadata = angular.copy(metadata);
            delete processedMetadata.pendingDescription;
            processedMetadata.archetypes = processedMetadata.archetypesPills.map((archetype) => archetype.value);
            delete processedMetadata.archetypesPills;
            return this.restApi.put<void>("updateColumnMetadata", {
                datasetName: Utils.filePathToId(datasetName),
                columnName: columnName, metadata: processedMetadata
            });
        }
    
        public updateDescription(datasetName: string, description: string): void {
            this.restApi.put("updateDescription", {
                datasetName: Utils.filePathToId(datasetName),
                description: description
            });
        }*/
    }
    services.service("tournament", TournamentService);
}
    //services.service("dataset", DatasetService);