function StockDetailPanel() {
}

StockDetailPanel.prototype.init = function (exchangeService, stockHistoricalChart) {
    this.stockHistoricalChart = stockHistoricalChart;
    this.exchangeService = exchangeService;

    this.selectedSymbol = null;
    this.priceDelta = null;
    this.timestamp = null;
    this.tickerSummary = null;
    this.historicalData = null;

    this.deltaPanelScriptTarget = document.getElementById("deltaPanelScript");
    this.deltaPanelPriceTarget = document.getElementById("deltaPanelPrice");
    this.deltaPanelSwingTarget = document.getElementById("deltaPanelSwing");
    this.deltaPctPanelSwingTarget = document.getElementById("deltaPctPanelSwing");

    this.timestampTarget = document.getElementById("timestamp");
    this.exchangeNameTarget = document.getElementById("exchangeName");

    this.rangeTarget = document.getElementById("range");
    this.fiftyTwoWeekTarget = document.getElementById("fiftyTwoWeek");
    this.openTarget = document.getElementById("open");
    this.volTarget = document.getElementById("vol");
    this.dividendYieldTarget = document.getElementById("dividendYield");
    this.epsTarget = document.getElementById("eps");
    this.sharesTarget = document.getElementById("shares");
    this.marketCapTarget = document.getElementById("marketCap");
};

StockDetailPanel.prototype.numberFormatter = function (input) {
    return input ? parseFloat(input).toFixed(2) : null;
};

StockDetailPanel.prototype.update = function (selectedExchange, selectedSymbol) {
    this.stockDetail = this.exchangeService.getTickerDetail(selectedSymbol);

    this.selectedSymbol = selectedSymbol;
    this.exchangeName = selectedExchange.name;
    this.pricingDelta = this.stockDetail.pricingDelta;
    this.tickerSummary = this.stockDetail.tickerSummary;
    this.historicalData = this.stockDetail.historicalData;
    this.delta = this.pricingDelta.currentPrice - this.pricingDelta.previousPrice;
    this.deltaPercentage = (this.pricingDelta.currentPrice - this.pricingDelta.previousPrice) / this.pricingDelta.currentPrice;

    this.updateGui();

    this.stockHistoricalChart.renderGraph(this.historicalData);
};

StockDetailPanel.prototype.updateGui = function () {
    let negativeSwingStyle = 'color: #; margin-right: 5px';
    let positiveSwingStyle = 'color: #093; margin-right: 5px';
    let swingPositive = this.delta >= 0;

    // delta panel
    //alert(JSON.stringify(this.selectedSymbol, null , 4));
    this.deltaPanelScriptTarget.innerText = this.selectedSymbol;
    this.deltaPanelPriceTarget.innerText = this.numberFormatter(this.pricingDelta.currentPrice);

    this.deltaPanelSwingTarget.innerText = this.numberFormatter(this.delta);

    // painful...ie11 you suck
    if(swingPositive) {
        this.deltaPanelSwingTarget.style.color = '#093';
    } else {
        this.deltaPanelSwingTarget.style.color = '#d14836';
    }
    this.deltaPanelSwingTarget.style.marginRight = '5px';


    this.deltaPctPanelSwingTarget.innerText = "(" + this.numberFormatter(this.deltaPercentage) + "%)";
    // this.deltaPctPanelSwingTarget.style = swingStyle;

    // timestamp
    this.timestampTarget.innerText = this.stockDetail.timestamp;
    this.exchangeNameTarget.innerText = this.exchangeName;

    // summary panel
    this.rangeTarget.innerText = this.tickerSummary.range;
    this.fiftyTwoWeekTarget.innerText = this.tickerSummary.fiftyTwoWeek;
    this.openTarget.innerText = this.tickerSummary.open;
    this.volTarget.innerText = this.tickerSummary.vol + "/" + this.tickerSummary.avg;
    this.dividendYieldTarget.innerText = this.tickerSummary.dividend + "/" + this.tickerSummary.yld;
    this.epsTarget.innerText = this.tickerSummary.eps;
    this.sharesTarget.innerText = this.tickerSummary.shares;
    this.marketCapTarget.innerText = this.tickerSummary.marketCap;

};