/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/* global currentSlide, xmlDoc */

var questionType;
var questionId;

$(document).ready(function () {
    // Add form dialog success
    $("#addFormDialog .submitDialog").on('click', function () {
        resetControlsAvailability();
        var formName = $("#formName").val();
        createNewForm(formName);
        clearCurrentForm();
        $("#form-container > h3").html("Dotazník: " + formName);
        allowAddingSlides();
    });

    // Add slide dialog success
    $("#addSlideDialog .submitDialog").on('click', function () {
        var slideName = $("#slideName").val();
        var slideDependency = $("#slideDependency").prop("checked");
        var slide = addSlide(slideName, slideDependency);
        addSlideHTML(slideName, slide.getAttribute('sid'));
        allowAddingQuestions();
    });

    // Add question dialogs
    $("#addQuestionGroup a").on('click', function () {
        questionType = $(this).data('type');
    });
    // range dialog
    $("#addRangeDialog .submitDialog").on('click', function () {
        var questionText = $("#rangeText").val();
        var min = $("#rangeMin").val();
        var max = $("#rangeMax").val();
        var step = $("#rangeStep").val();
        var question = addQuestion(currentSlide, questionType, questionText);
        setRange(question, min, max, step);
        addRangeHTML(question.getAttribute('qid'), questionText, min, max, step);
    });
    // question dialog
    $("#addQuestionDialog .submitDialog").on('click', function () {
        var questionText = $("#questionText").val();
        var question = addQuestion(currentSlide, questionType, questionText);
        addQuestionHTML(questionType, questionText, question.getAttribute('qid'));
    });
    // options dialog
    $("#addOptionsDialog .submitDialog").on('click', function () {
        var options = [];
        $('#addOptionsDialog input').each(function () {
            if ($(this).val() != "") {
                options.push($(this).val());
            }
        });
        setOptions(getQuestionById(questionId), options);
        addOptionsHTML(questionId, options);
    });

    $("#finishForm").on('click', function () {
//        $.post('doctor/createForm', {form: xmlDoc});
        $.ajax({
            method: "POST",
            url: "createForm",
            data: {
                form: new XMLSerializer().serializeToString(xmlDoc.documentElement)
            }
        });
    });

    $("#logXML").on('click', function () {
        logXML();
    });
    $(".modal .submitDialog").on('click', function () {
        $(this).parents('.modal').modal('hide');
    });

//    $("#test").on('click', function () {
//        alert("hi");
//        activateAddingDependencies();
//    });
});

//function activateAddingDependencies() {
//    $(".slide .form-group input").on('click', function () {
//        alert($(this).parent().data('qid'));
//    });
//}

/**
 * Resets controls avalalability to their default state
 * @returns {undefined}
 */
function resetControlsAvailability() {
    $(".control-buttons button").addClass("disabled");
    $(".control-buttons button[data-target='#addFormDialog']").removeClass('disabled');
}

/**
 * Enables add slide button
 * @returns {undefined}
 */
function allowAddingSlides() {
    $(".control-buttons button[data-target='#addSlideDialog']").removeClass('disabled');
}

/*
 * Enables add question button
 */
function allowAddingQuestions() {
    $(".control-buttons button.dropdown-toggle").removeClass('disabled');
}

/**
 * Adds slide thumbnail
 * 
 * @param {string} slideName name of the slide
 * @param {int} sid ID of the slide
 * @returns {undefined}
 */
function addSlideThumbnail(slideName, sid) {
    $("#slides-thumbnails-container").append("<div class='col-sm-1 slide-thumbnail' data-sid='" + sid + "'>" + "slide " + sid + "</br>" + slideName + "</div>");
    $(".slide-thumbnail[data-sid='" + sid + "'").on('click', function () {
        changeActiveSlide(sid);
    });
}

/**
 * Adds slide HTML to DOM
 * 
 * @param {string} slideName name of the slide
 * @param {int} sid ID of the slide
 * @returns {undefined}
 */
function addSlideHTML(slideName, sid) {
    $("#slide-container").append("<div class='slide' data-sid='" + sid + "'></div>");
    addSlideThumbnail(slideName, sid);
    changeActiveSlide(sid);
    $("#slide-container .slide.active").append('<h3>slide: ' + slideName + '</h3>');
}

/**
 * Adds question HTML to DOM
 * 
 * @param {string} type type of the question [ENUM]
 * @param {string} text text of the question
 * @param {id} qid ID of the question
 * @returns {undefined}
 */
function addQuestionHTML(type, text, qid) {
    var questionHTML = "";
    switch (type) {
        case "textbox":
            questionHTML = "<div class='form-group' data-qid='" + qid + "'><label>" + text + "</label><input type='textbox' class='form-control'></div>";
            break;
        case "date":
            questionHTML = "<div class='form-group' data-qid='" + qid + "'><label>" + text + "</label><input type='date' class='form-control'></div>";
            break;
        case "checkbox":
            questionHTML = "<div class='form-group' data-qid='" + qid + "'><p><b>" + text + "</b></p><button class='btn btn-default addQuestionOptions' data-type='checkbox' data-qid='" + qid + "'>Přidat možnosti</button></div>";
            break;
        case "radiobutton":
            questionHTML = "<div class='form-group' data-qid='" + qid + "'><p><b>" + text + "</b></p><button class='btn btn-default addQuestionOptions' data-type='radio' data-qid='" + qid + "'>Přidat možnosti</button></div>";
            break;
    }
    $("#slide-container .slide.active").append(questionHTML);
    bindAddingOptions(qid, type);
}

/**
 * Adds options HTML to specified question
 * 
 * @param {int} qid question ID
 * @param {array} options options to add
 * @returns {undefined}
 */
function addOptionsHTML(qid, options) {
    console.log(options);
    var type = $('.form-group[data-qid="' + qid + '"] .addQuestionOptions').data('type');
    for (var i = 0; i < options.length; i++) {
        $('.form-group[data-qid="' + qid + '"]').append("<input type='" + type + "' name='" + qid + "'>" + options[i] + " <br>");
    }
    $('.form-group[data-qid="' + qid + '"] .addQuestionOptions').remove();
}

/**
 * Adds range input HTML to DOM 
 * 
 * @param {int} qid question id
 * @param {string} text question text
 * @param {int} min range minimum
 * @param {int} max range maximum
 * @param {int} step range step
 * @returns {undefined}
 */
function addRangeHTML(qid, text, min, max, step) {
    var questionHTML = "<div class='form-group' qid='" + qid + "'><label>" + text + "</label><input type='range' min='" + min + "' max='" + max + "' step='" + step + "'></div>";
    $("#slide-container .slide.active").append(questionHTML);
}

/**
 * Binds adding options to add options buttons
 * 
 * @param {int} qid question ID
 * @param {string} type type of the question [ENUM]
 * @returns {undefined}
 */
function bindAddingOptions(qid, type) {
    $('.addQuestionOptions[data-qid="' + qid + '"]').on('click', function () {
        questionType = type;
        questionId = qid;
        $('#addOptionsDialog').modal('show');
    });
}

/**
 * Changes currently shown slide
 * 
 * @param {int} sid slide's ID
 * @returns {undefined}
 */
function changeActiveSlide(sid) {
    $("#slide-container .slide.active").removeClass("active");
    $("#slide-container .slide[data-sid='" + sid + "']").addClass('active');
    changeActiveThumbnail(sid);
    var slide = getSlideBySid(sid);
    setCurrentSlide(slide);
}

/**
 * Changes currently active thumbnail
 * 
 * @param {int} sid slide's ID
 * @returns {undefined}
 */
function changeActiveThumbnail(sid) {
    $("#slides-thumbnails-container .slide-thumbnail.active").removeClass('active');
    $("#slides-thumbnails-container .slide-thumbnail[data-sid='" + sid + "']").addClass('active');
}

/**
 * Clears current form and creates blank page for new form
 * 
 * @returns {undefined}
 */
function clearCurrentForm() {
    $("#form-container > h3").html("");
    $("#slide-container").html("");
    $("#slides-thumbnails-container").html("");
}
