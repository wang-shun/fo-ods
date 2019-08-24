function EqMarketData() {
}
EqMarketData.prototype.init = function () {
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
                field: 'dispNm',
                headerName: 'Name',
                width: 150                
            },
            {
                field: 'ltp',
                headerName: 'LTP',
                headerClass: 'align-right',
                cellRenderer: 'animateShowChange',
                cellClass: 'align-right',
                valueFormatter: this.amountFormatter,
                width: 80
            },
            {
                field: 'ncLtp',
                headerName: 'Net Chg',
                headerClass: 'align-right',
                cellRenderer: 'animateShowChange',
                cellClass: 'align-right',
                valueFormatter: this.amountFormatter,
                width: 90,
                cellClassRules: {
                    'fx-positive': 'x > 0.08',
                    'fx-null': 'x === null',
                    'fx-negative': 'x < -0.08'
                }
            },
            /*{
                field: 'pcLtp',
                headerName: '% Chg LTP',
                valueFormatter: this.amountFormatter,
                cellRenderer: HorizontalBarComponent,
                width: 100

            },*/
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
                width: 90,
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
                width: 90,
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
            },
            {
                field: 'qTs',
                headerName: 'Timestamp',
                comparator: this.dateComparator
            },
        ]
    };

    var eqtMarketDataGridRef = firebase.database().ref('eqtMarketDataGridRef/').limitToFirst(25);

    eqtMarketDataGridRef.on('value', function (snapshot) {
        let data = snapshot.val();
        let updatedEqDataList = '[';
        Object.keys(data).forEach(function (key) {
            //updatedEqDataList += data[key];
            updatedEqDataList += data[key].replace(key, key.replace('_','.'));
            updatedEqDataList += ',';
        })
        updatedEqDataList = updatedEqDataList.slice(0, -1);
        updatedEqDataList += ']';
        this.eqData = updatedEqDataList;
        this.gridApi.setRowData(JSON.parse(this.eqData));
    }.bind(this));
};

EqMarketData.prototype.onGridReady = function (params) {
    this.gridApi = params.api;
    this.columnApi = params.columnApi;
    this.gridApi.updateRowData({ add: this.getCurrentEqData() });
};

EqMarketData.prototype.getRowNodeId = function (data) {
    return data.ric;
};

EqMarketData.prototype.getCurrentEqData = function () {
    return _.cloneDeep(this.eqData);
};

EqMarketData.prototype.render = function (id) {
    // lookup the container we want the Grid to use
    let eGridDiv = document.querySelector('#'+id);
    // create the grid passing in the div to use together with the columns & data we want to use
    new agGrid.Grid(eGridDiv, this.gridOptions);
};

EqMarketData.prototype.dateComparator = function (date1, date2) {
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

EqMarketData.prototype.amountFormatter = function (params) {
    if (typeof params.value === 'number') {
        return params.value.toFixed(3);
    } else {
        return Number(params.value).toFixed(3);
    }
};

EqMarketData.prototype.percentFormatter = function (params) {
    if (typeof params.value === 'number') {
        return params.value.toFixed(3);
    } else {
        return Number(params.value).toFixed(3);
    }
};

//const EQ_COLUMNS = ["ric", "ltp", "ncLtp", "pcLtp","bid", "ncBid", "pcBid", "ask", "ncAsk", "pcAsk", "dispNm"];
const EQ_COLUMNS = ["ric", "dispNm", "ltp","bid", "ncBid", "pcBid", "ask", "ncAsk", "pcAsk", "qTs"];
