function FxMarketData() {
}
FxMarketData.prototype.init = function () {
    this.gridOptions = {
        onGridReady: this.onGridReady.bind(this),
        getRowNodeId: this.getRowNodeId,
        enableCellChangeFlash: true,
        enableSorting: true,
        rowSelection: 'single',
        enableColResize: true,
        columnDefs: [
            {
                field: 'ric',
                headerName: 'Symbol',
                width: 80
            },
            {
                field: 'bid',
                headerName: 'Bid',
                headerClass: 'align-right',
                cellRenderer: 'animateShowChange',
                cellClass: 'align-right',
                valueFormatter: this.amountFormatter,
                width: 80
            },
            {
                field: 'ncBid',
                headerName: 'Net Chg',
                headerClass: 'align-right',
                cellRenderer: 'animateShowChange',
                cellClass: 'align-right',
                valueFormatter: this.amountFormatter,
                width: 80,
                cellClassRules: {
                    'fx-positive': 'x > 0.08',
                    'fx-null': 'x === null',
                    'fx-negative': 'x < -0.08'
                }
            },
            {
                field: 'pcBid',
                headerName: '% Chg',
                valueFormatter: this.amountFormatter,
                cellRenderer: HorizontalBarComponent,
                width: 100

            },
            {
                field: 'ask',
                headerName: 'Ask',
                headerClass: 'align-right',
                cellRenderer: 'animateShowChange',
                cellClass: 'align-right',
                valueFormatter: this.amountFormatter,
                width: 80
            },
            {
                field: 'ncAsk',
                headerName: 'Net Chg',
                headerClass: 'align-right',
                cellRenderer: 'animateShowChange',
                cellClass: 'align-right',
                valueFormatter: this.amountFormatter,
                width: 80,
                cellClassRules: {
                    'fx-positive': 'x > 0.08',
                    'fx-null': 'x === null',
                    'fx-negative': 'x < -0.08'
                }
            },
            {
                field: 'pcAsk',
                headerName: '% Chg',
                valueFormatter: this.amountFormatter,
                cellRenderer: HorizontalBarComponent,
                width: 100

            }, {
                field: 'qTs',
                headerName: 'Timestamp',
                comparator: this.dateComparator
            },
            {
                field: 'qVal',
                headerName: 'Validity',
                comparator: this.dateComparator
            },
            {
                field: 'dispNm',
                headerName: 'Name'

            }
        ]//+[;    
/*
].concat(FX_CURRENCY_SYMBOLS.map(function (symbol) {
    "use strict";
    return {
        field: symbol,
        headerName: symbol,
        width: 87,
        cellClass: 'align-right',
        cellRenderer: 'animateShowChange',
        cellClassRules: {
            'fx-positive': 'x > 0.8',
            'fx-null': 'x === null',
            'fx-negative': 'x < -0.8'
        }
    }
}));
*/
    };

    var fxSpotMarketDataGridRef = firebase.database().ref('fxsMarketDataGridRef/').limitToFirst(25);

    fxSpotMarketDataGridRef.on('value', function (snapshot) {
        let data = snapshot.val();
        let updatedFxDataList = '[';
        Object.keys(data).forEach(function (key) {
            updatedFxDataList += data[key];
            updatedFxDataList += ',';
            if(key.startsWith('GBP') || key.startsWith('USD') || key.startsWith('JPY')){
                sessionStorage.setItem(key,JSON.parse(data[key]).ask);
            }
        })
        updatedFxDataList = updatedFxDataList.slice(0, -1);
        updatedFxDataList += ']';
        this.fxData = updatedFxDataList;
        this.gridApi.setRowData(JSON.parse(this.fxData));
    }.bind(this));
};

FxMarketData.prototype.onGridReady = function (params) {
    this.gridApi = params.api;
    this.columnApi = params.columnApi;
    this.gridApi.updateRowData({ add: this.getCurrentFxData() });
};

FxMarketData.prototype.getRowNodeId = function (data) {
    return data.ric;
};

FxMarketData.prototype.getCurrentFxData = function () {
    return _.cloneDeep(this.fxData);
};

FxMarketData.prototype.render = function (id) {
    // lookup the container we want the Grid to use
    let eGridDiv = document.querySelector('#'+id);
    // create the grid passing in the div to use together with the columns & data we want to use
    new agGrid.Grid(eGridDiv, this.gridOptions);
};

FxMarketData.prototype.dateComparator = function (date1, date2) {
    var date1Number = monthToComparableNumber(date1);
    var date2Number = monthToComparableNumber(date2);

    if (date1Number === null && date2Number === null) {
        return 0;
    }
    if (date1Number === null) {
        return -1;
    }
    if (date2Number === null) {
        return 1;
    }

    return date1Number - date2Number;
};

FxMarketData.prototype.amountFormatter = function (params) {
    if (typeof params.value === 'number') {
        return params.value.toFixed(4);
    } else {
        return Number(params.value).toFixed(4);
    }
};

FxMarketData.prototype.percentFormatter = function (params) {
    if (typeof params.value === 'number') {
        return params.value.toFixed(4);
    } else {
        return Number(params.value).toFixed(4);
    }
};

const FX_COLUMNS = ["ric", "bid", "ncBid", "pcBid", "ask", "ncAsk", "pcAsk", "qTs", "qVal", "dispNm"];
