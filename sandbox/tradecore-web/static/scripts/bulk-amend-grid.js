function BulkAmendGrid() {

}


/*BulkAmendGrid.prototype.init = function (tradeCoreStoreService,
                                            stockBlotterPanel) {*/
BulkAmendGrid.prototype.init = function (param) {
    
    this.gridOptions = {
        onGridReady: this.onGridReady.bind(this),
        //onSelectionChanged: this.onSelectionChanged.bind(this),
        //getRowNodeId: this.getRowNodeId,
        enableCellChangeFlash: true,
        enableFilter: true,
        enableSorting: true,
        rowHeight: 30,
        enableColResize: true,
//        enableRangeSelection: true,
//        allowContextMenuWithControlKey: true,
        rowSelection: 'single',
        columnDefs: this.getColDefs(param)
    };
    console.log(param);
    var bulkAmendGrid = firebase.database().ref(param);
   
    bulkAmendGrid.on('value', function (snapshot) {
        //console.log(this);
        //console.log(this.ordersList);
        let data = snapshot.val();
        if(data){
            console.log(data);
            let updatedAmendsList = '[';
            Object.keys(data).forEach(function (key) {
                updatedAmendsList += data[key];
                updatedAmendsList += ',';
            })
            updatedAmendsList = updatedAmendsList.slice(0, -1);
            updatedAmendsList += ']';
            this.amendsList = updatedAmendsList;
            //console.log(this.ordersList);
            this.gridApi.setRowData(JSON.parse(this.amendsList));
            //this.gridApi.updateRowData({ add: JSON.parse(this.ordersList) });
        }
    }.bind(this));
};

BulkAmendGrid.prototype.getColDefs = function(param) {

    if (param === 'irdBulkAmendGrid/') {
        sessionStorage.setItem('bulkAmendType', 'IRSTRCP');        
        return [
            {
                field: 'bulkAmendId',
                headerName: 'Bulk Amend ID',
                filter: "text"
            }, 
            {
                field: 'bulkAmendType',
                headerName: 'Type',
                filter: "text"
            }, 
            {
                field: 'description',
                headerName: 'Description',
                filter: "text"
            }, 
            {
                field: 'status',
                headerName: 'Status',
                filter: "text"
            }, 
            {
                field: 'lastModifiedAt',
                headerName: 'Last Modified',
                filter: "text"
            },
            {
                field: 'bulkAmendId',
                headerName: 'Action',
                filter: "text",
                cellRenderer: this.detailRenderer
            }                
        ];

    } else if (param === 'eqtBulkAmendGrid/') {
        sessionStorage.setItem('bulkAmendType', 'EQTSPLT');        
        return [
            {
                field: 'bulkAmendId',
                headerName: 'Bulk Amend ID',
                filter: "text"
            }, 
            {
                field: 'bulkAmendType',
                headerName: 'Type',
                filter: "text"
            }, 
            {
                field: 'description',
                headerName: 'Description',
                filter: "text"
            }, 
            {
                field: 'status',
                headerName: 'Status',
                filter: "text"
            }, 
            {
                field: 'lastModifiedAt',
                headerName: 'Last Modified',
                filter: "text"
            },
            {
                field: 'bulkAmendId',
                headerName: 'Action',
                filter: "text",
                cellRenderer: this.detailRenderer
            }                
        ];

    } else if (param.startsWith('irdBulkAmendDetailsGrid/')){
        return [
            {
                field: 'tradeId',
                headerName: 'Trade ID',
                filter: "text"
            }, 
            {
                field: 'legNumber',
                headerName: 'Leg Number',
                filter: "text"
            }, 
            {
                field: 'book',
                headerName: 'Book',
                filter: "text"
            }, 
            {
                field: 'instrumentId',
                headerName: 'Instrument ID',
                filter: "text"
            }, 
            {
                field: 'notional',
                headerName: 'Notional',
                filter: "text"
            },
            {
                field: 'maturityDate',
                headerName: 'Maturity Date',
                filter: "text"
            }                
        ];

    }else if (param.startsWith('eqtBulkAmendDetailsGrid/')){
        return [
            {
                field: 'tradeId',
                headerName: 'Trade ID',
                filter: "text"
            }, 
            {
                field: 'legNumber',
                headerName: 'Leg Number',
                filter: "text"
            }, 
            {
                field: 'book',
                headerName: 'Book',
                filter: "text"
            }, 
            {
                field: 'instrumentId',
                headerName: 'Instrument ID',
                filter: "text"
            }, 
            {
                field: 'price',
                headerName: 'Price',
                filter: "text"
            },
            {
                field: 'quantity',
                headerName: 'Quantity',
                filter: "text"
            }                
        ];

    }
};

// eg Sep 4, 2017 4:06:07 PM gets converted to 20170904160607
function monthToComparableNumber(date) {
    if (date === undefined || date === null || date.length > 24) {
        return null;
    }

    const monthMap = new Map();
    monthMap.set('Jan', 1);
    monthMap.set('Feb', 2);
    monthMap.set('Mar', 3);
    monthMap.set('Apr', 4);
    monthMap.set('May', 5);
    monthMap.set('Jun', 6);
    monthMap.set('Jul', 7);
    monthMap.set('Aug', 8);
    monthMap.set('Sep', 9);
    monthMap.set('Oct', 10);
    monthMap.set('Nov', 11);
    monthMap.set('Dec', 12);

    let dateArray = date.split(' ');
    let dayNumber = dateArray[1].split(',')[0];
    let timeArray = dateArray[3].split(':');

    var monthNumber = monthMap.get(dateArray[0]);
    let yearNumber = dateArray[2];
    let hourNumber = timeArray[0];
    if (dateArray[4] == 'PM') {
        if (parseInt(hourNumber) < 12) {
            hourNumber = parseInt(hourNumber) + 12;
        }
    }
    let minNumber = timeArray[1];
    let secNumber = timeArray[2];

    var result = (yearNumber * 100000000) + (monthNumber * 1000000) + (dayNumber * 10000) + (hourNumber * 100) + (minNumber * 1) + (secNumber);
    return result;
};

BulkAmendGrid.prototype.dateComparator = function (date1, date2) {
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

BulkAmendGrid.prototype.numberFormatter = function (params) {
    if (typeof params.value === 'number') {
        return params.value.toFixed(2);
    } else {
        return params.value;
    }
};

BulkAmendGrid.prototype.quantityFormatter = function (params) {
    if (typeof params.value === 'number') {
        return params.value.toFixed(0);
    } else {
        return params.value;
    }
};

BulkAmendGrid.prototype.detailRenderer = function (param) {
    var eDiv = document.createElement('div');
    eDiv.innerHTML = '<span class="my-css-class"><button onclick="showBulkAction(\'' + param.value + '\')" class="btn btn-xs btn-info">Details</button>&nbsp;<button class="btn btn-xs btn-primary" onclick="executeBulkAction(\'' + param.value + '\')">Execute</button>&nbsp;<button class="btn btn-xs btn-danger" onclick="cancelBulkAction(\'' + param.value +'\')">Cancel</button></span>';
    //console.log(eDiv.innerHTML);
    var eButton1 = eDiv.querySelectorAll('.btn-simple')[0];
    var eButton2 = eDiv.querySelectorAll('.btn-simple')[1];
    var eButton3 = eDiv.querySelectorAll('.btn-simple')[2];
    return eDiv;
};

function showBulkAction (params){
    sessionStorage.setItem('bulkAmendId', params);
    window.open('/bulk-amend-detail.html', 'tcgriddtls', 'directories=no,titlebar=no,toolbar=no,location=no,status=no,menubar=no,scrollbars=no,resizable=no,width=1300,height=650');
    console.log('showing' + params);   
    return false;
};

function executeBulkAction (params) {
    sessionStorage.setItem('bulkAmendId', params);
    document.getElementById('hdnBulkAmendId').value = params;
    document.getElementById('hdnBulkAmendType').value = sessionStorage.getItem('bulkAmendType');
    document.getElementById('hdnBulkAmendAction').value = 'EXEC';
    console.log('exec' + params);
    return false;

};

function cancelBulkAction (params) {
    sessionStorage.setItem('bulkAmendId', params);
    document.getElementById('hdnBulkAmendId').value = params;
    document.getElementById('hdnBulkAmendType').value = sessionStorage.getItem('bulkAmendType');   
    document.getElementById('hdnBulkAmendAction').value = 'CANC';    
    console.log('cancel' + params);
    return false;
};



BulkAmendGrid.prototype.onGridReady = function (params) {
    this.gridApi = params.api;
    this.columnApi = params.columnApi;

    if (this.amendsList != null){
    // make realistic - call in a batch
    let self = this;
    let rowData = JSON.parse(this.amendsList);
    this.gridApi.updateRowData({add: rowData});

    // select the first symbol to show the chart 
    this.gridApi.getModel().getRow(0).setSelected(true);

    this.gridApi.sizeColumnsToFit();
    }

};

BulkAmendGrid.prototype.render = function (id) {
    // lookup the container we want the Grid to use
    let tGridDiv = document.querySelector('#' + id);
    // create the grid passing in the div to use together with the columns & data we want to use
    agGrid.LicenseManager.setLicenseKey("ag-grid_Evaluation_License_Not_For_Production_Devs25_October_2017__MTUwODg4NjAwMDAwMA==2825d0ac7ca94208a61e2f99b138d6b1");
    new agGrid.Grid(tGridDiv, this.gridOptions);

};
