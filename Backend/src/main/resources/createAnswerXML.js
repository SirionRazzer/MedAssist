var xmlDoc = null;
var xmlDocStr = null;

// number of card-block classes used in html for other purposes
// used to determine number of questions
const NUM_OF_CB = 1;

createNewAnswer();
logXML();

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
    var tmpQuestion;
    for (var i = 0; i < numOfQuestions - NUM_OF_CB; i++) {
        tmpQuestion = document.getElementsByClassName("card-block")[i+1];
        var node = xmlDoc.createElement("answer");
        elements[0].appendChild(node);
        var x = xmlDoc.getElementsByTagName('answer');
        x[i].setAttribute("qid", tmpQuestion.getAttribute("id"));
        
        addValues(); // TODO
    }
}

/**
 * Adds all chosen values (== answers) for the current question
 */
function addValues() {
    // TODO
}