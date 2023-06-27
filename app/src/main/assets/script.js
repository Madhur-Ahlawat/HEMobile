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
            "font-family": "opensans_regular",
            "font-size": "16px",
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
            "font-family": "opensans_regular",
            "font-size": "16px",
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
            "font-family": "opensans_regular",
            "font-size": "22",
            "padding": "1px"
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
                "display": "show",
                "selector": "#cvv",
                "title": "CVV Code",
                "placeholder": "***"
            },
            "checkname": {
                "display": "show",
                "selector": "#name",
                "title": "Name on Checking Account",
                "placeholder": "Name",
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
                        errorMessage = "Card number must be 16 digits or more";
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
                      errorMessage = "Expiry date cannot be in the past";

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
            window.observer.postMessage("timedOUt");
            document.getElementById("errorPayment").style.display="";
            document.getElementById("errorPayment").innerText="Timed out";
        },
        "fieldsAvailableCallback" : function () {
            window.observer.postMessage("NMILoaded");
        },
        'callback': function(e) {
            window.observer.postMessage("3DSLoaded");
            var apiResponse = JSON.stringify(e, null, "");
            window.observer.postMessage("NMi Callback"+ apiResponse);
            var card = e.card;
            if ((card.type.localeCompare('visa') == "0") || (card.type.localeCompare('maestro') == "0") || (card.type.localeCompare('mastercard') == "0"))
            {
                window.observer.postMessage("3DStarted");
                document.getElementById("form1").style.display="none";
                const options = {
                paymentToken: e.token,
                currency:  document.getElementById("currency").innerText,
                amount:  document.getElementById("amount").value,
                email:  document.getElementById("email").innerText,
                phone:  document.getElementById("phone").innerText,
                city: document.getElementById("city").innerText,
                    //state: '10 address street',
                address1:  document.getElementById("address1").innerText,
                country:  document.getElementById("country").innerText,
                firstName:  document.getElementById("name").value,
                lastName:  document.getElementById("name").value,
                postalCode:  document.getElementById("postalCode").innerText
                };
                window.observer.postMessage(options);
                const threeDSecureInterface = threeDS.createUI(options);
                threeDSecureInterface.start('#threeDSMountPoint');
                
                threeDSecureInterface.on('challenge', function(e) {
                    window.observer.postMessage("3DSLoaded");
                });
                
                threeDSecureInterface.on('complete', function(e) {
                    var apiResponse = JSON.stringify(e, null, "");
                    window.observer.postMessage(apiResponse);
                });
                
                threeDSecureInterface.on('failure', function(e) {
                    window.observer.postMessage("cancelClicked");
                });
            } else {
                //errorPayment
                document.getElementById("errorPayment").style.display="";
                document.getElementById("errorPayment").innerText="Payment method is incorrect";
                window.observer.postMessage('cardtypeerror');
            }
        }
    });
    gateway.on('error', function (e) {
        window.observer.postMessage('3DSNotIntiated');
    })
});

function buttonClicked() {

    window.observer.postMessage("3DStarted");
//    window.opener.postMessage("3DStarted");
}



