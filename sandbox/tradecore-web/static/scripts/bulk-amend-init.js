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

window.onload = function() {
    let bulkAmendType = sessionStorage.getItem("initiateBulkAmendType");
    document.getElementById('txtAsOfDate').value = getStrDate(new Date());
    document.getElementById('splitBulkAmendInitiate').style.display = 'none';
    document.getElementById('tcBulkAmendInitiate').style.display = 'none';

    document.getElementById("hdnUserName").value = sessionStorage.getItem("userName");
    document.getElementById('hdnBulkAmendType').value = bulkAmendType;
    
    if(bulkAmendType === 'IRSTRCP'){
        document.getElementById('tcBulkAmendInitiate').style.display = 'block';
    } else if (bulkAmendType === 'EQTSPLT'){
        document.getElementById('splitBulkAmendInitiate').style.display = 'block';        
    } else {
        alert('This bulk amend is not yet implemented');
    }
};

function fnCalcAsOfate(){
    let tenure = document.getElementById('txtAsOfDate').value.toUpperCase();
    let startDate = getStrDate(new Date());
    if (tenure.endsWith('M')){
        document.getElementById('hdnTenor').value = tenure;
        tenure = tenure.replace('M','');
        var myDate = new Date(startDate);
        var endDate = myDate.addMonths(Number(tenure));
        document.getElementById('txtAsOfDate').value = getStrDate(endDate);
    } else if(tenure.endsWith('Y')) {
        document.getElementById("hdnTenor").value = tenure;
        tenure = tenure.replace('Y', '');
        var myDate = new Date(startDate);
        var endDate = myDate.addMonths(Number(tenure)*12);
        document.getElementById('txtAsOfDate').value = getStrDate(endDate);
    }
};

function getStrDate(date){
    
    const MONTHS = ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"];
    
    let day = date.getDate();
    if (Number(day) < 10) {
        day = '0' + day;
    }
    let strDate = day + '-' + (MONTHS[date.getMonth()]) + '-' + date.getFullYear();
    return strDate;
};