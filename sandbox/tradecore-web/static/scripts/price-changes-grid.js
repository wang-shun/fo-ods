function PriceChangesGrid() {
}

PriceChangesGrid.prototype.init = function (exchangeService,
                                            stockDetailPanel,exchangeCode) {
    this.stockDetailPanel = stockDetailPanel;

    this.exchangeService = exchangeService;
    this.exchangeService.init();

    this.selectedExchange = this.exchangeService.getExchangeInformation(exchangeCode);

    this.gridOptions = {
        onGridReady: this.onGridReady.bind(this),
        onSelectionChanged: this.onSelectionChanged.bind(this),
        getRowNodeId: this.getRowNodeId,
        enableSorting: true,
        enableFilter: true,
        enableRangeSelection: true,
        enableColResize: true,
        getContextMenuItems: getContextMenuItems,
        allowContextMenuWithControlKey: true,
        //suppressHorizontalScroll: true,
        rowSelection: 'single',
        columnDefs: [
            {
                field: 'symbol',
                headerName: 'Symbol',
                filter: "text",
                sort: 'asc'
            },
            {
                field: 'price',
                headerName: 'LTP',
                valueFormatter: this.numberFormatter,
                cellRenderer: 'animateShowChange',
                cellStyle: {'text-align': 'right'},
                filter: "number"
            },
            {
                field: 'bidq',
                headerName: 'Size',
                valueFormatter: this.quantityFormatter,
                cellRenderer: 'animateShowChange',
                cellStyle: {'text-align': 'right'},
                filter: "number"                
            },            
            {
                field: 'bid',
                headerName: 'Bid',
                valueFormatter: this.numberFormatter,
                cellRenderer: 'animateShowChange',
                cellStyle: {'text-align': 'right'},
                filter: "number"                
            },
            {
                field: 'ask',
                headerName: 'Ask',
                valueFormatter: this.numberFormatter,
                cellRenderer: 'animateShowChange',
                cellStyle: {'text-align': 'right'},
                filter: "number"                
            },
            {
                field: 'askq',
                headerName: 'Size',
                valueFormatter: this.quantityFormatter,
                cellRenderer: 'animateShowChange',
                cellStyle: {'text-align': 'right'},
                filter: "number"                
            }
        ]
    };
};


function openOrderForm(data,side){
sessionStorage.setItem("symbol", data.symbol);
sessionStorage.setItem("price", data.price);
sessionStorage.setItem("bid", data.bid);
sessionStorage.setItem("ask", data.ask);
sessionStorage.setItem("bidq", data.bidq);
sessionStorage.setItem("askq", data.askq);
sessionStorage.setItem("open", data.open);
sessionStorage.setItem("close", data.close);
sessionStorage.setItem("high", data.high);
sessionStorage.setItem("low", data.low);
sessionStorage.setItem("chg", data.chg);
sessionStorage.setItem("side", side);
sessionStorage.setItem("exchange", document.getElementById('cboExchangeCode').value);
 window.open('/casheq-trade.html','order','directories=no,titlebar=no,toolbar=no,location=no,status=no,menubar=no,scrollbars=no,resizable=no,width=1000,height=670');
}

function openIRSBookingForm(){
    sessionStorage.setItem("exchange", document.getElementById('cboExchangeCode').value);
    window.open('/irs-trade.html', 'order', 'directories=no,titlebar=no,toolbar=no,location=no,status=no,menubar=no,scrollbars=no,resizable=no,width=1200,height=810');
}

function openBulkAmendGrid(bulkAmendType) {
    sessionStorage.setItem("fullGridRef", bulkAmendType+'BulkAmendGrid/');
    window.open('/bulk-amend-grid.html', 'tcgrid', 'directories=no,titlebar=no,toolbar=no,location=no,status=no,menubar=no,scrollbars=no,resizable=no,width=1300,height=650');
}

function initiateBulkAmend(bulkAmendType){
    sessionStorage.setItem("initiateBulkAmendType", bulkAmendType);  
    if(bulkAmendType === 'IRSTRCP')
    {
        window.open('/bulk-amend-initiate.html', 'iba', 'directories=no,titlebar=no,toolbar=no,location=no,status=no,menubar=no,scrollbars=no,resizable=no,width=600,height=400');   
        return false;     
    }
    else if(bulkAmendType === 'EQTSPLT'){
        window.open('/bulk-amend-initiate.html', 'iba', 'directories=no,titlebar=no,toolbar=no,location=no,status=no,menubar=no,scrollbars=no,resizable=no,width=600,height=400');                
        return false;        
    }
    alert('Bulk Amend for this type is not yet implemented.');
}

function getContextMenuItems(params) {
    //alert(JSON.stringify(params, null, 4));
    var result = [
        { // custom item
            //name: 'Book '+params.value,
            name: 'Book '+params.node.data.symbol,
            //cssClasses: ['bold'],
            action: function () {
                //var dataItem = node.data
                //alert(JSON.stringify(params, null, 4));
               openOrderForm(params.node.data, 'S');
            }
        }, 
        'separator',
        {
            name: 'Trade',
            subMenu: [
                {name: 'Buy', action: function() {openOrderForm(params.node.data, 'B');} },
                {name: 'Sell', action: function() {openOrderForm(params.node.data, 'S');} }
            ]
        }, // built in separator
        'separator',// built in copy item
        'copy'
    ];
    return result;
}

PriceChangesGrid.prototype.numberFormatter = function (params) {
    if (typeof params.value === 'number') {
        return params.value.toFixed(2);
    } else {
        return params.value;
    }
};

PriceChangesGrid.prototype.quantityFormatter = function (params) {
    if (typeof params.value === 'number') {
        return params.value.toFixed(0);
    } else {
        return params.value;
    }
};

PriceChangesGrid.prototype.onGridReady = function (params) {
    this.gridApi = params.api;
    this.columnApi = params.columnApi;

    // make realistic - call in a batch
    let self = this;
    let rowData = _.map(this.selectedExchange.supportedStocks, function (symbol) {
        return self.exchangeService.getTicker(symbol);
    });
    this.gridApi.updateRowData({add: rowData});

    // select the first symbol to show the chart
    this.gridApi.getModel().getRow(0).setSelected(true);

    this.selectedExchange.supportedStocks.forEach(function (symbol) {
        self.exchangeService.addSubscriber(self.updateSymbol.bind(self), symbol);
    });

    this.gridApi.sizeColumnsToFit();
};

PriceChangesGrid.prototype.getRowNodeId = function (data) {
    return data.symbol;
};

PriceChangesGrid.prototype.onSelectionChanged = function () {
    let selectedNode = this.gridApi.getSelectedNodes()[0];
    this.stockDetailPanel.update(this.selectedExchange, selectedNode.data.symbol)
};

PriceChangesGrid.prototype.updateData = function (nextProps) {
    if (nextProps.selectedExchange.supportedStocks !== this.selectedExchange.supportedStocks) {
        if (!this.gridApi) {
            return;
        }
        this.gridApi.deselectAll();

        const currentSymbols = this.selectedExchange.supportedStocks;
        const nextSymbols = nextProps.selectedExchange.supportedStocks;

        // Unsubscribe to current ones that will be removed
        let self = this;
        const symbolsRemoved = _.difference(currentSymbols, nextSymbols);
        _.forEach(symbolsRemoved, function (symbol) {
            self.exchangeService.removeSubscriber(self.updateSymbol, symbol);
        });

        // Remove ag-grid nodes as necessary
        const rowsToRemove = [];
        this.gridApi.forEachNode(function (node) {
            data = node.dat;
            if (includes(symbolsRemoved, data.symbol)) {
                rowsToRemove.push(data);
            }
        });
        this.gridApi.updateRowData({remove: rowsToRemove});

        // Subscribe to new ones that need to be added
        const symbolsAdded = _.difference(nextSymbols, currentSymbols);
        _.forEach(symbolsAdded, function (symbol) {
            self.exchangeService.addSubscriber(self.updateSymbol, symbol);
        });

        // Insert new ag-grid nodes as necessary
        let rowData = _.map(symbolsAdded, function (symbol) {
            return self.exchangeService.getTicker(symbol)
        });
        this.gridApi.updateRowData({add: rowData});

        // select the first symbol to show the chart
        this.gridApi.getModel().getRow(0).setSelected(true);
    }
};

PriceChangesGrid.prototype.updateSymbol = function (symbol) {
    if (!this.gridApi) {
        // the grid isn't ready yet
        return;
    }

    this.gridApi.updateRowData({update: [symbol]});
};

PriceChangesGrid.prototype.render = function (id) {
    // lookup the container we want the Grid to use
    let eGridDiv = document.querySelector('#' + id);

    // create the grid passing in the div to use together with the columns & data we want to use
    // new agGrid.Grid(tGridDiv, this.gridOptions);
    //let agGrid = new agGrid.Grid(eGridDiv, this.gridOptions);
   // agGrid.LicenseManager.setLicenseKey("ag-grid_Evaluation_License_Not_For_Production_Devs25_October_2017__MTUwODg4NjAwMDAwMA==2825d0ac7ca94208a61e2f99b138d6b1");
    //new agGrid.Grid(eGridDiv, this.gridOptions).LicenseManager.setLicenseKey("ag-grid_Evaluation_License_Not_For_Production_Devs25_October_2017__MTUwODg4NjAwMDAwMA==2825d0ac7ca94208a61e2f99b138d6b1");
    agGrid.LicenseManager.setLicenseKey("ag-grid_Evaluation_License_Not_For_Production_Devs25_October_2017__MTUwODg4NjAwMDAwMA==2825d0ac7ca94208a61e2f99b138d6b1");
    new agGrid.Grid(eGridDiv, this.gridOptions);

    //alert(this.gridApi.getSelectedNodes()[0].data.symbol);
};