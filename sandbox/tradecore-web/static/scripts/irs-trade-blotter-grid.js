function IrsTradeBlotterGrid() {
}

/*IrsTradeBlotterGrid.prototype.init = function (tradeCoreStoreService,
                                            stockBlotterPanel) {*/
IrsTradeBlotterGrid.prototype.init = function () {
    
    this.gridOptions = {
        onGridReady: this.onGridReady.bind(this),
        //onSelectionChanged: this.onSelectionChanged.bind(this),
        //getRowNodeId: this.getRowNodeId,
        enableCellChangeFlash: true,
        enableFilter: true,
        enableSorting: true,
        enableRangeSelection: true,
        allowContextMenuWithControlKey: true,
        enableColResize: true,
        groupSuppressAutoColumn: true,
        suppressRowClickSelection: true,
        groupDefaultExpanded: 1,
        rowSelection: 'multiple',
        groupSelectsChildren: true,
        animateRows: true,
        columnDefs: [
            {
                headerName: 'Leg Number',
                showRowGroup: true, 
                cellRenderer: 'group', 
                field: 'legNumber'
            },
            {
                field: 'tradeId',
                headerName: 'Trade Id',
                filter: "text",
                rowGroup: 'true'
            },    
            {
                field: 'osTradeId',
                headerName: 'OS Trade Id',
                filter: "text"
            },
            {
                field: 'eventTS',
                headerName: 'Event Time',
                filter: "text",
                sort: 'desc',
                comparator: this.dateComparator
            },
            {
                field: 'tradeDate',
                headerName: 'Trade Date',
                filter: "text"
            },
            {
                field: 'exchange',
                headerName: 'Exchange',
                filter: "text"
            },
             {
                field: 'productType',
                headerName: 'Product Type',
                filter: "text"
            },
             {
                 field: 'origSystem',
                 headerName: 'Orig. System',
                 filter: "text"
             },
            {
                field: 'instrumentId',
                headerName: 'Symbol',
                filter: "text"
            },            
            {
                field: 'notional',
                headerName: 'Notional',
                valueFormatter: this.quantityFormatter,
                cellStyle: {'text-align': 'right'},
                filter: "number"
            },                
            {
                field: 'currency',
                headerName: 'Currency',
                filter: "text"
            },            
            {
                field: 'index',
                headerName: 'Index',
                filter: "text"
            },
            {
                field: 'term',
                headerName: 'Term',
                filter: "text"
            }, 
            {
                field: 'maturityDate',
                headerName: 'Maturity Date',
                filter: "text"
            },
            {
                field: 'spread',
                headerName: 'Spread',
                valueFormatter: this.quantityFormatter,
                cellStyle: { 'text-align': 'right' },
                filter: "number"
            },
            {
                field: 'eventStatus',
                headerName: 'Event Status',
                filter: "text"
            },   
            {
                field: 'trader',
                headerName: 'Trader',
                filter: "text"
            }   
                              
        ]
    };

    var tradeGridRef = firebase.database().ref('irdTradeGrid/').limitToFirst(100);
   
    tradeGridRef.on('value', function (snapshot) {
        //console.log(this);
        //console.log(this.TradeBlotterGrid);
        //console.log(this.ordersList);
        let data = snapshot.val();
        let updatedOrdersList = '[';
        Object.keys(data).forEach(function (key) {
            updatedOrdersList += data[key];
            updatedOrdersList += ',';
        })
        updatedOrdersList = updatedOrdersList.slice(0, -1);
        updatedOrdersList += ']';
        this.ordersList = updatedOrdersList;
        //console.log(this.ordersList);
        this.gridApi.setRowData(JSON.parse(this.ordersList));
        //this.gridApi.updateRowData({ add: JSON.parse(this.ordersList) });
    }.bind(this));
};

IrsTradeBlotterGrid.prototype.dateComparator = function (date1, date2) {
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

IrsTradeBlotterGrid.prototype.numberFormatter = function (params) {
    if (typeof params.value === 'number') {
        return params.value.toFixed(2);
    } else {
        return params.value;
    }
};

IrsTradeBlotterGrid.prototype.quantityFormatter = function (params) {
    if (typeof params.value === 'number') {
        return params.value.toFixed(0);
    } else {
        return params.value;
    }
};

IrsTradeBlotterGrid.prototype.onGridReady = function (params) {
    this.gridApi = params.api;
    this.columnApi = params.columnApi;

    if (this.ordersList != null){
    // make realistic - call in a batch
    let self = this;
   let rowData = JSON.parse(this.ordersList);
    this.gridApi.updateRowData({add: rowData});

    // select the first symbol to show the chart
    this.gridApi.getModel().getRow(0).setSelected(true);

    this.gridApi.sizeColumnsToFit();
    }

};

IrsTradeBlotterGrid.prototype.render = function (id) {
    // lookup the container we want the Grid to use
    let tGridDiv = document.querySelector('#' + id);
    // create the grid passing in the div to use together with the columns & data we want to use
    agGrid.LicenseManager.setLicenseKey("ag-grid_Evaluation_License_Not_For_Production_Devs25_October_2017__MTUwODg4NjAwMDAwMA==2825d0ac7ca94208a61e2f99b138d6b1");
    new agGrid.Grid(tGridDiv, this.gridOptions);

};

