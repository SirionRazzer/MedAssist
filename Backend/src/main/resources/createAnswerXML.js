var xmlDoc = null;
var xmlDocStr = null;

/**
 * Number of card-block classes used in html for other purposes
 * Used to determine number of questions
 */
var NUM_OF_CB = 1;

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
    var xmlString = "<answers></answers>";
    var parser = new DOMParser();
    xmlDoc = parser.parseFromString(xmlString, "text/xml");
        
    // add attribute to the slide element
    var elements = xmlDoc.getElementsByTagName("answers");
    
    var slides = document.getElementsByTagName("h2");
    
    //for each slide create slide element
    for (var i = 0; i < slides.length; i++) {
        var slideId = document.getElementsByTagName("h2")[i].getAttribute("id");
        var slideElem = xmlDoc.createElement("slide");
        var slideAttr = xmlDoc.createAttribute("sid");
        slideAttr.value = slideId;
        elements[0].appendChild(slideElem);
        var x = xmlDoc.getElementsByTagName("slide");
        x[i].setAttribute("sid", slideAttr.value);

        //add answers for this slide
        addAnswers(slideElem, i);
    }

    // convert xml to string
    var serializer = new XMLSerializer();
    xmlDocStr = serializer.serializeToString(xmlDoc);
    logXML();
}

/**
 * Add answers for current slide
 * @param {type} slideElem is xml element for which we will add answers
 * @param {type} i is number of current slide we want to read from
 */
function addAnswers(slideElem, i) {
    //locate all slides
    var slides = document.getElementsByClassName("slide-wrapper");
    //get questions from current slide
    var questions = slides[i].getElementsByClassName("card-block");

    for (var i = 0; i < questions.length - NUM_OF_CB; i++) {
        var answerElem = xmlDoc.createElement("answer");
        slideElem.appendChild(answerElem);
        var x = slideElem.getElementsByTagName('answer');
        x[i].setAttribute("qid", questions[i+1].getAttribute("id"));
        addValues(answerElem, questions[i+1], i);
    }   
}

/**
 * Add value element to the answerElem
 * @param answerElem is target answer element where the values will be stored
 * @param text is text node (!) which will be added to the answer element
 */
function addValue(answerElem, text) {
    var value = xmlDoc.createElement('value');
    value.appendChild(text);
    answerElem.appendChild(value);
}

/**
 * Add all answers for the current question to the answerElem
 * @param answerElem is the element where the values will be stored
 * @param question is the question from which we want to get answer values
 */
function addValues(answerElem, question) {
    var values = question.getElementsByClassName("form-group");

    var type = values[0].getAttribute("type");
    
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
            var text = xmlDoc.createTextNode(question.getElementsByTagName("textarea")[0].value);
            addValue(answerElem, text);
            break;
        case "date":
            var text = xmlDoc.createTextNode(question.getElementsByTagName("input")[0].value);
            addValue(answerElem, text); 
            break;
        case "range":
            var text = xmlDoc.createTextNode(question.getElementsByTagName("input")[0].value);
            addValue(answerElem, text); 
            break;
        default:
            console.log("ERROR: Unknown type of question!");
    }
}