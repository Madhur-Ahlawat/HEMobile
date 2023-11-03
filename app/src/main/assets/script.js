const gateway = Gateway.create('checkout_public_GYQ9D49Eq589t5BbEGQJ4R4QQ9Ame798');
const threeDS = gateway.get3DSecure();

document.addEventListener('DOMContentLoaded', function () {
    CollectJS.configure({
        "paymentSelector" : "#demoPayButton",
        "variant" : "inline",
        "styleSniffer" : "false",
        "customCss" : {
            "color": "#f0ffff",
            "background-color": "white",
            "width": "100px",
            "height" : "44PX",
            "text-align" : "",
            "box-sizing": "border-box",
            "border": "none",
            "border-bottom-style": "ridge",
            "font-size": "16px",
            "font-weight": "400",
            "line-height": " 1.25",
            "border-radius" : "0px",
            "padding": "1px"
        },
        "invalidCss": {
            "color": "black",
            "background-color": "white",
            "padding": "1px"
        },
        "validCss": {
            "color": "black",
            "background-color": "white",
            "width": "100px",
            "height" : "44PX",
            "text-align" : "",
            "box-sizing": "border-box",
            "border": "1",
            "border-bottom": "50PX solid red",
            "line-height": " 1.25",
            "padding": "1px"
        },
        "focusCss": {
            "color": "black",
            "background-color": "white",
            "width": "100px",
            "height" : "44PX",
            "text-align" : "",
            "box-sizing": "border-box",
            "border": "1",
            "border-bottom": "50PX solid red",
            "padding": "1px"
        },
        "placeholderCss": {
            "color": "#555B5A",
            "background-color": "#FFFFFF",
            "padding": "1px",
            "font-weight": "400",
            "line-height": " 1.25"
        },
        "fields": {
            "ccnumber": {
                "selector": "#ccNumber",
                "title": "Card Number",
                "placeholder": "0000 0000 0000 0000"
            },
            "ccexp": {
                "selector": "#ccExp",
                "title": "Card Expiration",
                "placeholder": "00 / 00"
            },
            "cvv": {
                "display": "required",
                "selector": "#cvv",
                "title": "CVV Code",
                "placeholder": "***"
            },
            "checkname": {
                "display": "show",
                "selector": "#name",
                "title": "Name on Checking Account",
                "placeholder": "Fred Bloggs"
            }
        },
        'validationCallback' : function(field, status, message) {
            if (status) {
                if  (field.localeCompare('ccnumber') == "0") {
                    document.getElementById("ccerrormesages").style.display = "none";
                } else  if (field.localeCompare('cvv') == "0") {
                    document.getElementById("cvverrormesages").style.display = "none";
                } else if (field.localeCompare('ccexp') == "0") {
                    document.getElementById("cscerrormesages").style.display = "none";
                } else if (field.localeCompare('checkname') == "0") {
                    document.getElementById("nameerrormesages").style.display = "none";
                }
            } else {
                var errorMessage = ""
                if (field.localeCompare('ccnumber')  == "0") {
                    if(message.localeCompare('Field is empty') == "0" ){
                        errorMessage = "Enter a card number";
                    } else if(message.localeCompare('Card number must be 13-19 digits and a recognizable card format') == "0") {
                        errorMessage = "Check the payment method and card number";
                    } else {
                        errorMessage = message;
                    }
                    document.getElementById("ccerrormesages").style.display = "";
                    document.getElementById("ccerrormesages").innerText = errorMessage;
                } else  if (field.localeCompare('cvv') == "0") {
                    if(message.localeCompare('Field is empty') == "0") {
                        errorMessage = "Enter the card security code";
                    } else if(message.localeCompare('CVV must be 3 or 4 digits') == "0") {
                        errorMessage = "Card security code must be 3 digits or more";
                    } else {
                        errorMessage = message;
                    }
                    document.getElementById("cvverrormesages").style.display = "";
                    document.getElementById("cvverrormesages").innerText = errorMessage;
                } else  if (field.localeCompare('ccexp') == "0") {
                    if(message.localeCompare('Field is empty') == "0") {
                        errorMessage = "Enter an expiry date";
                    } else if(message.localeCompare('Expiration date must be a present or future month and year') == "0") {
                        errorMessage = "Invalid date format";

                    } else {
                        errorMessage = message;
                    }
                    document.getElementById("cscerrormesages").style.display = "";
                    document.getElementById("cscerrormesages").innerText = errorMessage;
                } else if (field.localeCompare('checkname') == "0") {
                    if(message.localeCompare('Field is empty') == "0") {
                        errorMessage = "Enter the name on card";
                    } else if(message.localeCompare('Account owner\'s name should be at least 3 characters') == "0") {
                        errorMessage = "Name on card should be at least 3 characters";
                    } else {
                        errorMessage = message;
                    }
                    document.getElementById("nameerrormesages").style.display = "";
                    document.getElementById("nameerrormesages").innerText = errorMessage;
                }
            }

        },
        "timeoutDuration" : 10000,
        "timeoutCallback" : function () {
            window.appInterface.postMessage("timedOUt");
            document.getElementById("errorPayment").style.display="none";
        },
        "fieldsAvailableCallback" : function () {
            window.appInterface.postMessage("NMILoaded");
        },
        'callback': function(e) {
            window.appInterface.postMessage("3DSLoaded");
            var apiResponse = JSON.stringify(e, null, "");
            invokeCommand("NMi Callback",apiResponse)
//            window.appInterface.postMessage("NMi Callback"+ apiResponse);
            var card = e.card;
            var amt =  document.getElementById("amount").value;
            const pattern = /^(\w)[A-Za-z-\s\.']{2,50}$/i;
            var cardType = e.card.type

            if ((pattern.test(e.check.name) == false) || ((e.check.name) == null) || ((amt < 10) && (amt > 10000)) || (card.type == null) || (((card.type.localeCompare('visa') != "0") && (card.type.localeCompare('maestro') != "0") && (card.type.localeCompare('mastercard') != "0"))) || (amt == "") || (cardType == null)) {

                if (cardType == null) {
                    document.getElementById("ccerrormesages").style.display="";
                    document.getElementById("ccerrormesages").innerText="Payment method is incorrect";
                }

                if (pattern.test(e.check.name) == false) {
                    document.getElementById("nameerrormesages").style.display="";
                    document.getElementById("nameerrormesages").innerText = "The name on card must only include letters a to z, and special characters such as hyphens";
                }
                if (e.check.name == null) {
                    document.getElementById("nameerrormesages").style.display="";
                    document.getElementById("nameerrormesages").innerText =  "Enter the name on card";
                }
                if (amt == "") {
                    document.getElementById("errorMessageForAmount").style.display="";
                    document.getElementById("errorMessageForAmount").innerText = "Enter the amount";
                }
                if ((amt < 10) && (amt > 10000)) {
                    if (amt < 10) {
                        document.getElementById("errorMessageForAmount").style.display="";
                        document.getElementById("errorMessageForAmount").innerText = "Top-up amount must be 00A310 or more";
                    } else  if (amt > 10000) {
                        document.getElementById("errorMessageForAmount").style.display="";
                        document.getElementById("errorMessageForAmount").innerText = "Top-up amount must be 00A310,000 or less";
                    } else {
                        document.getElementById("errorMessageForAmount").style.display="none";
                    }
                }
            } else if (((card.type.localeCompare('visa') == "0") || (card.type.localeCompare('maestro') == "0") || (card.type.localeCompare('mastercard') == "0")) && (pattern.test(e.check.name)))
            {
                var amt =  document.getElementById("amount").value;
                //invokeCommand("amounttoIncrease",amt)
                window.appInterface.postMessage("amounttoIncrease"+ amt);
                window.appInterface.postMessage("3DStarted");
                document.getElementById("form1").style.display="none";
                const options = {
                paymentToken: e.token,
                currency:  document.getElementById("currency").innerText,
                amount:  document.getElementById("amount").value,
                email:  document.getElementById("email").innerText,
                phone:  document.getElementById("phone").innerText,
                city: document.getElementById("city").innerText,
                address1:  document.getElementById("address1").innerText,
                country:  "GB"/*document.getElementById("country").innerText*/,
                firstName: e.check.name,
                lastName:  e.check.name,
                postalCode:  document.getElementById("postalCode").innerText
                };
                window.appInterface.postMessage(options);
                const threeDSecureInterface = threeDS.createUI(options);
                threeDSecureInterface.start('#threeDSMountPoint');

                threeDSecureInterface.on('challenge', function(e) {
                    window.appInterface.postMessage("3DSLoaded");
                    window.appInterface.postMessage("3DStarted1");
                });

                threeDSecureInterface.on('complete', function(e) {
                    var apiResponse = JSON.stringify(e, null, "");
                    invokeCommand("threeDSecure",apiResponse)
                   // window.appInterface.postMessage(apiResponse);
                });

                threeDSecureInterface.on('failure', function(e) {
                    window.appInterface.postMessage("cancelClicked");
                });

                threeDSecureInterface.on('error', function(e) {
                    invokeCommand("errorMessage",JSON.stringify(e, null, ""))
                });
            } else {
                //errorPayment
                document.getElementById("ccerrormesages").style.display="";
                document.getElementById("ccerrormesages").innerText="Payment method is incorrect";
            }
        }
    });
    gateway.on('error', function (e) {
        window.appInterface.postMessage('3DSNotIntiated');
    })
});

function buttonClicked() {
    window.appInterface.postMessage("3DStarted");
}

function checkNumber() {
    var amt =  document.getElementById("amount").value;
    if (amt == "") {
        document.getElementById("errorMessageForAmount").style.display="";
        document.getElementById("errorMessageForAmount").innerText = "Enter the amount";
    } else if (amt < 10) {
        //errorMessageForAmount
        //Top-up amount must be £10,000 or less
        //Top-up amount must be £@ or more
        document.getElementById("errorMessageForAmount").style.display="";
        document.getElementById("errorMessageForAmount").innerText = "Top-up amount must be £10 or more";
    } else  if (amt > 10000) {
        document.getElementById("errorMessageForAmount").style.display="";
        document.getElementById("errorMessageForAmount").innerText = "Top-up amount must be £10,000 or less";
    } else {
        document.getElementById("errorMessageForAmount").style.display="none";
    }
}

function saveCardClick() {
    var checkBox = document.getElementById("cardChecked");
    invokeCommand("saveCardChecked",checkBox.checked)
}

function invokeCommand(action,params) {
    window.appInterface.postMessage(params);
}
