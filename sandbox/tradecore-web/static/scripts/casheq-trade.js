    function setStrValue(field) {
        document.getElementById("span" + field).innerHTML = sessionStorage.getItem(field);
        document.getElementById("hdn" + field).value = sessionStorage.getItem(field);
    }

    function setNumValue(field, precision) {
        var value = Number(sessionStorage.getItem(field)).toFixed(precision);
        document.getElementById("span" + field).innerHTML = value;
        if (field.match('askq')) {
            document.getElementById("quantity").value = value;
        } else if (field.match('ask')) {
            document.getElementById("price").value = value;
        }
    }

    function fnChangeCurrency(){
        let currency = document.getElementById("hedgeCurrency").value;
        let baseCurrency = document.getElementById("hdncurrencyCode").value;
        if(baseCurrency === 'USD'){
            baseCurrency = '';
        }
        if(currency === 'USD'){
            currency = '';
        }
        let exRate = '1.0000';
        if(baseCurrency != currency){
            exRate = sessionStorage.getItem(baseCurrency+currency);            
        }
        document.getElementById("hdnExchangeRate").innerHTML = exRate;
        document.getElementById("hdnSpotRate").value = exRate;
    }

    function onload() {
        document.getElementById("hdnUserName").value = sessionStorage.getItem("userName");
        
        setStrValue("symbol");
        setStrValue("currencyCode");
        setStrValue("exchange");
        setNumValue("chg", 2);
        setNumValue("open", 2);
        setNumValue("close", 2);
        setNumValue("high", 2);
        setNumValue("low", 2);
        setNumValue("price", 2);
        setNumValue("ask", 2);
        setNumValue("bid", 2);
        setNumValue("askq", 0);
        setNumValue("bidq", 0);
        fnSetOrderInd(sessionStorage.getItem("side"));
        //alert(JSON.stringify(ask, null, 4));
        if (Number(sessionStorage.getItem("price")) - Number(sessionStorage.getItem("close")) >= 0) {
            document.getElementById("spanchg").style.color = '#093';
        } else {
            document.getElementById("spanchg").style.color = '#d14836';
        }
    }

    function fnSetOrderInd(ind) {
        document.getElementById("hdnOrderInd").value = ind;
        if (ind.match('B')) {
            document.getElementById("quantity").value = Number(sessionStorage.getItem("bidq")).toFixed(0);
            document.getElementById("price").value = document.getElementById("spanbid").innerHTML;
            document.getElementById("btnBuySell").className  = "btn btn-lg btn-success";


        } else if (ind.match('S')) {
            document.getElementById("quantity").value = Number(sessionStorage.getItem("askq")).toFixed(0);
            document.getElementById("price").value = document.getElementById("spanask").innerHTML;
            document.getElementById("btnBuySell").className = "btn btn-lg btn-danger";
        } else {
            alert('Placing short orders would soon be available on TradeCore.');
        }
        return false;
    }