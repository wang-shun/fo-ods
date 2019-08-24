Date.isLeapYear = function (year) {
    return (((year % 4 === 0) && (year % 100 !== 0)) || (year % 400 === 0));
};

Date.getDaysInMonth = function (year, month) {
    return [31, (Date.isLeapYear(year) ? 29 : 28), 31, 30, 31, 30, 31, 31, 30, 31, 30, 31][month];
};

Date.prototype.isLeapYear = function () {
    return Date.isLeapYear(this.getFullYear());
};

Date.prototype.getDaysInMonth = function () {
    return Date.getDaysInMonth(this.getFullYear(), this.getMonth());
};

Date.prototype.addMonths = function (value) {
    var n = this.getDate();
    this.setDate(1);
    this.setMonth(this.getMonth() + value);
    this.setDate(Math.min(n, this.getDaysInMonth()));
    return this;
};

function getStrDate(date){
    
    const MONTHS = ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"];
    
    let day = date.getDate();
    if (Number(day) < 10) {
        day = '0' + day;
    }
    let strDate = day + '-' + (MONTHS[date.getMonth()]) + '-' + date.getFullYear();
    return strDate;
}

function fnOnLoad(){

    document.getElementById("hdnUserName").value = sessionStorage.getItem("userName");
    document.getElementById("hdnExchange").value = sessionStorage.getItem("exchangeCode");
    document.getElementById('txtStartDate').value = getStrDate(new Date());

}

function genNum(max, precision){    
    let pre = Math.floor(Math.random() * max)
    let post = Math.random();
    return Number(pre + post).toFixed(precision);
}

function fnDoChecks(){

    const CPTY = ["ABS Bank Plc", "ZBS Bank Plc", "RXS Bank Plc", "ASBC Bank Plc", "TNB Bank Plc", "TNT Bank Plc", "MBI Bank Plc", "LNB Bank Plc", "AIS Bank Plc", "MCH Bank Plc", "OOB Bank Plc"];
    const BRKR = ["Broker2352362", "Broker625255", "Broker83263532", "Broker723835", "Broker017242232", "Broker1623464", "Broker916333142", "Broker09353724", "Broker96242523", "Broker356684346", , "Broker87355523", "Broker92372666"];
    //const PROD = ["", ""];
    //const PROD_DESC = ["", ""];    
    const TERM = ["1W", "1M", "3M", "6M", "12M"];    
    const CLNG = ["ACS Bank", "LNC Bank", "RCS Bank", "XCS Bank", "LCH Bank", "NBS Bank", "CBS Bank", "NCS Bank", "PCS Bank", "ZCH Bank", "FNP Bank"];    
    const BOOK = ["CUSTOMER323", "CUSTOMER458", "CLIENT623", "CLIENT463", "BOOK2323", "BOOK6478", "DESK2352", "DESK7234", "FOLDER3555", "FOLDER6734", "DESK73562", "DESK4562352"];
    const RLDT = ["None", "Following", "Preceding", "Modified Following", "Modified Previous", "Following", "Preceding"];
    const CAL = ["LON", "LNY", "LON", "NYK", "LON", "LNY", "NYK"];

    //PayLeg
    //let payLegTerm = Math.floor(Math.random() * 5);
    //document.getElementById("cboPayLegTerm").selectedIndex = payLegTerm;
    //document.getElementById("cboPayLegTerm").value = TERM[payLegTerm];
    document.getElementById("txtPayLegRate").value = genNum(6,2);
    document.getElementById("cboPayLegBasis").selectedIndex = Math.floor(Math.random() * 11);
    if (document.getElementById("cboPayLegCcy").value != document.getElementById("cboRecLegCcy").value){
        document.getElementById("txtPayLegSpread").value = genNum(2, 2);
    }else{
        document.getElementById("txtPayLegSpread").value = "0.00";
    }

    //RecLeg
    //let recLegTerm = Math.floor(Math.random() * 5);
    //document.getElementById("cboRecLegTerm").value = TERM[recLegTerm];
    //document.getElementById("cboRecLegTerm").selectedIndex = recLegTerm;
    document.getElementById("txtRecLegRate").value = genNum(6, 2);
    document.getElementById("cboRecLegBasis").selectedIndex = Math.floor(Math.random() * 11);
    if (document.getElementById("cboPayLegCcy").value != document.getElementById("cboRecLegCcy").value) {
        document.getElementById("txtRecLegSpread").value = genNum(2, 2);
    } else {
        document.getElementById("txtRecLegSpread").value = "0.00";
    }    
    let product = document.getElementById("cboPayLegCcy").value + ' - ' + document.getElementById("cboRecLegCcy").value + ' - ' + document.getElementById("hdnTenor").value;
    product = product + ' : ' + document.getElementById("cboPayLegIdx").value + ' - ' + document.getElementById("cboPayLegTerm").value + ' SWAP'; 
    let product_desc = document.getElementById("cboPayLegCcy").value + ' - ' + document.getElementById("cboPayLegIdx").value + ' Swap';
    document.getElementById("txtCounterParty").value = CPTY[genNum(10,0)];
    document.getElementById("txtBroker").value = BRKR[genNum(10, 0)];
    document.getElementById("txtProduct").value = product;
    document.getElementById("txtProductDesc").value = product_desc;
    document.getElementById("txtClearing").value = CLNG[genNum(10, 0)];
    document.getElementById("txtTradeBook").value = BOOK[genNum(10, 0)];

    let payDay = genNum(8, 0);
    let recDay = genNum(8, 0);
    let payRD = genNum(5, 0);
    let recRD = genNum(5, 0);
    document.getElementById("txtPayPayFreq").value = document.getElementById("cboPayLegTerm").value;
    document.getElementById("txtPayPayDay").value = payDay;
    document.getElementById("txtPayPayRollDate").value = RLDT[payRD];
    document.getElementById("txtPayPayCal").value = CAL[payRD];
    document.getElementById("txtRecPayFreq").value = document.getElementById("cboRecLegTerm").value;
    document.getElementById("txtRecPayDay").value = recDay;
    document.getElementById("txtRecPayRollDate").value = RLDT[recRD];
    document.getElementById("txtRecPayCal").value = CAL[recRD];

    document.getElementById("txtPayResetFreq").value = document.getElementById("cboPayLegTerm").value;
    document.getElementById("txtPayResetDay").value = payDay;
    document.getElementById("txtPayResetRollDate").value = RLDT[payRD];
    document.getElementById("txtPayResetCal").value = CAL[payRD];
    document.getElementById("txtRecResetFreq").value = document.getElementById("cboRecLegTerm").value;
    document.getElementById("txtRecResetDay").value = recDay;
    document.getElementById("txtRecResetRollDate").value = RLDT[recRD];
    document.getElementById("txtRecResetCal").value = CAL[recRD];


}

function fnSetStartDate(){
    let startDate = document.getElementById('txtStartDate').value;
    if(startDate == ''){
    document.getElementById('txtStartDate').value = getStrDate(new Date());
    }
}

function fnCalcEndDate(){
    let tenure = document.getElementById('txtEndDate').value.toUpperCase();
    let startDate = document.getElementById('txtStartDate').value;
    if (tenure.endsWith('M')){
        document.getElementById("hdnTenor").value = tenure;
        tenure = tenure.replace('M','');
        var myDate = new Date(startDate);
        var endDate = myDate.addMonths(Number(tenure));
        document.getElementById('txtEndDate').value = getStrDate(endDate);
    } else if(tenure.endsWith('Y')) {
        document.getElementById("hdnTenor").value = tenure;
        tenure = tenure.replace('Y', '');
        var myDate = new Date(startDate);
        var endDate = myDate.addMonths(Number(tenure)*12);
        document.getElementById('txtEndDate').value = getStrDate(endDate);
    }
}
function numberWithCommas(x) {
    var parts = x.toString().split(".");
    parts[0] = parts[0].replace(/\B(?=(\d{3})+(?!\d))/g, ",");
    return parts.join(".");
}

function fnCalcAmount(field){
    let amount = document.getElementById('txt' + field).value.toUpperCase();
    let factor = 1;
    if (amount.endsWith('K')) {
        amount = amount.replace('K', '');
        factor = 1000;
    }else if (amount.endsWith('M')) {
        amount = amount.replace('M', '');
        factor = 1000000;
    } else if (amount.endsWith('B')) {
        amount = amount.replace('B', '');
        factor = 1000000000;
    } else if (amount.endsWith('H')) {
        amount = amount.replace('H', '');
        factor * 100;
    }
    document.getElementById('txt' + field).value = numberWithCommas(amount * factor);
}
