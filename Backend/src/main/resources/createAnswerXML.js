var xmlDoc = null;
var xmlDocStr = null;

/**
 * Number of card-block classes used in html for other purposes
 * Used to determine number of questions
 */
var NUM_OF_CB = 1;




createNewAnswer();
logXML();




/**
 * Log the output
 */
function logXML() {
    console.log(xmlDocStr);    
}

/**
 * Creates new xml document with root and root attribute only
 */
function createNewAnswer() {  
    // create xml document with root node
    var xmlString = "<slide></slide>";
    var parser = new DOMParser();
    xmlDoc = parser.parseFromString(xmlString, "text/xml");
        
    // add attribute to the slide element
    var elements = xmlDoc.getElementsByTagName("slide");
    var formSid = document.getElementsByTagName("h2")[0].getAttribute("id");
    var attr = xmlDoc.createAttribute("sid");
    attr.value = formSid;
    elements[0].setAttributeNode(attr);
    
    addAnswers();
    
    // convert xml to string
    var serializer = new XMLSerializer();
    xmlDocStr = serializer.serializeToString(xmlDoc);
}

/**
 * Adds all answers to the xml target
 */
function addAnswers() {
    var numOfQuestions = document.getElementsByClassName("card-block").length;
    var elements = xmlDoc.getElementsByTagName("slide"); // maybe redundant?
    console.log("Number of questions:", numOfQuestions - NUM_OF_CB);
    
    //cycle over all questions in form
    var tmpQuestion = document.getElementsByClassName("card-block");
    for (var i = 0; i < numOfQuestions - NUM_OF_CB; i++) {
        var answerElem = xmlDoc.createElement("answer");
        elements[0].appendChild(answerElem);
        var x = xmlDoc.getElementsByTagName('answer');
        x[i].setAttribute("qid", tmpQuestion[i+1].getAttribute("id"));
        
        addValues(answerElem, tmpQuestion[i+1], i);     
    }
}

function addValue(answerElem, text) {
    var value = xmlDoc.createElement('value');
    value.appendChild(text);
    answerElem.appendChild(value);
}

function addValues(answerElem, question, i) {
    var values = question.getElementsByClassName("form-group");

    //start_debug
    var type = values[0].getAttribute("type");
    var text = xmlDoc.createTextNode(type);
    addValue(answerElem, text);
    //end_debug
    
    switch (type) {
        case "checkbox": case "radio":
            var nl = question.getElementsByTagName("p");    
            for (var i = 0; i < nl.length; i++) {                                                            
                if (question.getElementsByTagName("input")[i].checked) {
                    var text = xmlDoc.createTextNode(nl[i].childNodes[0].nodeValue);
                    addValue(answerElem, text);
                }
            }
            break;
        case "textbox":
            //maybe doesn't work
            var text = xmlDoc.createTextNode(question.getElementsByTagName("textarea")[0].value);
            addValue(answerElem, text);
            break;
        case "date":
            //var text = xmlDoc.createTextNode(question.getElementsByTagName("date").value);
            //addValue(answerElem, text); 
            //TODO
            break;
        case "range":
            //TODO
            break;
        default:
            console.log("ERROR: Unknown type of question!");
    }
}