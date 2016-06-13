var xmlDoc = null;
var form = null;
var slides = null;
var tags = null;
var currentSlide = null;
var currentQuestion = null;
var currentTag = null;

function logXML() {
    console.log(xmlDoc);
}

//Form

/**
 * Creates new form element
 * 
 * @param {string} name form's name
 * @returns {form} form element
 */
function createNewForm(name) {
    var xmlString = "<form></form>";
    var parser = new DOMParser();
    xmlDoc = parser.parseFromString(xmlString, "text/xml"); //important to use "text/xml"
    form = getFormElement();

    setFormName(name);

    slides = xmlDoc.createElement("slides");
    form.appendChild(slides);

    tags = xmlDoc.createElement('tags');
    form.appendChild(tags);

    return form;
}

/**
 * Returns root form element
 * 
 * @returns form node
 */
function getFormElement() {
    return xmlDoc.getElementsByTagName('form')[0];
}

/**
 * Sets name of the form 
 * 
 * @param {string} name Form's name
 * @returns {void}
 */
function setFormName(name) {
    appendNameToElement(form, name);
}

// Tags
/**
 * Creates new tag
 * 
 * @param {string} name name/text of the tag
 * @returns {tag} tag element
 */
function addTag(name) {
    if (form) {
        var tag = xmlDoc.createElement('tag');
        appendNameToElement(tag, name);
        tags.appendChild(tag);
        currentTag = tag;
        return tag;
    }
}

//Slides

/**
 * Adds slide to form
 * 
 * @param {string} name name of the slide
 * @param {boolean} dependency dependency parameter
 * @returns {node} slide node
 */
function addSlide(name, dependency) {
    var slide = xmlDoc.createElement('slide');
    slide.setAttribute('sid', getNumberOfSlides());

    if (dependency) {
        slide.setAttribute('dependency', 'true');
        var dependencyAnswers = xmlDoc.createElement('dep_answers')
        slide.appendChild(dependencyAnswers);
    } else {
        slide.setAttribute('dependency', 'false')
    }

    appendNameToElement(slide, name);
    slides.appendChild(slide);

    setCurrentSlide(slide);
    return slide;
}

/**
 * Returns x'th slide element of the form
 * 
 * @param {Number} x slide's position
 * @returns slide node
 */
function getSlide(x) {
    return xmlDoc.getElementsByTagName('slide')[x];
}

/**
 * Returns slide by slide ID
 * 
 * @param {int} sid slide ID
 * @returns {slide} slide element
 */
function getSlideBySid(sid) {
    var slides = xmlDoc.getElementsByTagName('slide');
    for (var i = 0; i < slides.length; i++) {
        if (slides[i].getAttribute('sid') == sid) {
            return slides[i];
        }
    }
}

/**
 * Sets current slide 
 * 
 * @param {type} slide slide to be set as current
 * @returns {void}
 */
function setCurrentSlide(slide) {
    currentSlide = slide;
    currentQuestion = null;
}

/**
 * Returns number of slides in the form
 * 
 * @returns {Number} number of slides in form
 */
function getNumberOfSlides() {
    return xmlDoc.getElementsByTagName('slide').length;
}

//Questions

/**
 * Adds new question to slide
 * 
 * @param {slide} slide slide element, where question will be added
 * @param {string} type question type 
 * @param {string} text question text
 * @returns {question} new question element
 */
function addQuestion(slide, type, text) {
    var question = xmlDoc.createElement('question');

    // text of the question
    var textNode = xmlDoc.createElement('text');
    textNode.appendChild(xmlDoc.createTextNode(text));
    question.appendChild(textNode);

    // attributes of the question
    question.setAttribute('qid', getNumberOfQuestions());
    question.setAttribute('type', type);

    slide.appendChild(question);
    currentQuestion = question;

    return question;
}

/**
 * Returns question specified by ID
 * 
 * @param {int} qid question's ID
 * @returns {question} question node with specified ID
 */
function getQuestionById(qid) {
    var questions = xmlDoc.getElementsByTagName('question');
    for (var i = 0; i < questions.length; i++) {
        if (questions[i].getAttribute('qid') == qid) {
            return questions[i];
        }
    }
}

/**
 * Sets options for specified question
 * 
 * @param {question} question question node
 * @param {array} options options texts
 * @returns {question} updated question node
 */
function setOptions(question, options) {
    if (question.getAttribute('type') === 'checkbox' || question.getAttribute('type') === 'radiobutton') {
        var optionsNode = xmlDoc.createElement('options');
        for (var i = 0; i < options.length; i++) {
            var option = xmlDoc.createElement('option');
            var optionsText = xmlDoc.createTextNode(options[i]);
            option.appendChild(optionsText);
            optionsNode.appendChild(option);
        }
        question.appendChild(optionsNode);
    }

    return question;
}

/**
 * Sets range for range question
 * 
 * @param {question} question question node
 * @param {type} min range minimum
 * @param {type} max range maximum
 * @param {type} step range step
 * @returns {question} question node
 */
function setRange(question, min, max, step) {
    if (question.getAttribute('type') === 'range') {
        var minNode = xmlDoc.createElement('min_val');
        minNode.appendChild(xmlDoc.createTextNode(min));

        var maxNode = xmlDoc.createElement('max_val');
        maxNode.appendChild(xmlDoc.createTextNode(max));

        var stepNode = xmlDoc.createElement('step');
        stepNode.appendChild(xmlDoc.createTextNode(step));

        question.appendChild(minNode);
        question.appendChild(maxNode);
        question.appendChild(stepNode);
    }

    return question;
}

/**
 * Returns number of questions in form
 *   
 * @returns {int} number of question nodes
 */
function getNumberOfQuestions() {
    return xmlDoc.getElementsByTagName('question').length;
}

// Dependency answers
/**
 * Adds exact value dependency to node
 * 
 * @param {Node} parentNode parent node, where dependency will be added
 * @param {int} qid question ID
 * @param {array} values values to be set as dependency
 * @returns {answer} answer node
 */
function addExactValueDependency(parentNode, qid, values) {
    var answer = xmlDoc.createElement('answer');
    answer.setAttribute('qid', qid);
    answer.setAttribute('type', 'exact_value');

    var valuesNode = xmlDoc.createElement('values');
    for (var i = 0; i < values.length; i++) {
        var valueNode = xmlDoc.createElemenet('value');
        var valueText = xmlDoc.createTextNode(values[i]);
        valueNode.appendChild(valueText);
        valuesNode.appendChild(valueNode);
    }

    answer.appendChild(valuesNode);
    parentNode.appendChild(answer);

    return answer;
}

/**
 * Adds range value dependency to node
 * 
 * @param {Node} parentNode parent node, where dependency will be added
 * @param {int} qid question ID
 * @param {int} minValue minimum of dependency range
 * @param {int} maxValue maximum of dependency range
 * @returns {answer} answer node
 */
function addRangeDependency(parentNode, qid, minValue, maxValue) {
    var answer = xmlDoc.createElement('answer');
    answer.setAttribute('qid', qid);
    answer.setAttribute('type', 'range');

    var minNode = xmlDoc.createElement('min_value');
    minNode.appendChild(xmlDoc.createTextNode(minValue));
    var maxNode = xmlDoc.createElement('max_value');
    maxNode.appendChild(xmlDoc.createTextNode(maxValue));

    answer.appendChild(minNode);
    answer.appendChild(maxNode);
    parentNode.appendChild(answer);

    return answer;
}


//Common
/**
 * Adds name elemenent to specified node
 * 
 * @param {Node} element element to which the name is added
 * @param {string} name name of the element
 * @returns {void}
 */
function appendNameToElement(element, name) {
    var nameElement = xmlDoc.createElement('name');
    var text = xmlDoc.createTextNode(name);

    nameElement.appendChild(text);
    element.appendChild(nameElement)
}