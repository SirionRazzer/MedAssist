var xmlString = "<form></form>";
var parser = new DOMParser();
var xmlDoc = parser.parseFromString(xmlString, "text/xml"); //important to use "text/xml"
var slides = xmlDoc.createElement("slides");
getFormElement().appendChild(slides);

setFormName('Vstupni formular');

addSlide('banan', true);
addSlide('ananas');

console.log(xmlDoc);

//Form
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
    appendNameToElement(getFormElement(), name);
}

//Slides

/**
 * Adds slide to form
 * 
 * @param {string} name name of the slide
 * @param {boolean} dependency dependency parameter
 * @returns {void}
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
 * Returns number of slides in the form
 * 
 * @returns {Number} number of slides in form
 */
function getNumberOfSlides() {
    return xmlDoc.getElementsByTagName('slide').length;
}

//Questions
//    function addQuestion(slide, )

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